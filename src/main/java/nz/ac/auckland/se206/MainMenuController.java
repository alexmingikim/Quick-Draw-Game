package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class MainMenuController {

  @FXML private Label profileLabel;

  @FXML private Button btnPlay;

  @FXML private Button btnSwitchProfile;

  private Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private List<User> userProfiles = new ArrayList<User>();

  private User currentProfile;

  public void initialize() throws IOException {
    subInitialize();
  }

  public void subInitialize() throws IOException {
    // Changes the profile display name
    getCurrentProfile();
    if (currentProfile == null) {
      profileLabel.setText("Welcome, Guest!");
    } else {
      profileLabel.setText("Welcome, " + currentProfile.getName() + "!");
    }
  }

  private void getCurrentProfile() throws IOException {
    if (ProfileViewController.getCurrentUserId().equals("Zero")) {
      currentProfile = null;
    } else {
      try {
        // read existing user profiles from JSON file and store into array list
        FileReader fr = new FileReader("profiles/profiles.json");
        userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
        fr.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

      // select the current profile that was chosen by the user
      for (User userProfile : userProfiles) {
        if (userProfile.getId().equals(ProfileViewController.getCurrentUserId())) {
          currentProfile = userProfile;
        }
      }
    }
  }

  @FXML
  private void onPlay(ActionEvent event) throws IOException {
    ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).subInitialize();
    // set root to profile view scene if "let's start" button is pressed
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.CANVAS));
  }

  @FXML
  private void onSwitchProfile(ActionEvent event) throws IOException {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.PROFILE_VIEW));

    ProfileViewController.loadOpacity();
    ProfileViewController.loadUserLabels();
  }
}
