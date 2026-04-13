package repository;

import model.Project; 
import service.DatabaseConnectionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper; 
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing Project entities in the database.
 * Provides methods for CRUD operations and custom queries.
 * Depends on {@link DatabaseConnectionService} for managing DB connections and executing queries.
 * @author Russell Alexander
*/
@Repository
public class ProjectRepository {
  private final DatabaseConnectionService dbService;

  /* Constructor, initializes the DatabaseConnectionService for database access.
   * @param dbService The DatabaseConnectionService for managing DB connections.
  */
  public ProjectRepository(DatabaseConnectionService dbService) {
    this.dbService = dbService;
  }

  /* Helper method to get JdbcTemplate from DatabaseConnectionService.
   * @return The JdbcTemplate for executing queries.
   * @throws IllegalStateException if not connected to the database.
  */
  private JdbcTemplate jdbc() {
    return dbService.getJdbcTemplate();
  }

  /**
   * RowMapper to map SQL result set rows to Project objects.
   * Maps columns from the "projects" table to the fields of the Project model.
   * Handles null values and converts tags from a comma-separated string to a List<String>.
   * Assumes the "projects" table has columns: id, title, description, tags, archived, deleted, removal_date.
   * @return A RowMapper for Project objects.
   */
  private RowMapper<Project> rowMapper = (row, rowNum) -> {
    Project project = new Project(row.getString("title")); // set title from db
    project.setId(row.getInt("id")); // set ID from db
    // set description from db, default to empty string if null
    project.setDescription(row.getString("description") != null 
      ? row.getString("description") 
      : ""
    );
    String raw_tags = row.getString("tags"); // get raw tags string from db
    project.setTags((raw_tags == null || raw_tags.isBlank())
      ? new ArrayList<>()
      : Arrays.asList(raw_tags.split(","))
    );
    project.setArchived(row.getBoolean("archived"));
    project.setDeleted(row.getBoolean("deleted"));
    project.setRemovalDate(row.getTimestamp("removal_date") != null 
      ? row.getTimestamp("removal_date").toLocalDateTime() 
      : null
    );
    return project;
  };

  /**
   * Get all projects from the database.
   * @return A list of all projects.
  */
  public List<Project> findAll() {
    return jdbc().query("SELECT * FROM projects", rowMapper);
  }

  /**
   * Find a project by its ID.
   * @param id The ID of the project.
   * @return An Optional containing the project if found, or empty if not found.
  */
  public Optional<Project> findById(int id) {
    List<Project> results = jdbc().query("SELECT * FROM projects WHERE id = ?", rowMapper, id);
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  /**
   * Save a new project to the database.
   * @param project The project to save.
   * @return The saved project with its generated ID.
  */
  public Project save(Project project) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbc().update(connection -> {
      PreparedStatement query = connection.prepareStatement(
        "INSERT INTO projects (title, description, tags, archived, deleted, removal_date) VALUES (?, ?, ?, ?, ?, ?)", 
        Statement.RETURN_GENERATED_KEYS
      );
      query.setString(1, project.getTitle());
      query.setString(2, project.getDescription());
      query.setString(3, String.join(",", project.getTags()));
      query.setBoolean(4, project.isArchived());
      query.setBoolean(5, project.isDeleted());
      query.setTimestamp(6, project.getRemovalDate() != null 
        ? Timestamp.valueOf(project.getRemovalDate()) 
        : null
      );
      return query;
    }, keyHolder);
    int generatedId = keyHolder.getKey().intValue();
    project.setId(generatedId); // set generated ID back to project object
    return project;
  }

  /**
   * Update an existing project in the database.
   * @param project The project to update.
   * @return The updated project.
  */
  public Project update(Project project) {
    jdbc().update(
      "UPDATE projects SET title = ?, description = ?, tags = ?, archived = ?, deleted = ?, removal_date = ? WHERE id = ?", 
      project.getTitle(),
      project.getDescription(),
      String.join(",", project.getTags()),
      project.isArchived(),
      project.isDeleted(),
      project.getRemovalDate() != null ? Timestamp.valueOf(project.getRemovalDate()) : null,
      project.getId()
    );
    return project;
  }
}
