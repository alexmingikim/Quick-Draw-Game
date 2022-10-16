package nz.ac.auckland.se206;

import ai.djl.ModelException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.image.BufferedImage;
import java.io.FileReader;
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

  private static String previousScene = "canvas";

  @FXML private Label resultsLabel;

  @FXML private Label profileUsernameLabel;

  @FXML private Label earnedTitleLabel;

  @FXML private Label badgesEarnedLabel;

  @FXML private Label lblCategoryAnswer;

  @FXML private Label scoreLabel;

  @FXML private Label highScoreLabel;

  @FXML private Button goGameModeButton;

  @FXML private Button saveDrawingButton;

  @FXML private Button startNewGameButton;

  @FXML private ImageView finalDrawingImage;

  private User currentProfile;

  private Boolean isGameWon = false;

  private int score = 0;

  private List<Integer> newBadges = new ArrayList<Integer>();

  private List<User> userProfiles;

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

    // display correct category which user had to draw
    if (previousScene.equals("canvas")) {
      displayCorrectCategory(CanvasController.getCategory());
    } else {
      displayCorrectCategory(HiddenWordModeController.getCategory());
    }

    // get the existing profiles and display the score and high score
    getProfiles();
    displayScores();
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

  /**
   * Set the current game's score
   *
   * @param score the score of the game
   */
  public void setScore(int score) {
    this.score = score;
  }

  /** Display the current game's score and also the highest score across all profiles. */
  private void displayScores() {
    // Reset the score if the current game was a loss
    if (isGameWon == false) {
      score = 0;
    }
    scoreLabel.setText("Score: " + String.format("%,d", score));
    int highScore = 0;
    String highScoreName = "";
    // Obtain highest score out of all users if there are exists at least 1 profile
    if (!userProfiles.isEmpty()) {
      for (int i = 0; i < userProfiles.size(); i++) {
        if (highScore < userProfiles.get(i).getHighScore()) {
          highScore = userProfiles.get(i).getHighScore();
          highScoreName = userProfiles.get(i).getName();
        }
      }
      // display the high score stats
      highScoreLabel.setText(highScoreName + "\n" + String.format("%,d", highScore));
      highScoreLabel.setTextAlignment(TextAlignment.CENTER);
    } else {
      highScoreLabel.setText("-");
    }
  }

  /** Get all currently existing profiles. */
  private void getProfiles() {
    // initializing utilities to read the profiles
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
      fr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Retrieve the final snapshot of canvas after game is ended. */
  private void setFinalSnapshot() {
    if (previousScene.equals("canvas")) {
      // retrieve final snapshot of the canvas from canvas scene
      BufferedImage finalSnapshot =
          ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController())
              .getCurrentSnapshot();
      Image image = SwingFXUtils.toFXImage(finalSnapshot, null);
      finalDrawingImage.setImage(image);
    } else {
      // retrieve final snapshot of the canvas from hidden word scene
      BufferedImage finalSnapshot =
          ((HiddenWordModeController)
                  SceneManager.getLoader(AppUi.HIDDEN_WORD_MODE).getController())
              .getCurrentSnapshot();
      Image image = SwingFXUtils.toFXImage(finalSnapshot, null);
      finalDrawingImage.setImage(image);
    }
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
   * Identifies which scene (out of canvas and hidden word mode) called the results scene.
   *
   * @param previousScene either canvas or hidden word mode scene
   */
  public static void setPreviousScene(String previousScene) {
    ResultsController.previousScene = previousScene;
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
   * Switch from results scene to either canvas scene (classic) or hidden word mode scene.
   *
   * @param event the event triggered when the new game button is clicked
   * @throws IOException {@inheritDoc}
   * @throws ModelException {@inheritDoc}
   * @throws WordNotFoundException {@inheritDoc}
   */
  @FXML
  private void onStartNewGame(ActionEvent event)
      throws IOException, ModelException, WordNotFoundException {
    if (previousScene.equals("canvas")) {
      // change the scene back to canvas after resetting everything
      ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).startNewGame();
      Button btnSceneIsIn = (Button) event.getSource();
      Scene scene = btnSceneIsIn.getScene();
      scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.CANVAS));
    } else {
      // change the scene back to hidden word mode scene
      ((HiddenWordModeController) SceneManager.getLoader(AppUi.HIDDEN_WORD_MODE).getController())
          .subInitialize();
      Button btnSceneIsIn = (Button) event.getSource();
      Scene scene = btnSceneIsIn.getScene();
      scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.HIDDEN_WORD_MODE));
    }
  }

  /** Save the final snapshot of canvas. */
  @FXML
  private void onSaveDrawing() {
    if (previousScene.equals("canvas")) {
      ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).saveDrawing();
    } else {
      ((HiddenWordModeController) SceneManager.getLoader(AppUi.HIDDEN_WORD_MODE).getController())
          .saveDrawing();
    }
  }

  /**
   * Displays category which the user had to draw on the results scene.
   *
   * @param category category which user had to draw
   */
  public void displayCorrectCategory(String category) {
    lblCategoryAnswer.setText("Answer: " + category);
  }
}
