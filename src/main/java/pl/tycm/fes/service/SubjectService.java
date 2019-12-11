package pl.tycm.fes.service;

import java.util.List;

import pl.tycm.fes.exception.DecryptionKeyNotFoundException;
import pl.tycm.fes.exception.EncryptionKeyNotFoundException;
import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.model.TaskConfig;

public interface SubjectService {

	public List<String> subjectReceiveFiles(TaskConfig taskConfig, FileExchangeStatus fileExchangeStatus, Report report)
			throws ServerConfigNotFoundException, EncryptionKeyNotFoundException;

	public boolean subjectSendFiles(List<String> fileList, TaskConfig taskConfig, FileExchangeStatus fileExchangeStatus,
			Report report) throws ServerConfigNotFoundException, EncryptionKeyNotFoundException;

	public List<String> decompressFiles(List<String> fileList, TaskConfig taskConfig,
			FileExchangeStatus fileExchangeStatus, Report report) throws ServerConfigNotFoundException;

	public List<String> decryptFiles(List<String> fileList, TaskConfig taskConfig,
			FileExchangeStatus fileExchangeStatus, Report report) throws DecryptionKeyNotFoundException, ServerConfigNotFoundException;

	public void subjectTestConnection(TaskConfig taskConfig, FileExchangeStatus fileExchangeStatus) throws EncryptionKeyNotFoundException;
}
