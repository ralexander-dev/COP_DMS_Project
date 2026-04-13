package exception;

/**
 * Custom exception for validation errors, such as missing required fields or invalid data formats.
 * @author Russell Alexander
*/
public class ValidationException extends RuntimeException {
  /**
   * Constructor to create a ValidationException with a specific message.
   * @param message The detail message for the exception.
  */
  public ValidationException(String message) {
    super(message); // Call the constructor of the superclass (RuntimeException) with the message
  }
}
