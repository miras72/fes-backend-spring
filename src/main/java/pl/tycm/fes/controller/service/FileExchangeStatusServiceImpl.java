package pl.tycm.fes.controller.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.repository.FileExchangeStatusRepository;

@Service
public class FileExchangeStatusServiceImpl implements FileExchangeStatusService {

	@Autowired
	private FileExchangeStatusRepository fileExchangeStatusRepository;

	@Override
	public FileExchangeStatus createFileExchangeStatus(FileExchangeStatus fileExchangeStatus) {
		return fileExchangeStatusRepository.save(fileExchangeStatus);
	}

	@Override
	public List<FileExchangeStatus> getFileExchangeStatus(Long id, String eventDateTime) {
		return fileExchangeStatusRepository.findByTaskIDAndEventDateTimeContainingOrderById(id, eventDateTime);
	}
}
