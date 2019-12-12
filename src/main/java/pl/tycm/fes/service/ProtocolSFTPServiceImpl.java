package pl.tycm.fes.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.sshtools.net.SocketTransport;
import com.sshtools.publickey.InvalidPassphraseException;
import com.sshtools.publickey.SshPrivateKeyFile;
import com.sshtools.publickey.SshPrivateKeyFileFactory;
import com.sshtools.sftp.FileTransferProgress;
import com.sshtools.sftp.SftpClient;
import com.sshtools.sftp.SftpFile;
import com.sshtools.sftp.SftpStatusException;
import com.sshtools.ssh.HostKeyVerification;
import com.sshtools.ssh.PasswordAuthentication;
import com.sshtools.ssh.PublicKeyAuthentication;
import com.sshtools.ssh.SshAuthentication;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshConnector;
import com.sshtools.ssh.SshException;
import com.sshtools.ssh.components.SshKeyPair;
import com.sshtools.ssh.components.SshPublicKey;
import com.sshtools.ssh2.Ssh2Client;

import pl.tycm.fes.LogStatus;
import pl.tycm.fes.controller.service.EventService;
import pl.tycm.fes.model.Event;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.FileList;
import pl.tycm.fes.model.LocalFileList;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.model.TaskConfig;
import pl.tycm.fes.util.MTTools;

@Service
public class ProtocolSFTPServiceImpl implements ProtocolSFTPService {

	private final Logger logger = Logger.getLogger(this.getClass());

	private final EventService eventService;

	private final ReportService reportService;

	private final LocalFileListService localFileListService;

	public ProtocolSFTPServiceImpl(EventService eventService, ReportService reportService,
			LocalFileListService localFileListService) {
		this.eventService = eventService;
		this.reportService = reportService;
		this.localFileListService = localFileListService;
	}

	@Override
	public List<String> receiveFiles(TaskConfig taskConfig, String workingDirectory, String privateKeyName,
			byte[] privateKey, FileExchangeStatus fileExchangeStatus, Report report) {

		List<String> receiveFileList = new ArrayList<>();
		String subjectAddress = taskConfig.getSubjectAddress();
		String subjectLogin = taskConfig.getSubjectLogin();
		String subjectPassword = taskConfig.getSubjectPassword();
		String subjectDirectory = taskConfig.getSubjectDirectory();
		String sourceFileMode = taskConfig.getSourceFileMode();

		List<String> filesList = new ArrayList<>();
		for (FileList fileList : taskConfig.getFileList()) {
			filesList.add(fileList.getFileName());
		}

		Ssh2Client ssh2 = this.createSFTPConnection(subjectAddress, subjectLogin, subjectPassword, privateKeyName,
				privateKey, fileExchangeStatus, report);

		if (ssh2 != null) {
			logger.info("Pobieram pliki z serwera Podmiotu (" + subjectAddress + "):");
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
					+ "Pobieram pliki z serwera Podmiotu (" + subjectAddress + "):"));
			reportService.addMessage(report, "- Lista plików pobranych z serwera Podmiotu (" + subjectAddress + "):");

