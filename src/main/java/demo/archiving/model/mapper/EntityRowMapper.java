package demo.archiving.model.mapper;

import demo.archiving.model.entity.master.EntitySource;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityRowMapper implements RowMapper<EntitySource> {
    @Override
    public EntitySource mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }
}
