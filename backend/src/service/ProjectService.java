package service;
import dto.ImportResult;
import dto.SkippedEntry;
import exception.InvalidOperationException;
import exception.ProjectNotFoundException;
import exception.ValidationException;
import model.Project;
import validator.Validator;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import repository.ProjectRepository; 

/**
 * Service for handling business logic related to Projects. 
 * Depends on {@link ProjectRepository} for db access and {@link Validator} for validating inputs.
 * @author Russell Alexander
*/
@Service
public class ProjectService {
  //private List<Project> projects = new ArrayList<>();
  //private int nextId = 1;
  private Validator validator = new Validator();
  private final ProjectRepository projectRepository; // repository for DB access

  /**
   * Constructor, initializes the ProjectRepository for database access.
   * @param projectRepository The ProjectRepository for database operations.
  */
  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  /**
   * Get all projects from the database.
   * @return A list of all projects.
  */
  public List<Project> getAllProjects() {
    return projectRepository.findAll();
  }

  /**
   * Get a project by its ID.
   * @param id The ID of the project to retrieve.
   * @return The project with the specified ID.
   * @throws ProjectNotFoundException if no project with the given ID exists.
  */
  public Project getProjectById(int id) throws ProjectNotFoundException {
    return projectRepository.findById(id)
      .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + id));
  }

  /**
   * Validate new project and pass to {@link ProjectRepository} to save to db
   * @param title The title of the project.
   * @return The newly added project.
   * @throws ValidationException if the title is invalid or duplicate.
  */
  public Project addProject(String title) throws ValidationException {
    Project project = new Project(title);
    //project.setId(nextId);
    if (!validator.isValidTitle(project, getAllProjects())) {
      throw new ValidationException("Invalid or duplicate title: " + title);
    }
    return projectRepository.save(project); // save to DB
  }

  /**
   * Import projects from an input stream.
   * @param input The input stream containing project data.
   * @return The result of the import operation, including imported count and skipped entries.
   * @throws IOException if an I/O error occurs while reading the input stream.
  */
  public ImportResult importProjects(InputStream input) throws IOException {
    int importedCount = 0;
    List<SkippedEntry> skippedEntries = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
      String line;

      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isBlank()) continue;

        String[] parts = line.split("\\|", -1);
        String title = parts[0].trim();
        String desc = parts.length > 1 ? parts[1].trim() : "";
        String tagsStr = parts.length > 2 ? parts[2].trim() : "";

        Project project = new Project(title);
        //project.setId(nextId);
        if (!validator.isValidTitle(project, getAllProjects())) {
          SkippedEntry se = new SkippedEntry();
          se.entry = line;
          se.reason = "Invalid or duplicate title";
          skippedEntries.add(se);
          continue;
        }

        project.setDescription(desc);
        if (!validator.isValidDescription(project)) {
          SkippedEntry se = new SkippedEntry();
          se.entry = line;
          se.reason = "Invalid description";
          skippedEntries.add(se);
          continue;
        }

        List<String> validTags = new ArrayList<>();
        if (!tagsStr.isBlank()) {
          for (String tag : tagsStr.split(",")) {
            tag = tag.trim();
            if (validator.isValidTag(tag)) {
              validTags.add(tag);
            }
          }
        }
        project.setTags(validTags);

        projectRepository.save(project); // save to DB
        importedCount++;
      }
    } catch (IOException e) {
      throw new IOException("Error reading input stream: " + e.getMessage());
    }

    ImportResult result = new ImportResult();
    result.importedCount = importedCount;
    result.skippedEntries = skippedEntries;
    return result;
  }

  /**
   * Update the title of a project.
   * @param id The ID of the project to update.
   * @param title The new title for the project.
   * @return The updated project.
   * @throws ValidationException if the new title is invalid or duplicate.
  */
  public Project updateTitle(int id, String title) throws ValidationException {
    Project project = getProjectById(id);
    String oldTitle = project.getTitle();
    project.setTitle(title);
    if (!validator.isValidTitleEntry(title) || !validator.isUniqueTitle(project, projectRepository.findAll())) {
      project.setTitle(oldTitle);
      throw new ValidationException("Invalid or duplicate title: " + title);
    }
    return projectRepository.update(project);
  }

  /**
   * Update the description of a project.
   * @param id The ID of the project to update.
   * @param description The new description for the project.
   * @return The updated project.
   * @throws ValidationException if the new description is invalid.
  */
  public Project updateDescription(int id, String description) {
    Project project = getProjectById(id);
    String oldDescription = project.getDescription();
    project.setDescription(description);
    if (!validator.isValidDescription(project)) {
      project.setDescription(oldDescription);
      throw new ValidationException("Description exceeds maximum allowed length");
    }
    return projectRepository.update(project);
  }

  /**
   * Update the tags of a project.
   * @param id The ID of the project to update.
   * @param tags A comma-separated string of new tags for the project.
   * @return The updated project.
   * @throws ValidationException if no valid tags are provided.
  */
  public Project updateTags(int id, String tags) {
    Project project = getProjectById(id);
    List<String> validTags = new ArrayList<>();
    for (String tag : tags.split(",")) {
      tag = tag.trim();
      if (validator.isValidTag(tag)) {
        validTags.add(tag);
      }
    }
    if (validTags.isEmpty()) {
      throw new ValidationException("No valid tags provided");
    }
    project.setTags(validTags);
    return projectRepository.update(project);
  }

  /**
   * Toggle the archived state of a project.
   * @param id The ID of the project to update.
   * @return The updated project.
  */
  public Project toggleArchive(int id) {
    Project project = getProjectById(id);
    project.setArchived(!project.isArchived());
    return projectRepository.update(project);
  }

  /**
   * Soft-delete a project.
   * @param id The ID of the project to delete.
   * @return The deleted project.
   * @throws InvalidOperationException if the project is already deleted.
  */
  public Project deleteProject(int id) {
    Project project = getProjectById(id);
    if (project.isDeleted()) {
      throw new InvalidOperationException("Project is already deleted");
    }
    project.setDeleted(true);
    return projectRepository.update(project);
  }

  /**
   * Restore a soft-deleted project.
   * @param id The ID of the project to restore.
   * @return The restored project.
   * @throws InvalidOperationException if the project is not deleted.
  */
  public Project restoreProject(int id) {
    Project project = getProjectById(id);
    if (!project.isDeleted()) {
      throw new InvalidOperationException("Project is not deleted");
    }
    project.setDeleted(false);
    return projectRepository.update(project);
  }
}
