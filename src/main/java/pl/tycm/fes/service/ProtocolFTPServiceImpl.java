package pl.tycm.fes.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.LogStatus;
import pl.tycm.fes.controller.service.EventService;
import pl.tycm.fes.model.Event;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.FileList;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.model.TaskConfig;
import pl.tycm.fes.util.MTTools;

@Service
public class ProtocolFTPServiceImpl implements ProtocolFTPService {

	private final Logger logger = Logger.getLogger(this.getClass());
	private final int port = 21;

	@Autowired
	private EventService eventService;

	@Autowired
	private ReportService reportService;

	@Override
	public List<String> receiveFiles(TaskConfig taskConfig, String workingDirectory,
			FileExchangeStatus fileExchangeStatus, Report report) {

		List<String> receiveFileList = new ArrayList<>();
		String subjectAddress = taskConfig.getSubjectAddress();
		String subjectDirectory = taskConfig.getSubjectDirectory();
		String subjectLogin = taskConfig.getSubjectLogin();
		String subjectPassword = taskConfig.getSubjectPassword();

		FTPClient ftpClient = new FTPClient();

		List<String> filesList = new ArrayList<>();
		for (FileList fileList : taskConfig.getFileList()) {
			filesList.add(fileList.getFileName());
		}

		try {
			logger.info("Inicjalizacja połączenia...");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Inicjalizacja połączenia..."));

			logger.info("Trwa łączenie do " + subjectAddress + "...");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Trwa łączenie do " + subjectAddress + "..."));

			ftpClient.connect(subjectAddress, port);
			ftpClient.login(subjectLogin, subjectPassword);
			logger.info("Połączony.");
			eventService.createEvent(
					new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Połączony."));
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			FTPFileFilter filter = new FTPFileFilter() {
				
				@Override
				public boolean accept(FTPFile ftpFile) {
					boolean resultMatch = false;
					for (String fileName : filesList) {
						FileSystem fileSystem = FileSystems.getDefault();
						PathMatcher pathMatcher = fileSystem.getPathMatcher("glob:" + fileName); // Ustawiamy glob zamiast regex
						Path path = Paths.get(ftpFile.getName());
						resultMatch = pathMatcher.matches(path);
						if (resultMatch) {
							break;
						}
					}
					return ftpFile.isFile() && resultMatch;
				}
			};

			FTPFile[] result = ftpClient.listFiles(subjectDirectory, filter);

			logger.info("Pobieram pliki z serwera Podmiotu (" + subjectAddress + "):");
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
					+ "Pobieram pliki z serwera Podmiotu (" + subjectAddress + "):"));
			reportService.addMessage(report, "- Lista plików pobranych z serwera Podmiotu (" + subjectAddress + "):");
			if (result != null && result.length > 0) {
				for (FTPFile fileName : result) {
					logger.info("Pobieram plik: " + fileName.getName() + "...");
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.INFO.getDesc() + fileName.getName() + "..."));

					String remoteFile = fileName.getName();
					File downloadFile = new File(workingDirectory + File.separator + fileName.getName());
					OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
					InputStream inputStream = ftpClient.retrieveFileStream(remoteFile);

					byte[] bytesArray = new byte[16 * 4096];
					int bytesRead = -1;
					while ((bytesRead = inputStream.read(bytesArray)) != -1) {
						outputStream.write(bytesArray, 0, bytesRead);
					}
					boolean success = ftpClient.completePendingCommand();
					if (success) {
						receiveFileList.add(remoteFile);
						logger.info("Plik pobrany.");
						eventService.createEvent(new Event(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik pobrany."));
						reportService.addMessage(report, remoteFile);
					}
					outputStream.close();
					inputStream.close();
				}
			} else {
				logger.error("Brak plików do pobrania");
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "Brak plików do pobrania"));
				reportService.addMessage(report, "-> Brak plików do pobrania.");
			}
		} catch (FTPConnectionClosedException ex) {
			logger.fatal("Błąd połączenia: " + ex.getMessage());
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Błąd połączenia: " + ex.getMessage()));
			reportService.addMessage(report, "-> Błąd: Nie moża połączyć się z serwerem: " + subjectAddress);
		} catch (IOException ex) {
			logger.fatal("StackTrace: ", ex);
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Błąd: " + ex.getMessage()));
			reportService.addMessage(report, "-> Błąd podczas pobierania plików.");
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				logger.fatal("StackTrace: ", ex);
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Błąd: " + ex.getMessage()));
			}
		}
		return receiveFileList;
	}
}
