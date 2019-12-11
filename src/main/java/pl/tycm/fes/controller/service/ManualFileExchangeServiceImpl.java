package pl.tycm.fes.service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.model.FileList;
import pl.tycm.fes.model.ManualFileExchange;
import pl.tycm.fes.service.AppService;

@Service
public class ManualFileExchangeServiceImpl implements ManualFileExchangeService {
	
	@Autowired
	private AppService appBean;
	
	@Override
	public void startManualFileExchange(ManualFileExchange manualFileExchange) {
		
		List<FileList> fileList = manualFileExchange.getFileList();
		
		appBean.runApp(manualFileExchange.getTaskID(), manualFileExchange.getEventDateTime(), manualFileExchange.getFileDate(), fileList);
	}

	@Override
	public void stopManualFileExchange(Long id) {
		appBean.setStop(true);
	}

}
