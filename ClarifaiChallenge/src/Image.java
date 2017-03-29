import java.util.HashMap;

/**
 * Created by ninaliong on 3/15/17.
 */
public class Image {
  String url;
  HashMap<String, Float> keywordsScore;

  Image(String url) {
    this.url = url;
    keywordsScore = new HashMap<>();
  }
}
