// ============================================================
// Customer.java — Customer Model Class
// OOP: ENCAPSULATION — all fields are private, accessed through public getters/setters
// OOP: CLASS AND OBJECTS — this class is used to create Customer objects
// ============================================================

public class Customer {

    // OOP: ENCAPSULATION — private fields
    private int customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String registrationDate;

    // Constructor with parameters
    public Customer(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // Empty constructor
    public Customer() {
    }

    // --- Getters and Setters ---
    // OOP: ENCAPSULATION — public methods to access private fields

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + email + ")";
    }
}
