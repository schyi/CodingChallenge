import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException{

    if (args.length == 0) {
      printUsage();
      return;
    }

    KeywordImageDictionary keywordImageDictionary = new KeywordImageDictionary();
    if (args[0].equals("--fromcache")) {
      keywordImageDictionary.loadFromCache();
    }
    else {
      String fileName = args[0];
      try {
        keywordImageDictionary.generateFromFile(fileName);
      }
      catch (Exception e){
        e.printStackTrace();
        System.exit(1);
      }
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

      System.out.println("Enter search tag: " );
      String line = reader.readLine();

      while (true) {

        if (line.equals("--exit")) {
          System.out.println("Goodbye!");
          System.exit(0);
        }

        if (!keywordImageDictionary.dictionary.containsKey(line)) {
          System.out.println("Keyword not found, try another one:");
          line = reader.readLine();
          continue;
        }

        System.out.println("> Images found, in order of most relevant: ");
        ArrayList<Image> result = keywordImageDictionary.dictionary.get(line);
        List<Image> images = result;
        if (result.size() > 10) {
          // get first 10
          images = result.subList(0, 10);
        }
        for (Image image : images) {
          System.out.println("  " + image.url);
        }
        System.out.println("Enter search tag: " );
        line = reader.readLine();
      }
    }
  }

  private static void printUsage() {
    System.out.println("USAGE: fileName|[--fromcache]");
    System.out.println("fileName : name of the file with the list of URLs");
    System.out.println("EXAMPLE: java -jar ClarifaiChallenge.jar images.txt");
    System.out.println("\"--cache\" : if you want to load the model from cache instead");
    System.out.println("EXAMPLE: java -jar ClarifaiChallege.jar --fromcache");
  }
}
