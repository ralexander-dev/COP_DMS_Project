package view;
import model.Project;
import java.util.List;
import java.util.Scanner;

/**
  * View class responsible for displaying the menu and receiving user input.
  * Depends on the {@link Project} model to receive project data to display.
  * @author Russell Alexander
*/
public class MenuView {
  private Scanner scanner;
  private String separator = "-----------------------------";
  private String errorMessage = "\u001B[31m %s \u001B[0m";
  private String infoMessage = "\u001B[32m %s \u001B[0m";

  /**
    * Constructor, initializes the Scanner for user input.
  */
  public MenuView() {
    this.scanner = new Scanner(System.in);
  }

  /**
    * Method to display the main menu and receive the user's choice.
    * @return The user's menu choice as an integer.
  */
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

  /**
    * Prompt and recieve project title from the user.
    * @return The project title entered by the user.
  */
  public String recieveTitle() {
    System.out.print("Enter project title: ");
    return scanner.nextLine();
  }

  /**
    * Prompt and recieve project ID from the user.
    * @return The project ID entered by the user, or -1 if the input is invalid.

  */
  public int recieveId() {
    System.out.print("Enter project ID: ");
    try {
      return Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      System.out.println(errorMessage.formatted("Invalid input. Please enter a number."));
      return -1; // Return an invalid ID to trigger error handling in the controller
    }
  }

  /**
    * Prompt and recieve file path from the user.
    * @return The file path entered by the user.
  */
  public String recieveFilePath() {
    System.out.print("Enter file path: ");
    return scanner.nextLine();
  }

  /**
    * Prompt and recieve project description from the user.
    * @return The project description entered by the user.
  */
  public String recieveDescription() {
    System.out.print("Enter project description: ");
    return scanner.nextLine();
  }

  /**
    * Prompt and recieve project tags from the user.
    * @return The project tags entered by the user.
  */
  public String recieveTags() {
    System.out.print("Enter project tags (comma separated): ");
    String input = scanner.nextLine();
    return input.trim();
  }

  /**
    * Display an informational message to the user.
    * @param message The message to display.
  */
  public void displayInfo(String message) {
    System.out.println(infoMessage.formatted(message));
  }

  /**
    * Display an error message to the user.
    * @param message The message to display.
  */
  public void displayError(String message) {
    System.out.println(errorMessage.formatted(message));
  }

  /**
    * Display all projects to the user.
    * @param projects The list of projects to display.
  */
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

  /**
    * Display the update menu and return the user's choice.
    * @return The user's menu choice as an integer. 
  */
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
