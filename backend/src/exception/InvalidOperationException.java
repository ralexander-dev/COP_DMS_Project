package exception;

/**
 * Custom exception for invalid operations, such as trying to update a deleted project or restore an active project.
 * @author Russell Alexander
*/
public class InvalidOperationException extends RuntimeException {
  /**
   * Constructor to create an InvalidOperationException with a specific message.
   * @param message The detail message for the exception.
  */
  public InvalidOperationException(String message) {
    super(message); // Call the constructor of the superclass (RuntimeException) with the message
  }
}
