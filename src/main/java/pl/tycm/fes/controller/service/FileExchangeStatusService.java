package pl.tycm.fes.controller.service;

import java.util.List;

import pl.tycm.fes.model.FileExchangeStatus;

public interface FileExchangeStatusService {

	public List<FileExchangeStatus> getFileExchangeStatus(Long id, String eventDateTimes);
	
	public FileExchangeStatus createFileExchangeStatus(FileExchangeStatus fileExchangeStatus);
}
