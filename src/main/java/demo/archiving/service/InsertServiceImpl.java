package demo.archiving.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import demo.archiving.model.entity.arc.EntityInsert;
import demo.archiving.repository.entity.arc.EntityInsertRepository;
import demo.archiving.vo.ResultCheckException;

import java.util.List;

@Slf4j
public class InsertServiceImpl implements InsertService{

    @Autowired
    private EntityInsertRepository insertRepository;
    @Override
    @Transactional(value = "insertTransactionManager", rollbackFor = {Exception.class, RuntimeException.class, ResultCheckException.class})
    public void insertEntity(List<EntityInsert> insertList) throws Exception {
        List<EntityInsert> insertedRows = insertRepository.saveAll(insertList);
        if(insertedRows.size() == insertList.size()) {
            log.info("Save batch.");
        } else {
            throw new ResultCheckException("Not save all batch.");
        }
    }
}
