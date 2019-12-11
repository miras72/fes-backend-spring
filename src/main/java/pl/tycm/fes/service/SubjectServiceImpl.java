package pl.tycm.fes.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.LogStatus;
import pl.tycm.fes.exception.DecryptionKeyNotFoundException;
import pl.tycm.fes.exception.EncryptionKeyNotFoundException;
import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.DecryptionKey;
import pl.tycm.fes.model.Event;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.PrivateKeyDTO;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.model.ServerConfig;
import pl.tycm.fes.model.TaskConfig;
import pl.tycm.fes.service.controller.DecryptionKeyService;
import pl.tycm.fes.service.controller.EncryptionKeyService;
import pl.tycm.fes.service.controller.EventService;
import pl.tycm.fes.service.controller.ServerConfigService;
import pl.tycm.fes.util.MTTools;

@Service
public class SubjectServiceImpl implements SubjectService {

	private final Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private ServerConfigService serverConfigService;

	@Autowired
	private EncryptionKeyService encryptionKeyService;

	@Autowired
	private EventService eventService;

	@Autowired
	private ProtocolSFTPService protocolSFTPService;
	
	@Autowired
	private ProtocolSSLService protocolSSLService;

	@Autowired
	private ProtocolFTPService protocolFTPService;
	
	@Autowired
	private GzipService gzipService;
	
	@Autowired
	private ZipService zipService;
	
	@Autowired
	private DecryptionKeyService decryptionKeyService;
	
	@Autowired
	private PgpService pgpService;
	
	@Override
	public List<String> subjectReceiveFiles(TaskConfig taskConfig, FileExchangeStatus fileExchangeStatus, Report report)
			throws ServerConfigNotFoundException, EncryptionKeyNotFoundException {

		List<String> receiveFileList = new ArrayList<>();
		
		ServerConfig serverConfig = serverConfigService.getServerConfig();
		String workingDirectory = serverConfig.getWorkDirectory();

		switch (taskConfig.getSubjectExchangeProtocol()) {
		case "sftp":
			String privateKeyName = null;
			byte[] privateKey = null;

			if (taskConfig.getSubjectEncryptionKeyID() != 0) {
				PrivateKeyDTO privateKeyDTO = encryptionKeyService
						.getPrivateKey(taskConfig.getSubjectEncryptionKeyID());
				privateKeyName = privateKeyDTO.getPrivateKeyName();
				privateKey = privateKeyDTO.getPrivateKeyBinaryFile();
			}
			receiveFileList = protocolSFTPService.receiveFiles(taskConfig, workingDirectory, privateKeyName, privateKey, fileExchangeStatus, report);
			return receiveFileList;
		case "ssl":
			receiveFileList =  protocolSSLService.receiveFiles(taskConfig, workingDirectory, fileExchangeStatus, report);
			return receiveFileList;
		case "ftp":
			receiveFileList = protocolFTPService.receiveFiles(taskConfig, workingDirectory, fileExchangeStatus, report);
			return receiveFileList;
		default:
			logger.fatal("Nieprawidłowy protokół wymiany plików");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Nieprawidłowy protokół wymiany plików."));

			return receiveFileList;
		}
	}

	@Override
	public boolean subjectSendFiles(List<String> fileList, TaskConfig taskConfig, FileExchangeStatus fileExchangeStatus,
			Report report) throws ServerConfigNotFoundException, EncryptionKeyNotFoundException {

		ServerConfig serverConfig = serverConfigService.getServerConfig();
		String workingDirectory = serverConfig.getWorkDirectory();

		switch (taskConfig.getSubjectExchangeProtocol()) {
		case "sftp":
			String privateKeyName = null;
			byte[] privateKey = null;
			
			if (taskConfig.getSubjectEncryptionKeyID() != 0) {
				PrivateKeyDTO privateKeyDTO = encryptionKeyService
						.getPrivateKey(taskConfig.getSubjectEncryptionKeyID());
				privateKeyName = privateKeyDTO.getPrivateKeyName();
				privateKey = privateKeyDTO.getPrivateKeyBinaryFile();
			}
			return protocolSFTPService.sendFiles(fileList, taskConfig, workingDirectory, privateKeyName, privateKey, fileExchangeStatus, report);
		case "ssl":
			return protocolSSLService.sendFiles(fileList, taskConfig, workingDirectory, fileExchangeStatus, report);
		default:
			logger.fatal("Nieprawidłowy protokół wymiany plików");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Nieprawidłowy protokół wymiany plików."));
			return false;
		}
	}

