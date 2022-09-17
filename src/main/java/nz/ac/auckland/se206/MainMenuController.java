package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class MainMenuController {

  @FXML private Button btnStart;

  @FXML private Button createProfileButton;

  static FXMLLoader fxmlLoader;

  @FXML
  private void onStart(ActionEvent event) throws IOException {
    // set root to profile view scene if "let's start" button is pressed
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.PROFILE_VIEW));
  }
}
