import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.*;

public class DataStoreEndpointStub {

  HashMap<Integer, Clinic> masterClinics;
  static final String FILE_NAME = "Quartet_Master.json";
  DataStoreEndpointStub() {
    masterClinics = new HashMap<>();
  }

  /**
   * Inserts given clinics into the datastore.
   * For now, since it's a stub I store it as a json file.
   * @param
   * @return
   */
  public void add(Clinic[] clinics) {
    // assign an id and timestamp
    String timeStamp = new SimpleDateFormat("[yyyy.MM.dd HH:mm:ss]").format(new Date());
    for (int i = 0; i < clinics.length; i++) {
      clinics[i].id = i;
      clinics[i].dateModified = timeStamp;
      masterClinics.put(i,clinics[i]);
    }
    try {
      write();
    }
    catch (Exception e) {
      Logger.log(e.getMessage());
    }
  }

  public void add(Clinic clinic) {
    clinic.id = masterClinics.size();
    clinic.dateModified = new SimpleDateFormat("[yyyy.MM.dd HH:mm:ss]").format(new Date());
    masterClinics.put(clinic.id, clinic);
  }

  public void update(int clinicId, Clinic clinic) {
    clinic.id = clinicId;
    clinic.dateModified = new SimpleDateFormat("[yyyy.MM.dd HH:mm:ss]").format(new Date());
    masterClinics.put(clinicId, clinic);
  }

  public Clinic[] getAll() {
    if (masterClinics.size() == 0) {
      load();
    }
    return masterClinics.values().toArray(new Clinic[0]);
  }

  public void markOffline(Integer clinicId) {
    Clinic clinic = masterClinics.get(clinicId);
    clinic.isOffline = true;
    update(clinicId, clinic);
  }

  public void updateActiveStatus(Integer clinicId, boolean isActive) {
    Clinic clinic = masterClinics.get(clinicId);
    clinic.isActive = isActive;
    update(clinicId, clinic);
  }

  public Clinic[] getClinicsByState(String state) {
    // this is just a stub and we know it's all from NY, so get all.
    return masterClinics.values().toArray(new Clinic[0]);
  }

  void load() {
    Clinic[] clinics = new Clinic[0];
    try {
      clinics = DataHandler.loadFromFile(FILE_NAME);
    }
    catch(Exception e){
      Logger.log(e.getMessage());
    }
    for (Clinic c : clinics) {
      // assumes everything in the file has an id
      masterClinics.put(c.id, c);
    }
  }

  void write() throws IOException{
    Clinic[] clinics = masterClinics.values().toArray(new Clinic[0]);
    Gson gson = new Gson();
    String json = gson.toJson(clinics);
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(FILE_NAME)))) {
      writer.write(json);
    }
  }

}