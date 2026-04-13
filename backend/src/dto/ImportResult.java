package dto;
import java.util.List;

/**
 * DTO for import result, containing the count of successfully imported entries and a list of skipped entries with reasons.
 * @author Russell Alexander
*/
public class ImportResult {
  public int importedCount;
  public List<SkippedEntry> skippedEntries;
}
