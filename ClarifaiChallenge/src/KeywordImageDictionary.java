import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import clarifai2.dto.input.image.ClarifaiURLImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;

/**
 * Created by ninaliong on 3/15/17.
 */
public class KeywordImageDictionary {

  final String appId = "INSERT APP ID HERE";
  final String appSecret = "INSERT APP SECRET HERE";
  ClarifaiClient clarifaiClient;

  HashMap<String, ArrayList<Image>> dictionary;

  KeywordImageDictionary() {
    clarifaiClient = new ClarifaiBuilder(appId, appSecret)
            .client(new OkHttpClient())
            .buildSync();
    dictionary = new HashMap<>();
  }

  public void generateFromFile(String fileName) throws IOException{

    try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      String line = reader.readLine();
      while (line != null) {
        List<ClarifaiOutput<Concept>> outputList = getPredictions(ClarifaiInput.forImage(ClarifaiImage.of(line)));
        if (outputList != null) {
          populateDictionary(outputList);
        }
        line = reader.readLine();
      }
    }

    for (String keyword : dictionary.keySet()) {
      Collections.sort(dictionary.get(keyword),
              (Image i1, Image i2) -> Float.compare(i1.keywordsScore.get(keyword), i2.keywordsScore.get(keyword)));
      Collections.reverse(dictionary.get(keyword));
    }

    writeTofile();
  }

  public List<ClarifaiOutput<Concept>> getPredictions(ClarifaiInput input) {
    ClarifaiResponse<List<ClarifaiOutput<Concept>>> response = clarifaiClient.getDefaultModels().generalModel()
            .predict()
            .withInputs(input)
            .executeSync();
    return response.getOrNull();
  }

  // well turns out the API times out at about 7 items in...
  public List<ClarifaiOutput<Concept>> getPredictions(Collection<ClarifaiInput> inputs) {
    ClarifaiResponse<List<ClarifaiOutput<Concept>>> response = clarifaiClient.getDefaultModels().generalModel()
            .predict()
            .withInputs(inputs)
            .executeSync();
    return response.getOrNull();
  }

  public void populateDictionary(List<ClarifaiOutput<Concept>> outputList) {
    for (ClarifaiOutput<Concept> output : outputList) {

      ClarifaiURLImage clarifaiImage = (ClarifaiURLImage)output.input().image();
      Image image = new Image(clarifaiImage.url().toString());

      for (Concept concept : output.data()) {
        image.keywordsScore.put(concept.name(), concept.value());

        if (!dictionary.containsKey(concept.name())) {
          dictionary.put(concept.name(), new ArrayList<>());
        }
        dictionary.get(concept.name()).add(image);
      }
    }
  }

  public void loadFromCache() throws IOException{
    Gson gson = new GsonBuilder().create();
    try (BufferedReader reader = new BufferedReader(new FileReader("cache.json"))) {
      Type hashMapType = new TypeToken<HashMap<String, ArrayList<Image>>>(){}.getType();
      dictionary = gson.fromJson(reader, hashMapType);
    }
  }

  public void writeTofile() throws IOException{
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("cache.json")))) {
      gson.toJson(dictionary, writer);
    }
  }

}
