import com.google.gson.Gson;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;

public class DataHandler {

  public static Clinic[] load(String source) throws IOException {
    InputStream is = new URL(source).openStream();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))){
      StringBuffer sb = new StringBuffer();
      String line = reader.readLine();
      while (line != null) {
        sb.append(line.trim());
        line = reader.readLine();
      }

      String all = sb.toString();
      Gson gson = new Gson();
      Clinic[] clinics = gson.fromJson(all, Clinic[].class);
      return clinics;
    }
  }

  public static Clinic[] loadFromFile(String fileName) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
      StringBuffer sb = new StringBuffer();
      String line = reader.readLine();
      while (line != null) {
        sb.append(line.trim());
        line = reader.readLine();
      }

      String all = sb.toString();
      Gson gson = new Gson();
      Clinic[] clinics = gson.fromJson(all, Clinic[].class);
      return clinics;
    }
  }

  public static Clinic[] checkDataIntegrity(Clinic[] rawData) {
    ArrayList<Clinic> validRows = new ArrayList<>(rawData.length);
    for (Clinic clinic : rawData) {
      // check required fields
      if (clinic.name_1 == null || clinic.name_1.length() == 0) {
        continue;
      }
      if (clinic.street_1 == null || clinic.street_1.length() == 0) {
        Logger.log("Data integrity issue: " + clinic.street_1 + " missing street address.");
        continue;
      }
      if (clinic.city == null || clinic.city.length() == 0) {
        Logger.log("Data integrity issue: " + clinic.street_1 + " missing city.");
        continue;
      }
      validRows.add(clinic);
    }
    return validRows.toArray(new Clinic[0]);
  }

  public static Clinic[] dedupList(Clinic[] clinics) {
    HashSet<String> lookupSet = new HashSet<>(clinics.length);
    ArrayList<Clinic> uniques = new ArrayList<>(clinics.length);
    for (Clinic c : clinics) {
      String key = c.getAlternateIdentifier();
      if (!lookupSet.contains(key)) {
        lookupSet.add(key);
        uniques.add(c);
      }
    }
    return uniques.toArray(new Clinic[0]);
  }
}
