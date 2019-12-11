package pl.tycm.fes.service;

import java.util.List;

import pl.tycm.fes.model.LocalFileList;

public interface LocalFileListService {

	public LocalFileList createLocalFileList(LocalFileList localFileList);
	
	public List<LocalFileList> getAllLocalFileList(Long taskConfigId);
	
}
