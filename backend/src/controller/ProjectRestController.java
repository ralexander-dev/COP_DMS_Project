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

/* 
  ProjectRestController
  This class defines all REST API routes used by the DMS web UI. 
  Note: All business logic seen in ProjectController.java is implemented in ProjectService.java for the REST API.
*/
@RestController
// Base path for all routes in this controller is '/api/projects'
@RequestMapping("/api/projects")
public class ProjectRestController {

  private final ProjectService projectService; // initialize ProjectService (essentially the "controller" used internally by the REST API)

  // ProjectRestController constructor for dependency injection of ProjectService
  public ProjectRestController(ProjectService projectService) {
    this.projectService = projectService;
  }

  // GET ROUTES
  // '/api/projects' - Get all projects
  @GetMapping
  public ResponseEntity<List<Project>> getAllProjects() {
    return ResponseEntity.ok(projectService.getAllProjects()); // Return OK with list of all projects
  }

  // '/api/projects/{id}' - Get a project by ID
  @GetMapping("/{id}")
  public ResponseEntity<Project> getProjectById(@PathVariable int id) {
    return ResponseEntity.ok(projectService.getProjectById(id)); // Return OK with project
  }

  // POST ROUTES
  // '/api/projects' - Add a new project with title provided in request body
  @PostMapping
  public ResponseEntity<Project> addProject(@RequestBody Map<String, String> body) {
    return ResponseEntity.status(HttpStatus.CREATED).body(projectService.addProject(body.get("title"))); // Return CREATED with new project
  }

  // '/api/projects/import' - Import projects from uploaded file
  @PostMapping("/import")
  public ResponseEntity<ImportResult> importProjects(@RequestParam("file") MultipartFile file) throws IOException {
    return ResponseEntity.ok(projectService.importProjects(file.getInputStream())); // Return OK with import result
  }

  // PUT/PATCH/DELETE ROUTES
  
  @PutMapping("/{id}/title")
  // '/api/projects/{id}/title' - Update the title of a project by ID 
  public ResponseEntity<Project> updateTitle(@PathVariable int id, @RequestBody Map<String, String> body) {
    return ResponseEntity.ok(projectService.updateTitle(id, body.get("title"))); // Return OK with updated project
  }

  // '/api/projects/{id}/description' - Update the description of a project by ID
  @PutMapping("/{id}/description")
  public ResponseEntity<Project> updateDescription(@PathVariable int id, @RequestBody Map<String, String> body) {
    return ResponseEntity.ok(projectService.updateDescription(id, body.get("description"))); // Return OK with updated project
  }

  // '/api/projects/{id}/tags' - Update the tags of a project by ID
  @PutMapping("/{id}/tags")
  public ResponseEntity<Project> updateTags(@PathVariable int id, @RequestBody Map<String, String> body) {
    return ResponseEntity.ok(projectService.updateTags(id, body.get("tags"))); // Return OK with updated project
  }

  // '/api/projects/{id}/archive' - Toggle the archive status of a project by ID
  @PatchMapping("/{id}/archive")
  public ResponseEntity<Project> toggleArchive(@PathVariable int id) {
    return ResponseEntity.ok(projectService.toggleArchive(id)); // Return OK with updated project
  }

  // '/api/projects/{id}' - Delete a project by ID
  @DeleteMapping("/{id}")
  public ResponseEntity<Project> deleteProject(@PathVariable int id) {
    return ResponseEntity.ok(projectService.deleteProject(id)); // Return OK with deleted project (soft-deleted, not removed from list)
  }

  // '/api/projects/{id}/restore' - Restore a soft-deleted project by ID
  @PostMapping("/{id}/restore")
  public ResponseEntity<Project> restoreProject(@PathVariable int id) {
    return ResponseEntity.ok(projectService.restoreProject(id)); // Return OK with restored project
  }
}
