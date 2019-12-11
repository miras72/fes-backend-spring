package pl.tycm.fes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import pl.tycm.fes.model.TaskStatus;
import pl.tycm.fes.model.View;
import pl.tycm.fes.service.controller.TaskStatusService;

@RestController
@RequestMapping("/api")
public class TaskStatusController {

	@Autowired
	private TaskStatusService taskStatusService;

	@JsonView(View.TaskStatusView.class)
	@GetMapping("/tasks-status")
	@ResponseStatus(HttpStatus.OK)
	public List<TaskStatus> getAllTaskStatus(){

		return  taskStatusService.getAllTaskStatus();
	}
}
