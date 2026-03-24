package controller;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import view.MenuView;
import static org.mockito.Mockito.*;


/*
  ProjectControllerTest
  Unit tests for the ProjectController class
*/
public class ProjectControllerTest {

  private MenuView mockView;
  private ProjectController controller;

  @BeforeEach
  void setUp() {
    mockView = mock(MenuView.class);
    controller = new ProjectController(mockView);
  }

  /*---------------------------------------------------------------------------------------------------------------
  CREATE TESTS - Ensures that the controller correctly creates projects with valid input and rejects invalid input 
  ---------------------------------------------------------------------------------------------------------------*/

  // @ Tests for valid project titles

  @Test void addProject_validTitle_returnsTrue() {
    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    assertTrue(controller.addProject(), "Valid project title should return true");

    // Adding another project with a different valid title should also return true
    when(mockView.recieveTitle()).thenReturn("Another Valid Title"); // > Another valid project title
    assertTrue(controller.addProject(), "Another valid project title should return true");

    // Adding a project with a title that is exactly 200 characters long should return true
    when(mockView.recieveTitle()).thenReturn("a".repeat(200)); // > Valid project title with 200 characters
    assertTrue(controller.addProject(), "Project title with 200 characters should return true");
  }

  // @ Tests for invalid project titles

  @Test void addproject_invalidTitles_returnFalse() {
    when(mockView.recieveTitle()).thenReturn(""); // > Empty project title
    assertFalse(controller.addProject(), "Empty project title should return false");

    when(mockView.recieveTitle()).thenReturn("a".repeat(201)); // > Project title with more than 200 characters
    assertFalse(controller.addProject(), "Project title with more than 200 characters should return false");

    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    controller.addProject(); // add a valid project first
    controller.addProject(); // attempt to add a project with the same title
    assertFalse(controller.addProject(), "Duplicate project title should return false");
  }

  /*---------------------------------------------------------------------------------------------------------------
  READ TESTS - Ensures that the controller correctly reads projects
  ---------------------------------------------------------------------------------------------------------------*/
  
  // @ Test for reading selected project by ID

  @Test void searchProjectById_existingId_displaysProject() {
    when(mockView.recieveTitle()).thenReturn("ReadTestProject"); // > Valid project title
    controller.addProject(); // adds project with id = 1 
    controller.searchProjectById(1); // > Existing project ID
    verify(mockView).displayInfo(contains("ReadTestProject")); // Verify that the project information is displayed, checking for the project title in the displayed info
  }

  // @ Test for searching non-existing project by ID

  @Test void searchProjectById_nonExistingId_displaysError() {
    controller.searchProjectById(999); // > Non-existing project ID
    verify(mockView).displayError(anyString());
  }

  /*---------------------------------------------------------------------------------------------------------------
  UPDATE TESTS - Ensures that the controller correctly updates projects with valid input and rejects invalid input 
  ---------------------------------------------------------------------------------------------------------------*/
  // @ Test for selecting invalid project ID

  @Test void updateProjectSelect_invalidID_returnsFalse() {
    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    controller.addProject(); // adds project with id = 1
    when(mockView.updateMenu()).thenReturn(1); // > Update title menu option
    when(mockView.recieveId()).thenReturn(999); // > Non-existing project ID
    assertFalse(controller.updateProjectTitle(), "Selecting non-existing project ID should return false");
  }

  // @ Test for selecting valid project ID and updating

