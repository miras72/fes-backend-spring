package pl.tycm.fes.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonView;

import pl.tycm.fes.controller.service.TaskConfigService;
import pl.tycm.fes.exception.TaskConfigNotFoundException;
import pl.tycm.fes.model.TaskConfig;
import pl.tycm.fes.model.View;

@RestController
@RequestMapping("/api")
public class TaskConfigController {

	private final TaskConfigService taskConfigService;
	
	public TaskConfigController(TaskConfigService taskConfigService) {
		this.taskConfigService = taskConfigService;
	}

	@JsonView(View.TaskConfigView.class)
	@GetMapping("/tasks/{id}")
	@ResponseStatus(HttpStatus.OK)
	public TaskConfig getTaskConfgi(@PathVariable long id) throws TaskConfigNotFoundException {

		return taskConfigService.getTaskConfig(id);
	}

	@JsonView(View.TaskConfigView.class)
	@PostMapping("/tasks")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TaskConfig> addTaskConfig(@RequestBody TaskConfig taskConfig, UriComponentsBuilder ucb) {

		TaskConfig taskConfigCreated = taskConfigService.createTaskConfig(taskConfig);

		HttpHeaders headers = new HttpHeaders();
		URI locationUri = ucb.path("/api/task/").path(String.valueOf(taskConfigCreated.getId())).build().toUri();
		headers.setLocation(locationUri);
		return new ResponseEntity<TaskConfig>(taskConfigCreated, headers, HttpStatus.CREATED);
	}

	@PutMapping("/tasks/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateTaskConfig(@PathVariable long id, @RequestBody TaskConfig taskConfig)
			throws TaskConfigNotFoundException {

		taskConfigService.updateTaskConfig(taskConfig);
	}

	@DeleteMapping("/tasks/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public void deleteTaskConfig(@PathVariable long id) throws TaskConfigNotFoundException {

		taskConfigService.deleteTaskConfig(id);
	}
}
