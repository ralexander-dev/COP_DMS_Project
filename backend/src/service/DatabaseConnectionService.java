package service;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
  Service class for managing db connections
  @author Russell Alexander
*/
@Service
public class DatabaseConnectionService {
  
  @Value("${app.db.name}")
  private String dbName;

  // set to null initially; will be initialized on first connect
  private volatile HikariDataSource dataSource = null;
  private volatile JdbcTemplate jdbcTemplate = null; 

  /**
   * Connect to a MySQL db
   * @param username
   * @param password
   * @param host The database host.
   * @param port The database port.
   * @throws java.sql.SQLException if a database access error occurs.
  */
  public void connect(String username, String password, String host, Integer port) throws java.sql.SQLException {
    HikariDataSource db = new HikariDataSource();
    String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
      host != null ? host : "mysql",
      port != null ? port : 3306,
      dbName);
    db.setJdbcUrl(jdbcUrl);
    db.setUsername(username);
    db.setPassword(password);
    db.setConnectionTimeout(5000); // 5 second timeout
    db.getConnection().close(); // Test connection immediately
    if (this.dataSource != null) this.dataSource.close(); // close old connection if reconnecting
    this.dataSource = db;
    this.jdbcTemplate = new JdbcTemplate(this.dataSource); 
  }

  /**
   * Check if the service is connected to the database
   * @return true if connected, false otherwise
  */
  public boolean isConnected() {
    return this.dataSource != null && !this.dataSource.isClosed();
  }

  /**
   * Disconnect from the database
  */
  public void disconnect() {
    if (this.dataSource != null) {
      this.dataSource.close();
      this.dataSource = null;
      this.jdbcTemplate = null;
    }
  }

  /**
   * Get the JdbcTemplate for executing queries.
   * @return The JdbcTemplate for executing queries.
   * @throws IllegalStateException if not connected to the database.
  */
  public JdbcTemplate getJdbcTemplate() throws IllegalStateException {
    if (!isConnected()) {
      throw new IllegalStateException("Not connected to database");
    }
    return this.jdbcTemplate;
  }
}
