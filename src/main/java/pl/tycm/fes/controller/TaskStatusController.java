package pl.tycm.fes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import pl.tycm.fes.controller.service.TaskStatusService;
import pl.tycm.fes.model.TaskStatus;
import pl.tycm.fes.model.View;

@RestController
@RequestMapping("/api")
public class TaskStatusController {

	private final TaskStatusService taskStatusService;
	
	public TaskStatusController(TaskStatusService taskStatusService) {
		this.taskStatusService = taskStatusService;
	}

	@JsonView(View.TaskStatusView.class)
	@GetMapping("/tasks-status")
	@ResponseStatus(HttpStatus.OK)
	public List<TaskStatus> getAllTaskStatus(){

		return  taskStatusService.getAllTaskStatus();
	}
}
