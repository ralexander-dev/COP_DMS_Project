package exception;

/**
 * Custom exception for when a project is not found by ID.
 * @author Russell Alexander
*/
// Custom exception for when a project is not found by ID
public class ProjectNotFoundException extends RuntimeException {
  /**
   * Constructor to create a ProjectNotFoundException with a specific message.
   * @param message The detail message for the exception.
  */
  public ProjectNotFoundException(String message) {
    super(message); // Call the constructor of the superclass (RuntimeException) with the message
  }
}
