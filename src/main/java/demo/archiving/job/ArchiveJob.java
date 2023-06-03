package demo.archiving.job;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.support.ListPreparedStatementSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import demo.archiving.job.listener.ArchiveJobListener;
import demo.archiving.job.step.CustomProcessor;
import demo.archiving.job.step.CustomWriter;
import demo.archiving.model.entity.arc.EntityInsert;
import demo.archiving.model.entity.master.EntitySource;
import demo.archiving.model.mapper.EntityRowMapper;
import demo.archiving.repository.trk.CmTrkRepository;
import demo.archiving.service.DeleteService;
import demo.archiving.service.InsertService;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableBatchProcessing
@Configuration
public class ArchiveJob {
    @Setter(onMethod = @__(@Autowired))
    private JobBuilderFactory jobs;
    @Setter(onMethod = @__(@Autowired))
    private StepBuilderFactory steps;

    @Setter(onMethod = @__({@Autowired, @Qualifier("reposDataSource")}))
    private DataSource jobDataSource;

    @Setter(onMethod = @__({@Autowired, @Qualifier("slaveDataSource")}))
    private DataSource slaveSource;

    @Setter(onMethod = @__(@Autowired))
    private DeleteService deleteService;

    @Setter(onMethod = @__(@Autowired))
    private InsertService insertService;

    @Setter(onMethod = @__(@Autowired))
    private CmTrkRepository cmTrkRepository;


    @Value("${archive.batchsize}")
    private Integer BATCH_SIZE ;
    private final String stepName = "processStep";

    //TODO: Add thêm logic archive
    private final String SELECT_SQL ;


    @Bean("arcJobListener")
    public ArchiveJobListener jobListener() {
        ArchiveJobListener listener = new ArchiveJobListener();
        listener.setJobDataSource(jobDataSource);
        listener.setStepName(stepName);
        listener.setCmTrkRepo(cmTrkRepository);
        return listener;
    }

    public JdbcCursorItemReader<EntitySource> itemReader(Timestamp startTime, Timestamp endTime) {
        JdbcCursorItemReader<EntitySource> cursorItemReader = new JdbcCursorItemReader<>();
        Map<String, Object> namedParameters = new HashMap<>() {{
            put("startTime", startTime);
            put("endTime", endTime);
        }};
        cursorItemReader.setSql(NamedParameterUtils.substituteNamedParameters(SELECT_SQL, new MapSqlParameterSource(namedParameters)));
        cursorItemReader.setPreparedStatementSetter(
                new ListPreparedStatementSetter(Arrays.asList(NamedParameterUtils.buildValueArray(SELECT_SQL, namedParameters)))
        );
        cursorItemReader.setDataSource(slaveSource);
        cursorItemReader.setRowMapper(new EntityRowMapper());

        return cursorItemReader;
    }
    @Bean("arcItemProcessor")
    @StepScope
    public CustomProcessor itemProcessor() {
        return new CustomProcessor();
    }

    @Bean("arcItemWriter")
    @StepScope
    public CustomWriter itemWriter() {
        CustomWriter writer = new CustomWriter();
        writer.setDeleteService(deleteService);
        writer.setInsertService(insertService);
        return writer;
    }

    @Bean("processStep")
    @JobScope()
    public Step processStep(@Value("#{jobExecutionContext['startTime']}") Object startTime,
                            @Value("#{jobExecutionContext['endTime']}") Object endTime) {
        Timestamp start = (Timestamp) startTime;
        Timestamp end = (Timestamp) endTime;
        return steps.get(stepName)
                .<EntitySource, EntityInsert>chunk(BATCH_SIZE)
                .reader(itemReader(start,end))
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean("archiveJobInfo")
    public Job job() {
        return jobs.get("archiveJobInfo")
                .listener(jobListener())
                .start(processStep(null, null))
                .build();
    }
}
