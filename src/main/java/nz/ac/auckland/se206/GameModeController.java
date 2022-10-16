package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.util.MediaUtil;

public class GameModeController {

  protected MediaUtil player;

  @FXML private Button btnSwitchProfile;

  @FXML private Button settingsButton;

  @FXML private Button btnClassic;

  @FXML private Button btnZenMode;

  @FXML private Button btnHiddenWordMode;

  @FXML private Label profileLabel;

  private User currentProfile = ProfileViewController.getCurrentUser();

  /**
   * Initialises game mode scene. Updates label of "Welcome" button with name of user that is in
   * play.
   */
  public void initialize() {
    // Changes the profile display name
    currentProfile = ProfileViewController.getCurrentUser();
    if (currentProfile == null) {
      profileLabel.setText("Welcome, Guest!");
    } else {
      profileLabel.setText("Welcome, " + currentProfile.getName() + "!");
    }
  }

  /**
   * Switches to profile view scene from game modes scene. Loads names of established user profiles
   * and opacity data of user profile buttons.
   *
   * @param event when "Welcome" button is clicked
   * @throws IOException if an input or output exception occurred
   */
  @FXML
  private void onSwitchProfile(ActionEvent event) throws IOException, URISyntaxException {
    // play sound effect when button is clicked
    player = new MediaUtil(MediaUtil.buttonClickFile);
    player.play();

    // retrieve the scene of a mock button to set the next scene
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.PROFILE_VIEW));

    ProfileViewController.loadOpacity();
    ProfileViewController.loadUserLabels();
  }

  /**
   * Switches to difficulty settings scene from game modes scene, giving users the ability to select
   * difficulty levels.
   *
   * @param event when "Settings" button is clicked
   */
  @FXML
  private void onGoSettings(ActionEvent event) throws URISyntaxException {
    // play sound effect when button is clicked
    player = new MediaUtil(MediaUtil.buttonClickFile);
    player.play();

    ((SettingsController) SceneManager.getLoader(AppUi.SETTINGS).getController()).subInitialize();
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.SETTINGS));
  }

  /**
   * Starts the classic game mode. Users are given a word to draw under a time constraint.
   *
   * @param event when "Classic" button is clicked
   * @throws IOException if an input or output exception occurred
   * @throws URISyntaxException {@inheritDoc}
   * @throws ModelException {@inheritDoc}
   */
  @FXML
  private void onPlayClassic(ActionEvent event)
      throws IOException, URISyntaxException, ModelException {
    // play sound effect when button is clicked
    player = new MediaUtil(MediaUtil.buttonClickFile);
    player.play();

    ((CanvasController) SceneManager.getLoader(AppUi.CANVAS).getController()).startNewGame();
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.CANVAS));
  }

  /**
   * Starts the Zen game mode. Users are given a word to draw under no time constraints.
   *
   * @param event when "Zen Mode" button is clicked
   * @throws IOException if an input or output exception occurred
   * @throws URISyntaxException {@inheritDoc}
   * @throws ModelException {@inheritDoc}
   */
  @FXML
  private void onPlayZenMode(ActionEvent event)
      throws IOException, URISyntaxException, ModelException {
    // play sound effect when button is clicked
    player = new MediaUtil(MediaUtil.buttonClickFile);
    player.play();

    ((ZenModeController) SceneManager.getLoader(AppUi.ZEN_MODE).getController()).subInitialize();
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.ZEN_MODE));
  }

  /**
   * Starts the Hidden Word game mode. Users are provided a definition of the word they are supposed
   * to draw.
   *
   * @param event when "Hidden Word Mode" button is clicked
   * @throws IOException if an input or output exception occurred
   * @throws URISyntaxException if string cannot be parsed as a URI reference
   * @throws ModelException if there is an error in reading the input/output of the DL model
   * @throws WordNotFoundException if definition of string cannot be found using dictionary api
   */
  @FXML
  private void onPlayHiddenWordMode(ActionEvent event)
      throws IOException, URISyntaxException, ModelException, WordNotFoundException {
    player = new MediaUtil(MediaUtil.buttonClickFile);
    player.play();

    // intialise hidden word mode
    ((HiddenWordModeController) SceneManager.getLoader(AppUi.HIDDEN_WORD_MODE).getController())
        .subInitialize();
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.HIDDEN_WORD_MODE));
  }

  /**
   * Sets the opacity to 1.0 once cursor is hovered over any of the buttons for the 3 game modes.
   *
   * @param event when cursor is hovered over buttons for game modes
   */
  @FXML
  private void onMouseEntered(MouseEvent event) {
    Button button = (Button) event.getSource();
    button.setOpacity(1.0);
  }

  /**
   * Sets the opacity to 0.7 when cursor is no longer hovering the buttons for game modes.
   *
   * @param event when cursor is no longer hovered over buttons for game modes
   */
  @FXML
  private void onMouseExited(MouseEvent event) {
    Button button = (Button) event.getSource();
    button.setOpacity(0.7);
  }
}
