package dto;

/**
 * DTO for database connection request, containing the necessary credentials and connection details.
 * @author Russell Alexander
*/
public class DbConnectRequest {
  public String username;
  public String password;
  public String host;
  public Integer port;
}
