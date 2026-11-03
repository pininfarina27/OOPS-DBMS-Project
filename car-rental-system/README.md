# 🚗 Car Rental Management System

A complete **Car Rental Management System** built as a B.Tech 3rd-semester college project demonstrating both **Object-Oriented Programming (Java)** and **Database Management System (MySQL)** concepts.

---

## 📋 Project Description

This system allows an operator to:
- **Manage Cars** — Add, edit, delete, search, filter, and sort cars
- **Manage Customers** — Register, edit, delete, and search customers
- **Rent Cars** — Create rental bookings with automatic cost estimation
- **Process Returns** — Return cars with automatic cost calculation
- **View History** — Browse complete rental history with status filters
- **View Reports** — Revenue by category, popular cars, top customers, monthly revenue

All data is stored in a **MySQL database** and fetched live — no hardcoded data anywhere.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | HTML5 + CSS3 + Vanilla JavaScript |
| Backend | Core Java 17 (`com.sun.net.httpserver.HttpServer`) |
| Database | MySQL 8.0 + JDBC |
| JSON Library | Gson 2.11.0 |
| JDBC Driver | MySQL Connector/J 8.3.0 |

> **No Spring Boot, no React, no Bootstrap, no Maven/Gradle** — pure beginner-friendly stack.

---

## ✅ Prerequisites

Before running this project, make sure you have:

1. **Java 17+** — Check with: `java --version`
2. **MySQL 8.0+** — Check with: `mysql --version`
3. **JAR files** in the `lib/` folder:
   - `mysql-connector-j-8.3.0.jar`
   - `gson-2.11.0.jar`

---

## 🚀 Setup & Run

### Step 1: Set Up the Database

Open MySQL and run the SQL scripts:

```sql
-- First, create the tables:
SOURCE sql/schema.sql;

-- Then, insert sample data:
SOURCE sql/sample_data.sql;
```

**Or from command line (Windows):**
```
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < sql/schema.sql
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < sql/sample_data.sql
```

### Step 2: Configure Database Connection

Edit `db.properties` with your MySQL credentials:
```properties
db.url=jdbc:mysql://localhost:3306/car_rental_db
db.username=root
db.password=your_password_here
db.driver=com.mysql.cj.jdbc.Driver
```

### Step 3: Compile the Project

```
compile.bat
```

This compiles all `.java` files from `src/` into `out/` using the JARs in `lib/`.

### Step 4: Run the Server

```
run.bat
```

This starts the server at **http://localhost:8080**

### Step 5: Open the Application

Open your browser and go to:
```
http://localhost:8080
```

You should see the Dashboard with live data from MySQL!

---

## 📁 Project Structure

```
car-rental-system/
│
├── src/                        ← All Java source files (10 files, flat)
│   ├── Vehicle.java            ← Abstract base class (OOP: Abstraction, Inheritance)
│   ├── Car.java                ← Extends Vehicle (OOP: Inheritance, Polymorphism)
│   ├── Customer.java           ← Customer model (OOP: Encapsulation)
│   ├── Rental.java             ← Rental model (OOP: Composition)
│   ├── RentalException.java    ← Custom exception (OOP: Exception Handling)
│   ├── DatabaseHelper.java     ← DB connection helper
│   ├── CarOperations.java      ← Car CRUD + search/filter (DBMS: JOIN, LIKE, ORDER BY)
│   ├── CustomerOperations.java ← Customer CRUD + search (DBMS: INSERT, UPDATE, DELETE)
│   ├── RentalOperations.java   ← Rental ops + reports (DBMS: TRANSACTION, GROUP BY, SUM)
│   └── Main.java               ← HTTP Server + API routing (all if-else routing)
│
├── web/                        ← Frontend files
│   ├── index.html              ← Dashboard page
│   ├── cars.html               ← Car management page
│   ├── customers.html          ← Customer management page
│   ├── rentals.html            ← Active rentals page
│   ├── history.html            ← Rental history page
│   ├── reports.html            ← Reports & analytics page
│   ├── css/
│   │   └── style.css           ← Complete stylesheet
│   └── js/
│       ├── common.js           ← Shared helpers (apiCall, formatCurrency, etc.)
│       ├── dashboard.js        ← Dashboard page logic
│       ├── cars.js             ← Cars page logic (CRUD + filters)
│       ├── customers.js        ← Customers page logic (CRUD)
│       ├── rentals.js          ← Rentals page logic (rent + return)
│       ├── history.js          ← History page logic (filter)
│       └── reports.js          ← Reports page logic (bar charts)
│
├── sql/
│   ├── schema.sql              ← CREATE DATABASE + 4 tables
│   └── sample_data.sql         ← Sample cars, customers, rentals
│
├── lib/                        ← External JAR files
│   ├── mysql-connector-j-8.3.0.jar
│   └── gson-2.11.0.jar
│
├── out/                        ← Compiled .class files (auto-generated)
├── db.properties               ← Database connection settings
├── compile.bat                 ← Windows build script
├── run.bat                     ← Windows run script
└── README.md                   ← This file
```

