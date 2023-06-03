package demo.archiving.job;

import demo.archiving.repository.trk.CmTrkRepository;
import demo.archiving.util.TimeUtil;
import io.sentry.Sentry;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Setter
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ArchiveQuartzJob extends QuartzJobBean {

    private String jobName;
    private JobLauncher jobLauncher;
    private JobLocator jobLocator;
    private CmTrkRepository cmTrkRepo;
    private Integer scanTimeRange;
    private Integer dailyBatchSize;
    private Integer nightlyBatchSize;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            boolean isContinuousAccepted = checkCondition();
            if (!isContinuousAccepted) {
                log.info(">>> Quit.");
                return;
            }

            long chunkSize = 0;
            if (TimeUtil.isHotTime()) {
                chunkSize = dailyBatchSize;
            } else {
                chunkSize = nightlyBatchSize;
            }
            Job job = jobLocator.getJob(jobName);
            JobParameters params = new JobParametersBuilder()
                    .addString("Time", String.valueOf(System.currentTimeMillis()))
                    .addLong("chunkSize", chunkSize)
                    .toJobParameters();

            log.info(">>>>> START JOB: " + jobName + " - TIME: " + System.currentTimeMillis());
            jobLauncher.run(job, params);
        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
        }
    }

    private boolean checkCondition() {
        //TODO: Check condition
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        Date upperTime = cal.getTime();
        Timestamp lastEndAt = cmTrkRepo.findMaxEndAt();
        if (lastEndAt == null) {
            return true;
        }
        Timestamp nextTime = new Timestamp(lastEndAt.getTime() + TimeUnit.MINUTES.toMillis(scanTimeRange));
        return nextTime.before(upperTime);
    }
}
