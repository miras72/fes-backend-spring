package pl.tycm.fes.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb.SMB1NotSupportedException;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.io.InputStreamByteChunkProvider;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import pl.tycm.fes.LogStatus;
import pl.tycm.fes.model.Event;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Server;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.service.controller.EventService;
import pl.tycm.fes.util.MTTools;

@Service
public class ServerServiceImpl implements ServerService {

	private final Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private EventService eventService;

	@Autowired
	private ReportService reportService;

	@Override
	public boolean sendFiles(List<Server> servers, String workingDirectory, List<String> receiveFileList,
			FileExchangeStatus fileExchangeStatus, Report report) {

		boolean isOK = true;

		if (workingDirectory.endsWith("/"))
			workingDirectory = workingDirectory.substring(0, workingDirectory.length() - 1);

		File directory = new File(workingDirectory);
		for (Server server : servers) {
			logger.info("Kopiuje pliki na serwer (" + server.getServerAddress() + "):");
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
					+ "Kopiuje pliki na serwer (" + server.getServerAddress() + ")..."));
			reportService.addMessage(report,
					"- Lista plików skopiowanych na serwer (" + server.getServerAddress() + "):");

			SmbConfig config = SmbConfig.builder().withMultiProtocolNegotiate(true).build();
			SMBClient client = new SMBClient(config);
			SMB2CreateDisposition createDisposition = SMB2CreateDisposition.FILE_OVERWRITE_IF;

			String domainName = server.getServerLogin().substring(0, server.getServerLogin().indexOf("/"));
			String userName = server.getServerLogin()
					.substring(server.getServerLogin().lastIndexOf("/") + 1);

			InputStream in = null;
			com.hierynomus.smbj.share.File smbFile = null;

