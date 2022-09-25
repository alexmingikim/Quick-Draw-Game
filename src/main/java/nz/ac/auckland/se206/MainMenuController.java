package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class MainMenuController {

  @FXML private Button btnPlay;

  @FXML private Button btnSwitchProfile;

  @FXML
  private void onPlay(ActionEvent event) throws IOException {
    ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).sub_initialize();

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
  }
}
