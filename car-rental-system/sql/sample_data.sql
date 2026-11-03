-- ============================================================
-- Car Rental Management System — Sample Data
-- Realistic Indian data for testing and demonstration
-- ============================================================

USE car_rental_db;

-- ============================================================
-- DBMS: INSERT — adding records into car_categories table
-- 5 categories of vehicles
-- ============================================================
INSERT INTO car_categories (category_name, description) VALUES
('Hatchback', 'Small compact cars, great for city driving and fuel efficiency'),
('Sedan', 'Mid-size comfortable cars, ideal for families and long drives'),
('SUV', 'Sport Utility Vehicles, powerful and spacious for all terrains'),
('Luxury', 'Premium high-end cars with top-class features and comfort'),
('Convertible', 'Open-top sports cars for a thrilling driving experience');

-- ============================================================
-- DBMS: INSERT — adding records into cars table
-- 10 cars with Indian brands, realistic daily rates in INR
-- 8 cars AVAILABLE, 2 cars RENTED (matching active rentals below)
-- ============================================================
INSERT INTO cars (category_id, brand, model, year, license_plate, color, daily_rate, status) VALUES
(1, 'Maruti', 'Swift', 2023, 'MH-01-AB-1234', 'White', 800.00, 'AVAILABLE'),
(1, 'Hyundai', 'i20', 2024, 'MH-02-CD-5678', 'Red', 900.00, 'AVAILABLE'),
(3, 'Tata', 'Nexon', 2023, 'DL-03-EF-9012', 'Blue', 1200.00, 'AVAILABLE'),
(3, 'Mahindra', 'XUV700', 2024, 'DL-04-GH-3456', 'Black', 1800.00, 'RENTED'),
(2, 'Honda', 'City', 2023, 'KA-05-IJ-7890', 'Silver', 1500.00, 'AVAILABLE'),
(3, 'Toyota', 'Innova', 2022, 'KA-06-KL-2345', 'White', 2000.00, 'AVAILABLE'),
(2, 'Maruti', 'Dzire', 2024, 'TN-07-MN-6789', 'Grey', 1000.00, 'AVAILABLE'),
(3, 'Hyundai', 'Creta', 2024, 'TN-08-OP-1234', 'Blue', 1600.00, 'RENTED'),
(4, 'BMW', '3 Series', 2023, 'MH-09-QR-5678', 'Black', 4000.00, 'AVAILABLE'),
(4, 'Mercedes', 'C-Class', 2024, 'MH-10-ST-9012', 'White', 5000.00, 'MAINTENANCE');

-- ============================================================
-- DBMS: INSERT — adding records into customers table
-- 6 customers with realistic Indian names
-- ============================================================
INSERT INTO customers (first_name, last_name, email, phone, address, registration_date) VALUES
('Rahul', 'Sharma', 'rahul.sharma@gmail.com', '9876543210', '12, MG Road, Mumbai', '2026-08-15'),
('Priya', 'Patel', 'priya.patel@gmail.com', '9876543211', '45, Park Street, Delhi', '2026-08-20'),
('Amit', 'Singh', 'amit.singh@yahoo.com', '9876543212', '78, Brigade Road, Bangalore', '2026-09-01'),
('Sneha', 'Reddy', 'sneha.reddy@gmail.com', '9876543213', '23, Banjara Hills, Hyderabad', '2026-09-10'),
('Vikram', 'Joshi', 'vikram.joshi@outlook.com', '9876543214', '56, Anna Nagar, Chennai', '2026-09-25'),
('Ananya', 'Gupta', 'ananya.gupta@gmail.com', '9876543215', '89, Lajpat Nagar, Delhi', '2026-10-05');

-- ============================================================
-- DBMS: INSERT — adding records into rentals table
-- 5 rentals: 3 COMPLETED, 2 ACTIVE
-- COMPLETED rentals have actual_return_date and total_cost filled
-- ACTIVE rentals have actual_return_date and total_cost as NULL
-- The 2 ACTIVE rentals match the 2 RENTED cars (car_id 4 and 8)
-- ============================================================

-- Completed Rental 1: Rahul rented Maruti Swift for 3 days (800 x 3 = 2400)
INSERT INTO rentals (car_id, customer_id, rental_date, return_date, actual_return_date, total_cost, status) VALUES
(1, 1, '2026-09-01', '2026-09-04', '2026-09-04', 2400.00, 'COMPLETED');

-- Completed Rental 2: Priya rented Honda City for 5 days (1500 x 5 = 7500)
INSERT INTO rentals (car_id, customer_id, rental_date, return_date, actual_return_date, total_cost, status) VALUES
(5, 2, '2026-09-10', '2026-09-15', '2026-09-15', 7500.00, 'COMPLETED');

-- Completed Rental 3: Amit rented Toyota Innova for 2 days (2000 x 2 = 4000)
INSERT INTO rentals (car_id, customer_id, rental_date, return_date, actual_return_date, total_cost, status) VALUES
(6, 3, '2026-09-20', '2026-09-22', '2026-09-22', 4000.00, 'COMPLETED');

-- Active Rental 4: Sneha currently has Mahindra XUV700 (car_id 4 = RENTED)
INSERT INTO rentals (car_id, customer_id, rental_date, return_date, actual_return_date, total_cost, status) VALUES
(4, 4, '2026-10-20', '2026-10-27', NULL, NULL, 'ACTIVE');

-- Active Rental 5: Vikram currently has Hyundai Creta (car_id 8 = RENTED)
INSERT INTO rentals (car_id, customer_id, rental_date, return_date, actual_return_date, total_cost, status) VALUES
(8, 5, '2026-10-22', '2026-10-29', NULL, NULL, 'ACTIVE');

-- Sample data insertion complete!
