-- Create dms db
CREATE DATABASE IF NOT EXISTS dms_db;

-- Switch to the dms db
USE dms_db;

-- Create project table
CREATE TABLE IF NOT EXISTS projects (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  tags TEXT,
  archived BOOLEAN DEFAULT FALSE,
  deleted BOOLEAN DEFAULT FALSE,
  removal_date DATETIME NULL
);

-- Create trigger to set removal_date when deleted is set to true
DELIMITER $$
CREATE TRIGGER IF NOT EXISTS set_removal_date
BEFORE UPDATE ON projects
FOR EACH ROW
BEGIN
  IF NEW.deleted = TRUE AND OLD.deleted = FALSE THEN
    SET NEW.removal_date = NOW() + INTERVAL 30 DAY;
  END IF;
END$$
DELIMITER ;

-- Create trigger to set removal_date to NULL if deleted is set back to false
DELIMITER $$
CREATE TRIGGER IF NOT EXISTS clear_removal_date
BEFORE UPDATE ON projects
FOR EACH ROW
BEGIN
  IF NEW.deleted = FALSE AND OLD.deleted = TRUE THEN
    SET NEW.removal_date = NULL;
  END IF;
END$$
DELIMITER ;

-- Seed sample projects only if the table is empty
INSERT INTO projects (title, description, tags)
SELECT title, description, tags FROM (
  SELECT 'portfolio_site' AS title, 'my portfolio site' AS description, 'mine,something' AS tags UNION ALL
  SELECT 'portoflio_site' AS title, 'this should fail' AS description, 'failing,testing' AS tags UNION ALL
  SELECT 'portfol1o_site' AS title, 'this should work' AS description, 'working,testing' AS tags UNION ALL
  SELECT 'something_unique' AS title, 'this should work too' AS description, 'working,testing' AS tags UNION ALL
  SELECT 'something_special' AS title, 'this should also work' AS description, 'working,testing' AS tags UNION ALL
  SELECT 'something_different' AS title, 'this can work' AS description, 'working' AS tags UNION ALL
  SELECT 'something_odd' AS title, NULL AS description, 'why,testing' AS tags UNION ALL
  SELECT 'something_unordinary' AS title, 'also' AS description, 'testing' AS tags UNION ALL
  SELECT 'something_unexpected' AS title, 'also for' AS description, 'testing' AS tags UNION ALL
  SELECT 'one_more_thing' AS title, 'for testing' AS description, NULL AS tags UNION ALL
  SELECT 'something_else' AS title, 'also for testing' AS description, 'testing' AS tags UNION ALL
  SELECT 'something_new' AS title, NULL AS description, 'testing' AS tags UNION ALL
  SELECT 'something_strange' AS title, 'also for testing' AS description, 'testing' AS tags UNION ALL
  SELECT 'something_unusual' AS title, NULL AS description, 'nonya' AS tags UNION ALL
  SELECT 'important_item' AS title, 'this is important' AS description, 'important,testing' AS tags UNION ALL
  SELECT 'random_item' AS title, 'this is random' AS description, 'random,testing' AS tags UNION ALL
  SELECT 'unusual_item' AS title, 'this is unusual' AS description, 'unusual,testing' AS tags UNION ALL
  SELECT 'sample_1' AS title, 'this is a sample item' AS description, 'sample,testing' AS tags UNION ALL
  SELECT 'sample_2' AS title, 'this is another sample item' AS description, 'sample,testing' AS tags UNION ALL
  SELECT 'sample_3' AS title, 'this is yet another sample item' AS description, 'sample' AS tags
) AS sample_data
WHERE NOT EXISTS (SELECT 1 FROM projects LIMIT 1);



