package com.project.ayd.mechanic_workshop.features.reports.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class ReportRepository {

        @PersistenceContext
        private EntityManager entityManager;

        // Dashboard Queries
        public Long countActiveWorks() {
                String sql = """
                                SELECT COUNT(*)
                                FROM work w
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE ws.name = 'En progreso'
                                """;
                Query query = entityManager.createNativeQuery(sql);
                return ((Number) query.getSingleResult()).longValue();
        }

        public Long countCompletedWorks() {
                String sql = """
                                SELECT COUNT(*)
                                FROM work w
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE ws.name = 'Completado'
                                """;
                Query query = entityManager.createNativeQuery(sql);
                return ((Number) query.getSingleResult()).longValue();
        }

        public Long countPendingWorks() {
                String sql = """
                                SELECT COUNT(*)
                                FROM work w
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE ws.name IN ('Pendiente', 'Asignado')
                                """;
                Query query = entityManager.createNativeQuery(sql);
                return ((Number) query.getSingleResult()).longValue();
        }

        public BigDecimal getTotalIncomeToday() {
                String sql = """
                                SELECT COALESCE(SUM(i.total_amount), 0)
                                FROM invoice i
                                WHERE DATE(i.issued_date) = CURRENT_DATE
                                """;
                Query query = entityManager.createNativeQuery(sql);
                Number result = (Number) query.getSingleResult();
                return new BigDecimal(result.toString());
        }

        public BigDecimal getTotalIncomeThisMonth() {
                String sql = """
                                SELECT COALESCE(SUM(i.total_amount), 0)
                                FROM invoice i
                                WHERE EXTRACT(YEAR FROM i.issued_date) = EXTRACT(YEAR FROM CURRENT_DATE)
                                AND EXTRACT(MONTH FROM i.issued_date) = EXTRACT(MONTH FROM CURRENT_DATE)
                                """;
                Query query = entityManager.createNativeQuery(sql);
                Number result = (Number) query.getSingleResult();
                return new BigDecimal(result.toString());
        }

        public BigDecimal getTotalIncomeThisYear() {
                String sql = """
                                SELECT COALESCE(SUM(i.total_amount), 0)
                                FROM invoice i
                                WHERE EXTRACT(YEAR FROM i.issued_date) = EXTRACT(YEAR FROM CURRENT_DATE)
                                """;
                Query query = entityManager.createNativeQuery(sql);
                Number result = (Number) query.getSingleResult();
                return new BigDecimal(result.toString());
        }

        public BigDecimal getTotalPendingPayments() {
                String sql = """
                                SELECT COALESCE(SUM(i.total_amount - COALESCE(p.paid_amount, 0)), 0)
                                FROM invoice i
                                LEFT JOIN (
                                    SELECT invoice_id, SUM(amount) as paid_amount
                                    FROM payment
                                    GROUP BY invoice_id
                                ) p ON i.id = p.invoice_id
                                INNER JOIN payment_status ps ON i.payment_status_id = ps.id
                                WHERE ps.name IN ('Pendiente', 'Parcial')
                                """;
                Query query = entityManager.createNativeQuery(sql);
                Number result = (Number) query.getSingleResult();
                return new BigDecimal(result.toString());
        }

        public Long countPartsInStock() {
                String sql = "SELECT COUNT(*) FROM inventory_stock WHERE quantity_available > 0";
                Query query = entityManager.createNativeQuery(sql);
                return ((Number) query.getSingleResult()).longValue();
        }

        public Long countLowStockParts() {
                String sql = """
                                SELECT COUNT(*)
                                FROM inventory_stock ist
                                INNER JOIN part p ON ist.part_id = p.id
                                WHERE ist.quantity_available <= p.minimum_stock AND ist.quantity_available > 0
                                """;
                Query query = entityManager.createNativeQuery(sql);
                return ((Number) query.getSingleResult()).longValue();
        }

        public Long countOutOfStockParts() {
                String sql = "SELECT COUNT(*) FROM inventory_stock WHERE quantity_available = 0";
                Query query = entityManager.createNativeQuery(sql);
                return ((Number) query.getSingleResult()).longValue();
        }

        public Long countTotalEmployees() {
                String sql = """
                                SELECT COUNT(*)
                                FROM "user" u
                                INNER JOIN user_type ut ON u.user_type_id = ut.id
                                WHERE ut.name IN ('EMPLEADO', 'ESPECIALISTA')
                                """;
                Query query = entityManager.createNativeQuery(sql);
                return ((Number) query.getSingleResult()).longValue();
        }

        // Financial Reports
        @SuppressWarnings("unchecked")
        public List<Object[]> getIncomeByMonth(LocalDate startDate, LocalDate endDate) {
                String sql = """
                                SELECT
                                    TO_CHAR(i.issued_date, 'YYYY-MM') as month,
                                    COALESCE(SUM(i.total_amount), 0) as income
                                FROM invoice i
                                WHERE i.issued_date BETWEEN :startDate AND :endDate
                                GROUP BY TO_CHAR(i.issued_date, 'YYYY-MM')
                                ORDER BY month
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> getWorksByStatus(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    ws.name as status,
                                    COUNT(*) as count
                                FROM work w
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY ws.name
                                ORDER BY count DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> getWorksByType(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    st.name as service_type,
                                    COUNT(*) as count
                                FROM work w
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY st.name
                                ORDER BY count DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // Employee Performance
        @SuppressWarnings("unchecked")
        public List<Object[]> getEmployeePerformance(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    CONCAT(p.first_name, ' ', p.last_name) as employee_name,
                                    u.id as employee_id,
                                    COUNT(w.id) as total_works,
                                    COUNT(CASE WHEN ws.name = 'Completado' THEN 1 END) as completed_works,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_work_time,
                                    COALESCE(SUM(w.actual_cost), 0) as total_revenue
                                FROM "user" u
                                INNER JOIN person p ON u.person_cui = p.cui
                                INNER JOIN user_type ut ON u.user_type_id = ut.id
                                LEFT JOIN work w ON u.id = w.assigned_employee_id AND w.created_at BETWEEN :startDate AND :endDate
                                LEFT JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE ut.name IN ('EMPLEADO', 'ESPECIALISTA')
                                GROUP BY u.id, p.first_name, p.last_name
                                HAVING COUNT(w.id) > 0
                                ORDER BY total_revenue DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // Parts Usage Statistics
        @SuppressWarnings("unchecked")
        public List<Object[]> getPartUsageStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    p.name as part_name,
                                    pc.name as category_name,
                                    COALESCE(SUM(wp.quantity_used), 0) as total_quantity,
                                    COALESCE(SUM(wp.quantity_used * wp.unit_price), 0) as total_cost,
                                    COUNT(DISTINCT wp.work_id) as works_count
                                FROM work_part wp
                                INNER JOIN part p ON wp.part_id = p.id
                                INNER JOIN part_category pc ON p.category_id = pc.id
                                INNER JOIN work w ON wp.work_id = w.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY p.id, p.name, pc.name
                                HAVING SUM(wp.quantity_used) > 0
                                ORDER BY total_quantity DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // Vehicle Brand Statistics
        @SuppressWarnings("unchecked")
        public List<Object[]> getVehicleBrandStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    vb.name as brand_name,
                                    COUNT(w.id) as total_works,
                                    COALESCE(AVG(w.actual_cost), 0) as avg_cost,
                                    COUNT(DISTINCT v.id) as unique_vehicles
                                FROM work w
                                INNER JOIN vehicle v ON w.vehicle_id = v.id
                                INNER JOIN vehicle_model vm ON v.model_id = vm.id
                                INNER JOIN vehicle_brand vb ON vm.brand_id = vb.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY vb.id, vb.name
                                ORDER BY total_works DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // Service Type Statistics
        @SuppressWarnings("unchecked")
        public List<Object[]> getServiceTypeStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    st.name as service_name,
                                    COUNT(w.id) as total_works,
                                    COALESCE(AVG(w.actual_cost), 0) as avg_cost,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_duration,
                                    COALESCE(SUM(w.actual_cost), 0) as total_revenue
                                FROM work w
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY st.id, st.name
                                ORDER BY total_works DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // Parts by Vehicle Brand
        @SuppressWarnings("unchecked")
        public List<Object[]> getPartsByVehicleBrand(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    vb.name as brand_name,
                                    p.name as part_name,
                                    pc.name as category_name,
                                    COALESCE(SUM(wp.quantity_used), 0) as total_quantity,
                                    COALESCE(SUM(wp.quantity_used * wp.unit_price), 0) as total_cost
                                FROM work_part wp
                                INNER JOIN part p ON wp.part_id = p.id
                                INNER JOIN part_category pc ON p.category_id = pc.id
                                INNER JOIN work w ON wp.work_id = w.id
                                INNER JOIN vehicle v ON w.vehicle_id = v.id
                                INNER JOIN vehicle_model vm ON v.model_id = vm.id
                                INNER JOIN vehicle_brand vb ON vm.brand_id = vb.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY vb.name, p.name, pc.name
                                HAVING SUM(wp.quantity_used) > 0
                                ORDER BY vb.name, total_quantity DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // Client History
        @SuppressWarnings("unchecked")
        public List<Object[]> getClientHistory(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    CONCAT(p.first_name, ' ', p.last_name) as client_name,
                                    p.cui as client_cui,
                                    COUNT(w.id) as total_works,
                                    COALESCE(SUM(i.total_amount), 0) as total_spent,
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
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // Preventive Maintenance Report
        @SuppressWarnings("unchecked")
        public List<Object[]> getPreventiveMaintenanceReport(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    st.name as service_name,
                                    COUNT(w.id) as total_works,
                                    COALESCE(AVG(w.actual_cost), 0) as avg_cost,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_duration,
                                    COALESCE(SUM(w.actual_cost), 0) as total_revenue
                                FROM work w
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                AND st.name = 'Preventivo'
                                GROUP BY st.id, st.name
                                ORDER BY total_works DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }
}