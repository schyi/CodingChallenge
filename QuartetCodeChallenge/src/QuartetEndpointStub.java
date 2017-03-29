/**
 * Stub for proof of concept testing. It reads from a file.
 */
public class QuartetEndpointStub {

  Clinic[] activeClinics;
  static final String FILE_NAME = "Quartet_Active.json";
  QuartetEndpointStub() {
    try {
      activeClinics = DataHandler.loadFromFile(FILE_NAME);
    }
    catch (Exception e) {
      Logger.log("Unable to load from file: " + FILE_NAME);
    }
  }

  public Clinic[] getClinicsByState(String state) {
    // see README on notes for how it's supposed to work.
    // Since it's a stub and I only have a NY data set
    // this returns all.
    return activeClinics;
  }

}
