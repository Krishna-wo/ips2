-- ============================================================
-- NetPulse ISP Portal — MySQL Schema & Sample Queries
-- Database: netpulse_db
-- ============================================================

-- ─── CREATE / USE DATABASE ──────────────────────────────────
CREATE DATABASE IF NOT EXISTS netpulse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE netpulse_db;

-- ============================================================
-- DDL — Table Definitions
-- ============================================================

-- Plans (ISP service packages)
CREATE TABLE IF NOT EXISTS plans (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(100)   NOT NULL,
    download_speed_mbps INT            NOT NULL DEFAULT 0,
    upload_speed_mbps   INT            NOT NULL DEFAULT 0,
    data_cap_gb         INT            NOT NULL DEFAULT 0   COMMENT '0 = unlimited',
    monthly_price       DECIMAL(10,2)  NOT NULL,
    description         VARCHAR(255),
    active              TINYINT(1)     NOT NULL DEFAULT 1,
    INDEX idx_plans_active (active)
) ENGINE=InnoDB;

-- Users (authentication accounts)
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(150) NOT NULL UNIQUE,
    phone       VARCHAR(15)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('CUSTOMER','ADMIN','TECHNICIAN') NOT NULL DEFAULT 'CUSTOMER',
    status      ENUM('ACTIVE','SUSPENDED','TERMINATED') NOT NULL DEFAULT 'ACTIVE',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login  DATETIME,
    INDEX idx_users_email (email),
    INDEX idx_users_phone (phone)
) ENGINE=InnoDB;

-- Customers (profile + plan assignment)
CREATE TABLE IF NOT EXISTS customers (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL UNIQUE,
    account_id      VARCHAR(50)  NOT NULL UNIQUE,
    first_name      VARCHAR(80)  NOT NULL,
    last_name       VARCHAR(80),
    address         VARCHAR(255),
    city            VARCHAR(80),
    state           VARCHAR(80),
    pincode         VARCHAR(10),
    kyc_doc_type    VARCHAR(50),
    kyc_doc_number  VARCHAR(50),
    kyc_status      ENUM('PENDING','VERIFIED','REJECTED') NOT NULL DEFAULT 'PENDING',
    plan_id         BIGINT,
    join_date       DATE         NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_customers_plan FOREIGN KEY (plan_id) REFERENCES plans(id),
    INDEX idx_customers_account_id (account_id)
) ENGINE=InnoDB;

-- Invoices (billing records)
CREATE TABLE IF NOT EXISTS invoices (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number  VARCHAR(80)  NOT NULL UNIQUE,
    customer_id     BIGINT       NOT NULL,
    base_amount     DECIMAL(10,2) NOT NULL,
    gst_amount      DECIMAL(10,2) NOT NULL,
    total_amount    DECIMAL(10,2) NOT NULL,
    status          ENUM('PENDING','PAID','OVERDUE') NOT NULL DEFAULT 'PENDING',
    issue_date      DATE,
    due_date        DATE,
    paid_at         DATETIME,
    payment_method  VARCHAR(50),
    txn_id          VARCHAR(100),
    CONSTRAINT fk_invoices_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_invoices_customer (customer_id),
    INDEX idx_invoices_status   (status)
) ENGINE=InnoDB;

