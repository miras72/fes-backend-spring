package pl.tycm.fes;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import pl.tycm.fes.dao.TaskConfigDAO;
import pl.tycm.fes.model.TaskConfig;
import pl.tycm.fes.quartz.job.CronJob;
import pl.tycm.fes.quartz.service.JobService;
import pl.tycm.fes.util.MTTools;

@Component
public class AppStartupRunner implements ApplicationRunner {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	private final TaskConfigDAO taskConfigDAO;
	private final JobService jobService;
	
	public AppStartupRunner(TaskConfigDAO taskConfigDAO, JobService jobService) {
		this.taskConfigDAO = taskConfigDAO;
		this.jobService = jobService;
	}


	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		
		logger.info("Application initialization...");
		
		List<TaskConfig> tasksConfig = taskConfigDAO.getAllTaskConfig();
		
		if (!tasksConfig.isEmpty()) {
			logger.info("Create schedule Jobs...");
			for (TaskConfig taskConfig : tasksConfig) {
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

					jobService.scheduleCronJob(taskConfig.getId().toString(), taskConfig.getSubjectName(), taskConfig.getId(), CronJob.class, new Date(), cronExpression);
				}
			}
			logger.info("Schedule Jobs created.");
		}
		
		logger.info("Application initialization done.");
	}
}
