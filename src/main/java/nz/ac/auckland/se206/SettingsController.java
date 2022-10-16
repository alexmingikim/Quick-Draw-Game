package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
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

  static Difficulty[] unsavedGuest =
      new Difficulty[] {Difficulty.EASY, Difficulty.EASY, Difficulty.EASY, Difficulty.EASY};

  /**
   * Retrieve the difficulties of the default guest profile.
   *
   * @return the difficulty settings for guest profile
   */
  public static Difficulty[] getGuestDifficulty() {
    return unsavedGuest;
  }

  @FXML private Label difficultyLevelLabel;

  @FXML private Label accuracyDescriptionLabel;

  @FXML private Label wordsDescriptionLabel;

  @FXML private Label timeDescriptionLabel;

  @FXML private Label confidenceDescriptionLabel;

  @FXML private ImageView accuracyDescriptionImage;

  @FXML private ImageView wordsDescriptionImage;

  @FXML private ImageView timeDescriptionImage;

  @FXML private ImageView confidenceDescriptionImage;

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

  private Label[] descriptionLabels;

  private ImageView[] descriptionImages;

  private User currentProfile = ProfileViewController.getCurrentUser();

  private Animation delay;

  private MediaUtil player;

  /**
   * Initializes the settings scene to create the toggle button groups and setup the button and name
   * hashmap.
   */
  public void initialize() {
    // create the groups for difficulty buttons
    createGroups();
    // align buttons with their corresponding difficulty
    setUpMap();
    // set up the descriptions for all difficulty settings
    setUpDescriptions();
    // set difficulty based on profile's previous difficulty
    subInitialize();
  }

  /**
   * Sets up the initial difficulty settings to all easy for guest profile or the previous
   * difficulties if the current profile isn't guest.
   */
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
    difficultyLevelLabel.setText("");
  }

  /** Update the difficulty settings of the current user profile with new changes. */
  private void updateProfile() {
    // initializing utilities to read and store the profiles
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    List<User> userProfiles;

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

  /**
   * Change the selected accuracy button and store appropriately.
   *
   * @param selectedAccuracy the button the user clicked
   * @param selectedDifficulty the user chosen accuracy difficulty
   */
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

  /**
   * Change the selected words button and store appropriately.
   *
   * @param selectedAccuracy the button the user clicked
   * @param selectedDifficulty the user chosen words difficulty
   */
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

  /**
   * Change the selected time button and store appropriately.
   *
   * @param selectedAccuracy the button the user clicked
   * @param selectedDifficulty the user chosen time difficulty
   */
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

  /**
   * Change the selected confidence button and store appropriately.
   *
   * @param selectedAccuracy the button the user clicked
   * @param selectedDifficulty the user chosen confidence difficulty
   */
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

  /**
   * Create a delay before visualizing the description labels for the chosen difficulty setting.
   *
   * @param time the time of the delay
   * @param difficulty the chosen difficulty setting
   */
  private void delayVisibility(int time, int difficulty) {
    delay = new PauseTransition(Duration.seconds(time));
    // after delay, display description labels
    delay.setOnFinished(
        e -> {
          descriptionLabels[difficulty].setVisible(true);
          descriptionImages[difficulty].setVisible(true);
        });
  }

  /**
   * Hides the description for the difficulty setting hovered.
   *
   * @param difficulty the chosen difficulty setting
   */
  private void hideDescriptions(int difficulty) {
    delay.stop();
    descriptionLabels[difficulty].setVisible(false);
    descriptionImages[difficulty].setVisible(false);
  }

  /** Sets the default appearance of all difficulty setting descriptions. */
  private void setUpDescriptions() {
    // sets the description messages for each difficulty setting
    accuracyDescriptionLabel.setText(
        "This setting changes\nthe AI's prediction\nplacement required\nto win.");
    wordsDescriptionLabel.setText("This setting changes\nthe difficulty of\nthe words chosen.");
    timeDescriptionLabel.setText("This setting changes\nthe time limit of\nthe game.");
    confidenceDescriptionLabel.setText(
        "This setting changes\nthe AI's confidence\nof the answer\nrequired to win.");
    // sets all description label's visibility to false
    for (Label descriptionLabel : descriptionLabels) {
      descriptionLabel.setVisible(false);
    }
    // sets all description image's visibility to false
    for (ImageView descriptionImage : descriptionImages) {
      descriptionImage.setVisible(false);
    }
  }

  /** Map the names of the difficulty settings to their corresponding toggle buttons. */
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

  /** Set the difficulty setting toggle buttons to their corresponding toggle groups. */
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

    // store description labels for each difficulty setting into label array
    descriptionLabels = new Label[4];
    descriptionLabels[0] = accuracyDescriptionLabel;
    descriptionLabels[1] = wordsDescriptionLabel;
    descriptionLabels[2] = timeDescriptionLabel;
    descriptionLabels[3] = confidenceDescriptionLabel;

    // store description images for each difficulty setting into image array
    descriptionImages = new ImageView[4];
    descriptionImages[0] = accuracyDescriptionImage;
    descriptionImages[1] = wordsDescriptionImage;
    descriptionImages[2] = timeDescriptionImage;
    descriptionImages[3] = confidenceDescriptionImage;
  }

  /** Reset all toggle buttons so no button is disabled or selected. */
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

  /**
   * Switch from settings scene to game mode scene.
   *
   * @param event the event triggered when the back button is clicked
   */
  @FXML
  private void onGoBack(ActionEvent event) {
    // play sound effect when button is clicked
    try {
      player = new MediaUtil(MediaUtil.buttonClickFile);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    player.play();
    // switch scene back to game mode when button is clicked
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.GAME_MODE));
  }

  /** The method executed when the user selects the easy accuracy toggle button. */
  @FXML
  private void onSelectEasyAccuracy() {
    updateAccuracyButtons((ToggleButton) accuracyGroup.getSelectedToggle(), Difficulty.EASY);
    difficultyLevelLabel.setText(
        "Accuracy - Easy\nPlayer wins the game if the word is within\nthe top 3 guesses.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the medium accuracy toggle button. */
  @FXML
  private void onSelectMediumAccuracy() {
    updateAccuracyButtons((ToggleButton) accuracyGroup.getSelectedToggle(), Difficulty.MEDIUM);
    difficultyLevelLabel.setText(
        "Accuracy - Medium\nPlayer wins the game if the word is within\nthe top 2 guesses.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the hard accuracy toggle button. */
  @FXML
  private void onSelectHardAccuracy() {
    updateAccuracyButtons((ToggleButton) accuracyGroup.getSelectedToggle(), Difficulty.HARD);
    difficultyLevelLabel.setText(
        "Accuracy - Hard\nPlayer wins the game if the word is the\ntop guess.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the easy words toggle button. */
  @FXML
  private void onSelectEasyWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle(), Difficulty.EASY);
    difficultyLevelLabel.setText(
        "Words - Easy\nOnly words classified as Easy will appear\nin the game.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the medium words toggle button. */
  @FXML
  private void onSelectMediumWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle(), Difficulty.MEDIUM);
    difficultyLevelLabel.setText(
        "Words - Medium\nOnly words classified as Easy or Medium will\nappear in the game.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the hard words toggle button. */
  @FXML
  private void onSelectHardWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle(), Difficulty.HARD);
    difficultyLevelLabel.setText(
        "Words - Hard\nWords of all classifications namely\nEasy, Medium or Hard will all be in the game.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the master words toggle button. */
  @FXML
  private void onSelectMasterWords() {
    updateWordsButtons((ToggleButton) wordsGroup.getSelectedToggle(), Difficulty.MASTER);
    difficultyLevelLabel.setText(
        "Words - Master\nOnly words classified as Hard will appear\nin the game.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the easy time toggle button. */
  @FXML
  private void onSelectEasyTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle(), Difficulty.EASY);
    difficultyLevelLabel.setText(
        "Time - Easy\nThe time limit of the game will be set to\n60 seconds.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the medium time toggle button. */
  @FXML
  private void onSelectMediumTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle(), Difficulty.MEDIUM);
    difficultyLevelLabel.setText(
        "Time - Medium\nThe time limit of the game will be set to\n45 seconds.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the hard time toggle button. */
  @FXML
  private void onSelectHardTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle(), Difficulty.HARD);
    difficultyLevelLabel.setText(
        "Time - Hard\nThe time limit of the game will be set to\n30 seconds.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the master time toggle button. */
  @FXML
  private void onSelectMasterTime() {
    updateTimeButtons((ToggleButton) timeGroup.getSelectedToggle(), Difficulty.MASTER);
    difficultyLevelLabel.setText(
        "Time - Master\nThe time limit of the game will be set to\n15 seconds.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the easy confidence toggle button. */
  @FXML
  private void onSelectEasyConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle(), Difficulty.EASY);
    difficultyLevelLabel.setText(
        "Confidence - Easy\nPlayer wins the game if the AI's confidence\nin the word being correct is at least 1%.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the medium confidence toggle button. */
  @FXML
  private void onSelectMediumConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle(), Difficulty.MEDIUM);
    difficultyLevelLabel.setText(
        "Confidence - Medium\nPlayer wins the game if the AI's confidence\nin the word being correct is at least 10%.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the hard confidence toggle button. */
  @FXML
  private void onSelectHardConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle(), Difficulty.HARD);
    difficultyLevelLabel.setText(
        "Confidence - Hard\nPlayer wins the game if the AI's confidence\nin the word being correct is at least 25%.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user selects the master confidence toggle button. */
  @FXML
  private void onSelectMasterConfidence() {
    updateConfidenceButtons((ToggleButton) confidenceGroup.getSelectedToggle(), Difficulty.MASTER);
    difficultyLevelLabel.setText(
        "Confidence - Master\nPlayer wins the game if the AI's confidence\nin the word being correct is at least 50%.");
    difficultyLevelLabel.setTextAlignment(TextAlignment.CENTER);
  }

  /** The method executed when the user's mouse hovers over the accuracy difficulty's help icon. */
  @FXML
  private void onHoverAccuracy() {
    delayVisibility(1, 0);
    delay.playFromStart();
  }

  /** The method executed when the user's mouse hovers over the words difficulty's help icon. */
  @FXML
  private void onHoverWords() {
    delayVisibility(1, 1);
    delay.playFromStart();
  }

  /** The method executed when the user's mouse hovers over the time difficulty's help icon. */
  @FXML
  private void onHoverTime() {
    delayVisibility(1, 2);
    delay.playFromStart();
  }

  /**
   * The method executed when the user's mouse hovers over the confidence difficulty's help icon.
   */
  @FXML
  private void onHoverConfidence() {
    delayVisibility(1, 3);
    delay.playFromStart();
  }

  /** The method executed when the user's mouse leaves the accuracy difficulty's help button. */
  @FXML
  private void onExitAccuracy() {
    hideDescriptions(0);
  }

  /** The method executed when the user's mouse leaves the word difficulty's help button. */
  @FXML
  private void onExitWords() {
    hideDescriptions(1);
  }

  /** The method executed when the user's mouse leaves the time difficulty's help button. */
  @FXML
  private void onExitTime() {
    hideDescriptions(2);
  }

  /** The method executed when the user's mouse leaves the confidence difficulty's help button. */
  @FXML
  private void onExitConfidence() {
    hideDescriptions(3);
  }
}
