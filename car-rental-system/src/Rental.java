// ============================================================
// Rental.java — Rental Model Class
// OOP: COMPOSITION — Rental "has-a" car info and "has-a" customer info
// OOP: ENCAPSULATION — all fields are private with public getters/setters
// ============================================================

public class Rental {

    // OOP: ENCAPSULATION — private fields for rental data
    private int rentalId;
    private int carId;
    private int customerId;
    private String rentalDate;
    private String returnDate;
    private String actualReturnDate;
    private double totalCost;
    private String status;

    // OOP: COMPOSITION — these fields hold car and customer info from JOIN queries
    // Instead of storing full Car and Customer objects, we store the display fields
    // This data comes from INNER JOIN with cars and customers tables
    private String carBrand;
    private String carModel;
    private String licensePlate;
    private String customerName;
    private double dailyRate;

    // Constructor
    public Rental() {
    }

    // --- Getters and Setters for rental fields ---

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(String rentalDate) {
        this.rentalDate = rentalDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(String actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // --- Getters and Setters for composed (JOIN) fields ---
    // OOP: COMPOSITION — data from related tables

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    @Override
    public String toString() {
        return "Rental #" + rentalId + ": " + customerName + " -> " +
               carBrand + " " + carModel + " [" + status + "]";
    }
}
