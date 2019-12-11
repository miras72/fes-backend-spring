package pl.tycm.fes.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.tycm.fes.exception.TaskConfigNotFoundException;
import pl.tycm.fes.model.TaskConfig;
import pl.tycm.fes.model.TaskStatus;

@Repository
public class TaskConfigDAOImpl implements TaskConfigDAO {

	@PersistenceContext	
	private EntityManager entityManager;
	
	@Override
	public TaskConfig getTaskConfig(long id) throws TaskConfigNotFoundException {
		TaskConfig taskConfig = entityManager.find(TaskConfig.class, id);
		if(taskConfig == null)
			throw new TaskConfigNotFoundException(id);

		return taskConfig;
	}
	
	@Transactional(readOnly = true)
	@Override
	public TaskConfig getTaskConfigCryteriaFetch(long id) throws TaskConfigNotFoundException {
		TaskConfig taskConfig = entityManager.find(TaskConfig.class, id);
		if(taskConfig == null)
			throw new TaskConfigNotFoundException(id);
		taskConfig.getServers().size();
		taskConfig.getFileList().size();
		taskConfig.getMailingList().size();
		return taskConfig;
	}

	@Transactional
	@Override
	public void deleteTaskConfig(long id) throws TaskConfigNotFoundException {
		TaskConfig taskConfig = entityManager.find(TaskConfig.class, id);
		if (taskConfig == null)
			throw new TaskConfigNotFoundException(id);
		
		entityManager.remove(taskConfig);
	}

	@Transactional
	@Override
	public TaskConfig createTaskConfig(TaskConfig taskConfig) {
		taskConfig.getFileList().forEach((fl) -> fl.setTaskConfig(taskConfig));
		taskConfig.getServers().forEach((ks) -> ks.setTaskConfig(taskConfig));
		taskConfig.getMailingList().forEach((ml) -> ml.setTaskConfig(taskConfig));
		
		TaskStatus taskStatus = new TaskStatus();
		taskStatus.setTaskConfig(taskConfig);
		
		entityManager.persist(taskConfig);
		entityManager.persist(taskStatus);
		
		return taskConfig;
	}

	@Transactional
	@Override
	public void updateTaskConfig(TaskConfig taskConfigNew) throws TaskConfigNotFoundException {
		TaskConfig taskConfig = entityManager.find(TaskConfig.class, taskConfigNew.getId());
		if (taskConfig == null)
			throw new TaskConfigNotFoundException(taskConfigNew.getId());
		
		taskConfigNew.getFileList().forEach((fl) -> fl.setTaskConfig(taskConfigNew));
		taskConfigNew.getServers().forEach((ks) -> ks.setTaskConfig(taskConfigNew));
		taskConfigNew.getMailingList().forEach((ml) -> ml.setTaskConfig(taskConfigNew));
		
		entityManager.merge(taskConfigNew);
		entityManager.flush();
	}

	@Override
	public List<TaskConfig> getAllTaskConfig() {
		String hql = "FROM TaskConfig as ts";
		return (List<TaskConfig>) entityManager.createQuery(hql, TaskConfig.class).getResultList();
	}
}