			try {
				SftpClient sftp = new SftpClient(ssh2);
				/**
				 * Now perform some binary operations
				 */
				sftp.setTransferMode(SftpClient.MODE_BINARY);

				/**
				 * Change directory
				 */
				sftp.lcd(workingDirectory);
				sftp.cd(subjectDirectory);

				/**
				 * get all files in the remote directory
				 */
				sftp.setRegularExpressionSyntax(SftpClient.GlobSyntax);

				// create progress tracker
				FileTransferProgress progress = new FileTransferProgress() {
					// int progressSign = 0;
					String remoteFileName = null;

					public void completed() {
						logger.info("Plik pobrany.");
						eventService.createEvent(new Event(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik pobrany."));
						receiveFileList.add(remoteFileName);
						reportService.addMessage(report, remoteFileName);

						if (sourceFileMode != null) {
							switch (sourceFileMode) {
							case "rename":
								try {
									logger.info("Zmieniam na serwerze nazwę pliku z: " + remoteFileName + " na "
											+ remoteFileName + ".downloaded" + "...");
									eventService.createEvent(new Event(fileExchangeStatus,
											MTTools.getLogDate() + LogStatus.INFO.getDesc()
													+ "Zmieniam na serwerze nazwę pliku z: " + remoteFileName + " na "
													+ remoteFileName + ".downloaded" + "..."));

									sftp.rename(remoteFileName, remoteFileName + ".downloaded");
									logger.info("Nazwa została zmieniona.");
									eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
											+ LogStatus.INFO.getDesc() + "Nazwa została zmieniona."));
								} catch (SftpStatusException e) {
									logger.error("Błąd zmiany nazwy plików: " + e.getMessage());
									eventService.createEvent(new Event(fileExchangeStatus,
											MTTools.getLogDate() + LogStatus.ERROR.getDesc()
													+ "Błąd zmiany nazwy plików: " + e.getMessage()));
									reportService.addMessage(report, "-> Błąd zmiany nazwy pliku: " + remoteFileName);
									logger.error("StackTrace: ", e);
								} catch (SshException e) {
									logger.error("Błąd zmiany nazwy plików: " + e.getMessage());
									eventService.createEvent(new Event(fileExchangeStatus,
											MTTools.getLogDate() + LogStatus.ERROR.getDesc()
													+ "Błąd zmiany nazwy plików: " + e.getMessage()));
									reportService.addMessage(report, "-> Błąd zmiany nazwy pliku: " + remoteFileName);
									logger.error("StackTrace: ", e);
								}
								break;
							case "delete":
								try {
									logger.info("Kasuję na serwerze plik: " + remoteFileName + "...");
									eventService.createEvent(new Event(fileExchangeStatus,
											MTTools.getLogDate() + LogStatus.INFO.getDesc()
													+ "Kasuję na serwerze plik: " + remoteFileName + "..."));
									sftp.rm(remoteFileName);
									logger.info("Plik skasowany.");
									eventService.createEvent(new Event(fileExchangeStatus,
											MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik skasowany."));
								} catch (SftpStatusException e) {
									logger.error("Błąd kasowania plików: " + e.getMessage());
									eventService.createEvent(new Event(fileExchangeStatus,
											MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR "
													+ "Błąd kasowania plików: " + e.getMessage()));
									reportService.addMessage(report, "-> Błąd kasowania pliku: " + remoteFileName);
									logger.error("StackTrace: ", e);
								} catch (SshException e) {
									logger.error("Błąd kasowania plików: " + e.getMessage());
									eventService.createEvent(new Event(fileExchangeStatus,
											MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR "
													+ "Błąd kasowania plików: " + e.getMessage()));
									reportService.addMessage(report, "-> Błąd kasowania pliku: " + remoteFileName);
									logger.error("StackTrace: ", e);
								}
								break;
							case "sync":
								logger.info("Synchronizuje z serwerem plik: " + remoteFileName + "...");
								eventService.createEvent(
										new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
												+ "Synchronizuje z serwerem plik: " + remoteFileName + "..."));
								localFileListService.createLocalFileList(new LocalFileList(taskConfig, remoteFileName));
								logger.info("Plik zsynchronizowany.");
								eventService.createEvent(new Event(fileExchangeStatus,
										MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik zsynchronizowany."));
								break;
							}
						}
					}

					public boolean isCancelled() {
						return false;
					}

					public void progressed(long arg0) {
						/*
						 * switch (progressSign) { case 0: System.out.print("\r|"); progressSign++;
						 * break; case 1: System.out.print("\r/"); progressSign++; break; case 2:
						 * System.out.print("\r–"); progressSign++; break; case 3:
						 * System.out.print("\r\\"); progressSign = 0; break; }
						 */
					}

					public void started(long arg0, String arg1) {
						remoteFileName = arg1.substring(arg1.lastIndexOf("/") + 1);
						logger.info("Pobieram plik: " + remoteFileName + "...");
						eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
								+ LogStatus.INFO.getDesc() + "Pobieram plik: " + remoteFileName + "..."));
					}
				};

				if (sourceFileMode != null && sourceFileMode.equals("sync")) {
					List<LocalFileList> localFileList = localFileListService.getAllLocalFileList(taskConfig.getId());

					Collection<String> localFiles = new ArrayList<String>();
					for (LocalFileList localFile : localFileList) {
						localFiles.add(localFile.getFileName());
					}
					for (String fileName : filesList) {
						SftpFile[] remoteFileList = sftp.matchRemoteFiles(fileName);
						Collection<String> remoteFiles = new ArrayList<String>();

						for (SftpFile sftpFile : remoteFileList) {
							remoteFiles.add(sftpFile.getFilename());
						}
						
						remoteFiles.removeAll(localFiles);
						for (String file : remoteFiles) {
							//localFileListService.createLocalFileList(new LocalFileList(taskConfig, file));
							try {
								sftp.get(file, progress);
							} catch (FileNotFoundException th) {
								logger.fatal("Plik nie istnieje: " + th.getMessage());
								eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
										+ LogStatus.FATAL.getDesc() + "Plik nie istnieje: " + th.getMessage()));
							}
						}
					}
				} else {
					for (String fileName : filesList) {
						try {
							sftp.getFiles(fileName, progress);
						} catch (FileNotFoundException th) {
							logger.fatal("Plik nie istnieje: " + th.getMessage());
							eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
									+ LogStatus.FATAL.getDesc() + "Plik nie istnieje: " + th.getMessage()));
						}
					}
				}
			} catch (SftpStatusException th) {
				logger.fatal("Błąd pobierania plików: " + th.getMessage());
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc()
						+ "Błąd pobierania plików: " + th.getMessage()));
				reportService.addMessage(report, "-> Błąd pobierania plików: " + th.getMessage());
			} catch (Throwable th) {
				logger.fatal("StackTrace: ", th);
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.FATAL.getDesc() + th.getMessage()));
			}
			if (receiveFileList.isEmpty()) {
				logger.error("Brak plików do pobrania");
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "Brak plików do pobrania."));
				reportService.addMessage(report, "-> Błąd: Brak plików do pobrania");
			}
		} else {
			logger.fatal("Problem z nawiązaniem połączenia.");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Problem z nawiązaniem połączenia."));
			reportService.addMessage(report, "-> Problem z nawiązaniem połączenia.");
		}
		return receiveFileList;
	}

	@Override
	public boolean sendFiles(List<String> filesList, TaskConfig taskConfig, String workingDirectory,
			String privateKeyName, byte[] privateKey, FileExchangeStatus fileExchangeStatus, Report report) {

		String subjectAddress = taskConfig.getSubjectAddress();
		String subjectLogin = taskConfig.getSubjectLogin();
		String subjectPassword = taskConfig.getSubjectPassword();
		String subjectDirectory = taskConfig.getSubjectDirectory();

		Ssh2Client ssh2 = this.createSFTPConnection(subjectAddress, subjectLogin, subjectPassword, privateKeyName,
				privateKey, fileExchangeStatus, report);

		if (ssh2 != null) {
			logger.info("Wysyłam pliki na serwer Podmiotu (" + subjectAddress + "):");
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
					+ "Wysyłam pliki na serwer Podmiotu (" + subjectAddress + "):"));
			reportService.addMessage(report, "- Lista plików wysłanych na serwer Podmiotu (" + subjectAddress + "):");
			try {
				SftpClient sftp = new SftpClient(ssh2);
				/**
				 * Now perform some binary operations
				 */
				sftp.setTransferMode(SftpClient.MODE_BINARY);

				/**
				 * Change directory
				 */
				sftp.lcd(workingDirectory);
				sftp.cd(subjectDirectory);

				// sftp.setRegularExpressionSyntax(SftpClient.GlobSyntax);

				for (String fileName : filesList) {
					logger.info("Pobieram plik: " + fileName + "...");
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Pobieram plik: " + fileName + "..."));
					sftp.put(fileName);
					logger.info("Plik pobrany.");
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik pobrany."));
					reportService.addMessage(report, fileName);
				}
			} catch (SftpStatusException th) {
				logger.fatal("Błąd przesyłania plików: " + th.getMessage());
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc()
						+ "Błąd przesyłania plików: " + th.getMessage()));
				reportService.addMessage(report, "-> Błąd przesyłania plików: " + th.getMessage());
				return false;
			} catch (Throwable th) {
				logger.fatal("StackTrace: ", th);
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.FATAL.getDesc() + th.getMessage()));
				return false;
			}
			return true;
		} else {
			logger.fatal("Problem z nawiązaniem połączenia.");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Problem z nawiązaniem połączenia."));
			reportService.addMessage(report, "-> Problem z nawiązaniem połączenia.");
			return false;
		}
	}

	private Ssh2Client createSFTPConnection(String serverAddress, String remoteLogin, String remotePassword,
			String privateKeyName, byte[] privateKey, FileExchangeStatus fileExchangeStatus, Report report) {
		Ssh2Client ssh2 = null;

		try {
			logger.info("Inicjalizacja połączenia....");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Inicjalizacja połączenia...."));

			int idx = serverAddress.indexOf(':');
			int port = 22;
			if (idx > -1) {
				port = Integer.parseInt(serverAddress.substring(idx + 1));
				serverAddress = serverAddress.substring(0, idx);

			}

			logger.info("Trwa łączenie do " + serverAddress);
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Trwa łączenie do " + serverAddress));

			/**
			 * Create an SshConnector instance
			 */
			SshConnector con = SshConnector.createInstance();

			// con.setSupportedVersions(1);
			// Lets do some host key verification
			HostKeyVerification hkv = new HostKeyVerification() {
				public boolean verifyHost(String adresSerwera, SshPublicKey key) {
					try {
						logger.info("Klucz serwera (" + key.getAlgorithm() + "): ");
						eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
								+ LogStatus.INFO.getDesc() + "Klucz serwera (" + key.getAlgorithm() + "): "));

						logger.info(key.getFingerprint());
						eventService.createEvent(new Event(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.INFO.getDesc() + key.getFingerprint()));
					} catch (SshException e) {
						logger.fatal("Problem z połączeniem. Błąd: " + e.getMessage());
						eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
								+ LogStatus.FATAL.getDesc() + "Problem z połączeniem. Błąd: " + e.getMessage()));
						reportService.addMessage(report, "-> Problem z połączeniem. Błąd: " + e.getMessage());
						return false;
					}
					return true;
				}
			};

			con.getContext().setHostKeyVerification(hkv);

			/**
			 * Connect to the host
			 */
			SshClient ssh = con.connect(new SocketTransport(serverAddress, port), remoteLogin);

			logger.info(serverAddress + "... połączony");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + serverAddress + "... połączony"));

			logger.info("Trwa autoryzacja połączenia...");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Trwa autoryzacja połączenia..."));
			ssh2 = (Ssh2Client) ssh;

			int authStatus;
			if (privateKey != null) {
				authStatus = privateKeyAuthentication(privateKeyName, privateKey, ssh2, fileExchangeStatus);
				if (authStatus == SshAuthentication.FURTHER_AUTHENTICATION_REQUIRED) {
					authStatus = passwordAuthentication(remotePassword, ssh2, fileExchangeStatus);
				}
			} else {
				authStatus = passwordAuthentication(remotePassword, ssh2, fileExchangeStatus);
			}

			if (authStatus != SshAuthentication.COMPLETE && ssh.isConnected()) {
				return null;
			}
		} catch (NoRouteToHostException th) {
			logger.fatal(th.getMessage());
			eventService.createEvent(
					new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc() + th.getMessage()));
			reportService.addMessage(report, "-> " + serverAddress + ": " + th.getMessage());
			return null;
		} catch (FileNotFoundException th) {
			logger.fatal(th.getMessage());
			eventService.createEvent(
					new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc() + th.getMessage()));
			reportService.addMessage(report, "-> Błąd połączenia z serwerem " + serverAddress + ": " + th.getMessage());
			return null;
		} catch (SshException th) {
			logger.fatal(th.getMessage());
			eventService.createEvent(
					new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc() + th.getMessage()));
			reportService.addMessage(report, "-> Błąd połączenia z serwerem " + serverAddress + ": " + th.getMessage());
			return null;
		} catch (ConnectException th) {
			logger.fatal(th.getMessage());
			eventService.createEvent(
					new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc() + th.getMessage()));
			reportService.addMessage(report, "-> Błąd połączenia z serwerem " + serverAddress + ": " + th.getMessage());
			return null;
		} catch (Throwable th) {
			logger.fatal(th.getMessage());
			logger.error("StackTrace: ", th);
			eventService.createEvent(
					new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc() + th.getMessage()));
			return null;
		}
		logger.info("Autoryzacja poprawna.");
		eventService.createEvent(new Event(fileExchangeStatus,
				MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Autoryzacja poprawna."));
		return ssh2;
	}

	private int privateKeyAuthentication(String privateKeyName, byte[] privateKey, Ssh2Client ssh2,
			FileExchangeStatus fileExchangeStatus) throws IOException, InvalidPassphraseException, SshException {

		logger.info("Private key file: " + privateKeyName);
		;
		eventService.createEvent(new Event(fileExchangeStatus,
				MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Private key file: " + privateKeyName));
		/**
		 * Authenticate the user using public key authentication
		 */
		SshPrivateKeyFile pkfile = SshPrivateKeyFileFactory.parse((privateKey));
		SshKeyPair pair;
		pair = pkfile.toKeyPair(null);

		PublicKeyAuthentication pk = new PublicKeyAuthentication();
		pk.setPrivateKey(pair.getPrivateKey());
		pk.setPublicKey(pair.getPublicKey());

		int authStatus = ssh2.authenticate(pk);

		if (authStatus == SshAuthentication.FAILED) {
			logger.fatal("Niepoprawna autoryzacja kluczem publicznym. Kod błędu: " + authStatus);
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Niepoprawna autoryzacja kluczem publicznym."));
		}
		return authStatus;
	}

	private int passwordAuthentication(String remotePassword, Ssh2Client ssh2, FileExchangeStatus fileExchangeStatus)
			throws IOException, InvalidPassphraseException, SshException {
		logger.info("Password: " + "*****");
		eventService.createEvent(
				new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Hasło: " + "*****"));
		/**
		 * Authenticate the user using password authentication
		 */
		PasswordAuthentication pwd = new PasswordAuthentication();
		pwd.setPassword(remotePassword);
		int authStatus = ssh2.authenticate(pwd);

		switch (authStatus) {
		case SshAuthentication.FAILED:
			logger.fatal("Niepoprawna autoryzacja hasłem. Kod błędu: " + authStatus);
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Niepoprawna autoryzacja hasłem."));
			return authStatus;
		case SshAuthentication.FURTHER_AUTHENTICATION_REQUIRED:
			logger.fatal(
					"Wymagana jest dodatkowa autoryzacja przy pomocy klucza publicznego. Kod błędu: " + authStatus);
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc()
					+ "Wymagana jest dodatkowa autoryzacja przy pomocy klucza publicznego."));
			return authStatus;
		default:
			return authStatus;
		}
	}

	@Override
	public void testConnection(String subjectAddress, String subjectLogin, String subjectPassword,
			String privateKeyName, byte[] privateKey, FileExchangeStatus fileExchangeStatus) {

		Report report = new Report();

		Ssh2Client ssh2 = this.createSFTPConnection(subjectAddress, subjectLogin, subjectPassword, privateKeyName,
				privateKey, fileExchangeStatus, report);

		if (ssh2 != null) {
			System.out.println("Błąd połączenia.");
		}
	}
}
