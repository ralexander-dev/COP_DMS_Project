package controller;
import dto.ImportResult;
import model.Project;
import service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/** 
  * Defines all REST API routes used by the DMS web UI. 
  * Depends on {@link ProjectService} for handling business logic and database interactions related to projects, 
  * and on the {@link Project} model for representing project data in the API responses.
  * @author Russell Alexander
*/
@RestController
// Base path for all routes in this controller is '/api/projects'
@RequestMapping("/api/projects")
public class ProjectRestController {

  private final ProjectService projectService; // initialize ProjectService (essentially the "controller" used internally by the REST API)

  /**
   * Constructor to initialize the ProjectRestController with a ProjectService instance.
   * @param projectService The ProjectService instance to be injected.
   */
  public ProjectRestController(ProjectService projectService) {
    this.projectService = projectService;
  }

  // GET ROUTES

  /**
   * Route handler for getting all projects.
   * @return ResponseEntity with list of all projects and OK status
  */
  @GetMapping
  public ResponseEntity<List<Project>> getAllProjects() {
    return ResponseEntity.ok(projectService.getAllProjects()); // Return OK with list of all projects
  }

  /**
   * Route handler for getting a project by ID
   * @param id The ID of the project to retrieve.
   * @return ResponseEntity with the project and OK status
  */
  @GetMapping("/{id}")
  public ResponseEntity<Project> getProjectById(@PathVariable int id) {
    return ResponseEntity.ok(projectService.getProjectById(id)); // Return OK with project
  }

  // POST ROUTES
  /**
   * Route handler for adding a new project
   * @param body The request body containing the title of the project.
   * @return ResponseEntity with the newly added project and CREATED status
  */
  @PostMapping
  public ResponseEntity<Project> addProject(@RequestBody Map<String, String> body) {
    return ResponseEntity.status(HttpStatus.CREATED).body(projectService.addProject(body.get("title"))); // Return CREATED with new project
  }

  /**
   * Route handler for importing projects from an uploaded file.
   * @param file The uploaded file containing project data.
   * @return ResponseEntity with the import result and OK status
  */
  @PostMapping("/import")
  public ResponseEntity<ImportResult> importProjects(@RequestParam("file") MultipartFile file) throws IOException {
    return ResponseEntity.ok(projectService.importProjects(file.getInputStream())); // Return OK with import result
  }

  // PUT/PATCH/DELETE ROUTES
  
  /**
   * Route handler for updating the title of a project by ID.
   * @param id The ID of the project to update.
   * @param body The request body containing the new title.
   * @return ResponseEntity with the updated project and OK status
  */
  @PutMapping("/{id}/title")
  // '/api/projects/{id}/title' - Update the title of a project by ID 
  public ResponseEntity<Project> updateTitle(@PathVariable int id, @RequestBody Map<String, String> body) {
    return ResponseEntity.ok(projectService.updateTitle(id, body.get("title"))); // Return OK with updated project
  }

  /**
   * Route handler for updating the description of a project by ID.
   * @param id The ID of the project to update.
   * @param body The request body containing the new description.
   * @return ResponseEntity with the updated project and OK status
  */
  @PutMapping("/{id}/description")
  public ResponseEntity<Project> updateDescription(@PathVariable int id, @RequestBody Map<String, String> body) {
    return ResponseEntity.ok(projectService.updateDescription(id, body.get("description"))); // Return OK with updated project
  }

  /**
   * Route handler for updating the tags of a project by ID.
   * @param id The ID of the project to update.
   * @param body The request body containing the new tags.
   * @return ResponseEntity with the updated project and OK status
  */
  @PutMapping("/{id}/tags")
  public ResponseEntity<Project> updateTags(@PathVariable int id, @RequestBody Map<String, String> body) {
    return ResponseEntity.ok(projectService.updateTags(id, body.get("tags"))); // Return OK with updated project
  }

  /**
   * Route handler for toggling the archive status of a project by ID.
   * @param id The ID of the project to update.
   * @return ResponseEntity with the updated project and OK status
  */
  @PatchMapping("/{id}/archive")
  public ResponseEntity<Project> toggleArchive(@PathVariable int id) {
    return ResponseEntity.ok(projectService.toggleArchive(id)); // Return OK with updated project
  }

  /**
   * Route handler for deleting a project by ID.
   * @param id The ID of the project to delete.
   * @return ResponseEntity with the deleted project and OK status
  */
  @DeleteMapping("/{id}")
  public ResponseEntity<Project> deleteProject(@PathVariable int id) {
    return ResponseEntity.ok(projectService.deleteProject(id)); // Return OK with deleted project (soft-deleted, not removed from list)
  }

  /**
   * Route handler for restoring a soft-deleted project by ID.
   * @param id The ID of the project to restore.
   * @return ResponseEntity with the restored project and OK status
  */
  @PostMapping("/{id}/restore")
  public ResponseEntity<Project> restoreProject(@PathVariable int id) {
    return ResponseEntity.ok(projectService.restoreProject(id)); // Return OK with restored project
  }
}
