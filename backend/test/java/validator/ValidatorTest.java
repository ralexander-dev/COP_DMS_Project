package validator;

import model.Project;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/*
    ValidatorTest
    Unit tests for the Validator class
*/
class ValidatorTest {

    private Validator v;

    // initialize a new Validator before each test
    @BeforeEach
    void init() {
        v = new Validator();
    }

    /*-----------------------------------------------------------------------------------------
    Title tests - ensures that validator correctly identifies valid and invalid project titles
    -------------------------------------------------------------------------------------------*/
    // @ Tests for invalid title entries

    @Test void InvalidTitle_ReturnsFalse() {
        assertFalse(v.isValidTitleEntry(null), "Null title should be invalid"); // > Null title
        assertFalse(v.isValidTitleEntry(""), "Empty title should be invalid"); // > Empty title
        assertFalse(v.isValidTitleEntry("   "), "Whitespace title should be invalid"); // > Whitespace title
        assertFalse(v.isValidTitleEntry("a".repeat(201)), "Title with more than 200 characters should be invalid"); // > Title > 200 characters
    }

    // @ Tests for valid title entries

    @Test void ValidTitles_ReturnTrue() {
        assertTrue(v.isValidTitleEntry("Project Alpha"), "Title should be valid"); // > Valid title
        assertTrue(v.isValidTitleEntry("A"), "Single character title should be valid"); // > Valid title
        assertTrue(v.isValidTitleEntry("a".repeat(100)), "Title with 100 characters should be valid"); // > Valid title
    }

    // @ Test for unique Title on Empty List

    @Test void UniqueTitleOnEmptyList_ReturnsTrue() {
        Project project = new Project("Unique Project"); // > Project with unique title
        List<Project> projects = new ArrayList<>(); // > Empty list of projects
        assertTrue(v.isUniqueTitle(project, projects), "Unique title should be valid on empty list");
    }

    // @ Test for unique Title on Non-Empty List

    @Test void UniqueTitleOnNonEmptyList_ReturnsTrue() {
        Project existing = new Project("Existing Project"); // > Existing project
        Project newProject = new Project("New Unique Project"); // > New project with unique title
        List<Project> projects = Arrays.asList(existing); // > List with existing project
        assertTrue(v.isUniqueTitle(newProject, projects), "Unique title should be valid on non-empty list");
    }

    // @ Test for non-unique Title

    @Test void NonUniqueTitle_ReturnsFalse() {
        Project existing = new Project("Existing Project"); // > Existing project
        Project newProject = new Project("Existing Project"); // > New project with same title as existing project
        newProject.setId(1); // > Set new project ID to 1 to simulate new project being added to list
        List<Project> projects = Arrays.asList(existing); // > List with existing project
        assertFalse(v.isUniqueTitle(newProject, projects), "Non-unique title should be invalid");
    }

    /*-----------------------------------------------------------------------------------------------------
    Description tests - ensures that validator correctly identifies valid and invalid project descriptions
    ------------------------------------------------------------------------------------------------------*/

    // @ Tests for valid description entries

    @Test void ValidDescription_ReturnsTrue() {
        Project project = new Project("Test Project"); // > Project with valid title

        project.setDescription("This is a valid description."); // > Valid description
        assertTrue(v.isValidDescription(project), "Valid description should return true");

        project.setDescription(""); // > Empty description
        assertTrue(v.isValidDescription(project), "Empty description should be valid");

        project.setDescription(null); // > Null description
        assertTrue(v.isValidDescription(project), "Null description should be valid");
    }

    // @ Tests for invalid description entries

    @Test void InvalidDescription_ReturnsFalse() {
        Project project = new Project("Test Project"); // > Project with valid title
        project.setDescription("a".repeat(5001)); // > Description > 5000 characters
        assertFalse(v.isValidDescription(project), "Description with more than 5000 characters should be invalid");
    }

    /*-----------------------------------------------------------------------------------------------------
    Tags tests - ensures that validator correctly identifies valid and invalid project tags
    ------------------------------------------------------------------------------------------------------*/

    // @ Tests for valid tag entries

    @Test void ValidTag_ReturnsTrue() {
        assertTrue(v.isValidTag("tag1"), "Valid tag1 should return true"); // > Valid tag
        assertTrue(v.isValidTag("tag2"), "Valid tag2 should return true"); // > Valid tag
        assertTrue(v.isValidTag("a".repeat(200)), "Tag with 200 characters should be valid"); // > Tag with 200 characters
    }

    // @ Tests for invalid tag entries

    @Test void InvalidTag_ReturnsFalse() {
        assertFalse(v.isValidTag(""), "Empty tag should be invalid"); // > Empty tag
        assertFalse(v.isValidTag("   "), "Whitespace tag should be invalid"); // > Empty tag with whitespace
        assertFalse(v.isValidTag("a".repeat(201)), "Tag with more than 200 characters should be invalid"); // > Tag > 200 characters
    }
}