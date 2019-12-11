package pl.tycm.fes.dao;

import java.util.List;

import pl.tycm.fes.exception.TaskStatusNotFoundException;
import pl.tycm.fes.model.TaskStatus;

public interface TaskStatusDAO {

	public TaskStatus getTaskStatus(Long id) throws TaskStatusNotFoundException;

	public TaskStatus deleteTaskStatus(long id);

	public TaskStatus createTaskStatus(TaskStatus taskStatus);

	public boolean updateTaskStatus(TaskStatus taskStatus);

	public List<TaskStatus> getAllTaskStatus();
}
