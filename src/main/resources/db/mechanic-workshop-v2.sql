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
        WHERE mt.name = 'OUT' AND rt.name = 'WORK';
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

-- Apply to tables with updated_at column
CREATE TRIGGER trg_person_updated_at BEFORE UPDATE ON person FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_user_updated_at BEFORE UPDATE ON "user" FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_work_updated_at BEFORE UPDATE ON work FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_vehicle_updated_at BEFORE UPDATE ON vehicle FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();