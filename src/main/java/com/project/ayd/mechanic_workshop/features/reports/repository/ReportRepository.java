package com.project.ayd.mechanic_workshop.features.reports.repository;

import com.project.ayd.mechanic_workshop.features.reports.dto.DashboardResponse;
import com.project.ayd.mechanic_workshop.features.reports.dto.FinancialReportResponse;
import com.project.ayd.mechanic_workshop.features.reports.dto.OperationalReportResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ReportRepository extends JpaRepository<Object, Long> {

    // Dashboard Queries
    @Query(value = """
            SELECT COUNT(*)
            FROM work w
            INNER JOIN work_status ws ON w.work_status_id = ws.id
            WHERE ws.name = 'En progreso'
            """, nativeQuery = true)
    Long countActiveWorks();

    @Query(value = """
            SELECT COUNT(*)
            FROM work w
            INNER JOIN work_status ws ON w.work_status_id = ws.id
            WHERE ws.name = 'Completado'
            """, nativeQuery = true)
    Long countCompletedWorks();

    @Query(value = """
            SELECT COUNT(*)
            FROM work w
            INNER JOIN work_status ws ON w.work_status_id = ws.id
            WHERE ws.name IN ('Pendiente', 'Asignado')
            """, nativeQuery = true)
    Long countPendingWorks();

    @Query(value = """
            SELECT COALESCE(SUM(i.total_amount), 0)
            FROM invoice i
            WHERE DATE(i.issued_date) = CURRENT_DATE
            """, nativeQuery = true)
    BigDecimal getTotalIncomeToday();

    @Query(value = """
            SELECT COALESCE(SUM(i.total_amount), 0)
            FROM invoice i
            WHERE EXTRACT(YEAR FROM i.issued_date) = EXTRACT(YEAR FROM CURRENT_DATE)
            AND EXTRACT(MONTH FROM i.issued_date) = EXTRACT(MONTH FROM CURRENT_DATE)
            """, nativeQuery = true)
    BigDecimal getTotalIncomeThisMonth();

    @Query(value = """
            SELECT COALESCE(SUM(i.total_amount), 0)
            FROM invoice i
            WHERE EXTRACT(YEAR FROM i.issued_date) = EXTRACT(YEAR FROM CURRENT_DATE)
            """, nativeQuery = true)
    BigDecimal getTotalIncomeThisYear();

    @Query(value = """
            SELECT COALESCE(SUM(i.total_amount - COALESCE(p.paid_amount, 0)), 0)
            FROM invoice i
            LEFT JOIN (
                SELECT invoice_id, SUM(amount) as paid_amount
                FROM payment
                GROUP BY invoice_id
            ) p ON i.id = p.invoice_id
            INNER JOIN payment_status ps ON i.payment_status_id = ps.id
            WHERE ps.name IN ('Pendiente', 'Parcial')
            """, nativeQuery = true)
    BigDecimal getTotalPendingPayments();

    @Query(value = """
            SELECT COUNT(*)
            FROM inventory_stock
            WHERE quantity_available > 0
            """, nativeQuery = true)
    Long countPartsInStock();

    @Query(value = """
            SELECT COUNT(*)
            FROM inventory_stock ist
            INNER JOIN part p ON ist.part_id = p.id
            WHERE ist.quantity_available <= p.minimum_stock AND ist.quantity_available > 0
            """, nativeQuery = true)
    Long countLowStockParts();

    @Query(value = """
            SELECT COUNT(*)
            FROM inventory_stock
            WHERE quantity_available = 0
            """, nativeQuery = true)
    Long countOutOfStockParts();

    // Financial Reports
    @Query(value = """
            SELECT
                TO_CHAR(i.issued_date, 'YYYY-MM') as month,
                SUM(i.total_amount) as income
            FROM invoice i
            WHERE i.issued_date BETWEEN :startDate AND :endDate
            GROUP BY TO_CHAR(i.issued_date, 'YYYY-MM')
            ORDER BY month
            """, nativeQuery = true)
    List<Object[]> getIncomeByMonth(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
            SELECT
                ws.name as status,
                COUNT(*) as count
            FROM work w
            INNER JOIN work_status ws ON w.work_status_id = ws.id
            WHERE w.created_at BETWEEN :startDate AND :endDate
            GROUP BY ws.name
            """, nativeQuery = true)
    List<Object[]> getWorksByStatus(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = """
            SELECT
                st.name as service_type,
                COUNT(*) as count
            FROM work w
            INNER JOIN service_type st ON w.service_type_id = st.id
            WHERE w.created_at BETWEEN :startDate AND :endDate
            GROUP BY st.name
            """, nativeQuery = true)
    List<Object[]> getWorksByType(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Employee Performance
    @Query(value = """
            SELECT
                CONCAT(p.first_name, ' ', p.last_name) as employee_name,
                u.id as employee_id,
                COUNT(w.id) as total_works,
                COUNT(CASE WHEN ws.name = 'Completado' THEN 1 END) as completed_works,
                AVG(w.actual_hours) as avg_work_time,
                SUM(COALESCE(w.actual_cost, 0)) as total_revenue
            FROM "user" u
            INNER JOIN person p ON u.person_cui = p.cui
            LEFT JOIN work w ON u.id = w.assigned_employee_id
            LEFT JOIN work_status ws ON w.work_status_id = ws.id
            WHERE w.created_at BETWEEN :startDate AND :endDate
            GROUP BY u.id, p.first_name, p.last_name
            HAVING COUNT(w.id) > 0
            ORDER BY total_revenue DESC
            """, nativeQuery = true)
    List<Object[]> getEmployeePerformance(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Parts Usage Statistics
    @Query(value = """
            SELECT
                p.name as part_name,
                pc.name as category_name,
                SUM(wp.quantity_used) as total_quantity,
                SUM(wp.quantity_used * wp.unit_price) as total_cost,
                COUNT(DISTINCT wp.work_id) as works_count
            FROM work_part wp
            INNER JOIN part p ON wp.part_id = p.id
            INNER JOIN part_category pc ON p.category_id = pc.id
            INNER JOIN work w ON wp.work_id = w.id
            WHERE w.created_at BETWEEN :startDate AND :endDate
            GROUP BY p.id, p.name, pc.name
            ORDER BY total_quantity DESC
            """, nativeQuery = true)
    List<Object[]> getPartUsageStatistics(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Vehicle Brand Statistics
    @Query(value = """
            SELECT
                vb.name as brand_name,
                COUNT(w.id) as total_works,
                AVG(w.actual_cost) as avg_cost,
                COUNT(DISTINCT v.id) as unique_vehicles
            FROM work w
            INNER JOIN vehicle v ON w.vehicle_id = v.id
            INNER JOIN vehicle_model vm ON v.model_id = vm.id
            INNER JOIN vehicle_brand vb ON vm.brand_id = vb.id
            WHERE w.created_at BETWEEN :startDate AND :endDate
            GROUP BY vb.id, vb.name
            ORDER BY total_works DESC
            """, nativeQuery = true)
    List<Object[]> getVehicleBrandStatistics(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Service Type Statistics
    @Query(value = """
            SELECT
                st.name as service_name,
                COUNT(w.id) as total_works,
                AVG(w.actual_cost) as avg_cost,
                AVG(w.actual_hours) as avg_duration,
                SUM(COALESCE(w.actual_cost, 0)) as total_revenue
            FROM work w
            INNER JOIN service_type st ON w.service_type_id = st.id
            WHERE w.created_at BETWEEN :startDate AND :endDate
            GROUP BY st.id, st.name
            ORDER BY total_works DESC
            """, nativeQuery = true)
    List<Object[]> getServiceTypeStatistics(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Parts by Vehicle Brand
    @Query(value = """
            SELECT
                vb.name as brand_name,
                p.name as part_name,
                pc.name as category_name,
                SUM(wp.quantity_used) as total_quantity,
                SUM(wp.quantity_used * wp.unit_price) as total_cost
            FROM work_part wp
            INNER JOIN part p ON wp.part_id = p.id
            INNER JOIN part_category pc ON p.category_id = pc.id
            INNER JOIN work w ON wp.work_id = w.id
            INNER JOIN vehicle v ON w.vehicle_id = v.id
            INNER JOIN vehicle_model vm ON v.model_id = vm.id
            INNER JOIN vehicle_brand vb ON vm.brand_id = vb.id
            WHERE w.created_at BETWEEN :startDate AND :endDate
            GROUP BY vb.name, p.name, pc.name
            ORDER BY vb.name, total_quantity DESC
            """, nativeQuery = true)
    List<Object[]> getPartsByVehicleBrand(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Client History
    @Query(value = """
            SELECT
                CONCAT(p.first_name, ' ', p.last_name) as client_name,
                p.cui as client_cui,
                COUNT(w.id) as total_works,
                SUM(COALESCE(i.total_amount, 0)) as total_spent,
                MAX(w.created_at) as last_visit,
                STRING_AGG(DISTINCT st.name, ', ') as service_types
            FROM person p
            INNER JOIN vehicle v ON p.cui = v.owner_cui
            INNER JOIN work w ON v.id = w.vehicle_id
            LEFT JOIN invoice i ON w.id = i.work_id
            INNER JOIN service_type st ON w.service_type_id = st.id
            WHERE w.created_at BETWEEN :startDate AND :endDate
            GROUP BY p.cui, p.first_name, p.last_name
            ORDER BY total_spent DESC
            """, nativeQuery = true)
    List<Object[]> getClientHistory(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}