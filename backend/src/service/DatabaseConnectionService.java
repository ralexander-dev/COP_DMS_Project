package service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Jdbc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseConnectionService {

  @Value("${app.db.name}")
  private String dbName;

  // set to null initially; will be initialized on first connect
  private volatile HikariDataSource dataSource = null;
  private volatile JdbcTemplate jdbcTemplate = null; 

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

  public boolean isConnected() {
    return this.dataSource != null && !this.dataSource.isClosed();
  }

  public void disconnect() {
    if (this.dataSource != null) {
      this.dataSource.close();
      this.dataSource = null;
      this.jdbcTemplate = null;
    }
  }

  public JdbcTemplate getJdbcTemplate() {
    if (!isConnected()) {
      throw new IllegalStateException("Not connected to database");
    }
    return this.jdbcTemplate;
  }
}
