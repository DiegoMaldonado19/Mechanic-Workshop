package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.WorkComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkCommentRepository extends JpaRepository<WorkComment, Long> {

        @Query("SELECT wc FROM WorkComment wc LEFT JOIN FETCH wc.user LEFT JOIN FETCH wc.respondedBy WHERE wc.work.id = :workId ORDER BY wc.createdAt ASC")
        List<WorkComment> findByWorkIdOrderByCreatedAtAsc(@Param("workId") Long workId);

        @Query("SELECT wc FROM WorkComment wc WHERE wc.user.id = :userId ORDER BY wc.createdAt DESC")
        Page<WorkComment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

        @Query("SELECT wc FROM WorkComment wc WHERE wc.work.assignedEmployee.id = :employeeId ORDER BY wc.createdAt DESC")
        Page<WorkComment> findCommentsByAssignedEmployeeOrderByCreatedAtDesc(@Param("employeeId") Long employeeId,
                        Pageable pageable);

        @Query("SELECT wc FROM WorkComment wc WHERE wc.commentType = :commentType ORDER BY wc.createdAt DESC")
        List<WorkComment> findByCommentTypeOrderByCreatedAtDesc(
                        @Param("commentType") WorkComment.CommentType commentType);

        @Query("SELECT wc FROM WorkComment wc WHERE wc.isUrgent = true AND wc.response IS NULL ORDER BY wc.createdAt ASC")
        List<WorkComment> findUrgentUnrespondedComments();

        @Query("SELECT wc FROM WorkComment wc WHERE wc.requiresResponse = true AND wc.response IS NULL ORDER BY wc.createdAt ASC")
        List<WorkComment> findCommentsRequiringResponse();

        @Query("SELECT wc FROM WorkComment wc WHERE wc.work.vehicle.owner.cui = :clientCui ORDER BY wc.createdAt DESC")
        Page<WorkComment> findByClientCuiOrderByCreatedAtDesc(@Param("clientCui") String clientCui, Pageable pageable);

        @Query("SELECT COUNT(wc) FROM WorkComment wc WHERE wc.work.id = :workId")
        Long countByWorkId(@Param("workId") Long workId);

        @Query("SELECT COUNT(wc) FROM WorkComment wc WHERE wc.requiresResponse = true AND wc.response IS NULL")
        Long countPendingResponses();

        @Query("SELECT COUNT(wc) FROM WorkComment wc WHERE wc.isUrgent = true AND wc.response IS NULL")
        Long countUrgentPendingResponses();

        @Query("SELECT wc FROM WorkComment wc WHERE wc.work.id = :workId AND wc.commentType = :commentType ORDER BY wc.createdAt DESC")
        List<WorkComment> findByWorkIdAndCommentTypeOrderByCreatedAtDesc(@Param("workId") Long workId,
                        @Param("commentType") WorkComment.CommentType commentType);
}