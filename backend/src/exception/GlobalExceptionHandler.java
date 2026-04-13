package exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

/**
  * Handles a variety of exceptions thrown by the REST API and returns appropriate HTTP responses by grouping all exception handling logic in one place. 
  * @author Russell Alexander
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle ProjectNotFoundException - 404 Not Found
   * @param e The exception instance
   * @return ResponseEntity with error message and NOT_FOUND status
   */
  @ExceptionHandler(ProjectNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleProjectNotFound(ProjectNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage())); // Return NOT_FOUND with error message
  }

  /**
   * Handle ValidationException - 400 Bad Request
   * @param e The exception instance
   * @return ResponseEntity with error message and BAD_REQUEST status
  */
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, String>> handleValidation(ValidationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage())); // Return BAD_REQUEST with error message
  }

  /**
   * Handle InvalidOperationException - 409 Conflict
   * @param e The exception instance
   * @return ResponseEntity with error message and CONFLICT status
  */
  @ExceptionHandler(InvalidOperationException.class)
  public ResponseEntity<Map<String, String>> handleInvalidOperation(InvalidOperationException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage())); // Return CONFLICT with error message
  }

  /**
   * Handle IOException - 400 Bad Request
   * @param e The exception instance
   * @return ResponseEntity with error message and BAD_REQUEST status
  */
  @ExceptionHandler(java.io.IOException.class)
  public ResponseEntity<Map<String, String>> handleIOException(java.io.IOException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage())); // Return BAD_REQUEST with error message
  }
}
