package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.ClientFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientFeedbackRepository extends JpaRepository<ClientFeedback, Long> {

    @Query("SELECT cf FROM ClientFeedback cf LEFT JOIN FETCH cf.work w LEFT JOIN FETCH w.vehicle LEFT JOIN FETCH w.serviceType LEFT JOIN FETCH cf.client WHERE cf.work.id = :workId")
    Optional<ClientFeedback> findByWorkIdWithDetails(@Param("workId") Long workId);

    @Query("SELECT cf FROM ClientFeedback cf WHERE cf.client.cui = :clientCui ORDER BY cf.createdAt DESC")
    List<ClientFeedback> findByClientCuiOrderByCreatedAtDesc(@Param("clientCui") String clientCui);

    @Query("SELECT cf FROM ClientFeedback cf WHERE cf.rating = :rating ORDER BY cf.createdAt DESC")
    List<ClientFeedback> findByRatingOrderByCreatedAtDesc(@Param("rating") Integer rating);

    @Query("SELECT cf FROM ClientFeedback cf WHERE cf.wouldRecommend = :wouldRecommend ORDER BY cf.createdAt DESC")
    List<ClientFeedback> findByWouldRecommendOrderByCreatedAtDesc(@Param("wouldRecommend") Boolean wouldRecommend);

    @Query("SELECT AVG(CAST(cf.rating AS double)) FROM ClientFeedback cf WHERE cf.rating IS NOT NULL")
    Double getAverageRating();

    @Query("SELECT COUNT(cf) FROM ClientFeedback cf WHERE cf.rating = :rating")
    Long countByRating(@Param("rating") Integer rating);

    @Query("SELECT COUNT(cf) FROM ClientFeedback cf WHERE cf.wouldRecommend = true")
    Long countWouldRecommend();

    @Query("SELECT cf FROM ClientFeedback cf WHERE cf.work.assignedEmployee.id = :employeeId ORDER BY cf.createdAt DESC")
    Page<ClientFeedback> findByAssignedEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(cf) > 0 THEN true ELSE false END FROM ClientFeedback cf WHERE cf.work.id = :workId AND cf.client.cui = :clientCui")
    Boolean existsByWorkIdAndClientCui(@Param("workId") Long workId, @Param("clientCui") String clientCui);

    @Query("SELECT cf FROM ClientFeedback cf ORDER BY cf.createdAt DESC")
    Page<ClientFeedback> findAllOrderByCreatedAtDesc(Pageable pageable);
}