			try (Connection connection = client.connect(server.getServerAddress())) {
				AuthenticationContext ac = new AuthenticationContext(userName,
						server.getServerPassword().toCharArray(), domainName);
				Session session = connection.authenticate(ac);

				try (DiskShare share = (DiskShare) session.connectShare(server.getServerDirectory())) {

					for (String fileName : receiveFileList) {
						logger.info("Kopiuje plik: " + fileName);
						eventService.createEvent(new Event(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Kopiuje plik: " + fileName));
						in = new FileInputStream(directory + File.separator + fileName);
						smbFile = share.openFile(fileName,
								EnumSet.of(AccessMask.GENERIC_WRITE, AccessMask.GENERIC_READ), null, null,
								createDisposition, null);
						smbFile.write(new InputStreamByteChunkProvider(in));
						smbFile.close();
						in.close();

						logger.info("Plik skopiowany.");
						eventService.createEvent(new Event(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik skopiowany."));
						reportService.addMessage(report, fileName);

						isOK = true;
					}
				} catch (SMBApiException e) {
					logger.fatal(e.getMessage());
					eventService
							.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc()
									+ "Nieprawidłowa nazwa zasobu: " + server.getServerDirectory()));
					reportService.addMessage(report,
							"-> Błąd: Nieprawidłowa nazwa zasobu: " + server.getServerDirectory());
					isOK = false;
				} catch (Exception e) {
					logger.fatal("StackTrace: ", e);
					isOK = false;
				} finally {
					try {
						if (in != null) {
							in.close();
						}
					} catch (IOException e) {
						logger.fatal("StackTrace: ", e);
						isOK = false;
					}
				}
			} catch (IOException originalException) {
				Throwable cause = originalException;
				while (cause.getCause() != null) {
					cause = cause.getCause();
				}
				if (cause instanceof SMB1NotSupportedException) {
					logger.info("Serwer nie obsługuje protokołu SMB2!!! Zmiana protokołu na SMB1.");
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Serwer nie obsługuje protokołu SMB2!!! Zmiana protokołu na SMB1."));
					isOK = sendFilesSMB1(directory, receiveFileList, server, fileExchangeStatus, report);
				} else {
					logger.fatal("StackTrace: ", originalException);
					eventService
							.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc()
									+ "Problem z połączeniem do serwera: " + server.getServerAddress()));
					reportService.addMessage(report,
							"-> Błąd: Problem z połączeniem do serwera: " + server.getServerAddress());
					isOK = false;
				}
			}
		}
		return isOK;
	}

	@Override
	public List<String> receiveFiles(List<Server> servers, String workingDirectory, List<String> fileList,
			FileExchangeStatus fileExchangeStatus, Report report) {

		if (workingDirectory.endsWith("/"))
			workingDirectory = workingDirectory.substring(0, workingDirectory.length() - 1);

		File directory = new File(workingDirectory);
		List<String> receiveFileList = new ArrayList<>();

		boolean status = false;

		SMB2CreateDisposition createDisposition = SMB2CreateDisposition.FILE_OPEN;

		for (Server server : servers) {
			logger.info("Pobieram pliki z serwera (" + server.getServerAddress() + "):");
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
					+ "Pobieram pliki z serwera (" + server.getServerAddress() + ") ..."));
			reportService.addMessage(report,
					"- Lista plików pobranych z serwera (" + server.getServerAddress() + "):");

			SmbConfig config = SmbConfig.builder().withMultiProtocolNegotiate(true).build();
			SMBClient client = new SMBClient(config);

			String domainName = server.getServerLogin().substring(0, server.getServerLogin().indexOf("/"));
			String userName = server.getServerLogin()
					.substring(server.getServerLogin().lastIndexOf("/") + 1);

			FileOutputStream out = null;
			com.hierynomus.smbj.share.File smbFile = null;

			try (Connection connection = client.connect(server.getServerAddress())) {
				AuthenticationContext ac = new AuthenticationContext(userName,
						server.getServerPassword().toCharArray(), domainName);
				Session session = connection.authenticate(ac);

				try (DiskShare share = (DiskShare) session.connectShare(server.getServerDirectory())) {
					for (String pattern : fileList) {
						for (FileIdBothDirectoryInformation file : share.list(null, pattern)) {
							String fileName = file.getFileName();
							smbFile = share.openFile(file.getFileName(),
									EnumSet.of(AccessMask.GENERIC_READ), null, null, createDisposition, null);
							InputStream input = smbFile.getInputStream();
							out = new FileOutputStream(directory + File.separator + fileName);

							logger.info("Pobieram plik: " + fileName);
							eventService.createEvent(new Event(fileExchangeStatus,
									MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Pobieram plik: " + fileName));

							byte[] bytesArray = new byte[16 * 4096];
							int bytesRead = -1;
							while ((bytesRead = input.read(bytesArray)) != -1) {
								out.write(bytesArray, 0, bytesRead);
							}

							receiveFileList.add(fileName);
							status = true;

							logger.info("Plik pobrany.");
							eventService.createEvent(new Event(fileExchangeStatus,
									MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik pobrany."));
							reportService.addMessage(report, fileName);

							out.close();
							input.close();
							smbFile.close();
							share.rm(fileName);
						}
					}

				} catch (SMBApiException e) {
					logger.fatal(e.getMessage());
					eventService
							.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc()
									+ "Błąd podczas pobierania pliku z serwera: " + e.getStatusCode()));
					reportService.addMessage(report, "-> Błąd podczas pobierania pliku z serwera.");
					status = true;
				} catch (Exception e) {
					logger.fatal("StackTrace: ", e);
				} finally {
					try {
						if (out != null) {
							out.close();
						}
					} catch (IOException e) {
						logger.fatal("StackTrace: ", e);
					}
				}

			} catch (SMB1NotSupportedException es) {
			} catch (IOException e1) {
				logger.fatal("StackTrace: ", e1);
			}
		}
		if (!status) {
			logger.error("Brak plików do pobrania.");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Brak plików do pobrania."));
			reportService.addMessage(report, "-> Błąd: Brak plików do pobrania");
		}
		return receiveFileList;
	}

	private boolean sendFilesSMB1(File directory, List<String> receiveFileList, Server server,
			FileExchangeStatus fileExchangeStatus, Report report) {

		boolean isOK = true;

		for (String fileName : receiveFileList) {
			InputStream in = null;
			try {
				in = new FileInputStream(directory + File.separator + fileName);

				String domainName = server.getServerLogin().substring(0,
						server.getServerLogin().indexOf("/"));
				String userName = server.getServerLogin()
						.substring(server.getServerLogin().lastIndexOf("/") + 1);

				NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domainName, userName,
						server.getServerPassword());

				SmbFile newFile = new SmbFile("smb://" + server.getServerAddress() + "/"
						+ server.getServerDirectory() + "/" + fileName, auth);
				SmbFileOutputStream smbfos = new SmbFileOutputStream(newFile);
				logger.info("Kopiuje plik: " + fileName);
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Kopiuje plik: " + fileName));

				byte[] bytesArray = new byte[16 * 4096];
				int bytesRead = -1;
				while ((bytesRead = in.read(bytesArray)) != -1) {
					smbfos.write(bytesArray, 0, bytesRead);
				}
				in.close();
				smbfos.close();

				logger.info("Plik skopiowany.");
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik skopiowany."));
				reportService.addMessage(report, fileName);

				isOK = true;

			} catch (SmbException ex) {
				switch (ex.getMessage()) {
				case "0xC000007F":
					logger.fatal("Brak miejsca na dysku kod błądu: " + ex.getMessage());
					eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
							+ LogStatus.FATAL.getDesc() + "Brak miejsca na dysku kod błądu: " + ex.getMessage()));
					reportService.addMessage(report,
							"-> Błąd: Brak miejsca na dysku. Nie powiodła się próba skopiowania pliku: " + fileName);
					isOK = false;
					break;
				default:
					logger.fatal("StackTrace: ", ex);
					eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
							+ LogStatus.FATAL.getDesc() + "Nie powiodła się próba skopiowania pliku: " + fileName));
					reportService.addMessage(report, "-> Błąd: Nie powiodła się próba skopiowania pliku: " + fileName);
					isOK = false;
					break;
				}
			} catch (IOException ex) {
				logger.fatal("StackTrace: ", ex);
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc()
						+ "Nie powiodła się próba skopiowania pliku: " + fileName));
				reportService.addMessage(report, "-> Błąd: Nie powiodła się próba skopiowania pliku: " + fileName);
				isOK = false;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {
					logger.fatal("StackTrace: ", e);
				}
			}
		}
		return isOK;
	}
}
