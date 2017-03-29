public class InitialImportJob implements Job{

  DataStoreEndpointStub endpointStub;

  public InitialImportJob() {
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

    rawData = DataHandler.checkDataIntegrity(rawData);
    Clinic[] clinics = DataHandler.dedupList(rawData);
    endpointStub.add(clinics);
    // stub limitation - we need to tell it to write.
    try {
      endpointStub.write();
    }
    catch (Exception e) {
      Logger.log("Unable to write to file.");
      Logger.log(e.getMessage());
    }

    Logger.log("Success! Imported " + clinics.length + " rows.");
  }



}