-- Support Tickets
CREATE TABLE IF NOT EXISTS support_tickets (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_number   VARCHAR(50)  NOT NULL UNIQUE,
    customer_id     BIGINT       NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    category        VARCHAR(80),
    status          ENUM('OPEN','IN_PROGRESS','CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at     DATETIME,
    CONSTRAINT fk_tickets_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_tickets_customer (customer_id),
    INDEX idx_tickets_status   (status)
) ENGINE=InnoDB;

-- Outage Reports
CREATE TABLE IF NOT EXISTS outage_reports (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT       NOT NULL,
    issue_type  VARCHAR(80),
    severity    ENUM('COMPLETE','PARTIAL','DEGRADED') NOT NULL,
    status      ENUM('REPORTED','INVESTIGATING','RESOLVED') NOT NULL DEFAULT 'REPORTED',
    description TEXT,
    reported_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME,
    CONSTRAINT fk_outages_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_outages_status (status)
) ENGINE=InnoDB;

-- Usage Records
CREATE TABLE IF NOT EXISTS usage_records (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT        NOT NULL,
    record_date     DATE          NOT NULL,
    download_mb     DECIMAL(12,2) NOT NULL DEFAULT 0,
    upload_mb       DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_usage_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_usage_customer_date (customer_id, record_date)
) ENGINE=InnoDB;

-- Schedule Requests
CREATE TABLE IF NOT EXISTS schedule_requests (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT       NOT NULL,
    service_type    VARCHAR(80),
    preferred_date  DATE,
    time_slot       VARCHAR(50),
    status          VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_schedule_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB;


-- ============================================================
-- SEED DATA — Plans
-- Run these INSERTs once during initial setup.
-- ============================================================

INSERT INTO plans (name, download_speed_mbps, upload_speed_mbps, data_cap_gb, monthly_price, description, active)
VALUES
    ('Basic 50',   50,  10, 200,  499.00, '50 Mbps download, 200 GB/month', 1),
    ('Standard 100', 100, 20, 500, 799.00, '100 Mbps download, 500 GB/month', 1),
    ('Pro 200',    200,  50,   0, 1199.00, '200 Mbps download, Unlimited data', 1),
    ('Ultra 500',  500, 100,   0, 1999.00, '500 Mbps download, Unlimited data', 1);


-- ============================================================
-- PREREQUISITE QUERIES — Check before registering a customer
-- ============================================================

-- 1. List all active plans (shown on registration form)
SELECT id, name, download_speed_mbps, upload_speed_mbps,
       data_cap_gb, monthly_price, description
FROM   plans
WHERE  active = 1
ORDER  BY monthly_price;

-- 2. Check if email already exists
SELECT COUNT(*) AS cnt FROM users WHERE email = 'customer@example.com';

-- 3. Check if phone already exists
SELECT COUNT(*) AS cnt FROM users WHERE phone = '9876543210';


-- ============================================================
-- TRANSACTIONAL DML — Customer Registration + Plan Assignment
-- Run inside a single transaction.
-- ============================================================

START TRANSACTION;

-- Step 1: Insert user account
INSERT INTO users (email, phone, password, role, status)
VALUES ('john.doe@example.com', '9876543210',
        '$2a$12$hashed_password_here', 'CUSTOMER', 'ACTIVE');

-- Step 2: Generate account ID and insert customer profile
SET @user_id = LAST_INSERT_ID();
SET @account_id = CONCAT('NP-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', @user_id);

INSERT INTO customers (user_id, account_id, first_name, last_name, address, plan_id, join_date)
VALUES (@user_id, @account_id, 'John', 'Doe', '123 Main Street', 2, CURDATE());

-- Step 3: Create initial invoice for the selected plan (plan_id = 2 = Standard 100)
SET @customer_id = LAST_INSERT_ID();
SET @base   = (SELECT monthly_price FROM plans WHERE id = 2);
SET @gst    = ROUND(@base * 0.18, 2);
SET @total  = @base + @gst;
SET @inv_no = CONCAT('INV-', @account_id, '-', DATE_FORMAT(NOW(), '%Y%m%d'));

INSERT INTO invoices (invoice_number, customer_id, base_amount, gst_amount, total_amount,
                      status, issue_date, due_date)
VALUES (@inv_no, @customer_id, @base, @gst, @total,
        'PENDING', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY));

COMMIT;


-- ============================================================
-- DML — Support Ticket Creation
-- ============================================================

-- Create a new support ticket
INSERT INTO support_tickets (ticket_number, customer_id, title, description, category, status)
VALUES (CONCAT('TK-', UNIX_TIMESTAMP(NOW())),
        @customer_id,
        'Slow internet speed',
        'My connection drops to under 10 Mbps during peak hours.',
        'SPEED',
        'OPEN');

-- Fetch all open tickets for a customer
SELECT t.id, t.ticket_number, t.title, t.category, t.status, t.created_at
FROM   support_tickets t
JOIN   customers c ON c.id = t.customer_id
WHERE  c.account_id = 'NP-20250101-1'
  AND  t.status != 'CLOSED'
ORDER  BY t.created_at DESC;

-- Update ticket status (technician resolves it)
UPDATE support_tickets
SET    status = 'CLOSED', resolved_at = NOW()
WHERE  ticket_number = 'TK-1234567890';


-- ============================================================
-- DML — Outage Reporting & Resolution
-- ============================================================

-- Customer reports an outage
INSERT INTO outage_reports (customer_id, issue_type, severity, description, status)
VALUES (@customer_id, 'NO_CONNECTION', 'COMPLETE',
        'No internet since 8 PM yesterday.', 'REPORTED');

-- Admin marks outage as under investigation
UPDATE outage_reports
SET    status = 'INVESTIGATING'
WHERE  id = 1;

-- Admin resolves the outage
UPDATE outage_reports
SET    status = 'RESOLVED', resolved_at = NOW()
WHERE  id = 1;

-- List all active (unresolved) outages
SELECT o.id, o.issue_type, o.severity, o.status, o.description,
       o.reported_at, c.account_id
FROM   outage_reports o
JOIN   customers c ON c.id = o.customer_id
WHERE  o.status != 'RESOLVED'
ORDER  BY o.reported_at DESC;


-- ============================================================
-- DML — Invoice Generation (monthly billing cycle)
-- Generate invoices for all active customers with a plan
-- ============================================================

INSERT INTO invoices (invoice_number, customer_id, base_amount, gst_amount, total_amount,
                      status, issue_date, due_date)
SELECT  CONCAT('INV-', c.account_id, '-', DATE_FORMAT(NOW(), '%Y%m')),
        c.id,
        p.monthly_price,
        ROUND(p.monthly_price * 0.18, 2),
        p.monthly_price + ROUND(p.monthly_price * 0.18, 2),
        'PENDING',
        CURDATE(),
        DATE_ADD(CURDATE(), INTERVAL 30 DAY)
FROM    customers c
JOIN    plans p ON p.id = c.plan_id
WHERE   c.plan_id IS NOT NULL;

-- Mark an invoice as paid
UPDATE invoices
SET    status = 'PAID',
       paid_at = NOW(),
       payment_method = 'UPI',
       txn_id = 'UPI-TXN-20250101-001'
WHERE  invoice_number = 'INV-NP-20250101-1-20250101';

-- List unpaid (pending) invoices older than due_date → mark OVERDUE
UPDATE invoices
SET    status = 'OVERDUE'
WHERE  status = 'PENDING'
  AND  due_date < CURDATE();


-- ============================================================
-- USEFUL REPORTING QUERIES
-- ============================================================

-- Customer full profile with plan and pending invoice
SELECT c.account_id, c.first_name, c.last_name,
       u.email, u.phone, u.status AS account_status,
       p.name AS plan_name, p.monthly_price,
       i.invoice_number, i.total_amount, i.status AS invoice_status, i.due_date
FROM   customers c
JOIN   users      u ON u.id = c.user_id
LEFT   JOIN plans p ON p.id = c.plan_id
LEFT   JOIN invoices i ON i.customer_id = c.id AND i.status = 'PENDING'
WHERE  c.account_id = 'NP-20250101-1';

-- Revenue summary by plan
SELECT p.name, COUNT(i.id) AS invoices_paid,
       SUM(i.total_amount) AS total_revenue
FROM   invoices i
JOIN   customers c ON c.id = i.customer_id
JOIN   plans     p ON p.id = c.plan_id
WHERE  i.status = 'PAID'
GROUP  BY p.name
ORDER  BY total_revenue DESC;
