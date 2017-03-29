import java.io.*;

public class JobRunner {

  public static void main(String[] args) throws IOException {
    if (args.length < 2 ||  !args[0].equals("--job")) {
      printUsage();
      return;
    }

    String jobName = args[1];

    String source = "";
    if (args.length == 4) {
      if (!args[2].equals("--source")) {
        printUsage();
        return;
      }
      source = args[3];
    }

    Job job;
    switch(jobName) {
      case "Initial":
        job = new InitialImportJob();
        if (source.length() == 0) {
          System.out.println("The source url is required for this option.");
          printUsage();
          return;
        }
        break;
      case "Nightly":
        job = new NightlyImportJob();
        if (source.length() == 0) {
          System.out.println("The source url is required for this option.");
          printUsage();
          return;
        }
        break;
      case "Matching":
        job = new ActiveClinicMatchJob();
        break;

      default:
        printUsage();
        return;
    }
    job.run(source);
  }

  public static void printUsage(){
    System.out.println("USAGE: \n" +
            "java -jar QuartetCodeChallenge_jar/QuartetCodeChallenge.jar --job \033[4mjob\033[0m " +
            "[--source \033[4murl\033[0m]\n" +
            "OPTIONS:\n" +
            "job: [ Initial | Nightly | Matching ] \n" +
            "url: the url of the json file");
  }
}
