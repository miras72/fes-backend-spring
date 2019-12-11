package pl.tycm.fes.service;

import java.util.List;

import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;


public interface GzipService {

	public List<String> decompressGzipFile(String workingDirectory, List<String> receiveFileList, FileExchangeStatus fileExchangeStatus, Report report);
}
