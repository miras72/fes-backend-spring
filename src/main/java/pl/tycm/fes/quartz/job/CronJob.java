package pl.tycm.fes.quartz.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import pl.tycm.fes.controller.service.TaskStatusService;
import pl.tycm.fes.exception.TaskStatusNotFoundException;
import pl.tycm.fes.model.TaskStatus;
import pl.tycm.fes.service.AppService;

public class CronJob extends QuartzJobBean implements InterruptableJob {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	DateFormat eventDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");

	//private volatile boolean toStopFlag = true;

	private final AppService appService;

	private final TaskStatusService taskStatusService;


	public CronJob(AppService appService, TaskStatusService taskStatusService) {
		this.appService = appService;
		this.taskStatusService = taskStatusService;
	}

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		DateFormat nextScheduledDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

		// *********** For retrieving stored key-value pairs ***********/
		JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
		Long keyIdValue = dataMap.getLong("keyId");
		String keySubjectNameValue = dataMap.getString("keySubjectName");

		JobKey key = jobExecutionContext.getJobDetail().getKey();
		logger.info("Schedule occurred for Task: " + key.getName() + "(" + keySubjectNameValue + ")");
		Date nextScheduledDate = jobExecutionContext.getNextFireTime();
		logger.info("Next schedule will occurred: " + nextScheduledDate);

		TaskStatus taskStatus = new TaskStatus();

		try {
			taskStatus = taskStatusService.getTaskStatus(keyIdValue);
			
			taskStatus.setNextScheduledDate(nextScheduledDateFormat.format(nextScheduledDate));
			taskStatusService.updateTaskStatus(taskStatus);

			logger.info("Run Task: " + key.getName() + "(" + keySubjectNameValue + ")");
			String eventDateTime = eventDateFormat.format(new Date());
			String fileDate = new SimpleDateFormat("ddMMyyyy").format(new Date());
			appService.runApp(keyIdValue, eventDateTime, fileDate, null);
		} catch (TaskStatusNotFoundException e) {
			logger.error("StackTrace: ", e);
		}
		logger.info("Thread: " + Thread.currentThread().getName() + " completed.");
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		logger.info("Stopping thread... ");
		//toStopFlag = false;
	}

}