---

## 🎓 OOP Concepts Demonstrated

| Concept | Where It's Used | File |
|---------|----------------|------|
| **Abstraction** | `abstract class Vehicle` with `abstract getDetails()` method — cannot be instantiated directly | `Vehicle.java` |
| **Inheritance** | `Car extends Vehicle` — Car is a child class of Vehicle, uses `super()` | `Car.java` |
| **Polymorphism** | `Car` overrides `getDetails()` and `toString()` from `Vehicle`. `Vehicle v = new Car()` works. | `Car.java`, `Vehicle.java` |
| **Encapsulation** | All fields in all model classes are `private`. Accessed only through `public` getters and setters. | `Vehicle.java`, `Car.java`, `Customer.java`, `Rental.java` |
| **Composition** | `Rental` "has-a" car info (`carBrand`, `carModel`) and customer info (`customerName`) — not full objects | `Rental.java` |
| **Exception Handling** | `RentalException extends Exception` — thrown when business rules are violated (e.g., renting an unavailable car) | `RentalException.java`, `CarOperations.java`, `RentalOperations.java` |
| **Classes & Objects** | All model classes (`Car`, `Customer`, `Rental`) create objects to store and transfer data | All model files |

---

## 🗃️ DBMS Concepts Demonstrated

| Concept | Where It's Used | File |
|---------|----------------|------|
| **CREATE TABLE** | 4 tables: `car_categories`, `cars`, `customers`, `rentals` | `schema.sql` |
| **PRIMARY KEY** | Auto-increment PKs in all 4 tables | `schema.sql` |
| **FOREIGN KEY** | `cars.category_id → car_categories`, `rentals.car_id → cars`, `rentals.customer_id → customers` | `schema.sql` |
| **ENUM** | `cars.status` (AVAILABLE/RENTED/MAINTENANCE), `rentals.status` (ACTIVE/COMPLETED/CANCELLED) | `schema.sql` |
| **UNIQUE** | `license_plate` and `email` are unique | `schema.sql` |
| **INSERT** | Adding cars, customers, rentals | `CarOperations.java`, `CustomerOperations.java`, `RentalOperations.java` |
| **SELECT with WHERE** | Filtering by ID, status, category | All Operations files |
| **UPDATE** | Editing car/customer details, updating status on rent/return | All Operations files |
| **DELETE** | Removing cars and customers (with active rental check) | `CarOperations.java`, `CustomerOperations.java` |
| **LEFT JOIN** | Getting category name with each car (even if no category set) | `CarOperations.java` |
| **INNER JOIN** | Getting car + customer details with each rental (3-table join) | `RentalOperations.java` |
| **LIKE** | Searching cars by brand/model, customers by name/email | `CarOperations.java`, `CustomerOperations.java` |
| **ORDER BY** | Sorting results ASC/DESC | All Operations files |
| **COUNT()** | Counting total cars, customers, active rentals | `CarOperations.java`, `CustomerOperations.java`, `RentalOperations.java` |
| **SUM()** | Calculating total revenue | `RentalOperations.java` |
| **GROUP BY** | Grouping rentals by category, car, customer, month/year for reports | `RentalOperations.java` |
| **LIMIT** | Getting top 5 popular cars, top customers | `RentalOperations.java` |
| **TRANSACTION** | Rent car: INSERT rental + UPDATE car status — both must succeed or both rollback | `RentalOperations.java` |
| **PreparedStatement** | Every single SQL query uses `?` placeholders to prevent SQL injection | All Operations files |
| **DATEDIFF()** | Calculating number of rental days for cost calculation | `RentalOperations.java` |
| **CONCAT()** | Combining first_name + last_name | `RentalOperations.java` |
| **MONTH(), YEAR()** | Extracting month and year for monthly revenue report | `RentalOperations.java` |
| **COALESCE()** | Handling NULL values in SUM (returns 0 instead of NULL) | `RentalOperations.java` |

