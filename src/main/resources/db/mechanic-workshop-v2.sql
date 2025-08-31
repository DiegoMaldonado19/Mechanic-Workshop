CREATE DATABASE mechanic_workshop;

USE mechanic_workshop;

-- User Types
CREATE TABLE user_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Gender
CREATE TABLE gender (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Department
CREATE TABLE department (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Municipality
CREATE TABLE municipality (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id INTEGER NOT NULL REFERENCES department(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Address Details
CREATE TABLE address_detail (
    id SERIAL PRIMARY KEY,
    address TEXT,
    municipality_id INTEGER NOT NULL REFERENCES municipality(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Countries for brands
CREATE TABLE country (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle engine sizes
CREATE TABLE engine_size (
    id SERIAL PRIMARY KEY,
    size DECIMAL(2,1),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle transmission types
CREATE TABLE transmission_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle fuel types
CREATE TABLE fuel_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle brands
CREATE TABLE vehicle_brand (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    country_id INTEGER NOT NULL REFERENCES country(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle models
CREATE TABLE vehicle_model (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    brand_id INTEGER NOT NULL REFERENCES vehicle_brand(id),
    year INTEGER,
    engine_size_id INTEGER NOT NULL REFERENCES engine_size(id),
    transmission_type_id INTEGER NOT NULL REFERENCES transmission_type(id),
    fuel_type_id INTEGER NOT NULL REFERENCES fuel_type(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Service/work types
CREATE TABLE service_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Work status types
CREATE TABLE work_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment status types
CREATE TABLE payment_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Purchase order status types
CREATE TABLE purchase_order_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Part category types
CREATE TABLE part_category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Specialization types
CREATE TABLE specialization_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment methods
CREATE TABLE payment_method (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Movement Types
CREATE TABLE movement_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- References Type
CREATE TABLE reference_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Person Table
CREATE TABLE person (
    cui VARCHAR(13) PRIMARY KEY UNIQUE,
    nit VARCHAR(15) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    address_detail_id INTEGER REFERENCES address_detail(id),
    birth_date DATE,
    gender_id INTEGER REFERENCES gender(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    person_cui VARCHAR(13) NOT NULL REFERENCES person(cui) ON DELETE CASCADE,
    user_type_id INTEGER NOT NULL REFERENCES user_type(id),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    two_factor_code VARCHAR(6),
    two_factor_code_expires TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- password reset tokens table for auth
CREATE TABLE password_reset_token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INTEGER NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employee specializations
CREATE TABLE employee_specialization (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    specialization_type_id INTEGER NOT NULL REFERENCES specialization_type(id),
    certification_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles table
CREATE TABLE vehicle (
    id SERIAL PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    model_id INTEGER NOT NULL REFERENCES vehicle_model(id),
    color VARCHAR(50),
    vin VARCHAR(50) UNIQUE,
    owner_cui VARCHAR(13) NOT NULL REFERENCES person(cui),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Parts/inventory table
CREATE TABLE part (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id INTEGER NOT NULL REFERENCES part_category(id),
    unit_price DECIMAL(10,2) NOT NULL,
    minimum_stock INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inventory stock table
CREATE TABLE inventory_stock (
    id SERIAL PRIMARY KEY,
    part_id INTEGER NOT NULL REFERENCES part(id) UNIQUE,
    quantity_available INTEGER NOT NULL DEFAULT 0,
    quantity_reserved INTEGER NOT NULL DEFAULT 0,
    last_restocked TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Suppliers/Providers table
CREATE TABLE supplier (
    id SERIAL PRIMARY KEY,
    person_cui VARCHAR(13) REFERENCES person(cui) ON DELETE CASCADE UNIQUE,
    company_name VARCHAR(200),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    address_detail_id INTEGER REFERENCES address_detail(id),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (person_cui IS NOT NULL OR company_name IS NOT NULL)
);

-- Work Table
CREATE TABLE work (
    id SERIAL PRIMARY KEY,
    vehicle_id INTEGER NOT NULL REFERENCES vehicle(id),
    service_type_id INTEGER NOT NULL REFERENCES service_type(id),
    work_status_id INTEGER NOT NULL REFERENCES work_status(id),
    assigned_employee_id INTEGER REFERENCES "user"(id),
    problem_description TEXT NOT NULL,
    estimated_hours DECIMAL(5,2),
    actual_hours DECIMAL(5,2),
    estimated_cost DECIMAL(10,2),
    actual_cost DECIMAL(10,2),
    client_approved BOOLEAN DEFAULT FALSE,
    client_approved_at TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    priority_level INTEGER DEFAULT 1 CHECK (priority_level BETWEEN 1 AND 5),
    created_by INTEGER NOT NULL REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Work progress tracking table
CREATE TABLE work_progress (
    id SERIAL PRIMARY KEY,
    work_id INTEGER NOT NULL REFERENCES work(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    progress_description TEXT NOT NULL,
    hours_worked DECIMAL(5,2),
    observations TEXT,
    symptoms_detected TEXT,
    additional_damage_found TEXT,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Work parts
CREATE TABLE work_part (
    id SERIAL PRIMARY KEY,
    work_id INTEGER NOT NULL REFERENCES work(id) ON DELETE CASCADE,
    part_id INTEGER NOT NULL REFERENCES part(id),
    quantity_needed INTEGER NOT NULL,
    quantity_used INTEGER DEFAULT 0,
    unit_price DECIMAL(10,2) NOT NULL,
    requested_by INTEGER NOT NULL REFERENCES "user"(id),
    approved_by INTEGER REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(work_id, part_id)
);

-- Quotations table
CREATE TABLE quotation (
    id SERIAL PRIMARY KEY,
    work_id INTEGER NOT NULL REFERENCES work(id),
    total_parts_cost DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_labor_cost DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    valid_until DATE,
    client_approved BOOLEAN DEFAULT FALSE,
    approved_at TIMESTAMP,
    created_by INTEGER NOT NULL REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Invoices table
CREATE TABLE invoice (
    id SERIAL PRIMARY KEY,
    work_id INTEGER NOT NULL REFERENCES work(id),
    quotation_id INTEGER REFERENCES quotation(id),
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    issued_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE,
    payment_status_id INTEGER NOT NULL REFERENCES payment_status(id),
    created_by INTEGER NOT NULL REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payments table
CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    invoice_id INTEGER NOT NULL REFERENCES invoice(id),
    amount DECIMAL(10,2) NOT NULL,
    payment_method_id INTEGER NOT NULL REFERENCES payment_method(id),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reference_number VARCHAR(100),
    notes TEXT,
    received_by INTEGER NOT NULL REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Purchase Orders
CREATE TABLE purchase_order (
    id SERIAL PRIMARY KEY,
    supplier_id INTEGER NOT NULL REFERENCES supplier(id),
    total_amount DECIMAL(10,2) NOT NULL,
    order_date DATE NOT NULL DEFAULT CURRENT_DATE,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    purchase_order_status_id INTEGER NOT NULL REFERENCES purchase_order_status(id),
    created_by INTEGER NOT NULL REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Purchase order items
CREATE TABLE purchase_order_item (
    id SERIAL PRIMARY KEY,
    purchase_order_id INTEGER NOT NULL REFERENCES purchase_order(id) ON DELETE CASCADE,
    part_id INTEGER NOT NULL REFERENCES part(id),
    quantity_ordered INTEGER NOT NULL,
    quantity_received INTEGER DEFAULT 0,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(purchase_order_id, part_id)
);

-- Stock movement
CREATE TABLE stock_movement (
    id SERIAL PRIMARY KEY,
    part_id INTEGER NOT NULL REFERENCES part(id),
    movement_type_id INTEGER NOT NULL REFERENCES movement_type(id),
    quantity INTEGER NOT NULL,
    reference_type_id INTEGER REFERENCES reference_type(id),
    reference_id INTEGER,
    notes TEXT,
    created_by INTEGER NOT NULL REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Client feedback table
CREATE TABLE client_feedback (
    id SERIAL PRIMARY KEY,
    work_id INTEGER NOT NULL REFERENCES work(id),
    client_cui VARCHAR(13) NOT NULL REFERENCES person(cui),
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    comments TEXT,
    would_recommend BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(work_id, client_cui)
);

-- =============================================================================
-- INDEXES FOR PERFORMANCE
-- =============================================================================

-- Users table indexes
CREATE INDEX idx_user_person_cui ON "user"(person_cui);
CREATE INDEX idx_user_user_type ON "user"(user_type_id);
CREATE INDEX idx_user_username ON "user"(username);

-- Index for password reset tokens
CREATE INDEX idx_password_reset_token_token ON password_reset_token(token);
CREATE INDEX idx_password_reset_token_user ON password_reset_token(user_id);
CREATE INDEX idx_password_reset_token_expires ON password_reset_token(expires_at);

-- Vehicles table indexes
CREATE INDEX idx_vehicle_owner ON vehicle(owner_cui);
CREATE INDEX idx_vehicle_model ON vehicle(model_id);
CREATE INDEX idx_vehicle_license_plate ON vehicle(license_plate);

-- Works table indexes
CREATE INDEX idx_work_vehicle ON work(vehicle_id);
CREATE INDEX idx_work_assigned_employee ON work(assigned_employee_id);
CREATE INDEX idx_work_status ON work(work_status_id);
CREATE INDEX idx_work_service_type ON work(service_type_id);
CREATE INDEX idx_work_created_at ON work(created_at);

-- Work parts indexes
CREATE INDEX idx_work_part_work ON work_part(work_id);
CREATE INDEX idx_work_part_part ON work_part(part_id);

-- Inventory indexes
CREATE INDEX idx_inventory_stock_part ON inventory_stock(part_id);
CREATE INDEX idx_part_category ON part(category_id);

-- Invoice and payment indexes
CREATE INDEX idx_invoice_work ON invoice(work_id);
CREATE INDEX idx_invoice_status ON invoice(payment_status_id);
CREATE INDEX idx_payment_invoice ON payment(invoice_id);

-- Stock movements indexes
CREATE INDEX idx_stock_movement_part ON stock_movement(part_id);
CREATE INDEX idx_stock_movement_type ON stock_movement(movement_type_id);
CREATE INDEX idx_stock_movement_created_at ON stock_movement(created_at);

-- =============================================================================
-- Triggers
-- =============================================================================

-- Trigger to update stock when parts are used
CREATE OR REPLACE FUNCTION update_stock_on_part_usage()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'UPDATE' AND NEW.quantity_used > OLD.quantity_used THEN
        -- Update inventory stock
        UPDATE inventory_stock 
        SET quantity_available = quantity_available - (NEW.quantity_used - OLD.quantity_used),
            updated_at = CURRENT_TIMESTAMP
        WHERE part_id = NEW.part_id;
        
        -- Create stock movement record
        INSERT INTO stock_movement (part_id, movement_type_id, quantity, reference_type_id, reference_id, created_by)
        SELECT NEW.part_id, mt.id, -(NEW.quantity_used - OLD.quantity_used), rt.id, NEW.work_id, NEW.requested_by
        FROM movement_type mt, reference_type rt
        WHERE mt.name = 'Salida' AND rt.name = 'Trabajo';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_stock_on_part_usage
    AFTER UPDATE ON work_part
    FOR EACH ROW
    EXECUTE FUNCTION update_stock_on_part_usage();

-- Trigger to update work status when assigned
CREATE OR REPLACE FUNCTION update_work_status_on_assignment()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.assigned_employee_id IS NOT NULL AND OLD.assigned_employee_id IS NULL THEN
        UPDATE work 
        SET work_status_id = (SELECT id FROM work_status WHERE name = 'ASSIGNED'),
            updated_at = CURRENT_TIMESTAMP
        WHERE id = NEW.id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_work_status_on_assignment
    AFTER UPDATE ON work
    FOR EACH ROW
    EXECUTE FUNCTION update_work_status_on_assignment();

-- Trigger to update payment status based on payments
CREATE OR REPLACE FUNCTION update_payment_status()
RETURNS TRIGGER AS $$
DECLARE
    total_paid DECIMAL(10,2);
    invoice_total DECIMAL(10,2);
BEGIN
    -- Calculate total payments for this invoice
    SELECT COALESCE(SUM(amount), 0) INTO total_paid
    FROM payment 
    WHERE invoice_id = NEW.invoice_id;
    
    -- Get invoice total
    SELECT total_amount INTO invoice_total
    FROM invoice 
    WHERE id = NEW.invoice_id;
    
    -- Update payment status
    IF total_paid >= invoice_total THEN
        UPDATE invoice 
        SET payment_status_id = (SELECT id FROM payment_status WHERE name = 'PAID'),
            updated_at = CURRENT_TIMESTAMP
        WHERE id = NEW.invoice_id;
    ELSIF total_paid > 0 THEN
        UPDATE invoice 
        SET payment_status_id = (SELECT id FROM payment_status WHERE name = 'PARTIAL'),
            updated_at = CURRENT_TIMESTAMP
        WHERE id = NEW.invoice_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_payment_status
    AFTER INSERT ON payment
    FOR EACH ROW
    EXECUTE FUNCTION update_payment_status();

-- Trigger to automatically create inventory stock record when new part is added
CREATE OR REPLACE FUNCTION create_inventory_stock()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO inventory_stock (part_id, quantity_available, quantity_reserved)
    VALUES (NEW.id, 0, 0);
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_create_inventory_stock
    AFTER INSERT ON part
    FOR EACH ROW
    EXECUTE FUNCTION create_inventory_stock();

-- Trigger to update timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update inventory stock when stock movements are inserted
CREATE OR REPLACE FUNCTION update_inventory_from_stock_movement()
RETURNS TRIGGER AS $$
DECLARE
    movement_name VARCHAR(100);
BEGIN
    -- Get movement type name
    SELECT name INTO movement_name 
    FROM movement_type 
    WHERE id = NEW.movement_type_id;
    
    -- Update inventory stock based on movement type
    IF movement_name = 'Entrada' THEN
        -- Add quantity for entries
        UPDATE inventory_stock 
        SET quantity_available = quantity_available + NEW.quantity,
            updated_at = CURRENT_TIMESTAMP
        WHERE part_id = NEW.part_id;
        
    ELSIF movement_name = 'Salida' THEN
        -- Subtract quantity for exits
        UPDATE inventory_stock 
        SET quantity_available = quantity_available - NEW.quantity,
            updated_at = CURRENT_TIMESTAMP
        WHERE part_id = NEW.part_id;
        
    ELSIF movement_name = 'Ajuste' THEN
        -- Apply adjustment (can be positive or negative)
        UPDATE inventory_stock 
        SET quantity_available = quantity_available + NEW.quantity,
            updated_at = CURRENT_TIMESTAMP
        WHERE part_id = NEW.part_id;
        
    ELSIF movement_name = 'Devolución' THEN
        -- Add returned quantity back to stock
        UPDATE inventory_stock 
        SET quantity_available = quantity_available + NEW.quantity,
            updated_at = CURRENT_TIMESTAMP
        WHERE part_id = NEW.part_id;
        
    ELSIF movement_name = 'Merma' THEN
        -- Subtract lost/damaged quantity
        UPDATE inventory_stock 
        SET quantity_available = quantity_available - NEW.quantity,
            updated_at = CURRENT_TIMESTAMP
        WHERE part_id = NEW.part_id;
        
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_inventory_from_stock_movement
    AFTER INSERT ON stock_movement
    FOR EACH ROW
    EXECUTE FUNCTION update_inventory_from_stock_movement();

-- Trigger to update stock when purchase order items are received
CREATE OR REPLACE FUNCTION update_stock_on_purchase_receipt()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.quantity_received > OLD.quantity_received THEN
        -- Update inventory stock
        UPDATE inventory_stock 
        SET quantity_available = quantity_available + (NEW.quantity_received - OLD.quantity_received),
            last_restocked = CURRENT_TIMESTAMP,
            updated_at = CURRENT_TIMESTAMP
        WHERE part_id = NEW.part_id;
        
        -- Create stock movement record
        INSERT INTO stock_movement (part_id, movement_type_id, quantity, reference_type_id, reference_id, created_by)
        SELECT NEW.part_id, mt.id, (NEW.quantity_received - OLD.quantity_received), rt.id, NEW.purchase_order_id, 1
        FROM movement_type mt, reference_type rt
        WHERE mt.name = 'Entrada' AND rt.name = 'Orden de compra';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_stock_on_purchase_receipt
    AFTER UPDATE ON purchase_order_item
    FOR EACH ROW
    EXECUTE FUNCTION update_stock_on_purchase_receipt();

-- Trigger to update purchase order status when all items are received
CREATE OR REPLACE FUNCTION check_purchase_order_completion()
RETURNS TRIGGER AS $$
DECLARE
    total_items INTEGER;
    completed_items INTEGER;
BEGIN
    -- Count total items and completed items for this purchase order
    SELECT COUNT(*) INTO total_items 
    FROM purchase_order_item 
    WHERE purchase_order_id = NEW.purchase_order_id;
    
    SELECT COUNT(*) INTO completed_items 
    FROM purchase_order_item 
    WHERE purchase_order_id = NEW.purchase_order_id 
    AND quantity_received >= quantity_ordered;
    
    -- If all items are received, mark order as delivered
    IF total_items = completed_items THEN
        UPDATE purchase_order 
        SET purchase_order_status_id = (SELECT id FROM purchase_order_status WHERE name = 'Entregada'),
            actual_delivery_date = CURRENT_DATE,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = NEW.purchase_order_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_purchase_order_completion
    AFTER UPDATE ON purchase_order_item
    FOR EACH ROW
    EXECUTE FUNCTION check_purchase_order_completion();

-- Trigger to handle failed login attempts and account locking
CREATE OR REPLACE FUNCTION handle_failed_login_attempts()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.failed_login_attempts >= 5 THEN
        NEW.locked_until = CURRENT_TIMESTAMP + INTERVAL '30 minutes';
        NEW.is_active = FALSE;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_handle_failed_login_attempts
    BEFORE UPDATE ON "user"
    FOR EACH ROW
    WHEN (NEW.failed_login_attempts > OLD.failed_login_attempts)
    EXECUTE FUNCTION handle_failed_login_attempts();

-- Trigger to clean expired password reset tokens
CREATE OR REPLACE FUNCTION cleanup_expired_tokens()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM password_reset_token 
    WHERE expires_at < CURRENT_TIMESTAMP 
    AND used = FALSE;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cleanup_expired_tokens
    AFTER INSERT ON password_reset_token
    FOR EACH ROW
    EXECUTE FUNCTION cleanup_expired_tokens();

-- Trigger to generate invoice when work is completed
CREATE OR REPLACE FUNCTION generate_invoice_on_work_completion()
RETURNS TRIGGER AS $$
DECLARE
    total_labor DECIMAL(10,2);
    total_parts DECIMAL(10,2);
    total_amount DECIMAL(10,2);
BEGIN
    IF NEW.work_status_id = (SELECT id FROM work_status WHERE name = 'Completado') 
       AND OLD.work_status_id != (SELECT id FROM work_status WHERE name = 'Completado') THEN
        
        -- Calculate total parts cost
        SELECT COALESCE(SUM(wp.quantity_used * p.unit_price), 0) INTO total_parts
        FROM work_part wp
        JOIN part p ON wp.part_id = p.id
        WHERE wp.work_id = NEW.id;
        
        -- Use labor_cost from work table
        total_labor := COALESCE(NEW.labor_cost, 0);
        total_amount := total_labor + total_parts;
        
        -- Generate invoice
        INSERT INTO invoice (work_id, payment_status_id, subtotal, tax_amount, total_amount, issue_date, due_date)
        VALUES (
            NEW.id,
            (SELECT id FROM payment_status WHERE name = 'Pendiente'),
            total_amount,
            total_amount * 0.12, -- 12% IVA
            total_amount * 1.12,
            CURRENT_DATE,
            CURRENT_DATE + INTERVAL '30 days'
        );
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_generate_invoice_on_work_completion
    AFTER UPDATE ON work
    FOR EACH ROW
    EXECUTE FUNCTION generate_invoice_on_work_completion();

-- Trigger to reserve stock when parts are assigned to work
CREATE OR REPLACE FUNCTION reserve_stock_on_part_assignment()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR (TG_OP = 'UPDATE' AND NEW.quantity_required > OLD.quantity_required) THEN
        DECLARE
            quantity_to_reserve INTEGER;
        BEGIN
            quantity_to_reserve := CASE 
                WHEN TG_OP = 'INSERT' THEN NEW.quantity_required
                ELSE NEW.quantity_required - OLD.quantity_required
            END;
            
            -- Check if sufficient stock available
            IF (SELECT quantity_available - quantity_reserved 
                FROM inventory_stock 
                WHERE part_id = NEW.part_id) < quantity_to_reserve THEN
                RAISE EXCEPTION 'Insufficient stock available for part ID: %', NEW.part_id;
            END IF;
            
            -- Reserve the stock
            UPDATE inventory_stock 
            SET quantity_reserved = quantity_reserved + quantity_to_reserve,
                updated_at = CURRENT_TIMESTAMP
            WHERE part_id = NEW.part_id;
        END;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reserve_stock_on_part_assignment
    AFTER INSERT OR UPDATE ON work_part
    FOR EACH ROW
    EXECUTE FUNCTION reserve_stock_on_part_assignment();

-- Trigger to release reserved stock when work is completed or cancelled
CREATE OR REPLACE FUNCTION release_reserved_stock()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.work_status_id IN (
        SELECT id FROM work_status WHERE name IN ('Completado', 'Cancelado', 'Finalizado sin ejecución')
    ) AND OLD.work_status_id NOT IN (
        SELECT id FROM work_status WHERE name IN ('Completado', 'Cancelado', 'Finalizado sin ejecución')
    ) THEN
        -- Release reserved stock for unused parts
        UPDATE inventory_stock 
        SET quantity_reserved = quantity_reserved - wp.quantity_required + wp.quantity_used,
            updated_at = CURRENT_TIMESTAMP
        FROM work_part wp
        WHERE inventory_stock.part_id = wp.part_id 
        AND wp.work_id = NEW.id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_release_reserved_stock
    AFTER UPDATE ON work
    FOR EACH ROW
    EXECUTE FUNCTION release_reserved_stock();

-- Trigger to create low stock alerts
CREATE OR REPLACE FUNCTION check_low_stock_alert()
RETURNS TRIGGER AS $$
DECLARE
    part_minimum INTEGER;
    part_name VARCHAR(200);
BEGIN
    -- Get minimum stock and part name
    SELECT p.minimum_stock, p.name INTO part_minimum, part_name
    FROM part p 
    WHERE p.id = NEW.part_id;
    
    -- Check if stock is below minimum (you'd need to create a stock_alert table)
    IF NEW.quantity_available <= part_minimum THEN
        -- This would require a stock_alert table or notification system
        RAISE NOTICE 'Low stock alert: Part "%" (ID: %) has % units, minimum is %', 
                     part_name, NEW.part_id, NEW.quantity_available, part_minimum;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_low_stock_alert
    AFTER UPDATE ON inventory_stock
    FOR EACH ROW
    WHEN (NEW.quantity_available != OLD.quantity_available)
    EXECUTE FUNCTION check_low_stock_alert();

-- Apply to tables with updated_at column
CREATE TRIGGER trg_person_updated_at BEFORE UPDATE ON person FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_user_updated_at BEFORE UPDATE ON "user" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_work_updated_at BEFORE UPDATE ON work FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_vehicle_updated_at BEFORE UPDATE ON vehicle FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add missing updated_at triggers
CREATE TRIGGER trg_inventory_stock_updated_at BEFORE UPDATE ON inventory_stock FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_part_updated_at BEFORE UPDATE ON part FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_purchase_order_updated_at BEFORE UPDATE ON purchase_order FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_invoice_updated_at BEFORE UPDATE ON invoice FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();