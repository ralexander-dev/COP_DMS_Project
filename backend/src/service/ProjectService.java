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

/*
  ProjectService.java
  Author: Russell Alexander
  This class contains the business logic for the DMS application when accessed via web UI.
*/
@Service
public class ProjectService {
  //private List<Project> projects = new ArrayList<>();
  //private int nextId = 1;
  private Validator validator = new Validator();
  private final ProjectRepository projectRepository; // repository for DB access

  // constructor injection of ProjectRepository
  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  // get all projects
  public List<Project> getAllProjects() {
    return projectRepository.findAll();
  }

  // get project by ID; throws ProjectNotFoundException if not found
  public Project getProjectById(int id) {
    return projectRepository.findById(id)
      .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + id));
  }

  // add project; throws ValidationException if title is invalid
  public Project addProject(String title) {
    Project project = new Project(title);
    //project.setId(nextId);
    if (!validator.isValidTitle(project, getAllProjects())) {
      throw new ValidationException("Invalid or duplicate title: " + title);
    }
    return projectRepository.save(project); // save to DB
  }

  // Import Projects (InputStream input)
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
    }

    ImportResult result = new ImportResult();
    result.importedCount = importedCount;
    result.skippedEntries = skippedEntries;
    return result;
  }

  // update project title; throws ProjectNotFoundException or ValidationException
  public Project updateTitle(int id, String title) {
    Project project = getProjectById(id);
    String oldTitle = project.getTitle();
    project.setTitle(title);
    if (!validator.isValidTitleEntry(title) || !validator.isUniqueTitle(project, projectRepository.findAll())) {
      project.setTitle(oldTitle);
      throw new ValidationException("Invalid or duplicate title: " + title);
    }
    return projectRepository.update(project);
  }

  // update project description; throws ProjectNotFoundException or ValidationException
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

  // update project tags from a comma-separated string; throws ProjectNotFoundException or ValidationException
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

  // toggle archived state; throws ProjectNotFoundException
  public Project toggleArchive(int id) {
    Project project = getProjectById(id);
    project.setArchived(!project.isArchived());
    return projectRepository.update(project);
  }

  // soft-delete project; throws ProjectNotFoundException or InvalidOperationException
  public Project deleteProject(int id) {
    Project project = getProjectById(id);
    if (project.isDeleted()) {
      throw new InvalidOperationException("Project is already deleted");
    }
    project.setDeleted(true);
    return projectRepository.update(project);
  }

  // restore a soft-deleted project; throws ProjectNotFoundException or InvalidOperationException
  public Project restoreProject(int id) {
    Project project = getProjectById(id);
    if (!project.isDeleted()) {
      throw new InvalidOperationException("Project is not deleted");
    }
    project.setDeleted(false);
    return projectRepository.update(project);
  }
}
