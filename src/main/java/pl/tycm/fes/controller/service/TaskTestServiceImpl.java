package pl.tycm.fes.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.service.AppService;

@Service
public class TaskTestServiceImpl implements TaskTestService {

	@Autowired
	private AppService appBean;
	
	@Override
	public void startTaskTest(Long id, String eventDateTime) {

		appBean.startTaskStatus(id, eventDateTime);
	}
}
