package pl.tycm.fes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.model.LocalFileList;
import pl.tycm.fes.repository.LocalFileListRepository;

@Service
public class LocalFileListServiceImpl implements LocalFileListService {

	@Autowired
	private LocalFileListRepository  localFileListRepository;
	
	@Override
	public LocalFileList createLocalFileList(LocalFileList localFileList) {
		return localFileListRepository.save(localFileList);
	}

	@Override
	public List<LocalFileList> getAllLocalFileList(Long taskConfigId) {
		return localFileListRepository.findByTaskConfigId(taskConfigId);
	}

}
