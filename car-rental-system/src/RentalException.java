// ============================================================
// RentalException.java — Custom Exception Class
// OOP: EXCEPTION HANDLING — custom exception for business logic errors
// OOP: INHERITANCE — RentalException extends Exception (parent class)
// 
// This exception is thrown when:
// - A car is not available for renting
// - A rental is not found or already completed
// - A car or customer cannot be deleted because of active rentals
// - Input validation fails (empty fields, invalid values)
// ============================================================

public class RentalException extends Exception {

    // Constructor that takes an error message
    public RentalException(String message) {
        super(message);
    }
}
