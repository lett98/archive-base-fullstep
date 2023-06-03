package demo.archiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class TimedWindow {
    private Timestamp start;
    private Timestamp end;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
