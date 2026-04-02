/*
  Application.java
  Author: Russell Alexander
  This class serves as the entry point for my DMS application. It initilizes the controller and view and 
  starts the main application loop.
*/

package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = {"backend", "controller", "service", "config", "exception"})
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args); // run spring boot
  }
}

