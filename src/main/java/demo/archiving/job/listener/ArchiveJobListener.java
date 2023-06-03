package demo.archiving.job.listener;

import demo.archiving.model.JobReadWriterCount;
import demo.archiving.model.TimedWindow;
import demo.archiving.model.trk.CmTrk;
import io.sentry.Sentry;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import demo.archiving.model.TrackingStatus;
import demo.archiving.model.mapper.JobReadWriteCountRowMapper;
import demo.archiving.repository.trk.CmTrkRepository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@Setter
public class ArchiveJobListener extends JobExecutionListenerSupport{

    @Value("${cm.timewindow}")
    private int SCAN_TIME_RANGE_IN_MINUTES ;

    private TimedWindow timedWindow;

    private CmTrk cmTrk;
    private CmTrkRepository cmTrkRepo;
    private String stepName;
    private String tableName;

    private DataSource jobDataSource;

    private void resetAll() {
        this.cmTrk = null;
        this.timedWindow = null;
    }
    @Override
    public void beforeJob(JobExecution jobExecution) {
        resetAll();
        try {
            timedWindow = this.getTimedWindow();
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException(e);
        }
        log.info(">>> BeforeJob: TimeWindow: {} - {} " , timedWindow.getStart(), timedWindow.getEnd());
        jobExecution.getExecutionContext().put("trackingStatus", TrackingStatus.PENDING.value());


        jobExecution.getExecutionContext().put("startTime", timedWindow.getStart());
        jobExecution.getExecutionContext().put("endTime", timedWindow.getEnd());
        cmTrk = this.save(jobExecution);
        super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(">>> AfterJob: JobId: " + cmTrk.getJobId() + " - JobStatus: " + jobExecution.getExitStatus().getExitCode());
        JobReadWriterCount jobReadWriterCount = getReadWriteCount(jobExecution);
        Integer trackingStatus = (Integer) jobExecution.getExecutionContext().get("trackingStatus");
        if (cmTrk != null) {
            int effected = cmTrkRepo.updateJob(
                    jobReadWriterCount.getReadCount(),
                    jobReadWriterCount.getWriteCount(),
                    jobExecution.getExitStatus().getExitCode(),
                    new Timestamp(jobExecution.getEndTime().getTime()),
                    trackingStatus,
                    cmTrk.getJobId());
        }
        super.afterJob(jobExecution);
    }

    private JobReadWriterCount getReadWriteCount(JobExecution jobExecution) {
        String SQL = "select READ_COUNT, WRITE_COUNT from BATCH_STEP_EXECUTION " +
                "where STEP_NAME = ? " +
                "and JOB_EXECUTION_ID in (SELECT max(JOB_EXECUTION_ID) FROM BATCH_JOB_EXECUTION WHERE JOB_INSTANCE_ID = ?)";
        JdbcOperations jdbcOperations = new JdbcTemplate(jobDataSource);
        return jdbcOperations.queryForObject(SQL,
                new Object[] {stepName, jobExecution.getJobId()},
                new JobReadWriteCountRowMapper());
    }

    private CmTrk save(JobExecution jobExecution) {
        cmTrkRepo.insertNewJob(
                jobExecution.getJobId(),
                timedWindow.getStart(),
                timedWindow.getEnd(),
                jobExecution.getExitStatus().getExitCode(),
                new Timestamp(jobExecution.getCreateTime().getTime()),
                null,
                TrackingStatus.GENERATING.value());
        CmTrk cmTrk1 =  cmTrkRepo.findFirstByJobId(jobExecution.getJobId());
        return cmTrk1;
    }

    private TimedWindow getTimedWindow() throws Exception {
        Timestamp lastEndAt = cmTrkRepo.findMaxEndAt();
        if (lastEndAt == null) {
            throw new Exception("Start time must be set.");
        }

        Timestamp nextTime = new Timestamp(lastEndAt.getTime() + TimeUnit.MINUTES.toMillis(SCAN_TIME_RANGE_IN_MINUTES));
        return new TimedWindow(lastEndAt, nextTime);
    }

}
