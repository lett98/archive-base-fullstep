package demo.archiving.util;

import demo.archiving.model.entity.arc.EntityInsert;

import java.util.ArrayList;
import java.util.List;

public class BatchUtil {
    public static List<Long> getId(List<? extends EntityInsert> eventLogs) {
        List<Long> ids = new ArrayList<>(eventLogs.size());
        for(EntityInsert event : eventLogs) {
            //TODO
        }
        return ids;
    }
}
