package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/*
  Project class
  Defines the main data model for the DMS application
*/
public class Project {
  private int id;
  private String title;
  private String description;
  private List<String> tags;
  private boolean archived;
  private boolean deleted;
  private LocalDateTime removalDate;

  // constructor to initialize a project with a title and defaults for other fields. 
  public Project(String title) {
    this.id = 0;
    this.title = title;
    this.description = "";
    this.tags = new ArrayList<>();
    this.archived = false;
    this.deleted = false;
    this.removalDate = null;
  }

  // full constructor to initialize a project with all fields provided
  public Project(int id, String title, String description, List<String> tags, boolean archived, boolean deleted, LocalDateTime removalDate) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.tags = tags;
    this.archived = archived;
    this.deleted = deleted;
    this.removalDate = removalDate;
  }

  // getters
  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getTags() {
    return tags;
  }

  public boolean isArchived() {
    return archived;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public LocalDateTime getRemovalDate() {
    return removalDate;
  }

  // setters
  public void setId(int id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
    this.removalDate = deleted ? LocalDateTime.now().plusDays(30) : null; // Set removal date to 30 days from now when marked as deleted
  }

  public void setRemovalDate(LocalDateTime removalDate) {
    this.removalDate = removalDate;
  }

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