	@Override
	public List<String> decompressFiles(List<String> fileList, TaskConfig taskConfig,
			FileExchangeStatus fileExchangeStatus, Report report) throws ServerConfigNotFoundException {
		
		List<String> receiveFileList = new ArrayList<>();

		ServerConfig serverConfig = serverConfigService.getServerConfig();
		String workingDirectory = serverConfig.getWorkDirectory();

		switch (taskConfig.getDecompressionMethod()) {
		case "gzip":
			receiveFileList = gzipService.decompressGzipFile(workingDirectory, fileList, fileExchangeStatus, report);
			return receiveFileList;
		case "zip":
			receiveFileList = zipService.decompressZipFile(workingDirectory, fileList, fileExchangeStatus, report);
			return receiveFileList;
		default:
			logger.fatal("Nieprawidłowa metoda dekompresji");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Nieprawidłowa metoda dekompresji"));
			return receiveFileList;
		}
	}

	@Override
	public List<String> decryptFiles(List<String> fileList, TaskConfig taskConfig,
			FileExchangeStatus fileExchangeStatus, Report report) throws DecryptionKeyNotFoundException, ServerConfigNotFoundException {

		List<String> receiveFileList = new ArrayList<>();

		ServerConfig serverConfig = serverConfigService.getServerConfig();
		String workingDirectory = serverConfig.getWorkDirectory();

		switch (taskConfig.getDecryptionMethod()) {
		case "pgp":
			DecryptionKey decryptionKey = decryptionKeyService.getDecryptionKey(taskConfig.getDecryptionKeyID());
			byte[] keyFile = decryptionKey.getDecryptionKeyBinaryFile();
			if (keyFile == null)
				return receiveFileList;
			receiveFileList = pgpService.decryptPGPFile(workingDirectory, keyFile, fileList, fileExchangeStatus, report);
			return receiveFileList;
		default:
			logger.fatal("Nieprawidłowa metoda dekrypcji");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Nieprawidłowa metoda dekrypcji"));
			return receiveFileList;
		}
	}

	@Override
	public void subjectTestConnection(TaskConfig taskConfig, FileExchangeStatus fileExchangeStatus)
			throws EncryptionKeyNotFoundException {
		String subjectAddress = taskConfig.getSubjectAddress();
		String subjectLogin = taskConfig.getSubjectLogin();
		String subjectPassword = taskConfig.getSubjectPassword();

		switch (taskConfig.getSubjectExchangeProtocol()) {
		case "sftp":
			String privateKeyName = null;
			byte[] privateKey = null;
			if (taskConfig.getSubjectEncryptionKeyID() != 0) {
				PrivateKeyDTO privateKeyDTO = encryptionKeyService
						.getPrivateKey(taskConfig.getSubjectEncryptionKeyID());
				privateKeyName = privateKeyDTO.getPrivateKeyName();
				privateKey = privateKeyDTO.getPrivateKeyBinaryFile();
			}
			protocolSFTPService.testConnection(subjectAddress, subjectLogin, subjectPassword, privateKeyName,
					privateKey, fileExchangeStatus);
		case "ssl":

		case "ftp":

		default:
			logger.fatal("Nieprawidłowy protokół wymiany plików");
			eventService.createEvent(new Event(fileExchangeStatus,
					MTTools.getLogDate() + LogStatus.FATAL.getDesc() + "Nieprawidłowy protokół wymiany plików."));
		}
	}

}
