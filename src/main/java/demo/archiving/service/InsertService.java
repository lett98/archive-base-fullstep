package demo.archiving.service;

import demo.archiving.model.entity.arc.EntityInsert;

import java.util.List;

public interface InsertService {
    void insertEntity(List<EntityInsert> insertList) throws Exception;
}
