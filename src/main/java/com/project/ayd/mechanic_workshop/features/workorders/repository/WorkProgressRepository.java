package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.WorkProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface WorkProgressRepository extends JpaRepository<WorkProgress, Long> {

    @Query("SELECT wp FROM WorkProgress wp LEFT JOIN FETCH wp.user u WHERE wp.work.id = :workId ORDER BY wp.recordedAt ASC")
    List<WorkProgress> findByWorkIdOrderByRecordedAtAsc(@Param("workId") Long workId);

    @Query("SELECT wp FROM WorkProgress wp WHERE wp.user.id = :userId ORDER BY wp.recordedAt DESC")
    List<WorkProgress> findByUserIdOrderByRecordedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(wp.hoursWorked), 0) FROM WorkProgress wp WHERE wp.work.id = :workId")
    BigDecimal sumHoursWorkedByWorkId(@Param("workId") Long workId);

    @Query("SELECT COALESCE(SUM(wp.hoursWorked), 0) FROM WorkProgress wp WHERE wp.user.id = :userId")
    BigDecimal sumHoursWorkedByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(wp) FROM WorkProgress wp WHERE wp.work.id = :workId")
    Long countByWorkId(@Param("workId") Long workId);

    @Query("SELECT wp FROM WorkProgress wp WHERE wp.work.id = :workId AND wp.user.id = :userId ORDER BY wp.recordedAt DESC")
    List<WorkProgress> findByWorkIdAndUserIdOrderByRecordedAtDesc(@Param("workId") Long workId,
            @Param("userId") Long userId);
}