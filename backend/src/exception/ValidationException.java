package exception;

// Custom exception for validation errors, such as missing required fields or invalid input formats
public class ValidationException extends RuntimeException {
  // Constructor that takes a message and passes it to the superclass
  public ValidationException(String message) {
    super(message); // Call the constructor of the superclass (RuntimeException) with the message
  }
}
