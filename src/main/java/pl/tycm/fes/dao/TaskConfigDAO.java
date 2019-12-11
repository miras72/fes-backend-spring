package pl.tycm.fes.dao;

import java.util.List;

import pl.tycm.fes.exception.TaskConfigNotFoundException;
import pl.tycm.fes.model.TaskConfig;

public interface TaskConfigDAO {

	public TaskConfig getTaskConfig(long id) throws TaskConfigNotFoundException;
	
	public TaskConfig getTaskConfigCryteriaFetch(long id) throws TaskConfigNotFoundException;

	public void deleteTaskConfig(long id) throws TaskConfigNotFoundException;

	public TaskConfig createTaskConfig(TaskConfig taskConfig);

	public void updateTaskConfig(TaskConfig taskConfig) throws TaskConfigNotFoundException;

	public List<TaskConfig> getAllTaskConfig();
}
