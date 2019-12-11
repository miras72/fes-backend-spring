package pl.tycm.fes.service;

import java.util.List;

import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;

public interface ArchiveService {

	public boolean archiveFiles(String workingDirectory, String archiveDirectory, List<String> receiveFileList,
			FileExchangeStatus fileExchangeStatus, Report report);
}
