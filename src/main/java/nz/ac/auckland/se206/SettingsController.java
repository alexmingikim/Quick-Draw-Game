package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.util.MediaUtil;

public class SettingsController {

  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD,
    MASTER
  }

  private static HashMap<Difficulty, ToggleButton> accuracyMap =
      new HashMap<Difficulty, ToggleButton>();
  private static HashMap<Difficulty, ToggleButton> wordsMap =
      new HashMap<Difficulty, ToggleButton>();
  private static HashMap<Difficulty, ToggleButton> timeMap =
      new HashMap<Difficulty, ToggleButton>();
  private static HashMap<Difficulty, ToggleButton> confidenceMap =
      new HashMap<Difficulty, ToggleButton>();

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

  private User currentProfile = ProfileViewController.getCurrentUser();

  private MediaUtil player;

  static Difficulty[] unsavedGuest =
      new Difficulty[] {Difficulty.EASY, Difficulty.EASY, Difficulty.EASY, Difficulty.EASY};

  public static Difficulty[] getGuestDifficulty() {
    return unsavedGuest;
  }

  public void initialize() {
    // create the groups for difficulty buttons
    createGroups();
    // align buttons with their corresponding difficulty
    setUpMap();
    // set difficulty based on profile's previous difficulty
    subInitialize();
  }

  public void subInitialize() {
    currentProfile = ProfileViewController.getCurrentUser();
    resetAllButtons();
    if (currentProfile == null) {
      // set all difficulties to default (easy)
      accuracyGroup.selectToggle(accuracyMap.get(unsavedGuest[0]));
      wordsGroup.selectToggle(wordsMap.get(unsavedGuest[1]));
      timeGroup.selectToggle(timeMap.get(unsavedGuest[2]));
      confidenceGroup.selectToggle(confidenceMap.get(unsavedGuest[3]));

      // stores the predetermined difficulty settings
      lastAccuracyButton = accuracyMap.get(unsavedGuest[0]);
      lastWordsButton = wordsMap.get(unsavedGuest[1]);
      lastTimeButton = timeMap.get(unsavedGuest[2]);
      lastConfidenceButton = confidenceMap.get(unsavedGuest[3]);

      // update changes to buttons
      updateAccuracyButtons(accuracyMap.get(unsavedGuest[0]), unsavedGuest[0]);
      updateWordsButtons(wordsMap.get(unsavedGuest[1]), unsavedGuest[1]);
      updateTimeButtons(timeMap.get(unsavedGuest[2]), unsavedGuest[2]);
      updateConfidenceButtons(confidenceMap.get(unsavedGuest[3]), unsavedGuest[3]);

    } else {
      Difficulty[] difficultySettings = currentProfile.getDifficulties();
      // set difficulties to profile's set difficulties
      accuracyGroup.selectToggle(accuracyMap.get(difficultySettings[0]));
      wordsGroup.selectToggle(wordsMap.get(difficultySettings[1]));
      timeGroup.selectToggle(timeMap.get(difficultySettings[2]));
      confidenceGroup.selectToggle(confidenceMap.get(difficultySettings[3]));

      // stores the predetermined difficulty settings
      lastAccuracyButton = accuracyMap.get(difficultySettings[0]);
      lastWordsButton = wordsMap.get(difficultySettings[1]);
      lastTimeButton = timeMap.get(difficultySettings[2]);
      lastConfidenceButton = confidenceMap.get(difficultySettings[3]);

      // update changes to buttons
      updateAccuracyButtons(accuracyMap.get(difficultySettings[0]), difficultySettings[0]);
      updateWordsButtons(wordsMap.get(difficultySettings[1]), difficultySettings[1]);
      updateTimeButtons(timeMap.get(difficultySettings[2]), difficultySettings[2]);
      updateConfidenceButtons(confidenceMap.get(difficultySettings[3]), difficultySettings[3]);
    }
  }

  private void updateProfile() {
    // initializing utilities to read and store the profiles
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    List<User> userProfiles = new ArrayList<User>();

    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
      fr.close();

      // select the current profile that was chosen by the user
      int userIndex = -1;
      for (User userProfile : userProfiles) {
        if (userProfile.getId().equals(currentProfile.getId())) {
          userIndex = userProfiles.indexOf(userProfile);
        }
      }
      userProfiles.set(userIndex, currentProfile);

      // Write any updates from the current game to the json file
      FileWriter fw = new FileWriter("profiles/profiles.json");
      gson.toJson(userProfiles, fw);
      fw.flush();
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void updateAccuracyButtons(ToggleButton selectedAccuracy, Difficulty selectedDifficulty) {
    currentProfile = ProfileViewController.getCurrentUser();
    // update changes to accuracy setting buttons
    selectedAccuracy.setDisable(true);
    if (!(lastAccuracyButton == selectedAccuracy)) {
      lastAccuracyButton.setDisable(false);
    }
    // save selected difficulty to either local user database or in game save
    if (currentProfile == null) {
      unsavedGuest[0] = selectedDifficulty;
    } else {
      currentProfile.setAccuracy(selectedDifficulty);
      updateProfile();
    }
    lastAccuracyButton = selectedAccuracy;
  }

  private void updateWordsButtons(ToggleButton selectedWords, Difficulty selectedDifficulty) {
    currentProfile = ProfileViewController.getCurrentUser();
    // update changes to words setting buttons
    selectedWords.setDisable(true);
    if (!(lastWordsButton == selectedWords)) {
      lastWordsButton.setDisable(false);
    }
    // save selected difficulty to either local user database or in game save
    if (currentProfile == null) {
      unsavedGuest[1] = selectedDifficulty;
    } else {
      currentProfile.setWords(selectedDifficulty);
      updateProfile();
    }
    lastWordsButton = selectedWords;
  }

  private void updateTimeButtons(ToggleButton selectedTime, Difficulty selectedDifficulty) {
    currentProfile = ProfileViewController.getCurrentUser();
    // update changes to time setting buttons
    selectedTime.setDisable(true);
    if (!(lastTimeButton == selectedTime)) {
      lastTimeButton.setDisable(false);
    }
    // save selected difficulty to either local user database or in game save
    if (currentProfile == null) {
      unsavedGuest[2] = selectedDifficulty;
    } else {
      currentProfile.setTime(selectedDifficulty);
      updateProfile();
    }
    lastTimeButton = selectedTime;
  }

  private void updateConfidenceButtons(
      ToggleButton selectedConfidence, Difficulty selectedDifficulty) {
    currentProfile = ProfileViewController.getCurrentUser();
    // update changes to confidence setting buttons
    selectedConfidence.setDisable(true);
    if (!(lastConfidenceButton == selectedConfidence)) {
      lastConfidenceButton.setDisable(false);
    }
    // save selected difficulty to either local user database or in game save
    if (currentProfile == null) {
      unsavedGuest[3] = selectedDifficulty;
    } else {
      currentProfile.setConfidence(selectedDifficulty);
      updateProfile();
    }
    lastConfidenceButton = selectedConfidence;
  }

  private void setUpMap() {
    // store corresponding accuracy toggle buttons to label
    accuracyMap.put(Difficulty.EASY, easyAccuracyButton);
    accuracyMap.put(Difficulty.MEDIUM, mediumAccuracyButton);
    accuracyMap.put(Difficulty.HARD, hardAccuracyButton);

    // store corresponding words toggle buttons to label
    wordsMap.put(Difficulty.EASY, easyWordsButton);
    wordsMap.put(Difficulty.MEDIUM, mediumWordsButton);
    wordsMap.put(Difficulty.HARD, hardWordsButton);
    wordsMap.put(Difficulty.MASTER, masterWordsButton);

    // store corresponding time toggle buttons to label
    timeMap.put(Difficulty.EASY, easyTimeButton);
    timeMap.put(Difficulty.MEDIUM, mediumTimeButton);
    timeMap.put(Difficulty.HARD, hardTimeButton);
    timeMap.put(Difficulty.MASTER, masterTimeButton);

    // store corresponding confidence toggle buttons to label
    confidenceMap.put(Difficulty.EASY, easyConfidenceButton);
    confidenceMap.put(Difficulty.MEDIUM, mediumConfidenceButton);
    confidenceMap.put(Difficulty.HARD, hardConfidenceButton);
    confidenceMap.put(Difficulty.MASTER, masterConfidenceButton);
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

  private void resetAllButtons() {
    // enable and deselect all accuracy difficulty buttons
    for (Toggle accuracyButton : accuracyGroup.getToggles()) {
      ((ToggleButton) accuracyButton).setDisable(false);
      ((ToggleButton) accuracyButton).setSelected(false);
    }

    // enable and deselect all words difficulty buttons
    for (Toggle wordsButton : wordsGroup.getToggles()) {
      ((ToggleButton) wordsButton).setDisable(false);
      ((ToggleButton) wordsButton).setSelected(false);
    }

    // enable and deselect all time difficulty buttons
    for (Toggle timeButton : timeGroup.getToggles()) {
      ((ToggleButton) timeButton).setDisable(false);
      ((ToggleButton) timeButton).setSelected(false);
    }

    // enable and deselect all confidence difficulty buttons
    for (Toggle confidenceButton : confidenceGroup.getToggles()) {
      ((ToggleButton) confidenceButton).setDisable(false);
      ((ToggleButton) confidenceButton).setSelected(false);
    }
  }

  @FXML
  private void onGoBack(ActionEvent event) {
    try {
      player = new MediaUtil(MediaUtil.buttonClickFile);
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    player.play();

    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.GAME_MODE));
  }

  @FXML
  private void onSelectEasyAccuracy() {
    updateAccuracyButtons((ToggleButton) accuracyGroup.getSelectedToggle(), Difficulty.EASY);
  }

  @FXML
  private void onSelectMediumAccuracy() {
    updateAccuracyButtons((ToggleButton) accuracyGroup.getSelectedToggle(), Difficulty.MEDIUM);
  }

  @FXML
  private void onSelectHardAccuracy() {
    updateAccuracyButtons((ToggleButton) accuracyGroup.getSelectedToggle(), Difficulty.HARD);
  }

  @FXML
  private void onSelectEasyWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle(), Difficulty.EASY);
  }

  @FXML
  private void onSelectMediumWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle(), Difficulty.MEDIUM);
  }

  @FXML
  private void onSelectHardWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle(), Difficulty.HARD);
  }

  @FXML
  private void onSelectMasterWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle(), Difficulty.MASTER);
  }

  @FXML
  private void onSelectEasyTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle(), Difficulty.EASY);
  }

  @FXML
  private void onSelectMediumTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle(), Difficulty.MEDIUM);
  }

  @FXML
  private void onSelectHardTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle(), Difficulty.HARD);
  }

  @FXML
  private void onSelectMasterTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle(), Difficulty.MASTER);
  }

  @FXML
  private void onSelectEasyConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle(), Difficulty.EASY);
  }

  @FXML
  private void onSelectMediumConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle(), Difficulty.MEDIUM);
  }

  @FXML
  private void onSelectHardConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle(), Difficulty.HARD);
  }

  @FXML
  private void onSelectMasterConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle(), Difficulty.MASTER);
  }
}
