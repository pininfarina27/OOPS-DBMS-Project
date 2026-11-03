// ============================================================
// Main.java — HTTP Server + ALL API Routing + Static File Serving
// This is the entry point of the application
// Uses Java's built-in HttpServer (com.sun.net.httpserver)
// All API routing is done here using simple if-else
// ============================================================

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    // Gson object for converting Java objects to JSON and back
    static Gson gson = new Gson();

    public static void main(String[] args) throws Exception {

        // Create the HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // All URLs starting with /api/ go to the API handler
        server.createContext("/api/", new com.sun.net.httpserver.HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                // Add CORS headers so browser doesn't block requests
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

                // Handle OPTIONS preflight requests
                if (exchange.getRequestMethod().equals("OPTIONS")) {
                    exchange.sendResponseHeaders(200, -1);
                    return;
                }

                handleApiRequest(exchange);
            }
        });

        // All other URLs go to the static file handler (HTML, CSS, JS)
        server.createContext("/", new com.sun.net.httpserver.HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                handleStaticFile(exchange);
            }
        });

        server.start();
        System.out.println("========================================");
        System.out.println("  Car Rental Management System");
        System.out.println("  Server started at http://localhost:8080");
        System.out.println("========================================");
    }

    // ============================================================
    // API REQUEST HANDLER — routes all API calls using if-else
    // ============================================================
    static void handleApiRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        String response = "";
        int statusCode = 200;

        try {

            // ==============================================
            // DASHBOARD
            // ==============================================
            if (path.equals("/api/dashboard") && method.equals("GET")) {
                HashMap<String, Object> stats = RentalOperations.getDashboardStats();
                response = gson.toJson(stats);
            }

            // ==============================================
            // CARS — GET all cars (with optional filters)
            // ==============================================
            else if (path.equals("/api/cars") && method.equals("GET")) {
                // Read query parameters for filtering
                String search = getQueryParam(query, "search");
                String status = getQueryParam(query, "status");
                String category = getQueryParam(query, "category");
                String sortBy = getQueryParam(query, "sortBy");
                String order = getQueryParam(query, "order");

                ArrayList<Car> cars = CarOperations.getFilteredCars(search, status, category, sortBy, order);
                response = gson.toJson(cars);
            }

            // ==============================================  
            // CARS — GET single car by ID
            // ==============================================
            else if (path.matches("/api/cars/\\d+") && method.equals("GET")) {
                int carId = extractId(path);
                Car car = CarOperations.getCarById(carId);
                if (car != null) {
                    response = gson.toJson(car);
                } else {
                    statusCode = 404;
                    response = "{\"error\": \"Car not found\"}";
                }
            }

            // ==============================================
            // CARS — POST (add new car)
            // ==============================================
            else if (path.equals("/api/cars") && method.equals("POST")) {
                String body = readBody(exchange);
                Car car = gson.fromJson(body, Car.class);
                int newId = CarOperations.addCar(car);
                if (newId > 0) {
                    statusCode = 201;
                    response = "{\"message\": \"Car added successfully\", \"carId\": " + newId + "}";
                } else {
                    statusCode = 500;
                    response = "{\"error\": \"Failed to add car\"}";
                }
            }

            // ==============================================
            // CARS — PUT (update existing car)
            // ==============================================
            else if (path.matches("/api/cars/\\d+") && method.equals("PUT")) {
                int carId = extractId(path);
                String body = readBody(exchange);
                Car car = gson.fromJson(body, Car.class);
                car.setCarId(carId);
                boolean success = CarOperations.updateCar(car);
                if (success) {
                    response = "{\"message\": \"Car updated successfully\"}";
                } else {
                    statusCode = 404;
                    response = "{\"error\": \"Car not found or update failed\"}";
                }
            }

            // ==============================================
            // CARS — DELETE
            // ==============================================
            else if (path.matches("/api/cars/\\d+") && method.equals("DELETE")) {
                int carId = extractId(path);
                boolean success = CarOperations.deleteCar(carId);
                if (success) {
                    response = "{\"message\": \"Car deleted successfully\"}";
                } else {
                    statusCode = 404;
                    response = "{\"error\": \"Car not found\"}";
                }
            }

            // ==============================================
            // CATEGORIES — GET all
            // ==============================================
            else if (path.equals("/api/categories") && method.equals("GET")) {
                ArrayList<HashMap<String, String>> categories = CarOperations.getAllCategories();
                response = gson.toJson(categories);
            }

            // ==============================================
            // CUSTOMERS — GET all (with optional search)
            // ==============================================
            else if (path.equals("/api/customers") && method.equals("GET")) {
                String search = getQueryParam(query, "search");
                ArrayList<Customer> customers;
                if (search != null && !search.isEmpty()) {
                    customers = CustomerOperations.searchCustomers(search);
                } else {
                    customers = CustomerOperations.getAllCustomers();
                }
                response = gson.toJson(customers);
            }

            // ==============================================
            // CUSTOMERS — GET single by ID
            // ==============================================
            else if (path.matches("/api/customers/\\d+") && method.equals("GET")) {
                int customerId = extractId(path);
                Customer customer = CustomerOperations.getCustomerById(customerId);
                if (customer != null) {
                    response = gson.toJson(customer);
                } else {
                    statusCode = 404;
                    response = "{\"error\": \"Customer not found\"}";
                }
            }

            // ==============================================
            // CUSTOMERS — POST (add new customer)
            // ==============================================
            else if (path.equals("/api/customers") && method.equals("POST")) {
                String body = readBody(exchange);
                Customer customer = gson.fromJson(body, Customer.class);
                int newId = CustomerOperations.addCustomer(customer);
                if (newId > 0) {
                    statusCode = 201;
                    response = "{\"message\": \"Customer registered successfully\", \"customerId\": " + newId + "}";
                } else {
                    statusCode = 500;
                    response = "{\"error\": \"Failed to register customer\"}";
                }
            }

            // ==============================================
            // CUSTOMERS — PUT (update)
            // ==============================================
            else if (path.matches("/api/customers/\\d+") && method.equals("PUT")) {
                int customerId = extractId(path);
                String body = readBody(exchange);
                Customer customer = gson.fromJson(body, Customer.class);
                customer.setCustomerId(customerId);
                boolean success = CustomerOperations.updateCustomer(customer);
                if (success) {
                    response = "{\"message\": \"Customer updated successfully\"}";
                } else {
                    statusCode = 404;
                    response = "{\"error\": \"Customer not found or update failed\"}";
                }
            }

            // ==============================================
            // CUSTOMERS — DELETE
            // ==============================================
            else if (path.matches("/api/customers/\\d+") && method.equals("DELETE")) {
                int customerId = extractId(path);
                boolean success = CustomerOperations.deleteCustomer(customerId);
                if (success) {
                    response = "{\"message\": \"Customer deleted successfully\"}";
                } else {
                    statusCode = 404;
                    response = "{\"error\": \"Customer not found\"}";
                }
            }

            // ==============================================
            // RENTALS — GET all (with optional status filter)
            // ==============================================
            else if (path.equals("/api/rentals") && method.equals("GET")) {
                String status = getQueryParam(query, "status");
                ArrayList<Rental> rentals;
                if (status != null && !status.isEmpty()) {
                    rentals = RentalOperations.getRentalsByStatus(status);
                } else {
                    rentals = RentalOperations.getAllRentals();
                }
                response = gson.toJson(rentals);
            }

            // ==============================================
            // RENTALS — POST (rent a car)
            // Uses TRANSACTION in RentalOperations
            // ==============================================
            else if (path.equals("/api/rentals") && method.equals("POST")) {
                String body = readBody(exchange);
                JsonObject json = gson.fromJson(body, JsonObject.class);
                int carId = json.get("carId").getAsInt();
                int customerId = json.get("customerId").getAsInt();
                String rentalDate = json.get("rentalDate").getAsString();
                String returnDate = json.get("returnDate").getAsString();

                int rentalId = RentalOperations.rentCar(carId, customerId, rentalDate, returnDate);
                if (rentalId > 0) {
                    statusCode = 201;
                    response = "{\"message\": \"Car rented successfully\", \"rentalId\": " + rentalId + "}";
                } else {
                    statusCode = 500;
                    response = "{\"error\": \"Failed to rent car\"}";
                }
            }

            // ==============================================
            // RENTALS — PUT return a car
            // Uses TRANSACTION in RentalOperations
            // ==============================================
            else if (path.matches("/api/rentals/\\d+/return") && method.equals("PUT")) {
                // Extract rental ID from path like /api/rentals/5/return
                String[] parts = path.split("/");
                int rentalId = Integer.parseInt(parts[3]);

                boolean success = RentalOperations.returnCar(rentalId);
                if (success) {
                    response = "{\"message\": \"Car returned successfully\"}";
                } else {
                    statusCode = 500;
                    response = "{\"error\": \"Failed to return car\"}";
                }
            }

            // ==============================================
            // REPORTS — Revenue by category
            // ==============================================
            else if (path.equals("/api/reports/revenue-by-category") && method.equals("GET")) {
                ArrayList<HashMap<String, Object>> data = RentalOperations.getRevenueByCategory();
                response = gson.toJson(data);
            }

            // ==============================================
            // REPORTS — Popular cars
            // ==============================================
            else if (path.equals("/api/reports/popular-cars") && method.equals("GET")) {
                ArrayList<HashMap<String, Object>> data = RentalOperations.getPopularCars();
                response = gson.toJson(data);
            }

            // ==============================================
            // REPORTS — Top customers
            // ==============================================
            else if (path.equals("/api/reports/top-customers") && method.equals("GET")) {
                ArrayList<HashMap<String, Object>> data = RentalOperations.getTopCustomers();
                response = gson.toJson(data);
            }

            // ==============================================
            // REPORTS — Monthly revenue
            // ==============================================
            else if (path.equals("/api/reports/monthly-revenue") && method.equals("GET")) {
                ArrayList<HashMap<String, Object>> data = RentalOperations.getMonthlyRevenue();
                response = gson.toJson(data);
            }

            // ==============================================
            // 404 — Unknown API endpoint
            // ==============================================
            else {
                statusCode = 404;
                response = "{\"error\": \"API endpoint not found: " + method + " " + path + "\"}";
            }

        } catch (RentalException e) {
            // Business logic errors (car not available, active rentals, etc.)
            statusCode = 400;
            response = "{\"error\": \"" + e.getMessage() + "\"}";
        } catch (Exception e) {
            // Unexpected server errors
            statusCode = 500;
            response = "{\"error\": \"Server error: " + e.getMessage() + "\"}";
            e.printStackTrace();
        }

        // Send the JSON response back to the browser
        sendResponse(exchange, statusCode, response);
    }

    // ============================================================
    // STATIC FILE HANDLER — serves HTML, CSS, JS files from web/ folder
    // ============================================================
    static void handleStaticFile(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // If the user visits /, show index.html
        if (path.equals("/")) {
            path = "/index.html";
        }

        // Look for the file in the web/ folder
        File file = new File("web" + path);

        if (file.exists() && file.isFile()) {
            // Figure out what type of file it is
            String contentType = "text/html";
            if (path.endsWith(".css")) {
                contentType = "text/css";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript";
            } else if (path.endsWith(".png")) {
                contentType = "image/png";
            } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (path.endsWith(".ico")) {
                contentType = "image/x-icon";
            }

            // Read the file and send it
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileBytes);
            os.close();
        } else {
            // File not found
            String msg = "404 - File not found: " + path;
            exchange.sendResponseHeaders(404, msg.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(msg.getBytes());
            os.close();
        }
    }

    // ============================================================
    // HELPER: Send JSON response
    // ============================================================
    static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // ============================================================
    // HELPER: Read request body as a string
    // ============================================================
    static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        byte[] bytes = is.readAllBytes();
        return new String(bytes, "UTF-8");
    }

    // ============================================================
    // HELPER: Extract ID from URL path like /api/cars/5
    // ============================================================
    static int extractId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    // ============================================================
    // HELPER: Get a single query parameter value from the URL
    // For example: from "search=Hyundai&status=AVAILABLE" get "search" -> "Hyundai"
    // ============================================================
    static String getQueryParam(String query, String paramName) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        String[] pairs = query.split("&");
        for (int i = 0; i < pairs.length; i++) {
            String[] keyValue = pairs[i].split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                // Decode URL encoding (e.g., %20 -> space)
                try {
                    return java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                } catch (Exception e) {
                    return keyValue[1];
                }
            }
        }

        return null;
    }
}
