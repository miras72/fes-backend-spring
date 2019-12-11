package pl.tycm.fes.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import pl.tycm.fes.LogStatus;
import pl.tycm.fes.controller.service.EventService;
import pl.tycm.fes.controller.service.FileExchangeStatusService;
import pl.tycm.fes.controller.service.ServerConfigService;
import pl.tycm.fes.controller.service.TaskConfigService;
import pl.tycm.fes.controller.service.TaskStatusService;
import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.Event;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.FileList;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.model.ServerConfig;
import pl.tycm.fes.model.TaskConfig;
import pl.tycm.fes.model.TaskStatus;
import pl.tycm.fes.util.MTTools;

public class AppService {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	private boolean stop = false;

	private DateFormat lastStatusDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	DateFormat eventDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");

	@Autowired
	private TaskConfigService taskConfigService;

	@Autowired
	private TaskStatusService taskStatusService;

	@Autowired
	private FileExchangeStatusService fileExchangeStatusService;

	@Autowired
	private EventService eventService;

	@Autowired
	private ServerConfigService serverConfigService;

	@Autowired
	private SubjectService subjectService;
	
	@Autowired
	private ServerService serverService;
	
	@Autowired
	private ArchiveService archiveService;

	@Autowired
	private ReportService reportService;
	
	@Autowired
	private MailService mailService;
	
