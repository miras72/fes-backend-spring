package pl.tycm.fes.controller.service;

import java.util.List;
import org.springframework.stereotype.Service;

import pl.tycm.fes.model.FileList;
import pl.tycm.fes.model.ManualFileExchange;
import pl.tycm.fes.service.AppService;

@Service
public class ManualFileExchangeServiceImpl implements ManualFileExchangeService {
	
	private final AppService appBean;
	
	public ManualFileExchangeServiceImpl(AppService appBean) {
		this.appBean = appBean;
	}

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
