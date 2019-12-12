package pl.tycm.fes.controller.service;

import org.springframework.stereotype.Service;

import pl.tycm.fes.service.AppService;

@Service
public class TaskTestServiceImpl implements TaskTestService {

	private final AppService appBean;
	
	public TaskTestServiceImpl(AppService appBean) {
		this.appBean = appBean;
	}

	@Override
	public void startTaskTest(Long id, String eventDateTime) {
		appBean.startTaskStatus(id, eventDateTime);
	}
}
