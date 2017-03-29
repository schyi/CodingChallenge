import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by ninaliong on 3/9/17.
 */
public class DataHandlerTest {

  static final String TEST_FILE = "Local_NY_copy.json";
  @Test
  public void loadFromFile() throws Exception {
    String source = TEST_FILE;
    Clinic[] rawData = DataHandler.loadFromFile(source);
    assertEquals(848, rawData.length);
  }

  @Test
  public void checkDataIntegrity() throws Exception {
    String source = TEST_FILE;
    Clinic[] rawData = DataHandler.loadFromFile(source);
    Clinic[] data = DataHandler.checkDataIntegrity(rawData);
    assertEquals(848, data.length);
  }

  @Test
  public void dedupList() throws Exception {
    String source = TEST_FILE;
    Clinic[] rawData = DataHandler.loadFromFile(source);
    Clinic[] uniques = DataHandler.dedupList(rawData);
    assertEquals(831, uniques.length);
  }

}