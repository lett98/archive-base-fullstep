package demo.archiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobReadWriterCount {
    private Long readCount;
    private Long writeCount;
}
