package controller;

import dto.DbConnectRequest;
import dto.DbStatus;
import service.DatabaseConnectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/db")
public class DatabaseController {
  
  private final DatabaseConnectionService dbService; // service for managing DB connection

  // constructor injection of DatabaseConnectionService
  public DatabaseController(DatabaseConnectionService dbService) {
    this.dbService = dbService;
  }

  // GET /api/db/status - check if connected
  @GetMapping("/status")
  public ResponseEntity<DbStatus> getDbStatus() {
    boolean isConnected = dbService.isConnected();
    return ResponseEntity.ok(new DbStatus(isConnected));
  }

  // POST /api/db/connect - connect to db with provided creds
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

  // POST /api/db/disconnect - disconnect from db
  @PostMapping("/disconnect")
  public ResponseEntity<?> disconnect() {
    dbService.disconnect();
    return ResponseEntity.ok(new DbStatus(false));
  }
}
