package demo.archiving.config;

import lombok.Setter;
import org.quartz.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import demo.archiving.job.ArchiveQuartzJob;
import demo.archiving.repository.trk.CmTrkRepository;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfiguration {
    @Setter(onMethod = @__(@Autowired))
    private JobLauncher jobLauncher;

    @Setter(onMethod = @__(@Autowired))
    private JobLocator jobLocator;

    @Setter(onMethod = @__(@Autowired))
    private JobRegistry jobRegistry;
    @Setter(onMethod = @__(@Autowired))
    private CmTrkRepository cmTrkRepo;
    @Value("${daily.batchsize}")
    private Integer DAILY_BATCH_SIZE;
    @Value("${nightly.batchsize}")
    private Integer NIGHTLY_BATCH_SIZE;

    @Value("${daily.interval}")
    private Integer DAILY_INTERVAL_IN_SECOND;
    @Value("${nightly.interval}")
    private Integer NIGHTLY_INTERVAL_IN_SECOND;

    @Value("${archive.timewindow}")
    private int SCAN_TIME_RANGE_IN_MINUTES ;


    @Bean
    public JobDetail jobDetail() {
        //Set Job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "archiveJobInfo");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        jobDataMap.put("cmTrkRepo", cmTrkRepo);
        jobDataMap.put("scanTimeRange", SCAN_TIME_RANGE_IN_MINUTES);
        jobDataMap.put("dailyBatchSize", DAILY_BATCH_SIZE);
        jobDataMap.put("nightlyBatchSize", NIGHTLY_BATCH_SIZE);

        return JobBuilder.newJob(ArchiveQuartzJob.class)
                .withIdentity("archiveJobInfo")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }
    @Bean
    public Trigger dailyJobTrigger() {
        DailyTimeIntervalScheduleBuilder dailyScheduler = DailyTimeIntervalScheduleBuilder
                .dailyTimeIntervalSchedule()
                .startingDailyAt(new TimeOfDay(07,30,00))
                .endingDailyAt(new TimeOfDay(22,00,00))
                .withIntervalInSeconds(DAILY_INTERVAL_IN_SECOND);
        return TriggerBuilder
                .newTrigger()
                .forJob(jobDetail())
                .withIdentity("archiveTrigger1")
                .withSchedule(dailyScheduler)
                .build();
    }
    @Bean
    public Trigger nightlyJobTrigger1() {
        DailyTimeIntervalScheduleBuilder nightlyScheduler = DailyTimeIntervalScheduleBuilder
                .dailyTimeIntervalSchedule()
                .startingDailyAt(new TimeOfDay(22,00,00))
                .endingDailyAt(new TimeOfDay(23,59,59))
                .withIntervalInSeconds(NIGHTLY_INTERVAL_IN_SECOND);
        return TriggerBuilder
                .newTrigger()
                .forJob(jobDetail())
                .withIdentity("archiveTrigger2")
                .withSchedule(nightlyScheduler)
                .build();
    }

    @Bean
    public Trigger nightlyJobTrigger2() {
        DailyTimeIntervalScheduleBuilder nightlyScheduler = DailyTimeIntervalScheduleBuilder
                .dailyTimeIntervalSchedule()
                .startingDailyAt(new TimeOfDay(00,00,00))
                .endingDailyAt(new TimeOfDay(07,29,59))
                .withIntervalInSeconds(NIGHTLY_INTERVAL_IN_SECOND);
        return TriggerBuilder
                .newTrigger()
                .forJob(jobDetail())
                .withIdentity("archiveTrigger3")
                .withSchedule(nightlyScheduler)
                .build();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

    @Bean("schedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(dailyJobTrigger(), nightlyJobTrigger1(), nightlyJobTrigger2());
        scheduler.setQuartzProperties(quartzProperties());
        scheduler.setJobDetails(jobDetail());
        return scheduler;
    }

    @Bean("quartzProperties")
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}
