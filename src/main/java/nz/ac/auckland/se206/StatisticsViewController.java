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

public class StatisticsViewController {

  @FXML Button goBackButton;

  @FXML private Label usernameLabel;

  @FXML private Label gamesPlayedLabel;

  @FXML private Label gamesWonLabel;

  @FXML private Label gamesLostLabel;

  @FXML private Label averageDrawingTimeLabel;

  @FXML private Label totalGameTimeLabel;

  @FXML private Label fastestGameWonLabel;

  public void load() {
    String currentUserId = ProfileViewController.currentUserId;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    List<User> userProfiles = new ArrayList<User>();

    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());

    } catch (FileNotFoundException e) {

    }

    for (User user : userProfiles) {
      String userId = user.getId();

      if (userId.equals(currentUserId)) {
        usernameLabel.setText(user.getName());
        gamesPlayedLabel.setText(user.getNoOfGamesPlayed());
        gamesWonLabel.setText(user.getNoOfGamesWon());
        gamesLostLabel.setText(user.getNoOfGamesLost());
        averageDrawingTimeLabel.setText(user.getAverageDrawingTime());
        totalGameTimeLabel.setText(user.getTotalGameTime());
        fastestGameWonLabel.setText(user.getFastestWonGameTime());
      }
    }

    //		fr.close();
  }

  @FXML
  private void onGoBack(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.PROFILE_VIEW));
  }
}
