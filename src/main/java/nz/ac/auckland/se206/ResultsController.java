package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class ResultsController {

  @FXML private Label resultsLabel;

  @FXML private Label profileUsernameLabel;

  @FXML private Button goGameModeButton;

  @FXML private Button saveDrawingButton;

  @FXML private Button startNewGameButton;

  @FXML private ImageView finalDrawingImage;

  private User currentProfile;

  private Boolean isGameWon = false;

  public void initialize() {
    subInitialize();
  }

  public void subInitialize() {
    // get final snapshot of drawn image
    setFinalSnapshot();

    // change results label based on current game results
    if (isGameWon == true) {
      resultsLabel.setText("You Won");
    } else {
      resultsLabel.setText("You Lost");
    }

    // change username label based on current profile
    currentProfile = ProfileViewController.getCurrentUser();
    if (currentProfile == null) {
      profileUsernameLabel.setText("Guest");
    } else {
      profileUsernameLabel.setText(currentProfile.getName());
    }
  }

  public void setGameResults(Boolean isGameWon) {
    this.isGameWon = isGameWon;
  }

  private void setFinalSnapshot() {
    // retrieve final snapshot of the canvas from canvas scene
    BufferedImage finalSnapshot =
        ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController())
            .getCurrentSnapshot();
    Image image = SwingFXUtils.toFXImage(finalSnapshot, null);
    finalDrawingImage.setImage(image);
  }

  @FXML
  private void onGoGameMode(ActionEvent event) {
    Button btnSceneIsIn = (Button) event.getSource();
    Scene scene = btnSceneIsIn.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_MODE));
  }

  @FXML
  private void onStartNewGame(ActionEvent event) throws IOException, ModelException {
    // change the scene back to canvas after resetting everything
    ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).startNewGame();
    Button btnSceneIsIn = (Button) event.getSource();
    Scene scene = btnSceneIsIn.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
  }

  @FXML
  private void onSaveDrawing() {
    ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).saveDrawing();
  }
}
