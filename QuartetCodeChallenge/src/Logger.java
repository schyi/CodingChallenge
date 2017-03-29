import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

  public static void log(String message){
    String timeStamp = new SimpleDateFormat("[yyyy.MM.dd HH:mm:ss]").format(new Date());
    System.out.println(timeStamp + " " + message);
  }
}
