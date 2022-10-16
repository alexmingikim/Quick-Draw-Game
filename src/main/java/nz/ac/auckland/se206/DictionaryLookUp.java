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

  /**
   * Returns the definition of the input word.
   *
   * @param wordToLookUpDefinition word whose definition is to be found
   * @return definition of input word
   * @throws WordNotFoundException exception when definition cannot be found
   * @throws IOException {@inheritDoc}
   */
  public static String searchWordDefinition(String wordToLookUpDefinition)
      throws WordNotFoundException, IOException {

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(API_URL + wordToLookUpDefinition).build();
    Response response = client.newCall(request).execute();
    ResponseBody responseBody = response.body();

    String jsonString = responseBody.string();

    try {
      JSONObject jsonObj = (JSONObject) new JSONTokener(jsonString).nextValue();
      String subMessage = jsonObj.getString("message");

      // exception when definition cannot be found
      throw new WordNotFoundException(wordToLookUpDefinition, subMessage);
    } catch (ClassCastException e) {
      // do nothing
    }

    // JSON library
    JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString).nextValue();
    JSONObject jsonEntryObj = jsonArray.getJSONObject(0);
    JSONArray jsonMeanings = jsonEntryObj.getJSONArray("meanings");
    JSONObject jsonMeaningObj = jsonMeanings.getJSONObject(0);
    JSONArray jsonDefinitions = jsonMeaningObj.getJSONArray("definitions");
    JSONObject jsonDefinitionObj = jsonDefinitions.getJSONObject(0);

    // return definition
    String definition = jsonDefinitionObj.getString("definition");
    return definition;
  }
}
