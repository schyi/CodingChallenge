import java.util.HashMap;
import java.util.HashSet;

public class NightlyImportJob implements Job {
  DataStoreEndpointStub endpointStub;

  public NightlyImportJob() {
    endpointStub = new DataStoreEndpointStub();
  }
  public void run(String inputSource) {
    Clinic[] rawData;
    try {
      rawData = DataHandler.load(inputSource);
    }
    catch (Exception e) {
      Logger.log("Unable to import.");
      Logger.log(e.getMessage());
      e.printStackTrace();
      return;
    }

    if (rawData.length == 0) {
      Logger.log("Nothing to import. Exiting.");
      return;
    }

    rawData = DataHandler.checkDataIntegrity(rawData);
    Clinic[] clinics = DataHandler.dedupList(rawData);

    Clinic[] existingClinics = endpointStub.getAll();
    HashMap<String, Clinic> map = new HashMap<>();
    HashSet<Integer> tracker = new HashSet<>(existingClinics.length);
    for (Clinic existingClinic : existingClinics) {
      map.put(existingClinic.getAlternateIdentifier(), existingClinic);
      tracker.add(existingClinic.id);
    }

    for (Clinic currClinic : clinics) {
      String key = currClinic.getAlternateIdentifier();
      if (!map.containsKey(key))
      {
        endpointStub.add(currClinic);
      }
      else {
        Clinic target = map.get(key);
        if (!currClinic.equals(target)) {
          endpointStub.update(target.id, currClinic);
        }
        tracker.remove(target.id);
      }
    }

    for (Integer id : tracker) {
      endpointStub.markOffline(id);
    }

    // stub limitation - we need to tell it to write.
    try {
      endpointStub.write();
    }
    catch (Exception e) {
      Logger.log("Unable to write to file.");
      Logger.log(e.getMessage());
    }

    Logger.log("Nightly job: Success!");
  }
}
