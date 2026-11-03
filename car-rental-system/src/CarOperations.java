// ============================================================
// CarOperations.java — All Car Database Operations
// Contains static methods for car CRUD, search, filter, sort
// Also handles car_categories queries
// ============================================================

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class CarOperations {

    // --------------------------------------------------------
    // Get all cars with category names
    // DBMS: LEFT JOIN — joining cars with car_categories to get category name
    // DBMS: ORDER BY — sorting results by car_id
    // DBMS: PREPARED STATEMENT — using PreparedStatement for safe queries
    // --------------------------------------------------------
    public static ArrayList<Car> getAllCars() {
        ArrayList<Car> cars = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // DBMS: LEFT JOIN — if a car has no category, it still shows up (with NULL category_name)
            String sql = "SELECT c.*, cc.category_name FROM cars c " +
                         "LEFT JOIN car_categories cc ON c.category_id = cc.category_id " +
                         "ORDER BY c.car_id";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Car car = new Car();
                car.setCarId(rs.getInt("car_id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setLicensePlate(rs.getString("license_plate"));
                car.setColor(rs.getString("color"));
                car.setDailyRate(rs.getDouble("daily_rate"));
                car.setStatus(rs.getString("status"));
                car.setCategoryId(rs.getInt("category_id"));
                car.setCategoryName(rs.getString("category_name"));
                car.setDateAdded(rs.getString("date_added"));
                cars.add(car);
            }

        } catch (Exception e) {
            System.out.println("Error getting all cars: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return cars;
    }

    // --------------------------------------------------------
    // Get a single car by ID
    // DBMS: WHERE — filtering by primary key
    // DBMS: LEFT JOIN — to get category name
    // --------------------------------------------------------
    public static Car getCarById(int carId) {
        Car car = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT c.*, cc.category_name FROM cars c " +
                         "LEFT JOIN car_categories cc ON c.category_id = cc.category_id " +
                         "WHERE c.car_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, carId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                car = new Car();
                car.setCarId(rs.getInt("car_id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setLicensePlate(rs.getString("license_plate"));
                car.setColor(rs.getString("color"));
                car.setDailyRate(rs.getDouble("daily_rate"));
                car.setStatus(rs.getString("status"));
                car.setCategoryId(rs.getInt("category_id"));
                car.setCategoryName(rs.getString("category_name"));
                car.setDateAdded(rs.getString("date_added"));
            }

        } catch (Exception e) {
            System.out.println("Error getting car by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return car;
    }

    // --------------------------------------------------------
    // Search cars by brand or model name
    // DBMS: LIKE — searching for partial matches using % wildcard
    // --------------------------------------------------------
    public static ArrayList<Car> searchCars(String keyword) {
        ArrayList<Car> cars = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // DBMS: LIKE — % means "any characters before or after"
            String sql = "SELECT c.*, cc.category_name FROM cars c " +
                         "LEFT JOIN car_categories cc ON c.category_id = cc.category_id " +
                         "WHERE c.brand LIKE ? OR c.model LIKE ? " +
                         "ORDER BY c.car_id";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                Car car = new Car();
                car.setCarId(rs.getInt("car_id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setLicensePlate(rs.getString("license_plate"));
                car.setColor(rs.getString("color"));
                car.setDailyRate(rs.getDouble("daily_rate"));
                car.setStatus(rs.getString("status"));
                car.setCategoryId(rs.getInt("category_id"));
                car.setCategoryName(rs.getString("category_name"));
                car.setDateAdded(rs.getString("date_added"));
                cars.add(car);
            }

        } catch (Exception e) {
            System.out.println("Error searching cars: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return cars;
    }

    // --------------------------------------------------------
    // Get cars with combined filters: search, status, category, sort
    // DBMS: WHERE — dynamic filtering based on user selections
    // DBMS: LIKE — for search keyword
    // DBMS: ORDER BY — for sorting results
    // This method builds the SQL query dynamically based on which filters are provided
    // --------------------------------------------------------
    public static ArrayList<Car> getFilteredCars(String search, String status,
            String category, String sortBy, String order) {

        ArrayList<Car> cars = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // Start building the SQL query
            String sql = "SELECT c.*, cc.category_name FROM cars c " +
                         "LEFT JOIN car_categories cc ON c.category_id = cc.category_id " +
                         "WHERE 1=1";

            // We use ArrayList to collect parameter values for PreparedStatement
            ArrayList<Object> params = new ArrayList<>();

            // Add search filter if provided
            if (search != null && !search.isEmpty()) {
                sql = sql + " AND (c.brand LIKE ? OR c.model LIKE ?)";
                params.add("%" + search + "%");
                params.add("%" + search + "%");
            }

            // Add status filter if provided
            if (status != null && !status.isEmpty()) {
                sql = sql + " AND c.status = ?";
                params.add(status);
            }

            // Add category filter if provided
            if (category != null && !category.isEmpty()) {
                sql = sql + " AND c.category_id = ?";
                params.add(Integer.parseInt(category));
            }

            // DBMS: ORDER BY — add sorting
            if (sortBy != null && !sortBy.isEmpty()) {
                // Only allow safe column names to prevent SQL injection
                if (sortBy.equals("daily_rate") || sortBy.equals("brand") ||
                    sortBy.equals("year") || sortBy.equals("car_id")) {
                    sql = sql + " ORDER BY c." + sortBy;
                    if (order != null && order.equalsIgnoreCase("desc")) {
                        sql = sql + " DESC";
                    } else {
                        sql = sql + " ASC";
                    }
                } else {
                    sql = sql + " ORDER BY c.car_id ASC";
                }
            } else {
                sql = sql + " ORDER BY c.car_id ASC";
            }

            stmt = conn.prepareStatement(sql);

            // Set all parameter values
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                }
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                Car car = new Car();
                car.setCarId(rs.getInt("car_id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setLicensePlate(rs.getString("license_plate"));
                car.setColor(rs.getString("color"));
                car.setDailyRate(rs.getDouble("daily_rate"));
                car.setStatus(rs.getString("status"));
                car.setCategoryId(rs.getInt("category_id"));
                car.setCategoryName(rs.getString("category_name"));
                car.setDateAdded(rs.getString("date_added"));
                cars.add(car);
            }

        } catch (Exception e) {
            System.out.println("Error filtering cars: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return cars;
    }

    // --------------------------------------------------------
    // Add a new car to the database
    // DBMS: INSERT — inserting a new record into the cars table
    // Returns the auto-generated car_id
    // --------------------------------------------------------
    public static int addCar(Car car) {
        int generatedId = -1;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "INSERT INTO cars (category_id, brand, model, year, license_plate, " +
                         "color, daily_rate, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            // Statement.RETURN_GENERATED_KEYS tells MySQL to give us the new car_id
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, car.getCategoryId());
            stmt.setString(2, car.getBrand());
            stmt.setString(3, car.getModel());
            stmt.setInt(4, car.getYear());
            stmt.setString(5, car.getLicensePlate());
            stmt.setString(6, car.getColor());
            stmt.setDouble(7, car.getDailyRate());
            stmt.setString(8, car.getStatus() != null ? car.getStatus() : "AVAILABLE");

            stmt.executeUpdate();

            // Get the auto-generated ID
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }

            System.out.println("Car added successfully with ID: " + generatedId);

        } catch (Exception e) {
            System.out.println("Error adding car: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return generatedId;
    }

    // --------------------------------------------------------
    // Update an existing car
    // DBMS: UPDATE — modifying an existing record
    // --------------------------------------------------------
    public static boolean updateCar(Car car) {
        boolean success = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "UPDATE cars SET category_id = ?, brand = ?, model = ?, year = ?, " +
                         "license_plate = ?, color = ?, daily_rate = ?, status = ? " +
                         "WHERE car_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, car.getCategoryId());
            stmt.setString(2, car.getBrand());
            stmt.setString(3, car.getModel());
            stmt.setInt(4, car.getYear());
            stmt.setString(5, car.getLicensePlate());
            stmt.setString(6, car.getColor());
            stmt.setDouble(7, car.getDailyRate());
            stmt.setString(8, car.getStatus());
            stmt.setInt(9, car.getCarId());

            int rowsUpdated = stmt.executeUpdate();
            success = rowsUpdated > 0;

        } catch (Exception e) {
            System.out.println("Error updating car: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return success;
    }

    // --------------------------------------------------------
    // Delete a car (checks for active rentals first)
    // DBMS: DELETE — removing a record from the table
    // OOP: EXCEPTION HANDLING — throws RentalException if car has active rentals
    // --------------------------------------------------------
    public static boolean deleteCar(int carId) throws RentalException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // First check if this car has any active rentals
            String checkSql = "SELECT COUNT(*) FROM rentals WHERE car_id = ? AND status = 'ACTIVE'";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, carId);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // OOP: EXCEPTION HANDLING — throwing custom exception
                throw new RentalException("Cannot delete car: it has active rentals");
            }

            // Close the check statement before running delete
            rs.close();
            stmt.close();

            // Now delete the car
            String deleteSql = "DELETE FROM cars WHERE car_id = ?";
            stmt = conn.prepareStatement(deleteSql);
            stmt.setInt(1, carId);

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (RentalException e) {
            throw e; // pass business errors through
        } catch (Exception e) {
            System.out.println("Error deleting car: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    // --------------------------------------------------------
    // Update car status only (used during rent/return operations)
    // DBMS: UPDATE — changing just the status column
    // --------------------------------------------------------
    public static boolean updateCarStatus(int carId, String status) {
        boolean success = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "UPDATE cars SET status = ? WHERE car_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, carId);

            int rowsUpdated = stmt.executeUpdate();
            success = rowsUpdated > 0;

        } catch (Exception e) {
            System.out.println("Error updating car status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return success;
    }

    // --------------------------------------------------------
    // Count cars by status
    // DBMS: COUNT — aggregate function to count records
    // DBMS: WHERE — filtering by status value
    // --------------------------------------------------------
    public static int countCarsByStatus(String status) {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT COUNT(*) FROM cars WHERE status = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error counting cars: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return count;
    }

    // --------------------------------------------------------
    // Get all car categories
    // Used to populate dropdown menus in the frontend
    // --------------------------------------------------------
    public static ArrayList<HashMap<String, String>> getAllCategories() {
        ArrayList<HashMap<String, String>> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT * FROM car_categories ORDER BY category_id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                HashMap<String, String> category = new HashMap<>();
                category.put("categoryId", String.valueOf(rs.getInt("category_id")));
                category.put("categoryName", rs.getString("category_name"));
                category.put("description", rs.getString("description"));
                categories.add(category);
            }

        } catch (Exception e) {
            System.out.println("Error getting categories: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return categories;
    }
}
