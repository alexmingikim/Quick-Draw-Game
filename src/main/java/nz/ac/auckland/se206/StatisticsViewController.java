package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

public class StatisticsViewController {

  @FXML Button goBackButton;

  @FXML private Label usernameLabel;

  @FXML private Label gamesPlayedLabel;

  @FXML private Label gamesWonLabel;

  @FXML private Label gamesLostLabel;

  @FXML private Label averageDrawingTimeLabel;

  @FXML private Label totalGameTimeLabel;

  @FXML private Label fastestGameWonTimeLabel;

  @FXML private Label fastestGameWonLabel;

  @FXML private ScrollPane wordsEncounteredField;

  public void load() {

    String currentUserId = ProfileViewController.currentUserId;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    List<User> userProfiles = new ArrayList<User>();

    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
      // fr.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    for (User user : userProfiles) {
      String userId = user.getId();

      if (userId.equals(currentUserId)) {
        // display non-time related statistics of the current profile
        usernameLabel.setText(user.getName());
        gamesPlayedLabel.setText(user.getNoOfGamesPlayed());
        gamesWonLabel.setText(user.getNoOfGamesWon());
        gamesLostLabel.setText(user.getNoOfGamesLost());

        // initialize fields for time related statistics
        int totalTime = Integer.parseInt(user.getTotalGameTime());
        int totalMins = totalTime / 60;
        int totalSecs = totalTime % 60;

        // add a seconds (and minutes for total time) notation for the time statistics
        if (user.getTotalGameTime().equals("0")) {
          averageDrawingTimeLabel.setText(user.getAverageDrawingTime());
          totalGameTimeLabel.setText("0s");
          fastestGameWonTimeLabel.setText(user.getFastestWonGameTime());
        } else {
          averageDrawingTimeLabel.setText(user.getAverageDrawingTime() + "s");
          totalGameTimeLabel.setText(totalMins + "m " + totalSecs + "s");
          fastestGameWonTimeLabel.setText(user.getFastestWonGameTime() + "s");
        }

        fastestGameWonLabel.setText("(" + user.getFastestWonGame() + ")");

        // displaying all the words encountered by the current profile
        String wordsEncountered = user.getWordsEncountered();

        String[] words = wordsEncountered.split(",");

        StringBuilder sb = new StringBuilder();

        for (String word : words) {
          sb.append(word + "\n");
        }

        String displayWords = sb.toString();

        Text text = new Text(displayWords);

        wordsEncounteredField.setContent(text);
      }
    }
  }

  @FXML
  private void onGoBack(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.PROFILE_VIEW));
  }
}
