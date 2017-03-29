public class Clinic {
  int id;
  String name_1;
  String name_2;
  String street_1;
  String street_2;
  String city;
  String zip;
  double latitude;
  double longitude;
  String dateModified;
  boolean isOffline;
  boolean isActive;
  Clinic() {}

  public String getAlternateIdentifier() {
    return name_1 + name_2 + street_1;
  }

}