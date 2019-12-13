package pl.tycm.fes.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import pl.tycm.fes.LogStatus;
import pl.tycm.fes.controller.service.EventService;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.util.MTTools;

@Service
public class ZipServiceImpl implements ZipService {

	private final Logger logger = Logger.getLogger(this.getClass());

	private final EventService eventService;

	private final ReportService reportService;

	public ZipServiceImpl(EventService eventService, ReportService reportService) {
		this.eventService = eventService;
		this.reportService = reportService;
	}

	@Override
	public List<String> decompressZipFile(String workingDirectory, List<String> receiveFileList,
			FileExchangeStatus fileExchangeStatus, Report report) {

		List<String> newReceiveFileList = new ArrayList<>();

		for (String fileName : receiveFileList) {
			if (fileName.contains(".zip")) {
				FileInputStream fis = null;
				try {
					logger.info("Dekompresuje plik: " + fileName + "...");
					eventService.createEvent(fileExchangeStatus, MTTools.getLogDate()
							+ LogStatus.INFO.getDesc() + "Dekompresuje plik: " + fileName + "...");

					fis = new FileInputStream(workingDirectory + File.separator + fileName);
					ZipInputStream zis = new ZipInputStream(fis);
					ZipEntry ze = zis.getNextEntry();

					while (ze != null) {
						String newFileName = ze.getName();

						FileOutputStream fos = new FileOutputStream(workingDirectory + File.separator + newFileName);

						byte[] bytesArray = new byte[16 * 4096];
						int bytesRead = -1;
						while ((bytesRead = zis.read(bytesArray)) != -1) {
							fos.write(bytesArray, 0, bytesRead);
						}
						fos.close();
						logger.info("Plik z dekompresowany.");
						eventService.createEvent(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik z dekompresowany.");

						newReceiveFileList.add(newFileName);
						ze = zis.getNextEntry();
					}
					zis.closeEntry();
					zis.close();
					fis.close();
				} catch (IOException ex) {
					logger.error("StackTrace: ", ex);
					eventService.createEvent(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR " + ex.getMessage());
					reportService.addMessage(report,
							"-> Błąd: Nie powiodła się operacja dekompresji pliku: " + fileName);
				} finally {
					try {
						if (fis != null) {
							fis.close();
						}
					} catch (IOException e) {
						logger.fatal("StackTrace: ", e);
					}
					File file = new File(workingDirectory + File.separator + fileName);
					logger.info("Kasuje plik: " + fileName + "...");
					eventService.createEvent(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Kasuje plik: " + fileName + "...");

					if (file.delete()) {
						logger.info("Plik skasowany.");
						eventService.createEvent(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik skasowany.");
					} else {
						logger.error("Błąd: Nie można skasować pliku: " + fileName);
						eventService.createEvent(fileExchangeStatus, MTTools.getLogDate()
								+ LogStatus.ERROR.getDesc() + "Błąd: Nie można skasować pliku: " + fileName);
					}
				}
			} else {
				newReceiveFileList.add(fileName);
			}
		}
		return newReceiveFileList;
	}
}
