package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.util.MediaUtil;

public class MainMenuController {

  MediaPlayer player;

  @FXML private Button playButton;

  /**
   * Switches from main menu scene to game mode scene.
   *
   * @param event event triggered when the play button is clicked
   * @throws IOException if an file input or output error occurs
   * @throws ModelException if an error in the prediction model occurs
   * @throws URISyntaxException if an inappropriate URI is given
   */
  @FXML
  private void onPlay(ActionEvent event) throws IOException, ModelException, URISyntaxException {
    // play sound effect when button is clicked
    MediaUtil player = new MediaUtil(MediaUtil.buttonClickFile);
    player.play();

    ((GameModeController) SceneManager.getLoader(AppUi.GAME_MODE).getController()).initialize();
    // set root to profile view scene if "let's start" button is pressed
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.GAME_MODE));
  }
}
