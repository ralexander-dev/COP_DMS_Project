package dto;

/**
 * DTO for database connection status, indicating whether the application is currently connected to the database.
 * @author Russell Alexander
*/
public class DbStatus {
  public boolean connected;

  public DbStatus(boolean connected) {
    this.connected = connected;
  }
}
