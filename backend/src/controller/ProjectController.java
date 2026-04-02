package controller;

import model.Project;
import validator.Validator;
import view.MenuView;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
  ProjectController class
  This class contains all business logic related to the DMS application.
*/
public class ProjectController {
  public MenuView view;
  private List<Project> projects;
  private Project selectedProject;
  public Validator validator;

  // ! This variable is used to simulate ID auto-increment that will be implemented in the database.
  private int nextId;

  // constructor to intialize the controller with the user interface object
  public ProjectController(MenuView view) {
    this.view = view;
    this.projects = new ArrayList<>();
    this.validator = new Validator();
    this.nextId = 1; // Initialize nextId to 1
  }

  // main application loop
  public void run() {
    boolean exit = false;
    while (!exit) {
      int choice = view.mainMenu(); // display the main menu and recieve choice from the user
      switch (choice) {
        case 1 -> view.displayAllProjects(projects);
        case 2 -> searchProjectById(view.recieveId());
        case 3 -> addProject();
        case 4 -> loadProjectsFromFile();
        case 5 -> updateProject();
        case 6 -> toggleArchiveProject();
        case 7 -> deleteProject();
        case 8 -> restoreProject();
        case 0 -> exit = true;
        default -> view.displayError("Invalid choice. Please try again.");
      }
    }
  }

  // method to add a new project
  public boolean addProject() {
    view.displayInfo("Add a new project:");
    
    String title = view.recieveTitle(); // recieve title from the user
    selectedProject = new Project(title); // create a tmp project with the received title
    selectedProject.setId(nextId); 

    // validate title
    if (!validator.isValidTitle(selectedProject, projects)) {
      view.displayError("Invalid title. Please try again.");
      return false;
    }
    projects.add(selectedProject); // add the new project to the list of projects
    nextId++; // increment id
    view.displayInfo("Project added successfully.");
    return true;
  }

  // method to search for a project by its ID
  public void searchProjectById(int id) {
    // search for the project with the given ID
    Optional<Project> projectOpt = projects.stream()
      .filter(p -> p.getId() == id)
      .findFirst();

    // if a project was found, display its details, otherwise display an error message
    if (projectOpt.isPresent()) {
      view.displayInfo("Project found: " + projectOpt.get());
    } else {
      view.displayError("Project not found.");
    }
  }

  // method to load projects from file
  public boolean loadProjectsFromFile() {
    view.displayInfo("Enter the file path to load projects from:");
    String path = view.recieveFilePath(); // recieve file path from the user

    // validate file path
    if (path.isBlank()) {
      view.displayError("File path cannot be blank.");
      return false;
    }

    int importedCount = 0;
    int skippedCount = 0;

    // attempt to read file
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line;
      int lineNumber = 0;

      // while the file has more lines attempt to add projects
      while ((line = reader.readLine()) != null) {
        lineNumber++;
        line = line.trim();
        if (line.isBlank()) continue; // if line is blank, skip to next iteration
        selectedProject = new Project(""); // create a tmp project to hold the data while validating
        String[] parts = line.split("\\|", -1); // split line into parts, allowing for empty fields
        selectedProject.setTitle(parts[0].trim()); // set the title of the tmp project
        String newDesc = parts.length > 1 ? parts[1].trim() : ""; // get description if it exists, otherwise set to empty string
        String newTags = parts.length > 2 ? parts[2].trim() : ""; // get tags if they exist, otherwise set to empty string

        // validate title and uniqueness of title and ID
        if (!validator.isValidTitle(selectedProject, projects)) {
          view.displayError("Line %d: Invalid title. Skipping project.".formatted(lineNumber));
          skippedCount++;
          continue;
        }

        // set description and validate description length
        selectedProject.setDescription(newDesc);
        if (!validator.isValidDescription(selectedProject)) {
          view.displayError("Line %d: Description too long. Skipping project.".formatted(lineNumber));
          skippedCount++;
          selectedProject.setDescription(""); // reset description if invalid
          continue;
        }

        // validate tags
        List<String> validTags = new ArrayList<>(); // container for valid tags
        if (!newTags.isBlank()) {
          for (String tag : newTags.split(",")) {
            tag = tag.trim();
            if (!validator.isValidTag(tag)) {
              // display error if tag is invalid
              view.displayError("Line %d: Invalid tag '%s'. Skipping tag.".formatted(lineNumber, tag));
            } else {
              validTags.add(tag); // add validated item to valid tags container
            }
          }
        }
        selectedProject.setTags(validTags); // set tags on tmp project

        selectedProject.setId(nextId); // set id on tmp project
        projects.add(selectedProject); // add project to list
        nextId++; // increment Id
        importedCount++; // increment imported count
      }
    } catch (IOException e) {
      view.displayError("Error reading file: " + e.getMessage());
      return false;
    }

