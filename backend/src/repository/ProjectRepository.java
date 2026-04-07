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

@Repository
public class ProjectRepository {
  private final DatabaseConnectionService dbService;

  // constructor injection of DatabaseConnectionService
  public ProjectRepository(DatabaseConnectionService dbService) {
    this.dbService = dbService;
  }

  // helper method to get JdbcTemplate from DatabaseConnectionService
  private JdbcTemplate jdbc() {
    return dbService.getJdbcTemplate();
  }

  // Rowmapper to convert Rows into Project objects
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

  // get all projects from db
  public List<Project> findAll() {
    return jdbc().query("SELECT * FROM projects", rowMapper);
  }

  // find project by ID 
  public Optional<Project> findById(int id) {
    List<Project> results = jdbc().query("SELECT * FROM projects WHERE id = ?", rowMapper, id);
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  // save new project to db
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

  // update existing project in db
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
