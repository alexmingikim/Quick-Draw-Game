package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.util.MediaUtil;

public class GameModeController {

  MediaUtil player;

  @FXML private Button btnSwitchProfile;

  @FXML private Button settingsButton;

  @FXML private Button btnClassic;

  @FXML private Label profileLabel;

  private User currentProfile = ProfileViewController.getCurrentUser();

  public void initialize() {
    // Changes the profile display name
    currentProfile = ProfileViewController.getCurrentUser();
    if (currentProfile == null) {
      profileLabel.setText("Welcome, Guest!");
    } else {
      profileLabel.setText("Welcome, " + currentProfile.getName() + "!");
    }
  }

  @FXML
  private void onSwitchProfile(ActionEvent event) throws IOException, URISyntaxException {

    player = new MediaUtil("mixkit-sci-fi-click-900.mp3");
    //		player.play();
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // global variable textToSpeech initialized at the beginning
            player.play();
            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();

    // retrieve the scene of a mock button to set the next scene
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.PROFILE_VIEW));

    ProfileViewController.loadOpacity();
    ProfileViewController.loadUserLabels();
  }

  @FXML
  private void onGoSettings(ActionEvent event) throws URISyntaxException {
    player = new MediaUtil("mixkit-sci-fi-click-900.mp3");
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // global variable textToSpeech initialized at the beginning
            player.play();
            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
    ((SettingsController) SceneManager.getLoader(AppUi.SETTINGS).getController()).subInitialize();
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.SETTINGS));
  }

  @FXML
  private void onPlayClassic(ActionEvent event)
      throws IOException, ModelException, URISyntaxException {
    player = new MediaUtil("mixkit-sci-fi-click-900.mp3");
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // global variable textToSpeech initialized at the beginning
            player.play();
            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
    ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).subInitialize();
    // set root to profile view scene if "let's start" button is pressed
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.CANVAS));
  }

  @FXML
  private void onMouseEntered(MouseEvent event) {
    Button button = (Button) event.getSource();
    button.setOpacity(1.0);
  }

  @FXML
  private void onMouseExited(MouseEvent event) {
    Button button = (Button) event.getSource();
    button.setOpacity(0.7);
  }
}
