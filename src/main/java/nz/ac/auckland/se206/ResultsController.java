package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class ResultsController {

  @FXML private Label resultsLabel;

  @FXML private Label profileUsernameLabel;

  @FXML private Label earnedTitleLabel;

  @FXML private Label badgesEarnedLabel;

  @FXML private Button goGameModeButton;

  @FXML private Button saveDrawingButton;

  @FXML private Button startNewGameButton;

  @FXML private ImageView finalDrawingImage;

  private User currentProfile;

  private Boolean isGameWon = false;

  private List<Integer> newBadges = new ArrayList<Integer>();

  /** Initializes the results scene when the game app is ran. */
  public void initialize() {
    subInitialize();
  }

  /**
   * Gets the final snapshot of the canvas, changes the results display depending on game state and
   * changes username display depending on current user profile.
   */
  public void subInitialize() {
    // get final snapshot of drawn image
    setFinalSnapshot();
    badgesEarnedLabel.setText("");

    // change results label based on current game results
    if (isGameWon == true) {
      resultsLabel.setText("You Won");
      if (!newBadges.isEmpty()) {
        displayNewBadges();
        earnedTitleLabel.setVisible(true);
        badgesEarnedLabel.setVisible(true);
      } else {
        earnedTitleLabel.setVisible(false);
        badgesEarnedLabel.setVisible(false);
      }
    } else {
      resultsLabel.setText("You Lost");
      if (!newBadges.isEmpty()) {
        displayNewBadges();
        earnedTitleLabel.setVisible(true);
        badgesEarnedLabel.setVisible(true);
      } else {
        earnedTitleLabel.setVisible(false);
        badgesEarnedLabel.setVisible(false);
      }
    }

    // change username label based on current profile
    currentProfile = ProfileViewController.getCurrentUser();
    if (currentProfile == null) {
      profileUsernameLabel.setText("Guest");
    } else {
      profileUsernameLabel.setText(currentProfile.getName());
    }
  }

  /** Set every badge that was earned this game. */
  public void setNewBadges(List<Integer> newBadges) {
    this.newBadges = newBadges;
  }

  /**
   * Set the current game's results.
   *
   * @param isGameWon the result of the game
   */
  public void setGameResults(Boolean isGameWon) {
    this.isGameWon = isGameWon;
  }

  /** Retrieve the final snapshot of the canvas after the game ended. */
  private void setFinalSnapshot() {
    // retrieve final snapshot of the canvas from canvas scene
    BufferedImage finalSnapshot =
        ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController())
            .getCurrentSnapshot();
    Image image = SwingFXUtils.toFXImage(finalSnapshot, null);
    finalDrawingImage.setImage(image);
  }

  /** Display all new badges earned in the latest game in order by index of the badges. */
  private void displayNewBadges() {
    Collections.sort(newBadges);
    StringBuilder sb = new StringBuilder();
    // retrieve the name of every badge that was earned in the latest game
    for (Integer badgeIndex : newBadges) {
      String badgeName = BadgeViewController.getAllBadges().get(badgeIndex).getName();
      sb.append(badgeName).append(System.lineSeparator());
    }
    // display all badges earned
    badgesEarnedLabel.setText(sb.toString());
    badgesEarnedLabel.setTextAlignment(TextAlignment.RIGHT);
  }

  /**
   * Switch from results scene to game mode scene.
   *
   * @param event the event triggered when the game mode button is clicked
   */
  @FXML
  private void onGoGameMode(ActionEvent event) {
    Button btnSceneIsIn = (Button) event.getSource();
    Scene scene = btnSceneIsIn.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_MODE));
  }

  /**
   * Switch from results scene to canvas scene while resetting the canvas scene to start a new game.
   *
   * @param event the event triggered when the new game button is clicked
   * @throws IOException {@inheritDoc}
   * @throws ModelException {@inheritDoc}
   */
  @FXML
  private void onStartNewGame(ActionEvent event) throws IOException, ModelException {
    // change the scene back to canvas after resetting everything
    ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).startNewGame();
    Button btnSceneIsIn = (Button) event.getSource();
    Scene scene = btnSceneIsIn.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
  }

  /** Save the final snapshot of the canvas. */
  @FXML
  private void onSaveDrawing() {
    ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).saveDrawing();
  }
}
