package pl.tycm.fes.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.LogStatus;
import pl.tycm.fes.model.Event;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.FileList;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.model.TaskConfig;
import pl.tycm.fes.service.controller.EventService;
import pl.tycm.fes.util.MTTools;

@Service
public class ProtocolSSLServiceImpl implements ProtocolSSLService {

	private final Logger logger = Logger.getLogger(this.getClass());

	private static final String ERROR_DIRECTORY = "html";

	@Autowired
	private EventService eventService;

	@Autowired
	private ReportService reportService;

	@Override
	public List<String> receiveFiles(TaskConfig taskConfig, String workingDirectory,
			FileExchangeStatus fileExchangeStatus, Report report) {

		boolean status = false;
		List<String> receiveFileList = new ArrayList<>();
		String subjectAddress = taskConfig.getSubjectAddress();
		String subjectDirectory = taskConfig.getSubjectDirectory();
		String loginForm = taskConfig.getSubjectLoginForm();
		String logoutForm = taskConfig.getSubjectLogoutForm();
		String subjectLogin = taskConfig.getSubjectLogin();
		String subjectPassword = taskConfig.getSubjectPassword();

		boolean isConnected = this.createSSLConnection(subjectAddress, loginForm, subjectLogin, subjectPassword,
				fileExchangeStatus, report);
		if (!isConnected)
			return receiveFileList;

		List<String> filesList = new ArrayList<>();
		for (FileList fileList : taskConfig.getFileList()) {
			filesList.add(fileList.getFileName());
		}

		try {
			logger.info("Pobieram pliki z serwera Podmiotu (" + subjectAddress + "):");
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
					+ "Pobieram pliki z serwera Podmiotu (" + subjectAddress + "):"));
			reportService.addMessage(report, "- Lista plików pobranych z serwera Podmiotu (" + subjectAddress + "):");

