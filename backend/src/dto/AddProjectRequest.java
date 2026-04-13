package dto;

/**
 * DTO for adding a new project, containing the title of the project.
 * @author Russell Alexander
*/
public class AddProjectRequest {
  public String title;

  /**
   * Default constructor initializing the title to an empty string.
  */
  public AddProjectRequest() {
    this.title = "";
  }

  /**
   * Constructor to create an AddProjectRequest with a specific title.
   * @param title The title of the new project.
  */
  public AddProjectRequest(String title) {
    this.title = title;
  }
}
