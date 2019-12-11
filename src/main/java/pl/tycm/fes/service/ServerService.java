package pl.tycm.fes.service;

import java.util.List;

import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Server;
import pl.tycm.fes.model.Report;

public interface ServerService {

	public boolean sendFiles(List<Server> servers, String workingDirectory, List<String> receiveFileList,
			FileExchangeStatus fileExchangeStatus, Report report);

	public List<String> receiveFiles(List<Server> servers, String workingDirectory, List<String> fileList,
			FileExchangeStatus fileExchangeStatus, Report report);
}
