-- VIOLATION: Database Dependency - Using H2 with Oracle mode
-- This demonstrates tight coupling to database-specific features

-- Initialize Lab Management Database

-- Patients table
CREATE TABLE patients (
    patient_id LONG PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    medical_record_number VARCHAR(50),  -- VIOLATION: No index despite frequent searches
    date_of_birth DATE,
    registration_date TIMESTAMP,
    gender VARCHAR(10)  -- VIOLATION: String instead of ENUM
);

-- Specimens table
CREATE TABLE specimens (
    specimen_id LONG PRIMARY KEY AUTO_INCREMENT,
    specimen_type VARCHAR(100),
    collection_location VARCHAR(100),
    collection_date_time TIMESTAMP,
    specimen_image LONGBLOB,  -- VIOLATION: Database Dependency - BLOB storage
    local_storage_path VARCHAR(500),  -- VIOLATION: Software Portability - Windows path
    status VARCHAR(50),  -- VIOLATION: String instead of ENUM
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
);

-- Lab Equipment table
CREATE TABLE lab_equipment (
    equipment_id LONG PRIMARY KEY AUTO_INCREMENT,
    equipment_name VARCHAR(200),
    equipment_type VARCHAR(100),
    ip_address VARCHAR(15),  -- VIOLATION: Cloud Compatibility - Hardcoded IPs
    port_number INTEGER,
    calibration_data CLOB,  -- VIOLATION: Database Dependency - CLOB storage
    config_file_path VARCHAR(500),  -- VIOLATION: Software Portability - Windows path
    last_calibration_date TIMESTAMP,
    status VARCHAR(50)  -- VIOLATION: String instead of ENUM
);

-- Lab Tests table
CREATE TABLE lab_tests (
    test_id LONG PRIMARY KEY AUTO_INCREMENT,
    test_name VARCHAR(200),  -- VIOLATION: Code Sustainability - No index
    test_code VARCHAR(50),
    description VARCHAR(1000),
    specimen_id LONG,
    created_date TIMESTAMP,
    status VARCHAR(50),  -- VIOLATION: String instead of ENUM
    FOREIGN KEY (specimen_id) REFERENCES specimens(specimen_id)
);

-- Lab Results table
CREATE TABLE lab_results (
    result_id LONG PRIMARY KEY AUTO_INCREMENT,
    test_id LONG,
    result_value DOUBLE,
    unit VARCHAR(50),
    reference_range VARCHAR(100),
    analyzed_date_time TIMESTAMP,
    analysis_status VARCHAR(50),  -- VIOLATION: String instead of ENUM
    analyzed_by VARCHAR(100),
    approved_date_time TIMESTAMP,
    FOREIGN KEY (test_id) REFERENCES lab_tests(test_id)
);

-- Test Methods table
CREATE TABLE test_methods (
    method_id LONG PRIMARY KEY AUTO_INCREMENT,
    method_code VARCHAR(50),  -- VIOLATION: Code Sustainability - No index
    method_name VARCHAR(200),  -- VIOLATION: Code Sustainability - No index
    description VARCHAR(1000),
    reference_range VARCHAR(100),
    sample_requirement VARCHAR(500),
    analysis_type VARCHAR(100),  -- VIOLATION: String instead of ENUM
    created_date TIMESTAMP
);

-- Reagents table
CREATE TABLE reagents (
    reagent_id LONG PRIMARY KEY AUTO_INCREMENT,
    reagent_name VARCHAR(200),  -- VIOLATION: Code Sustainability - No index
    reagent_code VARCHAR(50),  -- VIOLATION: Code Sustainability - No index
    manufacturer VARCHAR(200),
    lot_number VARCHAR(100),
    expiration_date DATE,
    quantity_on_hand INTEGER,
    status VARCHAR(50)  -- VIOLATION: String instead of ENUM
);

-- Lab Technicians table
CREATE TABLE lab_technicians (
    technician_id LONG PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    employee_id VARCHAR(50),
    email VARCHAR(100),
    hire_date TIMESTAMP
);

