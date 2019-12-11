package pl.tycm.fes.service;

import java.util.List;

import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.model.TaskConfig;

public interface ProtocolSSLService {

	public List<String> receiveFiles(TaskConfig taskConfig, String workingDirectory,
			FileExchangeStatus fileExchangeStatus, Report report);

	public boolean sendFiles(List<String> filesList, TaskConfig taskConfig, String workingDirectory,
			FileExchangeStatus fileExchangeStatus, Report report);
}
