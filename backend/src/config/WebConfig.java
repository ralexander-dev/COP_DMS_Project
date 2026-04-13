package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
  * Backend configuration class
  * @author Russell Alexander
*/
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * CORS config to allow requests from the frontend UI
   * @param registry SpringBoot CorsRegistry
  */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
      .allowedOrigins("http://localhost:3000")
      .allowedMethods("*")
      .allowedHeaders("*");
  }

  /**
   * Configures view controllers for the application.
   * It redirects the "/docs" URL to the API documentation index page.
   * @param registry SpringBoot ViewControllerRegistry
  */
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/docs", "/docs/index.html");
  }

  /**
   * Configures resource handlers for serving static files.
   * It serves the API documentation files from the specified location.
   * @param registry SpringBoot ResourceHandlerRegistry
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/docs/**")
      .addResourceLocations("file:/app/target/reports/apidocs/");
  }
}