-- Quality Checks table
CREATE TABLE quality_checks (
    check_id LONG PRIMARY KEY AUTO_INCREMENT,
    technician_id LONG,
    check_date_time TIMESTAMP,
    check_type VARCHAR(100),  -- VIOLATION: String instead of ENUM
    pass_percentage DOUBLE,
    status VARCHAR(50),  -- VIOLATION: String instead of ENUM
    comments VARCHAR(1000),
    FOREIGN KEY (technician_id) REFERENCES lab_technicians(technician_id)
);

-- Equipment Readings table
CREATE TABLE equipment_readings (
    reading_id LONG PRIMARY KEY AUTO_INCREMENT,
    equipment_id LONG,
    reading_date_time TIMESTAMP,
    reading_value DOUBLE,
    reading_unit VARCHAR(50),
    FOREIGN KEY (equipment_id) REFERENCES lab_equipment(equipment_id)
);

-- Audit Logs table
CREATE TABLE audit_logs (
    log_id LONG PRIMARY KEY AUTO_INCREMENT,
    action VARCHAR(100),  -- VIOLATION: Code Sustainability - No index despite frequent searches
    entity_name VARCHAR(100),  -- VIOLATION: Code Sustainability - No index
    entity_id VARCHAR(100),
    timestamp TIMESTAMP,
    performed_by VARCHAR(100),
    details VARCHAR(1000)
);

-- VIOLATION: Database Dependency - Sample data with hardcoded values
INSERT INTO patients VALUES (1, 'John', 'Doe', 'MRN001', '1990-01-15', CURRENT_TIMESTAMP, 'M');
INSERT INTO patients VALUES (2, 'Jane', 'Smith', 'MRN002', '1985-05-20', CURRENT_TIMESTAMP, 'F');
INSERT INTO patients VALUES (3, 'Robert', 'Johnson', 'MRN003', '1975-12-10', CURRENT_TIMESTAMP, 'M');

INSERT INTO lab_equipment VALUES (1, 'Hematology Analyzer', 'Analyzer', '192.168.1.100', 8888, 'CALIBRATION_V1.0', 'C:\\lab\\equipment\\hematology.xml', CURRENT_TIMESTAMP, 'Active');
INSERT INTO lab_equipment VALUES (2, 'Chemistry Analyzer', 'Analyzer', '192.168.1.101', 8889, 'CALIBRATION_V1.5', 'C:\\lab\\equipment\\chemistry.xml', CURRENT_TIMESTAMP, 'Active');

INSERT INTO test_methods VALUES (1, 'CBC', 'Complete Blood Count', 'Full blood count analysis', '4.5-11.0 (WBC)', 'Whole blood EDTA', 'Quantitative', CURRENT_TIMESTAMP);
INSERT INTO test_methods VALUES (2, 'BMP', 'Basic Metabolic Panel', 'Metabolic panel analysis', 'Normal ranges vary', 'Serum or plasma', 'Quantitative', CURRENT_TIMESTAMP);

INSERT INTO reagents VALUES (1, 'EDTA Tube', 'REG_EDTA_001', 'BD Corporation', 'LOT123456', '2025-12-31', 500, 'Active');
INSERT INTO reagents VALUES (2, 'Reagent A', 'REG_A_002', 'Siemens Healthcare', 'LOT654321', '2024-06-30', 100, 'Active');

INSERT INTO lab_technicians VALUES (1, 'Alice', 'Wilson', 'EMP001', 'alice@labcorp.com', '2020-01-15');
INSERT INTO lab_technicians VALUES (2, 'Bob', 'Martinez', 'EMP002', 'bob@labcorp.com', '2019-06-01');

INSERT INTO audit_logs VALUES (1, 'CREATE', 'PATIENT', '1', CURRENT_TIMESTAMP, 'SYSTEM', 'Patient John Doe registered');
INSERT INTO audit_logs VALUES (2, 'CREATE', 'SPECIMEN', '1', CURRENT_TIMESTAMP, 'EMP001', 'Blood specimen collected');
