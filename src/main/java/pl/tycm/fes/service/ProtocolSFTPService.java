package pl.tycm.fes.service;

import java.util.List;

import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.model.TaskConfig;

public interface ProtocolSFTPService {

	public List<String> receiveFiles(TaskConfig taskConfig, String workingDirectory, String privateKeyName,
			byte[] privateKey, FileExchangeStatus fileExchangeStatus, Report report);

	public boolean sendFiles(List<String>fileList, TaskConfig taskConfig, String workingDirectory, String privateKeyName,
			byte[] privateKey, FileExchangeStatus fileExchangeStatus, Report report);

	public void testConnection(String subjectAddress, String subjectLogin, String subjectPassword,
			String privateKeyName, byte[] privateKey, FileExchangeStatus fileExchangeStatus);

}
