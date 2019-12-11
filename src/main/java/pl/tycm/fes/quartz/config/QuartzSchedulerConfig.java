package pl.tycm.fes.quartz.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import pl.tycm.fes.quartz.service.JobsListener;
import pl.tycm.fes.quartz.service.TriggerListner;
import pl.tycm.fes.quartz.spring.AutowiringSpringBeanJobFactory;

@Configuration
public class QuartzSchedulerConfig {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
    private TriggerListner triggerListner;

    @Autowired
    private JobsListener jobsListener;

	@Bean
	public SpringBeanJobFactory springBeanJobFactory() {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		logger.debug("Configuring Job factory");

		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}
	
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {

		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
		schedulerFactory.setOverwriteExistingJobs(true);
		schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
		
		//Register listeners to get notification on Trigger misfire etc
		schedulerFactory.setGlobalTriggerListeners(triggerListner);
		schedulerFactory.setGlobalJobListeners(jobsListener);

		logger.debug("Setting the Scheduler up");
		schedulerFactory.setJobFactory(springBeanJobFactory());

		return schedulerFactory;
	}
}
