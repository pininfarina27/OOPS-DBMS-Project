// ============================================================
// RentalOperations.java — All Rental Database Operations
// Contains static methods for rental CRUD, dashboard, reports
// THIS IS THE MOST IMPORTANT FILE — demonstrates most DBMS concepts
// DBMS concepts: INNER JOIN, TRANSACTION, GROUP BY, SUM, COUNT, LIMIT
// ============================================================

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class RentalOperations {

    // --------------------------------------------------------
    // Get all rentals with car and customer details
    // DBMS: INNER JOIN — joining rentals with cars AND customers tables
    // This is a THREE-TABLE JOIN to get all display information
    // --------------------------------------------------------
    public static ArrayList<Rental> getAllRentals() {
        ArrayList<Rental> rentals = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // DBMS: INNER JOIN — joining 3 tables: rentals + cars + customers
            // We get car brand, model, license_plate and customer name from the JOIN
            String sql = "SELECT r.*, c.brand, c.model, c.license_plate, c.daily_rate, " +
                         "CONCAT(cu.first_name, ' ', cu.last_name) AS customer_name " +
                         "FROM rentals r " +
                         "INNER JOIN cars c ON r.car_id = c.car_id " +
                         "INNER JOIN customers cu ON r.customer_id = cu.customer_id " +
                         "ORDER BY r.rental_id DESC";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Rental rental = new Rental();
                rental.setRentalId(rs.getInt("rental_id"));
                rental.setCarId(rs.getInt("car_id"));
                rental.setCustomerId(rs.getInt("customer_id"));
                rental.setRentalDate(rs.getString("rental_date"));
                rental.setReturnDate(rs.getString("return_date"));
                rental.setActualReturnDate(rs.getString("actual_return_date"));
                rental.setTotalCost(rs.getDouble("total_cost"));
                rental.setStatus(rs.getString("status"));
                // OOP: COMPOSITION — setting composed fields from JOIN data
                rental.setCarBrand(rs.getString("brand"));
                rental.setCarModel(rs.getString("model"));
                rental.setLicensePlate(rs.getString("license_plate"));
                rental.setDailyRate(rs.getDouble("daily_rate"));
                rental.setCustomerName(rs.getString("customer_name"));
                rentals.add(rental);
            }

        } catch (Exception e) {
            System.out.println("Error getting all rentals: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return rentals;
    }

    // --------------------------------------------------------
    // Get rentals filtered by status (ACTIVE, COMPLETED, CANCELLED)
    // DBMS: WHERE — filtering by status
    // DBMS: INNER JOIN — same 3-table join as getAllRentals
    // --------------------------------------------------------
    public static ArrayList<Rental> getRentalsByStatus(String status) {
        ArrayList<Rental> rentals = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT r.*, c.brand, c.model, c.license_plate, c.daily_rate, " +
                         "CONCAT(cu.first_name, ' ', cu.last_name) AS customer_name " +
                         "FROM rentals r " +
                         "INNER JOIN cars c ON r.car_id = c.car_id " +
                         "INNER JOIN customers cu ON r.customer_id = cu.customer_id " +
                         "WHERE r.status = ? " +
                         "ORDER BY r.rental_id DESC";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Rental rental = new Rental();
                rental.setRentalId(rs.getInt("rental_id"));
                rental.setCarId(rs.getInt("car_id"));
                rental.setCustomerId(rs.getInt("customer_id"));
                rental.setRentalDate(rs.getString("rental_date"));
                rental.setReturnDate(rs.getString("return_date"));
                rental.setActualReturnDate(rs.getString("actual_return_date"));
                rental.setTotalCost(rs.getDouble("total_cost"));
                rental.setStatus(rs.getString("status"));
                rental.setCarBrand(rs.getString("brand"));
                rental.setCarModel(rs.getString("model"));
                rental.setLicensePlate(rs.getString("license_plate"));
                rental.setDailyRate(rs.getDouble("daily_rate"));
                rental.setCustomerName(rs.getString("customer_name"));
                rentals.add(rental);
            }

        } catch (Exception e) {
            System.out.println("Error getting rentals by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return rentals;
    }

    // --------------------------------------------------------
    // Rent a car — THE MOST IMPORTANT METHOD
    // DBMS: TRANSACTION — two operations must both succeed or both fail:
    //   1. INSERT a new rental record
    //   2. UPDATE the car's status to 'RENTED'
    // If either fails, we ROLLBACK both. If both succeed, we COMMIT.
    // OOP: EXCEPTION HANDLING — throws RentalException if car is not available
    // --------------------------------------------------------
    public static int rentCar(int carId, int customerId, String rentalDate, String returnDate)
            throws RentalException {

        int generatedId = -1;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // First check if the car is AVAILABLE
            String checkSql = "SELECT status FROM cars WHERE car_id = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, carId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String carStatus = rs.getString("status");
                if (!carStatus.equals("AVAILABLE")) {
                    throw new RentalException("Car is not available for renting. Current status: " + carStatus);
                }
            } else {
                throw new RentalException("Car not found with ID: " + carId);
            }

            // Close the check resources
            rs.close();
            stmt.close();

            // DBMS: TRANSACTION — start the transaction
            // setAutoCommit(false) means changes won't be saved until we call commit()
            conn.setAutoCommit(false);

            try {
                // Step 1: INSERT the rental record
                // DBMS: INSERT — adding a new rental
                String insertSql = "INSERT INTO rentals (car_id, customer_id, rental_date, return_date, status) " +
                                   "VALUES (?, ?, ?, ?, 'ACTIVE')";

                stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, carId);
                stmt.setInt(2, customerId);
                stmt.setString(3, rentalDate);
                stmt.setString(4, returnDate);
                stmt.executeUpdate();

                // Get the generated rental ID
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
                rs.close();
                stmt.close();

                // Step 2: UPDATE the car status to RENTED
                // DBMS: UPDATE — changing the car status
                String updateSql = "UPDATE cars SET status = 'RENTED' WHERE car_id = ?";
                stmt = conn.prepareStatement(updateSql);
                stmt.setInt(1, carId);
                stmt.executeUpdate();
                stmt.close();

                // DBMS: COMMIT — both operations succeeded, save the changes
                conn.commit();
                System.out.println("Car rented successfully! Rental ID: " + generatedId);

            } catch (Exception e) {
                // DBMS: ROLLBACK — something went wrong, undo ALL changes
                conn.rollback();
                System.out.println("Transaction failed, rolled back: " + e.getMessage());
                throw new RentalException("Failed to rent car: " + e.getMessage());
            }

        } catch (RentalException e) {
            throw e; // pass business errors through
        } catch (Exception e) {
            System.out.println("Error renting car: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // reset auto-commit
                    conn.close();
                }
            } catch (Exception e) {}
        }

        return generatedId;
    }

    // --------------------------------------------------------
    // Return a car — SECOND MOST IMPORTANT METHOD
    // DBMS: TRANSACTION — two operations must both succeed:
    //   1. UPDATE the rental: set status=COMPLETED, actual_return_date=TODAY, calculate total_cost
    //   2. UPDATE the car: set status back to AVAILABLE
    // DBMS: DATEDIFF — used to calculate number of rental days
    // OOP: EXCEPTION HANDLING — throws RentalException if rental is not active
    // --------------------------------------------------------
    public static boolean returnCar(int rentalId) throws RentalException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // First get the rental details to check status and get car_id
            String checkSql = "SELECT r.*, c.daily_rate FROM rentals r " +
                              "INNER JOIN cars c ON r.car_id = c.car_id " +
                              "WHERE r.rental_id = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, rentalId);
            rs = stmt.executeQuery();

            int carId = 0;
            double dailyRate = 0;

            if (rs.next()) {
                String rentalStatus = rs.getString("status");
                if (!rentalStatus.equals("ACTIVE")) {
                    throw new RentalException("Rental is not active. Current status: " + rentalStatus);
                }
                carId = rs.getInt("car_id");
                dailyRate = rs.getDouble("daily_rate");
            } else {
                throw new RentalException("Rental not found with ID: " + rentalId);
            }

            rs.close();
            stmt.close();

            // DBMS: TRANSACTION — start the transaction
            conn.setAutoCommit(false);

            try {
                // Step 1: UPDATE the rental record
                // DBMS: DATEDIFF — calculates the number of days between two dates
                // We calculate: days = DATEDIFF(TODAY, rental_date), minimum 1 day
                // total_cost = days * daily_rate
                String updateRentalSql = "UPDATE rentals SET status = 'COMPLETED', " +
                                         "actual_return_date = CURRENT_DATE, " +
                                         "total_cost = GREATEST(DATEDIFF(CURRENT_DATE, rental_date), 1) * ? " +
                                         "WHERE rental_id = ?";

                stmt = conn.prepareStatement(updateRentalSql);
                stmt.setDouble(1, dailyRate);
                stmt.setInt(2, rentalId);
                stmt.executeUpdate();
                stmt.close();

                // Step 2: UPDATE the car status back to AVAILABLE
                String updateCarSql = "UPDATE cars SET status = 'AVAILABLE' WHERE car_id = ?";
                stmt = conn.prepareStatement(updateCarSql);
                stmt.setInt(1, carId);
                stmt.executeUpdate();
                stmt.close();

                // DBMS: COMMIT — both operations succeeded
                conn.commit();
                System.out.println("Car returned successfully! Rental #" + rentalId);
                return true;

            } catch (Exception e) {
                // DBMS: ROLLBACK — something went wrong, undo ALL changes
                conn.rollback();
                System.out.println("Return transaction failed, rolled back: " + e.getMessage());
                throw new RentalException("Failed to return car: " + e.getMessage());
            }

        } catch (RentalException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Error returning car: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {}
        }
    }

    // --------------------------------------------------------
    // Get dashboard statistics
    // DBMS: COUNT — counting records in different tables
    // DBMS: SUM — adding up total revenue
    // DBMS: INNER JOIN — for recent rentals
    // DBMS: LIMIT — getting only the 5 most recent rentals
    // --------------------------------------------------------
    public static HashMap<String, Object> getDashboardStats() {
        HashMap<String, Object> stats = new HashMap<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // Query 1: Count total cars
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM cars");
            rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("totalCars", rs.getInt(1));
            }
            rs.close();
            stmt.close();

            // Query 2: Count available cars
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM cars WHERE status = 'AVAILABLE'");
            rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("availableCars", rs.getInt(1));
            }
            rs.close();
            stmt.close();

            // Query 3: Count total customers
            // DBMS: COUNT — aggregate function
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM customers");
            rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("totalCustomers", rs.getInt(1));
            }
            rs.close();
            stmt.close();

            // Query 4: Count active rentals
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM rentals WHERE status = 'ACTIVE'");
            rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("activeRentals", rs.getInt(1));
            }
            rs.close();
            stmt.close();

            // Query 5: Total revenue from completed rentals
            // DBMS: SUM — aggregate function to add up all total_cost values
            stmt = conn.prepareStatement("SELECT COALESCE(SUM(total_cost), 0) FROM rentals WHERE status = 'COMPLETED'");
            rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("totalRevenue", rs.getDouble(1));
            }
            rs.close();
            stmt.close();

            // Query 6: 5 most recent rentals with car and customer details
            // DBMS: INNER JOIN — joining 3 tables
            // DBMS: ORDER BY DESC — getting most recent first
            // DBMS: LIMIT — only getting 5 results
            String recentSql = "SELECT r.rental_id, r.rental_date, r.status, r.total_cost, " +
                               "c.brand, c.model, " +
                               "CONCAT(cu.first_name, ' ', cu.last_name) AS customer_name " +
                               "FROM rentals r " +
                               "INNER JOIN cars c ON r.car_id = c.car_id " +
                               "INNER JOIN customers cu ON r.customer_id = cu.customer_id " +
                               "ORDER BY r.rental_id DESC LIMIT 5";

            stmt = conn.prepareStatement(recentSql);
            rs = stmt.executeQuery();

            ArrayList<HashMap<String, Object>> recentRentals = new ArrayList<>();
            while (rs.next()) {
                HashMap<String, Object> rental = new HashMap<>();
                rental.put("rentalId", rs.getInt("rental_id"));
                rental.put("rentalDate", rs.getString("rental_date"));
                rental.put("status", rs.getString("status"));
                rental.put("totalCost", rs.getDouble("total_cost"));
                rental.put("carName", rs.getString("brand") + " " + rs.getString("model"));
                rental.put("customerName", rs.getString("customer_name"));
                recentRentals.add(rental);
            }
            stats.put("recentRentals", recentRentals);

        } catch (Exception e) {
            System.out.println("Error getting dashboard stats: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return stats;
    }

    // --------------------------------------------------------
    // Report: Revenue by car category
    // DBMS: GROUP BY — grouping rentals by category
    // DBMS: SUM — total revenue per category
    // DBMS: COUNT — number of rentals per category
    // DBMS: INNER JOIN — joining rentals → cars → car_categories (3 tables)
    // --------------------------------------------------------
    public static ArrayList<HashMap<String, Object>> getRevenueByCategory() {
        ArrayList<HashMap<String, Object>> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT cc.category_name, " +
                         "COUNT(r.rental_id) AS total_rentals, " +
                         "COALESCE(SUM(r.total_cost), 0) AS total_revenue " +
                         "FROM rentals r " +
                         "INNER JOIN cars c ON r.car_id = c.car_id " +
                         "INNER JOIN car_categories cc ON c.category_id = cc.category_id " +
                         "WHERE r.status = 'COMPLETED' " +
                         "GROUP BY cc.category_name " +
                         "ORDER BY total_revenue DESC";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("categoryName", rs.getString("category_name"));
                row.put("totalRentals", rs.getInt("total_rentals"));
                row.put("totalRevenue", rs.getDouble("total_revenue"));
                results.add(row);
            }

        } catch (Exception e) {
            System.out.println("Error getting revenue by category: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return results;
    }

    // --------------------------------------------------------
    // Report: Most popular cars (most rented)
    // DBMS: GROUP BY — grouping by car
    // DBMS: COUNT — counting number of times each car was rented
    // DBMS: ORDER BY DESC — most rented first
    // DBMS: LIMIT — top 5 only
    // DBMS: INNER JOIN — joining rentals with cars
    // --------------------------------------------------------
    public static ArrayList<HashMap<String, Object>> getPopularCars() {
        ArrayList<HashMap<String, Object>> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT c.brand, c.model, c.license_plate, " +
                         "COUNT(r.rental_id) AS rental_count, " +
                         "COALESCE(SUM(r.total_cost), 0) AS total_earned " +
                         "FROM rentals r " +
                         "INNER JOIN cars c ON r.car_id = c.car_id " +
                         "GROUP BY c.car_id, c.brand, c.model, c.license_plate " +
                         "ORDER BY rental_count DESC " +
                         "LIMIT 5";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("carName", rs.getString("brand") + " " + rs.getString("model"));
                row.put("licensePlate", rs.getString("license_plate"));
                row.put("rentalCount", rs.getInt("rental_count"));
                row.put("totalEarned", rs.getDouble("total_earned"));
                results.add(row);
            }

        } catch (Exception e) {
            System.out.println("Error getting popular cars: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return results;
    }

    // --------------------------------------------------------
    // Report: Top customers (most rentals)
    // DBMS: GROUP BY — grouping by customer
    // DBMS: COUNT — counting rentals per customer
    // DBMS: SUM — total spent per customer
    // DBMS: INNER JOIN — joining rentals with customers
    // --------------------------------------------------------
    public static ArrayList<HashMap<String, Object>> getTopCustomers() {
        ArrayList<HashMap<String, Object>> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT CONCAT(cu.first_name, ' ', cu.last_name) AS customer_name, " +
                         "cu.email, " +
                         "COUNT(r.rental_id) AS rental_count, " +
                         "COALESCE(SUM(r.total_cost), 0) AS total_spent " +
                         "FROM rentals r " +
                         "INNER JOIN customers cu ON r.customer_id = cu.customer_id " +
                         "GROUP BY cu.customer_id, cu.first_name, cu.last_name, cu.email " +
                         "ORDER BY rental_count DESC";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("customerName", rs.getString("customer_name"));
                row.put("email", rs.getString("email"));
                row.put("rentalCount", rs.getInt("rental_count"));
                row.put("totalSpent", rs.getDouble("total_spent"));
                results.add(row);
            }

        } catch (Exception e) {
            System.out.println("Error getting top customers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return results;
    }

    // --------------------------------------------------------
    // Report: Monthly revenue
    // DBMS: GROUP BY — grouping by month and year
    // DBMS: SUM — total revenue per month
    // DBMS: COUNT — number of rentals per month
    // DBMS: MONTH(), YEAR() — MySQL date functions to extract month and year
    // DBMS: ORDER BY — chronological order
    // --------------------------------------------------------
    public static ArrayList<HashMap<String, Object>> getMonthlyRevenue() {
        ArrayList<HashMap<String, Object>> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT YEAR(rental_date) AS rental_year, " +
                         "MONTH(rental_date) AS rental_month, " +
                         "COUNT(rental_id) AS total_rentals, " +
                         "COALESCE(SUM(total_cost), 0) AS total_revenue " +
                         "FROM rentals " +
                         "WHERE status = 'COMPLETED' " +
                         "GROUP BY YEAR(rental_date), MONTH(rental_date) " +
                         "ORDER BY rental_year DESC, rental_month DESC";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("year", rs.getInt("rental_year"));
                row.put("month", rs.getInt("rental_month"));
                row.put("totalRentals", rs.getInt("total_rentals"));
                row.put("totalRevenue", rs.getDouble("total_revenue"));
                results.add(row);
            }

        } catch (Exception e) {
            System.out.println("Error getting monthly revenue: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return results;
    }
}
