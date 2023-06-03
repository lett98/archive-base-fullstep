package demo.archiving.repository.trk;

import demo.archiving.model.trk.CmTrk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

public interface CmTrkRepository extends JpaRepository<CmTrk, Long> {
    @Query(value = "select max(end_at) from cm_trk", nativeQuery = true)
    Timestamp findMaxEndAt();

    @Modifying
    @Transactional
    @Query(value = "insert into cm_trk(job_id, start_at, end_at, status, job_start_at, job_end_at,  archiving_status) " +
            "values (:jobId, :startAt, :endAt, :status, :jobStartAt, :jobEndAt, :archivingStatus)", nativeQuery = true)
    int insertNewJob(@Param(value = "jobId") Long jobId,
                     @Param(value = "startAt") Timestamp startAt,
                     @Param(value = "endAt") Timestamp endAt,
                     @Param(value = "status") String status,
                     @Param(value = "jobStartAt") Timestamp jobStartAt,
                     @Param(value = "jobEndAt") Timestamp jobEndAt,
                     @Param(value = "archivingStatus") Integer archivingStatus);

    CmTrk findFirstByJobId(Long jobId);


    @Modifying
    @Transactional
    @Query(value = "update cm_trk set read_size= :readSize, write_size= :writeSize, status= :status, job_end_at= :jobEndAt, archiving_status= :archivingStatus " +
            "where job_id = :jobId", nativeQuery = true)
    int updateJob(@Param(value = "readSize") Long readSize,
                           @Param(value = "writeSize") Long writeSize,
                           @Param(value = "status") String status,
                           @Param(value = "jobEndAt") Timestamp jobEndAt,
                           @Param(value = "archivingStatus") Integer archivingStatus,
                           @Param(value = "jobId") Long jobId);

}
