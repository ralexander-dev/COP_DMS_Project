package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Model for Project entity, representing a project with its attributes and behaviors.
 * @author Russell Alexander
*/
public class Project {
  private int id;
  private String title;
  private String description;
  private List<String> tags;
  private boolean archived;
  private boolean deleted;
  private LocalDateTime removalDate;

  /**
   * Constructor to initialize a project with a title and defaults for other fields.
   * @param title The title of the project.
  */
  public Project(String title) {
    this.id = 0;
    this.title = title;
    this.description = "";
    this.tags = new ArrayList<>();
    this.archived = false;
    this.deleted = false;
    this.removalDate = null;
  }

  /**
   * Overloaded constructor to initialize a project with all fields provided.
   * @param id The ID of the project.
   * @param title The title of the project.
   * @param description The description of the project.
   * @param tags The tags associated with the project.
   * @param archived Whether the project is archived.
   * @param deleted Whether the project is deleted.
   * @param removalDate The removal date of the project.
  */
  public Project(int id, String title, String description, List<String> tags, boolean archived, boolean deleted, LocalDateTime removalDate) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.tags = tags;
    this.archived = archived;
    this.deleted = deleted;
    this.removalDate = removalDate;
  }

  /**
   * Get the ID of the project.
   * @return The ID of the project.
  */
  public int getId() {
    return id;
  }

  /**
   * Get the title of the project.
   * @return The title of the project.
  */
  public String getTitle() {
    return title;
  }

  /**
   * Get the description of the project.
   * @return The description of the project.
  */
  public String getDescription() {
    return description;
  }
  
  /**
   * Get the tags associated with the project.
   * @return A list of tags associated with the project.
  */
  public List<String> getTags() {
    return tags;
  }

  /**
   * Check if the project is archived.
   * @return True if the project is archived, false otherwise.
  */
  public boolean isArchived() {
    return archived;
  }

  /**
   * Check if the project is deleted.
   * @return True if the project is deleted, false otherwise.
  */
  public boolean isDeleted() {
    return deleted;
  }

  /**
   * Get the removal date of the project.
   * @return The removal date of the project, or null if not set.
  */
  public LocalDateTime getRemovalDate() {
    return removalDate;
  }

  /** Set the ID of the project.
   * @param id The ID to set for the project.
  */
  public void setId(int id) {
    this.id = id;
  }

  /** Set the title of the project.
   * @param title The title to set for the project.
  */
  public void setTitle(String title) {
    this.title = title;
  }

  /** Set the description of the project.
   * @param description The description to set for the project.
  */
  public void setDescription(String description) {
    this.description = description;
  }

  /** Set the tags of the project.
    * @param tags The list of tags to set for the project.
  */
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  /** Set the archived state of the project.
   * @param archived The archived state to set for the project.
  */
  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  /** Set the deleted state of the project.
   * @param deleted The deleted state to set for the project.
  */
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
    this.removalDate = deleted ? LocalDateTime.now().plusDays(30) : null; // Set removal date to 30 days from now when marked as deleted
  }

  /** Set the removal date of the project.
   * @param removalDate The removal date to set for the project.
  */
  public void setRemovalDate(LocalDateTime removalDate) {
    this.removalDate = removalDate;
  }

  /**
    * Override toString method to provide a formatted string representation of the project.
    * Includes title, description, tags, and status indicators for archived and deleted states.
    * @return A string representation of the project.
  */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("[%d] %s", id, title));
    if (!description.isBlank()) {
      sb.append("\n    Description: ").append(description);
    }
    if (!tags.isEmpty()) {
      sb.append("\n    Tags: ").append(String.join(", ", tags));
    }
    if (archived) {
      sb.append(" (Archived)");
    }
    if (deleted) {
      sb.append(" (Deleted, scheduled for removal on ").append(removalDate).append(")");
    }
    return sb.toString();
  }
}
