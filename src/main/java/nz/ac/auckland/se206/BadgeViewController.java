package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class BadgeViewController {

  @FXML private Button btnGoBack;

  @FXML private Button viewStatisticsButton;

  @FXML private Label usernameLabel;

  private User currentProfile = ProfileViewController.getCurrentUser();

  private List<Badge> allBadges;

  public void initialize() {
    subInitialize();
    getBadges();
  }

  public void subInitialize() {
    currentProfile = ProfileViewController.getCurrentUser();
    if (currentProfile != null) {
      usernameLabel.setText(currentProfile.getName());
    }
  }

  private void getBadges() {
    // initializing utilities to read and store the profiles
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    List<Badge> allBadges = new ArrayList<Badge>();

    try {
      // read all badges from JSON file and store into array list
      FileReader fr = new FileReader("src/main/resources/badges.json");
      allBadges = gson.fromJson(fr, new TypeToken<List<Badge>>() {}.getType());
      fr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void onGoBack(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.PROFILE_VIEW));
  }

  @FXML
  private void onViewStatistics(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.STATISTICS_VIEW));
  }
}
