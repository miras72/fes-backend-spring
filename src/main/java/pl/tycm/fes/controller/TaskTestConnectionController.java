package pl.tycm.fes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.tycm.fes.exception.TaskConfigNotFoundException;
import pl.tycm.fes.service.controller.TaskTestService;

@RestController
@RequestMapping("/api")
public class TaskTestConnectionController {

	@Autowired
	private TaskTestService taskTestService;

	@GetMapping("/tasks/test/{id}{eventDateTime}")
	@ResponseStatus(HttpStatus.OK)
	public void testTaskConnection(@PathVariable long id, @PathVariable String eventDateTime)
			throws TaskConfigNotFoundException {

		taskTestService.startTaskTest(id, eventDateTime);
	}
}