  @Test void updateProjectSelect_validID_returnsTrue() {
    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    controller.addProject(); // adds project with id = 1

    when(mockView.updateMenu()).thenReturn(1); // > Update title menu option
    when(mockView.recieveId()).thenReturn(1); // > Existing project ID

    when(mockView.recieveTitle()).thenReturn("Updated Title"); // > Valid new title
    assertTrue(controller.updateProjectTitle(), "Selecting existing project ID should return true");

    when(mockView.recieveTitle()).thenReturn("Another Updated Title"); // > Valid new title
    assertTrue(controller.updateProjectTitle(), "Updating title to another valid title should return true");

    when(mockView.recieveTitle()).thenReturn(""); // > Invalid new title (empty)
    assertFalse(controller.updateProjectTitle(), "Updating title to an invalid title should return false");

    when(mockView.updateMenu()).thenReturn(2); // > Update description menu option
    when(mockView.recieveDescription()).thenReturn("This is a valid description."); // > Valid new description
    assertTrue(controller.updateProjectDescription(), "Updating description to a valid description should return true");
    
    when(mockView.recieveDescription()).thenReturn("a".repeat(5001)); // > Invalid new description (too long)
    assertFalse(controller.updateProjectDescription(), "Updating description to an invalid description should return false");

    when(mockView.updateMenu()).thenReturn(3); // > Update tags menu option
    when(mockView.recieveTags()).thenReturn("valid-tag"); // > Valid new tags
    assertTrue(controller.updateProjectTags(), "Updating tags to valid tags should return true");

    when(mockView.recieveTags()).thenReturn(""); // > Valid new tags (empty)
    assertTrue(controller.updateProjectTags(), "Updating tags to valid tags should return true");
    
    String invalidTag = "a".repeat(201); // > Invalid new tags (too long)
    when(mockView.recieveTags()).thenReturn(invalidTag);
    assertFalse(controller.updateProjectTags(), "Updating tags to invalid tags should return false");
  }

  /*---------------------------------------------------------------------------------------------------------------
  DELETE TESTS - Ensures that the controller correctly deletes projects
  ---------------------------------------------------------------------------------------------------------------*/

  // @ Test for deleting non-existing project

  @Test void deleteProject_invalidID_returnsFalse() {
    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    controller.addProject();
    when(mockView.recieveId()).thenReturn(999); // > Non-existing project ID
    assertFalse(controller.deleteProject(), "Deleting non-existing project should return false");
  }

  // @ Test for deleting existing project

  @Test void deleteProject_validID_returnsTrue() {
    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    controller.addProject();
    when(mockView.recieveId()).thenReturn(1); // > Existing project ID
    assertTrue(controller.deleteProject(), "Deleting existing project should return true");
  }

  // @ Test for deleting already deleted project

  @Test void deleteProject_alreadyDeleted_returnsFalse() {
    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    controller.addProject();
    when(mockView.recieveId()).thenReturn(1); // > Existing project ID
    controller.deleteProject(); // delete the project first
    assertFalse(controller.deleteProject(), "Deleting an already deleted project should return false");
  }

  /*---------------------------------------------------------------------------------------------------------------
  RESTORE TESTS - Ensures that the controller correctly restores projects
  ---------------------------------------------------------------------------------------------------------------*/

  // @ Test for restoring non-existing project

  @Test void restoreProject_invalidID_returnsFalse() {
    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    controller.addProject();
    when(mockView.recieveId()).thenReturn(999); // > Non-existing project ID
    assertFalse(controller.restoreProject(), "Restoring non-existing project should return false");
  }

  // @ Test for restoring existing deleted project

  @Test void restoreProject_validID_returnsTrue() {
    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    controller.addProject();
    when(mockView.recieveId()).thenReturn(1); // > Existing project ID
    controller.deleteProject(); // delete the project first
    assertTrue(controller.restoreProject(), "Restoring existing deleted project should return true");
  }

  // @ Test for restoring a project that is not deleted

  @Test void restoreProject_notDeleted_returnsFalse() {
    when(mockView.recieveTitle()).thenReturn("TestProject"); // > Valid project title
    controller.addProject();
    when(mockView.recieveId()).thenReturn(1); // > Existing project ID
    assertFalse(controller.restoreProject(), "Restoring a project that is not deleted should return false");
  }

  // @ Test for file Upload

  @Test void loadProjects_validFile_returnsTrue() {
    String filePath = "data/sample_projects.txt"; // > Path to a valid JSON file containing projects
    when(mockView.recieveFilePath()).thenReturn(filePath); // > Valid file path
    assertTrue(controller.loadProjectsFromFile(), "Uploading projects from a valid file should return true");

    String invalidFilePath = "data/non_existing_file.txt"; // > Path to a non-existing file
    when(mockView.recieveFilePath()).thenReturn(invalidFilePath); // > Invalid file path
    assertFalse(controller.loadProjectsFromFile(), "Uploading projects from a non-existing file should return false");
  }
}
