/*
  Application.java
  Author: Russell Alexander
  This class serves as the entry point for my DMS application. It starts the
  Spring Boot application and initializes all components.
*/

package backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;


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
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args); // run spring boot
  }
}

