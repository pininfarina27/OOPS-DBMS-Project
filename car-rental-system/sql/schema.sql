-- ============================================================
-- Car Rental Management System — Database Schema
-- DBMS: MySQL 8.0
-- ============================================================

-- Create the database
CREATE DATABASE IF NOT EXISTS car_rental_db;
USE car_rental_db;

-- Drop tables in correct order (child tables first because of foreign keys)
-- DBMS: DROP TABLE — removing tables if they already exist for clean setup
DROP TABLE IF EXISTS rentals;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS car_categories;
DROP TABLE IF EXISTS customers;

-- ============================================================
-- Table 1: car_categories
-- DBMS: PRIMARY KEY — category_id is the unique identifier
-- DBMS: AUTO_INCREMENT — MySQL automatically generates the ID
-- DBMS: UNIQUE — category_name cannot be duplicated
-- ============================================================
CREATE TABLE car_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- ============================================================
-- Table 2: cars
-- DBMS: PRIMARY KEY — car_id is the unique identifier
-- DBMS: FOREIGN KEY — category_id references car_categories table
-- DBMS: ON DELETE SET NULL — if a category is deleted, car keeps its data but category becomes NULL
-- DBMS: ON UPDATE CASCADE — if category_id changes in parent, it updates here too
-- DBMS: ENUM — status can only be one of three fixed values
-- DBMS: UNIQUE — license_plate must be unique for each car
-- DBMS: DEFAULT — status defaults to 'AVAILABLE', date_added defaults to today
-- ============================================================
CREATE TABLE cars (
    car_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    color VARCHAR(30),
    daily_rate DECIMAL(10, 2) NOT NULL,
    status ENUM('AVAILABLE', 'RENTED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    date_added DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (category_id) REFERENCES car_categories(category_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ============================================================
-- Table 3: customers
-- DBMS: PRIMARY KEY — customer_id is the unique identifier
-- DBMS: UNIQUE — email must be unique for each customer
-- DBMS: DEFAULT — registration_date defaults to today
-- ============================================================
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15) NOT NULL,
    address VARCHAR(200),
    registration_date DATE DEFAULT (CURRENT_DATE)
);

-- ============================================================
-- Table 4: rentals
-- DBMS: PRIMARY KEY — rental_id is the unique identifier
-- DBMS: FOREIGN KEY — car_id references cars table
-- DBMS: FOREIGN KEY — customer_id references customers table
-- DBMS: ON DELETE RESTRICT — cannot delete a car or customer that has rental records
-- DBMS: ENUM — status can only be ACTIVE, COMPLETED, or CANCELLED
-- ============================================================
CREATE TABLE rentals (
    rental_id INT AUTO_INCREMENT PRIMARY KEY,
    car_id INT NOT NULL,
    customer_id INT NOT NULL,
    rental_date DATE NOT NULL,
    return_date DATE NOT NULL,
    actual_return_date DATE,
    total_cost DECIMAL(10, 2),
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
    FOREIGN KEY (car_id) REFERENCES cars(car_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Schema creation complete!
