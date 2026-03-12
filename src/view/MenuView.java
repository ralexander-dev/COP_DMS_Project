package view;

import model.Project;
import java.util.List;
import java.util.Scanner;
/*
  MenuView class
  Defines the view component of the DMS application, responsible for displaying the menu and receiving user input. 
  It provides methods to display information, errors, and project details, as well as methods to receive input for project attributes and menu choices.
*/
public class MenuView {
  private Scanner scanner;
  private String separator = "-----------------------------";
  private String errorMessage = "\u001B[31m %s \u001B[0m";
  private String infoMessage = "\u001B[32m %s \u001B[0m";

  // constructor to initialize the view with a scanner for user input
  public MenuView() {
    this.scanner = new Scanner(System.in);
  }

  // method to display the main menu and receive the user's choice
  public int mainMenu() {
    System.out.println(separator);
    System.out.println(infoMessage.formatted("Main Menu"));
    System.out.println(separator);
    System.out.println("1. View all projects");
    System.out.println("2. Search projects by ID");
    System.out.println("3. Add a Project (Manual)");
    System.out.println("4. Add Projects (File Upload)");
    System.out.println("5. Edit a Project");
    System.out.println("6. Archive/Unarchive a Project");
    System.out.println("7. Delete a Project");
    System.out.println("8. Restore a Deleted Project");
    System.out.println("0. Exit");
    System.out.println(separator);
    System.out.print("Enter your choice: ");
    try {
      return Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      System.out.println(errorMessage.formatted("Invalid input. Please enter a number."));
      return -1; // Return an invalid choice to trigger error handling in the controller
    }
  }

  // receive the project title from the user
  public String recieveTitle() {
    System.out.print("Enter project title: ");
    return scanner.nextLine();
  }

  // receive the project ID from the user
  public int recieveId() {
    System.out.print("Enter project ID: ");
    try {
      return Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      System.out.println(errorMessage.formatted("Invalid input. Please enter a number."));
      return -1; // Return an invalid ID to trigger error handling in the controller
    }
  }

  // recieve file path from the user
  public String recieveFilePath() {
    System.out.print("Enter file path: ");
    return scanner.nextLine();
  }

  // recieve description from the user
  public String recieveDescription() {
    System.out.print("Enter project description: ");
    return scanner.nextLine();
  }

  // recieve tags from the user 
  public String recieveTags() {
    System.out.print("Enter project tags (comma separated): ");
    String input = scanner.nextLine();
    return input.trim();
  }

  // display info message
  public void displayInfo(String message) {
    System.out.println(infoMessage.formatted(message));
  }

  // display error message
  public void displayError(String message) {
    System.out.println(errorMessage.formatted(message));
  }

  // display all projects
  public void displayAllProjects(List<Project> projects) {
    if (projects.isEmpty()) {
      System.out.println(infoMessage.formatted("No projects found."));
    } else {
      System.out.println(separator);
      System.out.println(infoMessage.formatted("All Projects:"));
      System.out.println(separator);
      for (Project project : projects) {
        System.out.println(project);
        System.out.println(separator);
      }
    }
  }

  // display update menu and return choice
  public int updateMenu() {
    System.out.println(separator);
    System.out.println(infoMessage.formatted("Update Project"));
    System.out.println(separator);
    System.out.println("1. Update Title");
    System.out.println("2. Update Description");
    System.out.println("3. Update Tags");
    System.out.println("0. Return to Main Menu");
    System.out.println(separator);
    System.out.print("Enter your choice: ");
    try {
      return Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      System.out.println(errorMessage.formatted("Invalid input. Please enter a number."));
      return -1; // Return an invalid choice to trigger error handling in the controller
    }
  }
}
