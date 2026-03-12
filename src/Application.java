/*
  Application.java
  Author: Russell Alexander
  This class serves as the entry point for my DMS application. It initilizes the controller and view and 
  starts the main application loop.
*/

import controller.ProjectController;
import view.MenuView;

// Main application class definition
public class Application {
  private ProjectController controller;
  private MenuView view;

  // constructor to initialize the application with the view and controller
  public Application() {
    this.view = new MenuView();
    this.controller = new ProjectController(view);
  }

  // Main method to start the application
  public static void main(String[] args) {
    Application app = new Application();
    app.controller.run();
  }
}

