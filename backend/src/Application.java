package backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;


/**
  * Entry point for the Spring Boot application.
  * @author Russell Alexander
*/
@SpringBootApplication(
  // specify base packages to scan for components
  scanBasePackages = {"backend", "controller", "service", "config", "exception", "repository"},
  // exclude auto-configurations for DataSource and JdbcTemplate so they can be managed manually
  exclude = {
    DataSourceAutoConfiguration.class,
    JdbcTemplateAutoConfiguration.class
  }
)
public class Application {

  /**
   * Default constructor for the Application class.
  */
  public Application() {}

  /**
   * Main method to start the Spring Boot application.
   * @param args Command-line arguments (not used).
  */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args); // run spring boot
  }
}

