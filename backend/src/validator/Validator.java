package validator;
import model.Project;
import java.util.List;
/*
  Validator class
  Defines configuration constants and validation methods for the DMS project
*/
public class Validator {
  public static int MAX_TITLE_LEN = 200;
  public static int MAX_DESC_LEN = 5000;
  public static int MAX_TAG_LEN = 200;
  private static String TAG_PATTERN = "[\\w-]+"; // ensures tags are made up of valid characters with no spaces or blanks. 

  // check for unique ID
  public boolean isUniqueID(Project project, List<Project> projects) {
    return projects.stream().noneMatch(p -> p.getId() == project.getId());
  }

  // check for unique title
  public boolean isUniqueTitle(Project project, List<Project> projects) {
    return projects.stream()
      .filter(p -> p.getId() != project.getId())
      .noneMatch(p -> p.getTitle().equals(project.getTitle()));
  }

  // check for valid title input
  public boolean isValidTitleEntry (String title) {
    if ( title != null ) {
      return
        !title.isBlank()
        && title.length() <= MAX_TITLE_LEN;
    };
    return false;
  }

  // check for valid title
  public boolean isValidTitle(Project project, List<Project> projects) {
    return 
      isValidTitleEntry(project.getTitle())
      && isUniqueTitle(project, projects)
      && isUniqueID(project, projects);
  }

  // check for valid description
  public boolean isValidDescription(Project project) {
    String desc = project.getDescription();
    if ( desc != null ) {
      return desc.length() <= MAX_DESC_LEN;
    }
    return true;
  }

  // check for valid tag
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
