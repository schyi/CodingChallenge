import java.io.IOException;
import java.util.HashMap;

public class ActiveClinicMatchJob implements Job{

  DataStoreEndpointStub endpointStub;
  QuartetEndpointStub quartetStub;
  ActiveClinicMatchJob() {
    endpointStub = new DataStoreEndpointStub();
    endpointStub.load();
    quartetStub = new QuartetEndpointStub();
  }

  public void run(String inputSource) {
    Clinic[] nyClinics = endpointStub.getClinicsByState("NY");
    HashMap<String, Clinic> map = new HashMap<>(nyClinics.length);
    for (Clinic c : nyClinics) {
      map.put(c.getAlternateIdentifier(), c);
    }

    Clinic[] activeClinics = quartetStub.getClinicsByState("NY");
    for (Clinic activeClinic : activeClinics) {
      String key = activeClinic.getAlternateIdentifier();
      if (map.containsKey(key)) {
        Clinic targetClinic = map.get(key);
        endpointStub.updateActiveStatus(targetClinic.id, true);
        map.remove(key);
      }
    }

    for (String key : map.keySet()) {
      endpointStub.updateActiveStatus(map.get(key).id, false);
    }

    try {
      endpointStub.write();
    } catch (IOException e) {
      Logger.log(e.getMessage());
      e.printStackTrace();
    }

    Logger.log("Active match job: Success!");
  }
}
