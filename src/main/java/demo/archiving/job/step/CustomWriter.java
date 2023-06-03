package demo.archiving.job.step;

import demo.archiving.service.InsertService;
import io.sentry.Sentry;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import demo.archiving.model.TrackingStatus;
import demo.archiving.model.entity.arc.EntityInsert;
import demo.archiving.service.DeleteService;

import java.util.List;

@Slf4j
@Setter
public class CustomWriter implements ItemWriter<EntityInsert> {
    private InsertService insertService;
    private DeleteService deleteService;
    private Integer trackingStatus;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        trackingStatus = (Integer) jobExecutionContext.get("trackingStatus");
    }


    @Override
    public void write(List<? extends EntityInsert> items) throws Exception {
        List<EntityInsert> entityInserts = (List<EntityInsert>) items;
        try {
            insertService.insertEntity(entityInserts);
            trackingStatus = TrackingStatus.INSERTED.value();
        } catch (Exception e) {
            Sentry.captureException(e);
            log.error(">>> Insert fail. Message: {}", e);
            trackingStatus = TrackingStatus.INSERT_ERROR.value();
            throw e;
        }

        try {
            //TODO: Delete theo primary key
            int deletedRows = deleteService.deleteEntity();
            trackingStatus = TrackingStatus.DELETED.value();
        }catch (Exception e) {
            Sentry.captureException(e);
            log.error(">>> Delete fail. Message: {}", e);
            trackingStatus = TrackingStatus.DELETE_ERROR.value();
            throw e;
        }
        log.info(">>> Archive success. Batch = {}", items.size());
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put("trackingStatus", trackingStatus);
    }
}