    // display counts
    view.displayInfo("Imported " + importedCount + " projects.");
    if (skippedCount > 0) {
      view.displayError("Skipped " + skippedCount + " invalid projects.");
    }
    return true;
  }

  // method to update a project
  private void updateProject() {
    boolean exit = false;
    while (!exit) {
      int choice = view.updateMenu(); // display updateMenu
      switch (choice) {
        case 1 -> updateProjectTitle();
        case 2 -> updateProjectDescription();
        case 3 -> updateProjectTags();
        case 0 -> exit = true;
        default -> view.displayError("Invalid choice. Please try again.");
      }
    }
  }

  // update a project title
  public boolean updateProjectTitle() {
    view.displayInfo("Enter the ID of the project you want to update:");
    int id = view.recieveId(); // recieve ID of project from user
    // search for project by ID
    Optional<Project> projectOpt = projects.stream()
      .filter(p -> p.getId() == id)
      .findFirst();

    // if no project found
    if (projectOpt.isEmpty()) {
      view.displayError("Project not found.");
      return false;
    }

    selectedProject = projectOpt.get(); // set tmp project to matching search result
    String newTitle = view.recieveTitle(); // recieve new title from user
    String oldTitle = selectedProject.getTitle(); 
    selectedProject.setTitle(newTitle); // set tmp project title to new title

    // validate title
    if (!validator.isValidTitleEntry(newTitle) || !validator.isUniqueTitle(selectedProject, projects)) {
      selectedProject.setTitle(oldTitle); // reset title if invalid
      view.displayError("Invalid title. Please try again.");
      return false;
    }

    view.displayInfo("Project title updated successfully.");
    return true;
  }

  // update a project description
  public boolean updateProjectDescription() {
    view.displayInfo("Enter the ID of the project you want to update:");
    int id = view.recieveId(); // recieve the ID of the project to update
    // search for project by ID
    Optional<Project> projectOpt = projects.stream()
      .filter(p -> p.getId() == id)
      .findFirst();

    // if no project found
    if (projectOpt.isEmpty()) {
      view.displayError("Project not found.");
      return false;
    }

    selectedProject = projectOpt.get(); // set tmp project to matching search result
    String newDesc = view.recieveDescription(); // recieve description from the user
    String oldDesc = selectedProject.getDescription();
    selectedProject.setDescription(newDesc); // set description on tmp project to new description

    // validate description
    if (!validator.isValidDescription(selectedProject)) {
      selectedProject.setDescription(oldDesc);
      view.displayError("Description too long. Please try again.");
      return false;
    }

    view.displayInfo("Project description updated successfully.");
    return true;
  }

  // update a projects tags
  public boolean updateProjectTags() {
    view.displayInfo("Enter the ID of the project you want to update:");
    int id = view.recieveId(); // recieve the ID of the project to update
    // search for project by ID
    Optional<Project> projectOpt = projects.stream()
      .filter(p -> p.getId() == id)
      .findFirst();

    // if no project found
    if (projectOpt.isEmpty()) {
      view.displayError("Project not found.");
      return false;
    }

    selectedProject = projectOpt.get(); // set tmp project to matching search result
    String newTags = view.recieveTags(); // recieve new tags from user
    List<String> validTags = new ArrayList<>(); // container for valid tags
    // validate tags
    if (!newTags.isBlank()) {
      for (String tag : newTags.split(",")) {
        tag = tag.trim();
        if (!validator.isValidTag(tag)) {
          view.displayError("Invalid tag '%s'. Skipping tag.".formatted(tag));
        } else {
          validTags.add(tag);
        }
      }
    }

    if (validTags.isEmpty() && !newTags.isBlank()) { // if there were tags entered but all were invalid, display error and return
      view.displayError("No valid tags entered. Please try again.");
      return false;
    } 
    selectedProject.setTags(validTags); // set tags on tmp project to validated tags

    view.displayInfo("Project tags updated successfully.");
    return true;
  }

  // method to toggle a projects archived status
  public boolean toggleArchiveProject() {
    view.displayInfo("Enter the ID of the project you want to archive/unarchive:");
    int id = view.recieveId(); // recieve the ID of the project to archive/unarchive
    // search for project by ID
    Optional<Project> projectOpt = projects.stream()
      .filter(p -> p.getId() == id)
      .findFirst();

    // if no project found
    if (projectOpt.isEmpty()) {
      view.displayError("Project not found.");
      return false;
    }
    
    selectedProject = projectOpt.get(); // set tmp project to matching search result
    selectedProject.setArchived(!selectedProject.isArchived()); // toggle archived status on tmp project
    String status = selectedProject.isArchived() ? "archived" : "unarchived"; // determine status message based on new archived status
    view.displayInfo("Project %s successfully.".formatted(status));
    return true;
  }

  // method to delete a project  (sets deleted status to true, simulating a soft delete)
  public boolean deleteProject() {
    view.displayInfo("Enter the ID of the project you want to delete:");
    int id = view.recieveId(); // recieve the ID of the project to delete
    // search for project by ID
    Optional<Project> projectOpt = projects.stream()
      .filter(p -> p.getId() == id)
      .findFirst();

    // if no project found
    if (projectOpt.isEmpty()) {
      view.displayError("Project not found.");
      return false;
    }

    selectedProject = projectOpt.get(); // set tmp project to matching search result
    if (selectedProject.isDeleted()) { // if project is already marked as deleted, display error and return
      view.displayError("Project is already deleted.");
      return false;
    }

    selectedProject.setDeleted(true); // mark project as deleted
    view.displayInfo("Project deleted successfully.");
    return true;
  }

  // method to restore a deleted project (sets deleted status to false)
  public boolean restoreProject() { 
    view.displayInfo("Enter the ID of the project you want to restore:");
    int id = view.recieveId(); // recieve the ID of the project to restore
    Optional<Project> projectOpt = projects.stream()
      .filter(p -> p.getId() == id)
      .findFirst();

    // if no project found
    if (projectOpt.isEmpty()) {
      view.displayError("Project not found.");
      return false;
    }

    selectedProject = projectOpt.get(); // set tmp project to matching search result
    if (!selectedProject.isDeleted()) {
      view.displayError("Project is not deleted.");
      return false;
    }

    selectedProject.setDeleted(false); // mark project as not deleted
    view.displayInfo("Project restored successfully.");
    return true;
  }
}
