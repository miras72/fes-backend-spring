package pl.tycm.fes.service;

import java.util.List;

import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;

public interface PgpService {

	public List<String> decryptPGPFile(String workingDirectory, byte[] decryptionKey, List<String> receiveFileList,
			FileExchangeStatus fileExchangeStatus, Report report);

}
