package pl.tycm.fes.controller.service;

import java.util.List;

import pl.tycm.fes.exception.TaskStatusNotFoundException;
import pl.tycm.fes.model.TaskStatus;

public interface TaskStatusService {

	public TaskStatus getTaskStatus(Long taskConfigId) throws TaskStatusNotFoundException;

	//public TaskStatus deleteTaskStatus(Long id);

	public TaskStatus createTaskStatus(TaskStatus taskStatus);

	public void updateTaskStatus(TaskStatus taskStatus);

	public List<TaskStatus> getAllTaskStatus();
}
