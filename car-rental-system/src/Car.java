// ============================================================
// Car.java — Extends Vehicle
// OOP: INHERITANCE — Car is a child class of Vehicle
// OOP: POLYMORPHISM — Car overrides getDetails() and toString()
// ============================================================

public class Car extends Vehicle {

    // OOP: ENCAPSULATION — all fields are private
    private int carId;
    private int categoryId;
    private String categoryName;   // filled from JOIN query with car_categories table
    private String licensePlate;
    private String color;
    private double dailyRate;
    private String status;
    private String dateAdded;

    // Constructor that calls parent constructor
    // OOP: INHERITANCE — using super() to call Vehicle's constructor
    public Car(String brand, String model, int year) {
        super(brand, model, year);
    }

    // Empty constructor
    public Car() {
        super();
    }

    // --- Getters and Setters ---
    // OOP: ENCAPSULATION — public getters and setters for private fields

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    // OOP: POLYMORPHISM — overriding the abstract method from Vehicle
    @Override
    public String getDetails() {
        return getBrand() + " " + getModel() + " (" + getYear() + ") - " +
               licensePlate + " - Rs." + dailyRate + "/day - " + status;
    }

    // OOP: POLYMORPHISM — overriding toString() from Vehicle
    @Override
    public String toString() {
        return "Car: " + super.toString() + " [" + licensePlate + "]";
    }
}
