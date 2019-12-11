package pl.tycm.fes.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import pl.tycm.fes.exception.TaskStatusNotFoundException;
import pl.tycm.fes.model.TaskStatus;

@Repository
public class TaskStatusDAOImpl implements TaskStatusDAO{
	
	@PersistenceContext	
	private EntityManager entityManager;	
	
	@Override
	public TaskStatus getTaskStatus(Long id) throws TaskStatusNotFoundException {
		TaskStatus taskStatus = entityManager.find(TaskStatus.class, id);
		if (taskStatus == null)
			throw new TaskStatusNotFoundException(id);
		return taskStatus;
	}

	@Override
	public TaskStatus deleteTaskStatus(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskStatus createTaskStatus(TaskStatus taskStatus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateTaskStatus(TaskStatus taskStatus) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<TaskStatus> getAllTaskStatus() {
		String hql = "From TaskStatus as ts ORDER BY ts.taskConfig.subjectName";
		return (List<TaskStatus>) entityManager.createQuery(hql, TaskStatus.class).getResultList();
	}

}
