package nz.ac.auckland.se206;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class SettingsController {

  public enum Difficulty {
    Easy,
    Medium,
    Hard,
    Master
  }

  @FXML private Button btnGoBack;

  @FXML private ToggleButton easyAccuracyButton;

  @FXML private ToggleButton mediumAccuracyButton;

  @FXML private ToggleButton hardAccuracyButton;

  @FXML private ToggleButton easyWordsButton;

  @FXML private ToggleButton mediumWordsButton;

  @FXML private ToggleButton hardWordsButton;

  @FXML private ToggleButton masterWordsButton;

  @FXML private ToggleButton easyTimeButton;

  @FXML private ToggleButton mediumTimeButton;

  @FXML private ToggleButton hardTimeButton;

  @FXML private ToggleButton masterTimeButton;

  @FXML private ToggleButton easyConfidenceButton;

  @FXML private ToggleButton mediumConfidenceButton;

  @FXML private ToggleButton hardConfidenceButton;

  @FXML private ToggleButton masterConfidenceButton;

  private ToggleButton lastAccuracyButton;

  private ToggleButton lastWordsButton;

  private ToggleButton lastTimeButton;

  private ToggleButton lastConfidenceButton;

  private ToggleGroup accuracyGroup = new ToggleGroup();

  private ToggleGroup wordsGroup = new ToggleGroup();

  private ToggleGroup timeGroup = new ToggleGroup();

  private ToggleGroup confidenceGroup = new ToggleGroup();

  public void initialize() {
    // create the groups for difficulty buttons and set the default difficulty to
    // easy
    createGroups();
    accuracyGroup.selectToggle(easyAccuracyButton);
    wordsGroup.selectToggle(easyWordsButton);
    timeGroup.selectToggle(easyTimeButton);
    confidenceGroup.selectToggle(easyConfidenceButton);

    // update changes to buttons
    updateAccuracyButtons(easyAccuracyButton);
    updateWordsButtons(easyWordsButton);
    updateTimeButtons(easyTimeButton);
    updateConfidenceButtons(easyConfidenceButton);

    // stores the difficulty setting for the next change
    lastAccuracyButton = easyAccuracyButton;
    lastWordsButton = easyWordsButton;
    lastTimeButton = easyTimeButton;
    lastConfidenceButton = easyConfidenceButton;
  }

  private void updateAccuracyButtons(ToggleButton selectedAccuracy) {
    // update changes to accuracy setting buttons
    selectedAccuracy.setDisable(true);
    if (!(lastAccuracyButton == null)) {
      lastAccuracyButton.setDisable(false);
    }
    lastAccuracyButton = selectedAccuracy;
  }

  private void updateWordsButtons(ToggleButton selectedWords) {
    // update changes to words setting buttons
    selectedWords.setDisable(true);
    if (!(lastWordsButton == null)) {
      lastWordsButton.setDisable(false);
    }
    lastWordsButton = selectedWords;
  }

  private void updateTimeButtons(ToggleButton selectedTime) {
    // update changes to time setting buttons
    selectedTime.setDisable(true);
    if (!(lastTimeButton == null)) {
      lastTimeButton.setDisable(false);
    }
    lastTimeButton = selectedTime;
  }

  private void updateConfidenceButtons(ToggleButton selectedConfidence) {
    // update changes to confidence setting buttons
    selectedConfidence.setDisable(true);
    if (!(lastConfidenceButton == null)) {
      lastConfidenceButton.setDisable(false);
    }
    lastConfidenceButton = selectedConfidence;
  }

  private void createGroups() {
    // store corresponding accuracy toggle buttons into toggle group
    easyAccuracyButton.setToggleGroup(accuracyGroup);
    mediumAccuracyButton.setToggleGroup(accuracyGroup);
    hardAccuracyButton.setToggleGroup(accuracyGroup);

    // store corresponding words toggle buttons into toggle group
    easyWordsButton.setToggleGroup(wordsGroup);
    mediumWordsButton.setToggleGroup(wordsGroup);
    hardWordsButton.setToggleGroup(wordsGroup);
    masterWordsButton.setToggleGroup(wordsGroup);

    // store corresponding time toggle buttons into toggle group
    easyTimeButton.setToggleGroup(timeGroup);
    mediumTimeButton.setToggleGroup(timeGroup);
    hardTimeButton.setToggleGroup(timeGroup);
    masterTimeButton.setToggleGroup(timeGroup);

    // store corresponding confidence toggle buttons into toggle group
    easyConfidenceButton.setToggleGroup(confidenceGroup);
    mediumConfidenceButton.setToggleGroup(confidenceGroup);
    hardConfidenceButton.setToggleGroup(confidenceGroup);
    masterConfidenceButton.setToggleGroup(confidenceGroup);
  }

  @FXML
  private void onGoBack(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.MAIN_MENU));
  }

  @FXML
  private void onEasyAccuracy() {
    updateAccuracyButtons((ToggleButton) accuracyGroup.getSelectedToggle());
  }

  @FXML
  private void onMediumAccuracy() {
    updateAccuracyButtons((ToggleButton) accuracyGroup.getSelectedToggle());
  }

  @FXML
  private void onHardAccuracy() {
    updateAccuracyButtons((ToggleButton) accuracyGroup.getSelectedToggle());
  }

  @FXML
  private void onEasyWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle());
  }

  @FXML
  private void onMediumWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle());
  }

  @FXML
  private void onHardWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle());
  }

  @FXML
  private void onMasterWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle());
  }

  @FXML
  private void onEasyTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle());
  }

  @FXML
  private void onMediumTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle());
  }

  @FXML
  private void onHardTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle());
  }

  @FXML
  private void onMasterTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle());
  }

  @FXML
  private void onEasyConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle());
  }

  @FXML
  private void onMediumConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle());
  }

  @FXML
  private void onHardConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle());
  }

  @FXML
  private void onMasterConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle());
  }
}
