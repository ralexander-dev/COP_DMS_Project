package validator;
import model.Project;
import java.util.List;

/**
  * Validator class to verify user input for data integrity and consistency
  * @author Russell Alexander
*/
public class Validator {
  public static int MAX_TITLE_LEN = 200;
  public static int MAX_DESC_LEN = 5000;
  public static int MAX_TAG_LEN = 200;
  private static String TAG_PATTERN = "[\\w-]+"; // ensures tags are made up of valid characters with no spaces or blanks. 
  
  /**
    * Check if the project ID is unique within the list of projects.
    * @param project The project to validate.
    * @param projects The list of existing projects to check against.
    * @return True if the project ID is unique, false otherwise.
  */
  public boolean isUniqueID(Project project, List<Project> projects) {
    return projects.stream().noneMatch(p -> p.getId() == project.getId());
  }

  /**
    * Check if the project title is unique within the list of projects.
    * @param project The project to validate.
    * @param projects The list of existing projects to check against.
    * @return True if the project title is unique, false otherwise.
  */
  public boolean isUniqueTitle(Project project, List<Project> projects) {
    return projects.stream()
      .filter(p -> p.getId() != project.getId())
      .noneMatch(p -> p.getTitle().equals(project.getTitle()));
  }

  /**
    * Check if the project title entry is valid.
    * @param title The project title to validate.
    * @return True if the title entry is valid, false otherwise.
  */
  public boolean isValidTitleEntry (String title) {
    if ( title != null ) {
      return
        !title.isBlank()
        && title.length() <= MAX_TITLE_LEN;
    };
    return false;
  }

  /**
    * Check if the project title is valid (valid entry, unique title, and unique ID)
    * @param project The project to validate.
    * @param projects The list of existing projects to check against.
    * @return True if the project title is valid, false otherwise.
  */
  public boolean isValidTitle(Project project, List<Project> projects) {
    return 
      isValidTitleEntry(project.getTitle())
      && isUniqueTitle(project, projects)
      && isUniqueID(project, projects);
  }

  /**
    * Check if the project description is valid.
    * @param project The project to validate.
    * @return True if the project description is valid, false otherwise.
  */
  public boolean isValidDescription(Project project) {
    String desc = project.getDescription();
    if ( desc != null ) {
      return desc.length() <= MAX_DESC_LEN;
    }
    return true;
  }

  /**
    * Check if the project tags are valid.
    * @param tag The tag to validate.
    * @return True if the tag is valid, false otherwise.
  */
  public boolean isValidTag(String tag) {
    if ( tag != null ) {
      return
        !tag.isBlank()
        && tag.length() <= MAX_TAG_LEN
        && tag.matches(TAG_PATTERN);
    }
    return false;
  }
}
