package pl.tycm.fes.controller.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import pl.tycm.fes.exception.TaskStatusNotFoundException;
import pl.tycm.fes.model.TaskStatus;
import pl.tycm.fes.repository.TaskStatusRepository;

@Service
public class TaskStatusServiceImpl implements TaskStatusService{

	private final TaskStatusRepository taskStatusRepository;
	//private TaskStatusDAO taskStatusDAO;
	
	public TaskStatusServiceImpl(TaskStatusRepository taskStatusRepository) {
		this.taskStatusRepository = taskStatusRepository;
	}
	
	@Override
	public TaskStatus getTaskStatus(Long taskConfigId) throws TaskStatusNotFoundException {
		TaskStatus taskStatus = taskStatusRepository.findByTaskConfigId(taskConfigId);
		if (taskStatus==null) {
			throw new TaskStatusNotFoundException(taskConfigId);
		}
		return taskStatus;
	}

	/*@Override
	public TaskStatus deleteTaskStatus(Long id) {
		// TODO Auto-generated method stub
		return null;
	}*/

	@Override
	public TaskStatus createTaskStatus(TaskStatus taskStatus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public void updateTaskStatus(TaskStatus taskStatus) {
		taskStatusRepository.save(taskStatus);
	}

	@Override
	public List<TaskStatus> getAllTaskStatus() {
		
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "taskConfig.subjectName"));
		return taskStatusRepository.findAll(sort);
	}

}
