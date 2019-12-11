package pl.tycm.fes.controller.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.dao.TaskConfigDAO;
import pl.tycm.fes.exception.TaskConfigNotFoundException;
import pl.tycm.fes.exception.TaskStatusNotFoundException;
import pl.tycm.fes.model.TaskConfig;
import pl.tycm.fes.model.TaskStatus;
import pl.tycm.fes.quartz.job.CronJob;
import pl.tycm.fes.quartz.service.JobService;
import pl.tycm.fes.util.MTTools;

@Service
public class TaskConfigServiceImpl implements TaskConfigService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	DateFormat nextScheduledDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	@Autowired
	private TaskConfigDAO taskConfigDAO;

	@Autowired
	JobService jobService;

	@Autowired
	private TaskStatusService taskStatusService;

	@Override
	public TaskConfig getTaskConfig(long id) throws TaskConfigNotFoundException {
		return taskConfigDAO.getTaskConfig(id);
	}

	@Override
	public TaskConfig getTaskConfigCryteriaFetch(long id) throws TaskConfigNotFoundException {
		return taskConfigDAO.getTaskConfigCryteriaFetch(id);
	}

	@Override
	public void deleteTaskConfig(long id) throws TaskConfigNotFoundException {
		taskConfigDAO.deleteTaskConfig(id);
		if (jobService.isJobWithNamePresent(Long.toString(id))) {
			jobService.deleteJob(Long.toString(id));
		}
	}

	@Override
	public TaskConfig createTaskConfig(TaskConfig taskConfig) {
		return taskConfigDAO.createTaskConfig(taskConfig);
	}

	@Override
	public void updateTaskConfig(TaskConfig taskConfig) throws TaskConfigNotFoundException {

		taskConfigDAO.updateTaskConfig(taskConfig);

		TaskStatus taskStatus = new TaskStatus();

		try {
			taskStatus = taskStatusService.getTaskStatus(taskConfig.getId());
		} catch (TaskStatusNotFoundException e) {
			logger.error("StackTrace: ", e);
		}

		if (taskConfig.isScheduledIsActive()) {
			String cronExpSeconds = "0" + " ";
			String cronExpMinutes = taskConfig.getMinutes() + " ";
			String cronExpHours = taskConfig.getHours() + " ";
			String cronExpDayOfMonth = "?" + " ";
			String cronExpMonth = MTTools.monthScheduleExpression(taskConfig) + " ";
			String cronExpDayOfWeek = MTTools.dayOfWeekScheduleExpression(taskConfig) + " ";
			String cronExpYear = "*";
			String cronExpression = cronExpSeconds + cronExpMinutes + cronExpHours + cronExpDayOfMonth + cronExpMonth
					+ cronExpDayOfWeek + cronExpYear;

			if (jobService.isJobWithNamePresent(taskConfig.getId().toString())) {
				jobService.updateCronJob(taskConfig.getId().toString(), taskConfig.getSubjectName(), new Date(),
						cronExpression);
				if (taskStatus != null) {
					Map<String, Object> jobDetail = jobService.getJob(taskConfig.getId().toString());

					taskStatus.setNextScheduledDate(nextScheduledDateFormat.format(jobDetail.get("nextFireTime")));
					taskStatusService.updateTaskStatus(taskStatus);
				}
			} else {
				jobService.scheduleCronJob(taskConfig.getId().toString(), taskConfig.getSubjectName(),
						taskConfig.getId(), CronJob.class, new Date(), cronExpression);
				if (taskStatus != null) {
					Map<String, Object> jobDetail = jobService.getJob(taskConfig.getId().toString());
					taskStatus.setNextScheduledDate(nextScheduledDateFormat.format(jobDetail.get("nextFireTime")));
					taskStatusService.updateTaskStatus(taskStatus);
				}
			}
		} else if (jobService.isJobWithNamePresent(taskConfig.getId().toString())) {
			jobService.deleteJob(taskConfig.getId().toString());
			if (taskStatus != null) {
				taskStatus.setNextScheduledDate("");
				taskStatusService.updateTaskStatus(taskStatus);
			}
		}
	}

	@Override
	public List<TaskConfig> getAllTaskConfig() {
		return taskConfigDAO.getAllTaskConfig();
	}
}