---

## 🗄️ Database Design

```
car_categories          cars                    customers
──────────────          ────────────────        ──────────────────
category_id (PK)  ←──  category_id (FK)        customer_id (PK)
category_name           car_id (PK)             first_name
description             brand                   last_name
                        model                   email (UNIQUE)
                        year                    phone
                        license_plate (UNIQUE)  address
                        color                   registration_date
                        daily_rate
                        status (ENUM)
                        date_added
                            │
                            │              rentals
                            │              ──────────────────
                            └──────────── car_id (FK)
                                          customer_id (FK) ──→ customers
                                          rental_id (PK)
                                          rental_date
                                          return_date
                                          actual_return_date
                                          total_cost
                                          status (ENUM)
```

---

## 🌐 API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/dashboard` | Stats + recent 5 rentals |
| GET | `/api/cars` | All cars (supports `?search=&status=&category=&sortBy=&order=`) |
| GET | `/api/cars/{id}` | Single car by ID |
| POST | `/api/cars` | Add new car |
| PUT | `/api/cars/{id}` | Edit car |
| DELETE | `/api/cars/{id}` | Delete car |
| GET | `/api/categories` | All car categories |
| GET | `/api/customers` | All customers (supports `?search=`) |
| GET | `/api/customers/{id}` | Single customer by ID |
| POST | `/api/customers` | Register new customer |
| PUT | `/api/customers/{id}` | Edit customer |
| DELETE | `/api/customers/{id}` | Delete customer |
| GET | `/api/rentals` | All rentals (supports `?status=ACTIVE/COMPLETED`) |
| POST | `/api/rentals` | Create new rental |
| PUT | `/api/rentals/{id}/return` | Return a car |
| GET | `/api/reports/revenue-by-category` | Revenue grouped by car category |
| GET | `/api/reports/popular-cars` | Most rented cars (top 5) |
| GET | `/api/reports/top-customers` | Customers with most rentals (top 5) |
| GET | `/api/reports/monthly-revenue` | Revenue grouped by month and year |

---

## 👨‍💻 Author

B.Tech 3rd Semester — CSE  
College Project: OOP (Java) + DBMS (MySQL)

---

## 📝 Note for Viva

**Key things to explain:**

1. **Why `HttpServer` instead of Tomcat?** → HttpServer is built into the JDK. No installation needed. Shows Core Java skills.

2. **What is a TRANSACTION?** → In `rentCar()`, we INSERT a rental AND UPDATE car status. If either step fails, both are rolled back using `conn.rollback()`. This ensures the database stays consistent.

3. **What is `PreparedStatement`?** → Instead of writing `"WHERE id = " + id` (SQL injection risk), we write `"WHERE id = ?"` and set the value separately. MySQL handles escaping automatically.

4. **What is polymorphism here?** → `Vehicle v = new Car(...)` — the variable type is `Vehicle` but the actual object is `Car`. When we call `v.getDetails()`, Java calls `Car`'s version (not `Vehicle`'s abstract one).

5. **What is composition vs inheritance?** → `Car extends Vehicle` is inheritance (Car IS-A Vehicle). `Rental` has `carBrand`, `carModel`, `customerName` fields — that's composition (Rental HAS-A car info, HAS-A customer info).
