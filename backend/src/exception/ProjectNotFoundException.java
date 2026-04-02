package exception;

// Custom exception for when a project is not found by ID
public class ProjectNotFoundException extends RuntimeException {
  // Constructor that takes a message and passes it to the superclass
  public ProjectNotFoundException(String message) {
    super(message); // Call the constructor of the superclass (RuntimeException) with the message
  }
}
