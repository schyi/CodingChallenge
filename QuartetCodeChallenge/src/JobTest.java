import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class JobTest {
  @Test
  public void initialImport() throws Exception {
    InitialImportJob job = new InitialImportJob();
    job.run("https://data.cityofnewyork.us/resource/8nqg-ia7v.json");
    Clinic[] importedClinics = DataHandler.loadFromFile(DataStoreEndpointStub.FILE_NAME);
    Logger.log("Test: Imported " + importedClinics.length + " rows.");
    assertTrue("Was not able to import anything.", importedClinics.length > 0);
  }

  @Test
  public void nightlyImportUpdate() throws Exception {
    Clinic[] existingClinics = DataHandler.loadFromFile(DataStoreEndpointStub.FILE_NAME);
    existingClinics[0].street_2 = "TEST STREET 2";

    NightlyImportJob job = new NightlyImportJob();
    job.endpointStub.update(0, existingClinics[0]);
    job.run("https://data.cityofnewyork.us/resource/8nqg-ia7v.json");
    Clinic[] importedClinics = DataHandler.loadFromFile(DataStoreEndpointStub.FILE_NAME);
    assertFalse("Test string was not updated.",
            Arrays.stream(importedClinics).anyMatch((clinic)-> clinic.street_2 == "TEST STREET 2"));
    assertNotEquals("TEST STREET 2", importedClinics[0]);
  }

  @Test
  public void nightlyImportAdd() throws Exception {
    Clinic original = new Clinic();
    original.name_1 = "NAME1";
    original.name_2 = "NAME2";
    original.street_1  = "STREET1";
    original.street_2 = "STREET2";

    // make the endpoint stub only have 1 clinic
    NightlyImportJob job = new NightlyImportJob();
    job.endpointStub.masterClinics = new HashMap<>();
    job.endpointStub.masterClinics.put(0, original);
    job.endpointStub.write();

    job.run("https://data.cityofnewyork.us/resource/8nqg-ia7v.json");
    Clinic[] importedClinics = DataHandler.loadFromFile(DataStoreEndpointStub.FILE_NAME);
    assertTrue("Original test clinic not found.",
            Arrays.stream(importedClinics)
                    .anyMatch(
                    (clinic)-> clinic.getAlternateIdentifier().equals(original.getAlternateIdentifier())));
    assertTrue("No new clinics added.", importedClinics.length > 1);
    assertTrue("Original clinic was not marked for deletion.", importedClinics[0].isOffline);
  }

  @Test
  public void matchingJob() throws Exception {
    Job job = new ActiveClinicMatchJob();
    job.run("doesn't matter");
    Clinic[] importedClinics = DataHandler.loadFromFile(DataStoreEndpointStub.FILE_NAME);
    List<Clinic> list = Arrays.stream(importedClinics)
            .filter((clinic) -> clinic.isActive).collect(Collectors.toList());
    assertEquals(10, list.size());
  }
}