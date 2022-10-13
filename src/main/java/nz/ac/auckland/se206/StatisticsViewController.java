package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.util.MediaUtil;

public class StatisticsViewController {

  @FXML private Button btnGoBack;

  @FXML private Button viewBadgesButton;

  @FXML private Label usernameLabel;

  @FXML private Label gamesPlayedLabel;

  @FXML private Label gamesWonLabel;

  @FXML private Label gamesLostLabel;

  @FXML private Label averageDrawingTimeLabel;

  @FXML private Label totalGameTimeLabel;

  @FXML private Label fastestGameWonTimeLabel;

  @FXML private Label fastestGameWonLabel;

  @FXML private ScrollPane wordsEncounteredField;

  private MediaUtil player;

  public void load() {
    // initializing tools for dealing with json files and retrieving current user
    String currentUserId = ProfileViewController.currentUserId;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    List<User> userProfiles = new ArrayList<User>();

    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
      fr.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
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
        // making each word encountered appear on a new line
        for (String word : words) {
          sb.append(word + "\n");
        }
        // changing the font of the text to maintain consistency
        String displayWords = sb.toString();
        Text text = new Text(displayWords);
        text.setFont(Font.font("Courier New", FontPosture.REGULAR, 16));
        wordsEncounteredField.setContent(text);
      }
    }
  }

  @FXML
  private void onGoBack(ActionEvent event) {
    // Add sound effects for when the button is clicked
    try {
      player = new MediaUtil(MediaUtil.buttonClickFile);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    player.play();
    // change the current scene back to profile view
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.PROFILE_VIEW));
  }

  @FXML
  private void onViewBadges(ActionEvent event) {
    // run a method in BadgeViewController to change the username display to the
    // current user
    ((BadgeViewController) SceneManager.getLoader(AppUi.BADGE_VIEW).getController())
        .subInitialize();
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.BADGE_VIEW));
  }
}