			for (String fileName : filesList) {
				URL obj = new URL(subjectDirectory + fileName);
				HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
				conn.setUseCaches(false);
				conn.setRequestMethod("GET");
				logger.info("Pobieram plik: " + fileName + "...");
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Pobieram plik: " + fileName + "..."));

				int responseCode = conn.getResponseCode();

				if (responseCode == 200) {

					File downloadFile = new File(workingDirectory + File.separator + fileName);
					OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
					InputStream inputStream = (InputStream) conn.getInputStream();

					byte[] bytesArray = new byte[16 * 4096];
					int bytesRead = -1;
					// int progressSign = 0;
					while ((bytesRead = inputStream.read(bytesArray)) != -1) {
						outputStream.write(bytesArray, 0, bytesRead);
						/*
						 * switch (progressSign) { case 0: System.out.print("\r|"); progressSign++;
						 * break; case 1: System.out.print("\r/"); progressSign++; break; case 2:
						 * System.out.print("\r-"); progressSign++; break; case 3:
						 * System.out.print("\r\\"); progressSign = 0; break; }
						 */
					}
					outputStream.close();
					inputStream.close();
					logger.info("Plik pobrany");
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik pobrany."));

					// Sprawdza czy pobrany plik jest w formacie html
					// jezeli jest to pobrany plik zawiera strone z bledem "brak pliku"
					BufferedReader br = null;
					boolean found = false;
					try {
						br = new BufferedReader(new InputStreamReader(
								new FileInputStream(workingDirectory + File.separator + fileName)));
						String line;
						while ((line = br.readLine()) != null) {
							if (line.contains("<!DOCTYPE")) {
								found = true;
								break;
							}
						}
					} finally {
						try {
							if (br != null)
								br.close();
						} catch (Exception e) {
							logger.error("Problem podaczas zamykania BufferReader " + e.toString());
							eventService.createEvent(
									new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc()
											+ "Problem podaczas zamykania BufferReader " + e.toString()));
						}
					}

					if (!found) {
						receiveFileList.add(fileName);
						reportService.addMessage(report, fileName);
						status = true;
					} else {
						File sourceFile = new File(workingDirectory + File.separator + fileName);

						if (!Files.isDirectory(Paths.get(workingDirectory + File.separator + ERROR_DIRECTORY))) {
							logger.info("Tworzę katalog na pliki html: " + ERROR_DIRECTORY);
							eventService.createEvent(
									new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
											+ "Tworzę katalog na pliki html: " + ERROR_DIRECTORY + "..."));

							if (!new File(workingDirectory + File.separator + ERROR_DIRECTORY).mkdirs()) {
								logger.error("Nie można utworzyć katalogu na pliki html.");
								eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
										+ LogStatus.ERROR.getDesc() + "Nie można utworzyć katalogu na pliki html."));
							}
							logger.info("Katalog na pliki html utworzony.");
							eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
									+ LogStatus.INFO.getDesc() + "Katalog na pliki html utworzony."));
						}
						File destinationFile = new File(workingDirectory + File.separator + ERROR_DIRECTORY
								+ File.separator + fileName + ".html");
						logger.info("Zmieniam nazwę pliku: " + fileName + " -> " + fileName + ".html");
						eventService.createEvent(
								new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
										+ "Zmieniam nazwę pliku: " + fileName + " -> " + fileName + ".html"));

						destinationFile.delete();
						if (!sourceFile.renameTo(destinationFile)) {
							logger.error("Błąd: Nie można zmienić nazwy pliku: " + fileName);
							eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
									+ LogStatus.ERROR.getDesc() + "Błąd: Nie można zmienić nazwy pliku: " + fileName));
						}
					}
				} else {
					logger.error("Problem z pobraniem pliku. Kod odpowiedzi serwera https: " + responseCode);
					eventService
							.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc()
									+ "Problem z pobraniem pliku. Kod odpowiedzi serwera https: " + responseCode));
					reportService.addMessage(report, "-> Problem z pobraniem pliku: " + fileName);
				}
			}

		} catch (IOException ex) {
			logger.fatal("StackTrace: ", ex);
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Błąd: " + ex.getMessage()));
		} finally {
			this.closeSSLConnection(subjectAddress, logoutForm, fileExchangeStatus, report);
		}
		if (!status) {
			logger.error("Brak plików do pobrania.");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "Brak plików do pobrania."));
			reportService.addMessage(report, "-> Brak plików do pobrania");
		}
		return receiveFileList;
	}

	private boolean createSSLConnection(String serverAddress, String loginForm, String subjectLogin,
			String subjectPassword, FileExchangeStatus fileExchangeStatus, Report report) {

		String url = serverAddress;
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		try {
			logger.info("Inicjalizacja połączenia...");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Inicjalizacja połączenia..."));

			logger.info("Trwa łączenie do " + serverAddress);
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Trwa łączenie do " + serverAddress));

			if (loginForm != null)
				url = MTTools.getConvertUrlForm(serverAddress, subjectLogin, subjectPassword, loginForm);
			URL obj = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

			// default is GET
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);

			if (loginForm == null) {
				logger.info("Autoryzacja BasicAuth...");
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Autoryzacja BasicAuth..."));

				String userPass = subjectLogin + ":" + subjectPassword;
				String basicAuth = "Basic " + new String(Base64.encodeBase64(userPass.getBytes()));
				conn.setRequestProperty("Authorization", basicAuth);
			}
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				logger.info("Autoryzacja na serwerzez https poprawna. Kod odpowiedzi serwera: " + responseCode);
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
						+ "Autoryzacja na serwerzez https poprawna. Kod odpowiedzi serwera: " + responseCode));
			} else {
				logger.fatal("Błąd autoryzacji. Kod odpowiedzi serwera https: " + responseCode);
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.FATAL.getDesc()
						+ "Błąd autoryzacji. Kod odpowiedzi serwera https: " + responseCode));
				reportService.addMessage(report, "-> Błąd: Nie powiodła się autoryzacja na serwerze");
				return false;
			}
			return true;
		} catch (ConnectException ex) {
			logger.fatal("Błąd połączenia: " + ex.getMessage());
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Błąd połączenia: " + ex.getMessage()));
			reportService.addMessage(report, "-> Błąd: Nie moża połączyć się z serwerem");
			return false;
		} catch (IOException ex) {
			logger.fatal("StackTrace: ", ex);
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Błąd: " + ex.getMessage()));
			return false;
		}
	}

	private void closeSSLConnection(String serverAddress, String logoutForm, FileExchangeStatus fileExchangeStatus,
			Report report) {
		if (logoutForm != null) {
			try {
				URL obj = new URL(serverAddress + logoutForm);
				HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

				int responseCode = conn.getResponseCode();
				if (responseCode == 200) {
					logger.info("Wylogowanie z serwera poprawne. Kod odpowiedzi serwera: " + responseCode);
					eventService
							.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
									+ "Wylogowanie z serwera poprawne. Kod odpowiedzi serwera: " + responseCode));

				} else {
					logger.error("Błąd wylogowania z serwera. Kod odpowiedzi serwera https: " + responseCode);
					eventService
							.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc()
									+ "Błąd wylogowania z serwera. Kod odpowiedzi serwera https: " + responseCode));
				}
			} catch (IOException ex) {
				logger.fatal("StackTrace: ", ex);
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Błąd: " + ex.getMessage()));
			}
		}
	}

	@Override
	public boolean sendFiles(List<String> filesList, TaskConfig taskConfig, String workingDirectory,
			FileExchangeStatus fileExchangeStatus, Report report) {

		String lineEnd = "\r\n";
		String twoHyphens = "--";

		String subjectAddress = taskConfig.getSubjectAddress();
		String subjectDirectory = taskConfig.getSubjectDirectory();
		String loginForm = taskConfig.getSubjectLoginForm();
		String logoutForm = taskConfig.getSubjectLogoutForm();
		String subjectLogin = taskConfig.getSubjectLogin();
		String subjectPassword = taskConfig.getSubjectPassword();
		String[] postList = taskConfig.getSubjectPostOptions().split(",");
		String responseString = taskConfig.getSubjectResponseString();

		boolean isConnected = this.createSSLConnection(subjectAddress, loginForm, subjectLogin, subjectPassword,
				fileExchangeStatus, report);

		if (!isConnected)
			return false;

		logger.info("Wysyłam pliki na serwer Podmiotu (" + subjectAddress + "):");
		eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
				+ "Wysyłam pliki na serwer Podmiotu (" + subjectAddress + "):"));
		reportService.addMessage(report, "- Lista plików wysłanych na serwer Podmiotu (" + subjectAddress + "):");

		for (String fileName : filesList) {
			try {
				String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random
																				// value.
				URL obj = new URL(subjectDirectory + fileName);
				HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);

				conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
				logger.info("Przesyłam plik: " + fileName + "...");
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Przesyłam plik: " + fileName + "..."));

				File uploadFile = new File(workingDirectory + File.separator + fileName);
				FileInputStream inputStream = new FileInputStream(uploadFile);
				DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());

				// Start Header
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				for (String postOptions : postList) {
					if (!postOptions.contains("@")) {
						outputStream.writeBytes("Content-Disposition: form-data; name=\"" + postOptions.split("=", 2)[0]
								+ "\"" + lineEnd);
						outputStream.writeBytes(lineEnd);
						outputStream.writeBytes(postOptions.split("=", 2)[1]);
						outputStream.writeBytes(lineEnd);
						outputStream.writeBytes(twoHyphens + boundary + lineEnd);
					}
				}
				for (String postOptions : postList) {
					if (postOptions.contains("@")) {
						outputStream.writeBytes("Content-Disposition: form-data; name=\"" + postOptions.split("=", 2)[0]
								+ "\";filename=\"" + fileName + "\"" + lineEnd);
						outputStream.writeBytes(lineEnd);
					}
				}
				// End Header

				// create a buffer of maximum size
				int bytesAvailable = inputStream.available();

				int maxBufferSize = 1024;
				int bufferSize = Math.min(bytesAvailable, maxBufferSize);
				byte[] buffer = new byte[bufferSize];

				// read file and write it into form...
				int bytesRead = inputStream.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					outputStream.write(buffer, 0, bufferSize);
					bytesAvailable = inputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = inputStream.read(buffer, 0, bufferSize);
				}
				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// close streams
				inputStream.close();
				outputStream.flush();

				logger.info("Plik skopiowany.");
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik skopiowany."));

				int responseCode = conn.getResponseCode();
				logger.info("Kod odpowiedzi serwera https: " + responseCode);
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc()
						+ "Kod odpowiedzi serwera https: " + responseCode));

				if (responseCode == 200) {
					// Sprawdza czy plik odpowiedzi serwera zawiera responseString
					// jezeli jest to plik zostal poprawnie przeslany na serwer
					BufferedReader br = null;
					boolean found = false;
					try {
						InputStream is = conn.getInputStream();
						br = new BufferedReader(new InputStreamReader(is));
						String line;
						while ((line = br.readLine()) != null) {
							if (line.contains(responseString)) {
								found = true;
								break;
							}
						}
					} finally {
						try {
							if (br != null)
								br.close();
						} catch (Exception e) {
							logger.error("Problem podaczas zamykania BufferReader " + e.toString());
							eventService.createEvent(
									new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc()
											+ "Problem podaczas zamykania BufferReader " + e.toString()));
							;
						}
					}

					if (found) {
						logger.info("Plik przesłany na serwer.");
						eventService.createEvent(new Event(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik przesłany na serwer"));
						reportService.addMessage(report, fileName);
					} else {
						logger.error("Problem z przesłaniem pliku: " + fileName);
						eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
								+ LogStatus.ERROR.getDesc() + "Problem z przesłaniem pliku: " + fileName));
						reportService.addMessage(report, "-> Problem z przesłaniem pliku: " + fileName);
					}
				} else {
					logger.error("Problem z przesłaniem pliku. Kod odpowiedzi serwera https: " + responseCode);
					eventService
							.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc()
									+ "Problem z przesłaniem pliku. Kod odpowiedzi serwera https: " + responseCode));
					reportService.addMessage(report, "-> Problem z przesłaniem pliku: " + fileName);
				}
			} catch (FileNotFoundException ex) {
				logger.fatal("Błąd: " + ex.getMessage());
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Błąd: " + ex.getMessage()));
				reportService.addMessage(report, "-> Błąd: Nie istnieje plik: " + fileName);
			} catch (IOException ex) {
				logger.fatal("StackTrace: ", ex);
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Błąd: " + ex.getMessage()));
			}
		}
		this.closeSSLConnection(subjectAddress, logoutForm, fileExchangeStatus, report);
		return true;
	}
}