	@Async
	public Future<Integer> runApp(Long id, String eventDateTime, String fileDate, List<FileList> newFileList) {

		boolean isSend = false;
		boolean isArchiveCreated = true;
		
		String reportMessage;
		
		try {
			TaskConfig taskConfig = taskConfigService.getTaskConfigCryteriaFetch(id);
			logger.info(String.format("Zadanie pobierania plików z %s uruchomione...", taskConfig.getSubjectName()));
						
			String dateFormat = taskConfig.getDateFormat();
			String subjectExchangeProtocol = taskConfig.getSubjectExchangeProtocol();
			
			List<String> receiveFileList  = new ArrayList<>();
			
			if(newFileList == null) {
				List<FileList> fileList = taskConfig.getFileList();
				List<FileList> convertFileList = MTTools.getConvertFileList(fileList, fileDate, dateFormat,
						subjectExchangeProtocol);
				taskConfig.setFileList(convertFileList);
			} else {
				List<FileList> convertFileList = MTTools.getConvertFileList(newFileList, fileDate, dateFormat,
						subjectExchangeProtocol);
				taskConfig.setFileList(convertFileList);
			}
			
			TaskStatus taskStatus = taskStatusService.getTaskStatus(id);

			taskStatus.setLastStatus("Running");
			String lastDataStatus = lastStatusDateFormat.format(new Date());
			taskStatus.setLastDataStatus(lastDataStatus);
			taskStatusService.updateTaskStatus(taskStatus);

			FileExchangeStatus fileExchangeStatus = new FileExchangeStatus();
			fileExchangeStatus.setTaskID(id);
			fileExchangeStatus.setEventDateTime(eventDateTime);
			FileExchangeStatus fileExchangeStatusCreated = fileExchangeStatusService
					.createFileExchangeStatus(fileExchangeStatus);

			eventService.createEvent(new Event(fileExchangeStatusCreated, "-------"));

			logger.info("Start logowania zdarzeń");
			eventService.createEvent(new Event(fileExchangeStatusCreated,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Start logowania zdarzeń"));

			logger.info(String.format("Wymiana danych z podmiotem %s...", taskConfig.getSubjectName()));
			eventService.createEvent(
					new Event(fileExchangeStatusCreated, MTTools.getLogDate() + LogStatus.INFO.getDesc()
							+ String.format("Wymiana danych z podmiotem %s...", taskConfig.getSubjectName())));

			ServerConfig serverConfig = serverConfigService.getServerConfig();
			String workingDirectory = serverConfig.getWorkDirectory();

			if (!Files.isDirectory(Paths.get(workingDirectory))) {
				logger.info("Tworzę katalog roboczy: " + workingDirectory);
				eventService.createEvent(new Event(fileExchangeStatusCreated, MTTools.getLogDate()
						+ LogStatus.INFO.getDesc() + "Tworzę katalog roboczy: " + workingDirectory + "..."));

				if (!new File(workingDirectory).mkdirs()) {
					logger.error("Nie można utworzyć katalogu roboczego.");
					eventService.createEvent(new Event(fileExchangeStatusCreated, MTTools.getLogDate()
							+ LogStatus.ERROR.getDesc() + "Nie można utworzyć katalogu roboczego."));

					taskStatus.setLastStatus("ERROR");
					lastDataStatus = lastStatusDateFormat.format(new Date());
					taskStatus.setLastDataStatus(lastDataStatus);
					taskStatusService.updateTaskStatus(taskStatus);
					return new AsyncResult<Integer>(2);
				}

				logger.info("Katalog utworzony.");
				eventService.createEvent(new Event(fileExchangeStatusCreated,
						MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Katalog utworzony."));
			}

			Report report = new Report();

			switch (taskConfig.getSubjectMode()) {
			case "download":
				logger.info("Tryb wymiany: download");
				eventService.createEvent(new Event(fileExchangeStatusCreated,
						MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Tryb wymiany: download"));
				
				if (stop) {
					logger.info("Zadanie zostało zatrzymane.");
					cancelTask(fileExchangeStatusCreated, taskStatus);
					return new AsyncResult<Integer>(2);
				}
				
				receiveFileList = subjectService.subjectReceiveFiles(taskConfig, fileExchangeStatusCreated, report);
				
				if (stop) {
					logger.info("Zadanie zostało zatrzymane.");
					cancelTask(fileExchangeStatusCreated, taskStatus);
					deleteFiles(receiveFileList, fileExchangeStatus);
					return new AsyncResult<Integer>(2);
				}
				
				if (taskConfig.getDecompressionMethod() != null && !receiveFileList.isEmpty())
					receiveFileList = subjectService.decompressFiles(receiveFileList, taskConfig, fileExchangeStatusCreated, report);
				
				if (stop) {
					logger.info("Zadanie zostało zatrzymane.");
					cancelTask(fileExchangeStatusCreated, taskStatus);
					return new AsyncResult<Integer>(2);
				}
				
				if (taskConfig.getDecryptionMethod() != null && !receiveFileList.isEmpty())
					receiveFileList = subjectService.decryptFiles(receiveFileList, taskConfig, fileExchangeStatusCreated, report);
				
				if (stop) {
					logger.info("Zadanie zostało zatrzymane.");
					cancelTask(fileExchangeStatusCreated, taskStatus);
					return new AsyncResult<Integer>(2);
				}
				
				if (!receiveFileList.isEmpty()) {
					isSend = serverService.sendFiles(taskConfig.getServers(), workingDirectory, receiveFileList, fileExchangeStatusCreated, report);
				}
				
				if (stop) {
					logger.info("Zadanie zostało zatrzymane.");
					cancelTask(fileExchangeStatusCreated, taskStatus);
					return new AsyncResult<Integer>(2);
				}
				
				if (!receiveFileList.isEmpty() && taskConfig.isFileArchive()) {
					String archiveDirectory = serverConfig.getArchDirectory();
					if (!archiveDirectory.endsWith("/") || !archiveDirectory.endsWith("\\")) {
						archiveDirectory = archiveDirectory + File.separator + taskConfig.getSubjectName() + "."
								+ taskConfig.getSubjectMode();
					} else {
						archiveDirectory = archiveDirectory + taskConfig.getSubjectName() + "."
								+ taskConfig.getSubjectMode();
					}
					isArchiveCreated = archiveService.archiveFiles(workingDirectory, archiveDirectory, receiveFileList, fileExchangeStatusCreated, report);
				}
				
				if (!receiveFileList.isEmpty() && isArchiveCreated && isSend) {
					taskStatus.setLastStatus("OK");
					lastDataStatus = lastStatusDateFormat.format(new Date());
					taskStatus.setLastDataStatus(lastDataStatus);
					taskStatusService.updateTaskStatus(taskStatus);
				} else {
					taskStatus.setLastStatus("Error");
					lastDataStatus = lastStatusDateFormat.format(new Date());
					taskStatus.setLastDataStatus(lastDataStatus);
					taskStatusService.updateTaskStatus(taskStatus);
				}
				if (!receiveFileList.isEmpty())
					deleteFiles(receiveFileList, fileExchangeStatus);

				reportMessage = reportService.getReport(report);
				
				mailService.sendMail(taskConfig.getMailSubject(), taskConfig.getMailFrom(), taskConfig.getMailingList(), reportMessage);

				logger.info("Koniec logowania zdarzeń");
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Koniec logowania zdarzeń."));
				break;
				
			case "upload":
				logger.info("Tryb wymiany: upload");
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Tryb wymiany: upload"));

				if (stop) {
					logger.info("Zadanie zostało zatrzymane.");
					cancelTask(fileExchangeStatusCreated, taskStatus);
					return new AsyncResult<Integer>(2);
				}
				
				List<String> fileList = new ArrayList<>();
				for (FileList file : taskConfig.getFileList()) {
					fileList.add(file.getFileName());
				}
				
				receiveFileList = serverService.receiveFiles(taskConfig.getServers(), workingDirectory, fileList, fileExchangeStatusCreated, report);
				
				if (stop) {
					logger.info("Zadanie zostało zatrzymane.");
					cancelTask(fileExchangeStatusCreated, taskStatus);
					return new AsyncResult<Integer>(2);
				}
				
				if (!receiveFileList.isEmpty())
					isSend = subjectService.subjectSendFiles(receiveFileList, taskConfig, fileExchangeStatusCreated, report);
				
				if (!receiveFileList.isEmpty() && isSend) {
					taskStatus.setLastStatus("OK");
					lastDataStatus = lastStatusDateFormat.format(new Date());
					taskStatus.setLastDataStatus(lastDataStatus);
					taskStatusService.updateTaskStatus(taskStatus);
				} else {
					taskStatus.setLastStatus("Error");
					lastDataStatus = lastStatusDateFormat.format(new Date());
					taskStatus.setLastDataStatus(lastDataStatus);
					taskStatusService.updateTaskStatus(taskStatus);
				}
				if (!receiveFileList.isEmpty())
					deleteFiles(receiveFileList, fileExchangeStatus);

				reportMessage = reportService.getReport(report);
				
				mailService.sendMail(taskConfig.getMailSubject(), taskConfig.getMailFrom(), taskConfig.getMailingList(), reportMessage);

				logger.info("Koniec logowania zdarzeń");
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Koniec logowania zdarzeń."));
				break;
			}

		} catch (Exception e) {
			logger.error("StackTrace: ", e);
		}

		return new AsyncResult<>(1);
	}
	
	public void setStop(boolean stop) {
        this.stop = stop;
    }
	
	private void deleteFiles(List<String> receiveFileList, FileExchangeStatus fileExchangeStatus)
			throws ServerConfigNotFoundException {

		ServerConfig serverConfig = serverConfigService.getServerConfig();
		String workingDirectory = serverConfig.getWorkDirectory();

		if (receiveFileList != null) {
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Kasuje pliki w katalogu roboczy..."));
			for (String fileName : receiveFileList) {

				File file = new File(workingDirectory + File.separator + fileName);
				logger.info("Kasuje w katalogu roboczym plik: " + fileName + "...");

				if (file.delete()) {
					logger.info("Plik skasowany.");
				} else {
					logger.error("Błąd: Nie można skasować pliku: " + fileName);
					eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate()
							+ LogStatus.ERROR.getDesc() + "Błąd: Nie można skasować pliku: " + fileName));
				}
			}
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Pliki w katalogu roboczy skasowane."));
		}
	}

	private void cancelTask(FileExchangeStatus fileExchangeStatus, TaskStatus taskStatus) {
		logger.info("Zadanie zostało przerwane");
		eventService.createEvent(new Event(fileExchangeStatus,
				MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "Zadanie zostało przerwane."));

		taskStatus.setLastStatus("OK");
		String lastDataStatus = lastStatusDateFormat.format(new Date());
		taskStatus.setLastDataStatus(lastDataStatus);
		taskStatusService.updateTaskStatus(taskStatus);
	}
	
	@Async
	public void startTaskStatus(Long id, String eventDateTime) {
		
		try {
			TaskConfig taskConfig = taskConfigService.getTaskConfig(id);
			
			FileExchangeStatus fileExchangeStatus = new FileExchangeStatus();
			fileExchangeStatus.setTaskID(id);
			fileExchangeStatus.setEventDateTime(eventDateTime);
			FileExchangeStatus fileExchangeStatusCreated = fileExchangeStatusService
					.createFileExchangeStatus(fileExchangeStatus);

			eventService.createEvent(new Event(fileExchangeStatusCreated, "-------"));

			logger.info("Start logowania zdarzeń");
			eventService.createEvent(new Event(fileExchangeStatusCreated,
					MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Start logowania zdarzeń"));

			logger.info(String.format("Test połączenia z podmiotem %s...", taskConfig.getSubjectName()));
			eventService.createEvent(
					new Event(fileExchangeStatusCreated, MTTools.getLogDate() + LogStatus.INFO.getDesc()
							+ String.format("Test połączenia z podmiotem %s...", taskConfig.getSubjectName())));
			
			subjectService.subjectTestConnection(taskConfig, fileExchangeStatusCreated);
			
		} catch (Exception e) {
			logger.error("StackTrace: ", e);;
		}
	}
}
