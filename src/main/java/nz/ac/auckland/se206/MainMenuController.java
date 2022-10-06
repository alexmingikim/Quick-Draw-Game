package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class MainMenuController {

  @FXML private Label profileLabel;

  @FXML private Button playButton;

  @FXML private Button btnSwitchProfile;

  @FXML private Button settingsButton;
  ;

  private User currentProfile = ProfileViewController.getCurrentUser();

  public void initialize() throws IOException {
    subInitialize();
  }

  public void subInitialize() throws IOException {
    // Changes the profile display name
    currentProfile = ProfileViewController.getCurrentUser();
    if (currentProfile == null) {
      profileLabel.setText("Welcome, Guest!");
    } else {
      profileLabel.setText("Welcome, " + currentProfile.getName() + "!");
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
    // retrieve the scene of a mock button to set the next scene
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.PROFILE_VIEW));

    ProfileViewController.loadOpacity();
    ProfileViewController.loadUserLabels();
  }

  @FXML
  private void onGoSettings(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.SETTINGS));
  }
}
