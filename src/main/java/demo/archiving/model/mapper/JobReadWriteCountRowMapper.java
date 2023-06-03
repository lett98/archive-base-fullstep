package demo.archiving.model.mapper;

import demo.archiving.model.JobReadWriterCount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JobReadWriteCountRowMapper implements RowMapper<JobReadWriterCount> {
    @Override
    public JobReadWriterCount mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new JobReadWriterCount(rs.getLong("READ_COUNT"), rs.getLong("WRITE_COUNT"));
            }
}
