package demo.archiving.job.step;

import demo.archiving.model.entity.arc.EntityInsert;
import demo.archiving.model.entity.master.EntitySource;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

public class CustomProcessor implements ItemProcessor<EntitySource, EntityInsert> {
    @Override
    public EntityInsert process(EntitySource item) throws Exception {
        EntityInsert insert = new EntityInsert();
        BeanUtils.copyProperties(item, insert);
        return insert;
    }
}
