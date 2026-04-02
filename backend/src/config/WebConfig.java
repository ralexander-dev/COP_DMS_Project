package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
  Backend configuration class
*/
@Configuration
public class WebConfig implements WebMvcConfigurer {
  // CORS configuration to allow requests from the frontend
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
      .allowedOrigins("http://localhost:3000")
      .allowedMethods("*")
      .allowedHeaders("*");
  }
}
