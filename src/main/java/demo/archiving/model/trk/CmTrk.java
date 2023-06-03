package demo.archiving.model.trk;

import lombok.Data;
import lombok.Getter;
import demo.archiving.model.TrackingStatus;
import demo.archiving.model.converter.TrackingStatusConverter;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "iam_event_trk")
@Entity
@Data
@Getter
public class CmTrk {

    @Id
    @Column(name="job_id")
    private Long jobId;

    @Column(name="start_at")
    private Timestamp startAt;

    @Column(name="end_at")
    private Timestamp endAt;

    @Column(name="read_size")
    private Integer readSize;

    @Column(name="write_size")
    private Integer writeSize;

    @Column(name="status")
    private String status;

    @Column(name="job_start_at")
    private Timestamp jobStartAt;

    @Column(name="job_end_at")
    private Timestamp jobEndAt;

    @Convert(converter = TrackingStatusConverter.class)
    @Column(name="archiving_status")
    private TrackingStatus archivingStatus;
}
