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

        // ================================
        // DASHBOARD QUERIES
        // ================================

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

        // ================================
        // FINANCIAL REPORTS
        // ================================

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
        public List<Object[]> getIncomeByWeek(LocalDate startDate, LocalDate endDate) {
                String sql = """
                                SELECT
                                    TO_CHAR(i.issued_date, 'YYYY-IW') as week,
                                    COALESCE(SUM(i.total_amount), 0) as income
                                FROM invoice i
                                WHERE i.issued_date BETWEEN :startDate AND :endDate
                                GROUP BY TO_CHAR(i.issued_date, 'YYYY-IW')
                                ORDER BY week
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> getExpensesByMonth(LocalDate startDate, LocalDate endDate) {
                String sql = """
                                SELECT
                                    TO_CHAR(po.order_date, 'YYYY-MM') as month,
                                    COALESCE(SUM(po.total_amount), 0) as expenses
                                FROM purchase_order po
                                INNER JOIN purchase_order_status pos ON po.purchase_order_status_id = pos.id
                                WHERE po.order_date BETWEEN :startDate AND :endDate
                                AND pos.name IN ('Entregada', 'Confirmada')
                                GROUP BY TO_CHAR(po.order_date, 'YYYY-MM')
                                ORDER BY month
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> getProviderExpenses(LocalDate startDate, LocalDate endDate) {
                String sql = """
                                SELECT
                                    COALESCE(s.company_name, CONCAT(p.first_name, ' ', p.last_name)) as provider_name,
                                    COUNT(po.id) as total_orders,
                                    COALESCE(SUM(po.total_amount), 0) as total_amount,
                                    MAX(po.order_date) as last_order_date
                                FROM purchase_order po
                                INNER JOIN supplier s ON po.supplier_id = s.id
                                LEFT JOIN person p ON s.person_cui = p.cui
                                INNER JOIN purchase_order_status pos ON po.purchase_order_status_id = pos.id
                                WHERE po.order_date BETWEEN :startDate AND :endDate
                                AND pos.name IN ('Entregada', 'Confirmada')
                                GROUP BY s.id, s.company_name, p.first_name, p.last_name
                                ORDER BY total_amount DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // ================================
        // OPERATIONAL REPORTS
        // ================================

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

        @SuppressWarnings("unchecked")
        public List<Object[]> getWorksByDateAndType(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    DATE(w.created_at) as work_date,
                                    st.name as service_type,
                                    COUNT(*) as count,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_hours,
                                    COALESCE(SUM(w.actual_cost), 0) as total_cost
                                FROM work w
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY DATE(w.created_at), st.name
                                ORDER BY work_date DESC, count DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> getVehicleMaintenanceHistory(String licensePlate, LocalDateTime startDate,
                        LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    w.id as work_id,
                                    st.name as service_type,
                                    w.problem_description,
                                    ws.name as status,
                                    w.actual_cost,
                                    w.actual_hours,
                                    w.created_at,
                                    w.completed_at,
                                    CONCAT(p.first_name, ' ', p.last_name) as mechanic_name
                                FROM work w
                                INNER JOIN vehicle v ON w.vehicle_id = v.id
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                LEFT JOIN "user" u ON w.assigned_employee_id = u.id
                                LEFT JOIN person p ON u.person_cui = p.cui
                                WHERE v.license_plate = :licensePlate
                                AND w.created_at BETWEEN :startDate AND :endDate
                                ORDER BY w.created_at DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("licensePlate", licensePlate);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // ================================
        // EMPLOYEE PERFORMANCE
        // ================================

        @SuppressWarnings("unchecked")
        public List<Object[]> getEmployeePerformance(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    CONCAT(p.first_name, ' ', p.last_name) as employee_name,
                                    u.id as employee_id,
                                    COUNT(w.id) as total_works,
                                    COUNT(CASE WHEN ws.name = 'Completado' THEN 1 END) as completed_works,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_work_time,
                                    COALESCE(SUM(w.actual_cost), 0) as total_revenue,
                                    ROUND(
                                        COUNT(CASE WHEN ws.name = 'Completado' THEN 1 END) * 100.0 /
                                        NULLIF(COUNT(w.id), 0), 2
                                    ) as completion_rate
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

        @SuppressWarnings("unchecked")
        public List<Object[]> getWorksByEmployee(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    CONCAT(p.first_name, ' ', p.last_name) as employee_name,
                                    DATE(w.created_at) as work_date,
                                    st.name as service_type,
                                    w.actual_hours,
                                    w.actual_cost,
                                    ws.name as status
                                FROM work w
                                INNER JOIN "user" u ON w.assigned_employee_id = u.id
                                INNER JOIN person p ON u.person_cui = p.cui
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                ORDER BY p.first_name, p.last_name, w.created_at DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // ================================
        // PARTS AND INVENTORY REPORTS
        // ================================

        @SuppressWarnings("unchecked")
        public List<Object[]> getPartUsageStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    p.name as part_name,
                                    pc.name as category_name,
                                    COALESCE(SUM(wp.quantity_used), 0) as total_quantity,
                                    COALESCE(SUM(wp.quantity_used * wp.unit_price), 0) as total_cost,
                                    COUNT(DISTINCT wp.work_id) as works_count,
                                    COALESCE(AVG(wp.unit_price), 0) as avg_unit_price
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

        @SuppressWarnings("unchecked")
        public List<Object[]> getMostUsedPartsByCategory(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    pc.name as category_name,
                                    COUNT(DISTINCT p.id) as different_parts,
                                    COALESCE(SUM(wp.quantity_used), 0) as total_quantity,
                                    COALESCE(SUM(wp.quantity_used * wp.unit_price), 0) as total_cost
                                FROM work_part wp
                                INNER JOIN part p ON wp.part_id = p.id
                                INNER JOIN part_category pc ON p.category_id = pc.id
                                INNER JOIN work w ON wp.work_id = w.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY pc.id, pc.name
                                HAVING SUM(wp.quantity_used) > 0
                                ORDER BY total_quantity DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // ================================
        // VEHICLE AND BRAND STATISTICS
        // ================================

        @SuppressWarnings("unchecked")
        public List<Object[]> getVehicleBrandStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    vb.name as brand_name,
                                    COUNT(w.id) as total_works,
                                    COALESCE(AVG(w.actual_cost), 0) as avg_cost,
                                    COUNT(DISTINCT v.id) as unique_vehicles,
                                    COALESCE(SUM(w.actual_cost), 0) as total_revenue,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_hours
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

        @SuppressWarnings("unchecked")
        public List<Object[]> getServiceTypeStatistics(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    st.name as service_name,
                                    COUNT(w.id) as total_works,
                                    COALESCE(AVG(w.actual_cost), 0) as avg_cost,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_duration,
                                    COALESCE(SUM(w.actual_cost), 0) as total_revenue,
                                    COUNT(CASE WHEN ws.name = 'Completado' THEN 1 END) as completed_count
                                FROM work w
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY st.id, st.name
                                ORDER BY total_works DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // ================================
        // CLIENT REPORTS
        // ================================

        @SuppressWarnings("unchecked")
        public List<Object[]> getClientHistory(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    CONCAT(p.first_name, ' ', p.last_name) as client_name,
                                    p.cui as client_cui,
                                    p.email as client_email,
                                    COUNT(w.id) as total_works,
                                    COALESCE(SUM(i.total_amount), 0) as total_spent,
                                    MAX(w.created_at) as last_visit,
                                    COUNT(DISTINCT v.id) as vehicles_count,
                                    STRING_AGG(DISTINCT st.name, ', ') as service_types
                                FROM person p
                                INNER JOIN vehicle v ON p.cui = v.owner_cui
                                INNER JOIN work w ON v.id = w.vehicle_id
                                LEFT JOIN invoice i ON w.id = i.work_id
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                GROUP BY p.cui, p.first_name, p.last_name, p.email
                                ORDER BY total_spent DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> getClientServiceRatings(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    CONCAT(p.first_name, ' ', p.last_name) as client_name,
                                    COUNT(cf.id) as total_ratings,
                                    COALESCE(AVG(cf.rating), 0) as avg_rating,
                                    COUNT(CASE WHEN cf.would_recommend = true THEN 1 END) as recommendations,
                                    COUNT(CASE WHEN cf.rating >= 4 THEN 1 END) as positive_ratings
                                FROM client_feedback cf
                                INNER JOIN work w ON cf.work_id = w.id
                                INNER JOIN person p ON cf.client_cui = p.cui
                                WHERE cf.created_at BETWEEN :startDate AND :endDate
                                GROUP BY p.cui, p.first_name, p.last_name
                                HAVING COUNT(cf.id) > 0
                                ORDER BY avg_rating DESC, total_ratings DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // ================================
        // MAINTENANCE SPECIFIC REPORTS
        // ================================

        @SuppressWarnings("unchecked")
        public List<Object[]> getPreventiveMaintenanceReport(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    DATE(w.created_at) as maintenance_date,
                                    COUNT(w.id) as total_works,
                                    COALESCE(AVG(w.actual_cost), 0) as avg_cost,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_duration,
                                    COALESCE(SUM(w.actual_cost), 0) as total_revenue,
                                    COUNT(CASE WHEN ws.name = 'Completado' THEN 1 END) as completed_count
                                FROM work w
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                AND st.name = 'Preventivo'
                                GROUP BY DATE(w.created_at)
                                ORDER BY maintenance_date DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> getCorrectiveMaintenanceReport(LocalDateTime startDate, LocalDateTime endDate) {
                String sql = """
                                SELECT
                                    DATE(w.created_at) as maintenance_date,
                                    COUNT(w.id) as total_works,
                                    COALESCE(AVG(w.actual_cost), 0) as avg_cost,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_duration,
                                    COALESCE(SUM(w.actual_cost), 0) as total_revenue,
                                    COUNT(CASE WHEN ws.name = 'Completado' THEN 1 END) as completed_count
                                FROM work w
                                INNER JOIN service_type st ON w.service_type_id = st.id
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE w.created_at BETWEEN :startDate AND :endDate
                                AND st.name = 'Correctivo'
                                GROUP BY DATE(w.created_at)
                                ORDER BY maintenance_date DESC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                return query.getResultList();
        }

        // ================================
        // ADDITIONAL UTILITY QUERIES
        // ================================

        @SuppressWarnings("unchecked")
        public List<Object[]> getTopPerformingMechanics(LocalDateTime startDate, LocalDateTime endDate, int limit) {
                String sql = """
                                SELECT
                                    CONCAT(p.first_name, ' ', p.last_name) as mechanic_name,
                                    COUNT(w.id) as total_works,
                                    COALESCE(SUM(w.actual_cost), 0) as total_revenue,
                                    COALESCE(AVG(w.actual_hours), 0) as avg_hours_per_work
                                FROM "user" u
                                INNER JOIN person p ON u.person_cui = p.cui
                                INNER JOIN user_type ut ON u.user_type_id = ut.id
                                INNER JOIN work w ON u.id = w.assigned_employee_id
                                INNER JOIN work_status ws ON w.work_status_id = ws.id
                                WHERE ut.name IN ('EMPLEADO', 'ESPECIALISTA')
                                AND w.created_at BETWEEN :startDate AND :endDate
                                AND ws.name = 'Completado'
                                GROUP BY u.id, p.first_name, p.last_name
                                ORDER BY total_revenue DESC
                                LIMIT :limit
                                """;
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                query.setParameter("limit", limit);
                return query.getResultList();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> getLowStockAlerts() {
                String sql = """
                                SELECT
                                    p.name as part_name,
                                    pc.name as category_name,
                                    ist.quantity_available,
                                    p.minimum_stock,
                                    p.unit_price
                                FROM inventory_stock ist
                                INNER JOIN part p ON ist.part_id = p.id
                                INNER JOIN part_category pc ON p.category_id = pc.id
                                WHERE ist.quantity_available <= p.minimum_stock
                                AND ist.quantity_available >= 0
                                ORDER BY (ist.quantity_available - p.minimum_stock) ASC
                                """;
                Query query = entityManager.createNativeQuery(sql);
                return query.getResultList();
        }
}