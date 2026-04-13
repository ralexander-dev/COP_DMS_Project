package controller;

import dto.DbConnectRequest;
import dto.DbStatus;
import service.DatabaseConnectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


/** 
  * Controller for handling database connection related API routes. 
  * Depends on {@link DatabaseConnectionService} for managing the actual connection logic and status.
  * @author Russell Alexander
*/
@RestController
@RequestMapping("/api/db")
public class DatabaseController {
  
  private final DatabaseConnectionService dbService; // service for managing DB connection

  /**
   * Constructor to initialize the DatabaseController with a DatabaseConnectionService instance.
   * @param dbService The DatabaseConnectionService instance to be injected.
   */
  public DatabaseController(DatabaseConnectionService dbService) {
    this.dbService = dbService;
  }

  /**
   * Retrieves the current status of the database connection.
   * @return ResponseEntity containing the database connection status.
  */
  @GetMapping("/status")
  public ResponseEntity<DbStatus> getDbStatus() {
    boolean isConnected = dbService.isConnected();
    return ResponseEntity.ok(new DbStatus(isConnected));
  }

  /**
   * Connects to the database using the provided credentials.
   * @param request The database connection request containing username, password, host, and port.
   * @return ResponseEntity indicating the result of the connection attempt.
  */
  @PostMapping("/connect")
  public ResponseEntity<?> connect(@RequestBody DbConnectRequest request) {
    try {
      dbService.connect(request.username, request.password, request.host, request.port);
      return ResponseEntity.ok(new DbStatus(true));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("message", "Connection failed: " + e.getMessage()));
    }
  }

  /**
   * Disconnects from the database.
   * @return ResponseEntity indicating that the disconnection was successful.
  */
  @PostMapping("/disconnect")
  public ResponseEntity<?> disconnect() {
    dbService.disconnect();
    return ResponseEntity.ok(new DbStatus(false));
  }
}
