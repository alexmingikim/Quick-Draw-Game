package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class MainMenuController {

  @FXML private Button playButton;

  @FXML
  private void onPlay(ActionEvent event) throws IOException, ModelException {
    ((GameModeController) SceneManager.getLoader(AppUi.GAME_MODE).getController()).initialize();
    // set root to profile view scene if "let's start" button is pressed
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.GAME_MODE));
  }
}
