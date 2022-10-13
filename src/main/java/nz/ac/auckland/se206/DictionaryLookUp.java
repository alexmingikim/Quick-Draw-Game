package nz.ac.auckland.se206;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DictionaryLookUp {
  private static final String API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

  public static String searchWordDefinition(String wordToLookUpDefinition)
      throws WordNotFoundException, IOException {

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(API_URL + wordToLookUpDefinition).build();
    Response response = client.newCall(request).execute();
    ResponseBody responseBody = response.body();

    String jsonString = responseBody.string();

    try {
      JSONObject jsonObj = (JSONObject) new JSONTokener(jsonString).nextValue();
      String title = jsonObj.getString("title");
      String subMessage = jsonObj.getString("message");
      throw new WordNotFoundException(wordToLookUpDefinition, title, subMessage);
    } catch (ClassCastException e) {
    }

    JSONArray jArray = (JSONArray) new JSONTokener(jsonString).nextValue();
    JSONObject jsonEntryObj = jArray.getJSONObject(0);
    JSONArray jsonMeanings = jsonEntryObj.getJSONArray("meanings");
    JSONObject jsonMeaningObj = jsonMeanings.getJSONObject(0);
    JSONArray jsonDefinitions = jsonMeaningObj.getJSONArray("definitions");
    JSONObject jsonDefinitionObj = jsonDefinitions.getJSONObject(0);

    String definition = jsonDefinitionObj.getString("definition");
    return definition;
  }
}
