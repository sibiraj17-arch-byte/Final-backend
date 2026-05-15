CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    mobile_number VARCHAR(10) NOT NULL,
    email VARCHAR(255) NULL,
    role VARCHAR(20) NOT NULL,
    is_active BIT(1) NULL DEFAULT b'1',
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_mobile_number UNIQUE (mobile_number),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS specializations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS patients (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    gender VARCHAR(20) NULL,
    date_of_birth DATE NULL,
    address VARCHAR(500) NULL,
    blood_group VARCHAR(50) NULL,
    allergies VARCHAR(200) NULL,
    emergency_contact VARCHAR(500) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_patients_user_id UNIQUE (user_id),
    CONSTRAINT fk_patients_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    specialization_id BIGINT NULL,
    qualification VARCHAR(200) NOT NULL,
    experience VARCHAR(2000) NULL,
    bio VARCHAR(500) NULL,
    availability_status VARCHAR(20) NOT NULL,
    experience_years INT NULL,
    consultation_fee DOUBLE NULL,
    is_verified BIT(1) NULL DEFAULT b'0',
    total_experience VARCHAR(100) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_doctors_user_id UNIQUE (user_id),
    CONSTRAINT fk_doctors_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_doctors_specialization FOREIGN KEY (specialization_id) REFERENCES specializations (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS medical_histories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    condition_text VARCHAR(300) NOT NULL,
    diagnosis VARCHAR(1000) NULL,
    diagnosis_date DATE NULL,
    status VARCHAR(255) NULL,
    notes VARCHAR(2000) NULL,
    treatment VARCHAR(200) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_medical_histories_patient FOREIGN KEY (patient_id) REFERENCES patients (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NULL,
    start_time TIME(6) NULL,
    end_time TIME(6) NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(1000) NULL,
    notes VARCHAR(1000) NULL,
    symptoms VARCHAR(500) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_appointments_code UNIQUE (code),
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS availability_slots (
    id BIGINT NOT NULL AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    slot_date DATE NOT NULL,
    start_time TIME(6) NOT NULL,
    end_time TIME(6) NOT NULL,
    is_available BIT(1) NULL DEFAULT b'1',
    created_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_availability_slots_doctor_date_start UNIQUE (doctor_id, slot_date, start_time),
    CONSTRAINT fk_availability_slots_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message VARCHAR(2000) NULL,
    type VARCHAR(20) NOT NULL,
    is_read BIT(1) NULL DEFAULT b'0',
    created_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_id BIGINT NULL,
    diagnosis VARCHAR(1000) NOT NULL,
    instructions VARCHAR(2000) NULL,
    follow_up VARCHAR(500) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_prescriptions_appointment_id UNIQUE (appointment_id),
    CONSTRAINT fk_prescriptions_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_prescriptions_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_prescriptions_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS prescription_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    prescription_id BIGINT NOT NULL,
    medicine_name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NULL,
    frequency VARCHAR(200) NULL,
    duration VARCHAR(100) NULL,
    notes VARCHAR(500) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_prescription_items_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_id BIGINT NULL,
    rating INT NULL,
    comment VARCHAR(2000) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_reviews_appointment_id UNIQUE (appointment_id),
    CONSTRAINT fk_reviews_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_reviews_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_reviews_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    appointment_id BIGINT NULL,
    patient_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_id VARCHAR(255) NULL,
    payment_method VARCHAR(255) NULL,
    razorpay_order_id VARCHAR(255) NULL,
    razorpay_payment_id VARCHAR(255) NULL,
    razorpay_signature VARCHAR(255) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_payments_appointment_id UNIQUE (appointment_id),
    CONSTRAINT fk_payments_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id),
    CONSTRAINT fk_payments_patient FOREIGN KEY (patient_id) REFERENCES patients (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS medical_documents (
    id BIGINT NOT NULL AUTO_INCREMENT,
    medical_history_id BIGINT NOT NULL,
    document_name VARCHAR(300) NOT NULL,
    document_type VARCHAR(100) NULL,
    file_url VARCHAR(500) NULL,
    description VARCHAR(1000) NULL,
    created_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_medical_documents_history FOREIGN KEY (medical_history_id) REFERENCES medical_histories (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS medical_reports (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_id BIGINT NULL,
    report_type VARCHAR(200) NOT NULL,
    title VARCHAR(300) NOT NULL,
    description TEXT NULL,
    findings TEXT NULL,
    recommendations VARCHAR(2000) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_medical_reports_appointment_id UNIQUE (appointment_id),
    CONSTRAINT fk_medical_reports_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_medical_reports_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_medical_reports_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB;

CREATE INDEX idx_doctors_specialization_id ON doctors (specialization_id);
CREATE INDEX idx_medical_histories_patient_id ON medical_histories (patient_id);
CREATE INDEX idx_appointments_patient_id ON appointments (patient_id);
CREATE INDEX idx_appointments_doctor_id ON appointments (doctor_id);
CREATE INDEX idx_appointments_appointment_date ON appointments (appointment_date);
CREATE INDEX idx_notifications_user_id ON notifications (user_id);
CREATE INDEX idx_prescriptions_patient_id ON prescriptions (patient_id);
CREATE INDEX idx_prescriptions_doctor_id ON prescriptions (doctor_id);
CREATE INDEX idx_prescription_items_prescription_id ON prescription_items (prescription_id);
CREATE INDEX idx_reviews_patient_id ON reviews (patient_id);
CREATE INDEX idx_reviews_doctor_id ON reviews (doctor_id);
CREATE INDEX idx_payments_patient_id ON payments (patient_id);
CREATE INDEX idx_medical_documents_history_id ON medical_documents (medical_history_id);
CREATE INDEX idx_medical_reports_patient_id ON medical_reports (patient_id);
CREATE INDEX idx_medical_reports_doctor_id ON medical_reports (doctor_id);
