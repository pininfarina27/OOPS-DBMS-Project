// ============================================================
// CustomerOperations.java — All Customer Database Operations
// Contains static methods for customer CRUD, search, count
// ============================================================

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CustomerOperations {

    // --------------------------------------------------------
    // Get all customers
    // DBMS: SELECT — retrieving all records from customers table
    // DBMS: ORDER BY — sorting by customer_id
    // DBMS: PREPARED STATEMENT — using PreparedStatement for safe queries
    // --------------------------------------------------------
    public static ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT * FROM customers ORDER BY customer_id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setRegistrationDate(rs.getString("registration_date"));
                customers.add(customer);
            }

        } catch (Exception e) {
            System.out.println("Error getting all customers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return customers;
    }

    // --------------------------------------------------------
    // Get a single customer by ID
    // DBMS: WHERE — filtering by primary key
    // --------------------------------------------------------
    public static Customer getCustomerById(int customerId) {
        Customer customer = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT * FROM customers WHERE customer_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setRegistrationDate(rs.getString("registration_date"));
            }

        } catch (Exception e) {
            System.out.println("Error getting customer by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return customer;
    }

    // --------------------------------------------------------
    // Search customers by name, email, or phone
    // DBMS: LIKE — searching for partial matches using % wildcard
    // DBMS: OR — matching across multiple columns
    // --------------------------------------------------------
    public static ArrayList<Customer> searchCustomers(String keyword) {
        ArrayList<Customer> customers = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // DBMS: LIKE — % means "any characters before or after"
            String sql = "SELECT * FROM customers " +
                         "WHERE first_name LIKE ? OR last_name LIKE ? " +
                         "OR email LIKE ? OR phone LIKE ? " +
                         "ORDER BY customer_id";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");
            stmt.setString(4, "%" + keyword + "%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setRegistrationDate(rs.getString("registration_date"));
                customers.add(customer);
            }

        } catch (Exception e) {
            System.out.println("Error searching customers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return customers;
    }

    // --------------------------------------------------------
    // Add a new customer
    // DBMS: INSERT — inserting a new record into the customers table
    // Returns the auto-generated customer_id
    // --------------------------------------------------------
    public static int addCustomer(Customer customer) {
        int generatedId = -1;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "INSERT INTO customers (first_name, last_name, email, phone, address) " +
                         "VALUES (?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());

            stmt.executeUpdate();

            // Get the auto-generated ID
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }

            System.out.println("Customer added successfully with ID: " + generatedId);

        } catch (Exception e) {
            System.out.println("Error adding customer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return generatedId;
    }

    // --------------------------------------------------------
    // Update an existing customer
    // DBMS: UPDATE — modifying an existing record
    // --------------------------------------------------------
    public static boolean updateCustomer(Customer customer) {
        boolean success = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, " +
                         "phone = ?, address = ? WHERE customer_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setInt(6, customer.getCustomerId());

            int rowsUpdated = stmt.executeUpdate();
            success = rowsUpdated > 0;

        } catch (Exception e) {
            System.out.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return success;
    }

    // --------------------------------------------------------
    // Delete a customer (checks for active rentals first)
    // DBMS: DELETE — removing a record from the table
    // OOP: EXCEPTION HANDLING — throws RentalException if customer has active rentals
    // --------------------------------------------------------
    public static boolean deleteCustomer(int customerId) throws RentalException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            // First check if this customer has any active rentals
            String checkSql = "SELECT COUNT(*) FROM rentals WHERE customer_id = ? AND status = 'ACTIVE'";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, customerId);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // OOP: EXCEPTION HANDLING — throwing custom exception
                throw new RentalException("Cannot delete customer: they have active rentals");
            }

            // Close the check statement before running delete
            rs.close();
            stmt.close();

            // Now delete the customer
            String deleteSql = "DELETE FROM customers WHERE customer_id = ?";
            stmt = conn.prepareStatement(deleteSql);
            stmt.setInt(1, customerId);

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (RentalException e) {
            throw e; // pass business errors through
        } catch (Exception e) {
            System.out.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    // --------------------------------------------------------
    // Count total number of customers
    // DBMS: COUNT — aggregate function to count all records
    // --------------------------------------------------------
    public static int countCustomers() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();

            String sql = "SELECT COUNT(*) FROM customers";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error counting customers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        return count;
    }
}
