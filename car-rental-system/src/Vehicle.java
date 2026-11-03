// ============================================================
// Vehicle.java — Abstract Base Class
// OOP: ABSTRACTION — abstract class cannot be instantiated directly
// OOP: INHERITANCE — this is the parent class for Car
// ============================================================

public abstract class Vehicle {

    // OOP: ENCAPSULATION — fields are private, accessed through getters/setters
    private String brand;
    private String model;
    private int year;

    // Constructor with parameters
    public Vehicle(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    // Empty constructor (needed for creating objects and setting fields later)
    public Vehicle() {
    }

    // --- Getters and Setters ---
    // OOP: ENCAPSULATION — public methods to access private fields

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    // OOP: ABSTRACTION — abstract method, child classes MUST implement this
    public abstract String getDetails();

    // OOP: POLYMORPHISM — child classes can override this method
    @Override
    public String toString() {
        return brand + " " + model + " (" + year + ")";
    }
}
