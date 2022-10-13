package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.util.MediaUtil;

public class ProfileViewController {

  private static ToggleButton[] arrayButtons;
  private static Label[] userLabels;

  static ToggleButton lastUserButtonPressed;
  static Label labelAssociatedToLastUserButtonPressed;

  static String currentUserId = "Zero";

  static User currentUser = null;

  @FXML private Button btnCreateNewProfile;

  @FXML private Button btnViewStatistics;

  @FXML private Button btnDeleteProfile;

  @FXML private Button btnGoBack;

  @FXML private ToggleButton btnUserOne;
  @FXML private ToggleButton btnUserTwo;
  @FXML private ToggleButton btnUserThree;
  @FXML private ToggleButton btnUserFour;
  @FXML private ToggleButton btnUserFive;
  @FXML private ToggleButton btnUserSix;
  @FXML private ToggleButton btnGuest;

  @FXML private Label lblUserOne;
  @FXML private Label lblUserTwo;
  @FXML private Label lblUserThree;
  @FXML private Label lblUserFour;
  @FXML private Label lblUserFive;
  @FXML private Label lblUserSix;

  private MediaUtil player;

  /**
   * Gives the current user's ID.
   *
   * @return the current user's ID
   */
  public static String getCurrentUserId() {
    return currentUserId;
  }

  /**
   * Gives the current user as an object.
   *
   * @return current user
   */
  public static User getCurrentUser() {
    return currentUser;
  }

  /**
   * Gives the last user profile slot the user clicked.
   *
   * @return the last user profile button
   */
  public static ToggleButton getLastUserButtonPressed() {
    return lastUserButtonPressed;
  }

  /**
   * Gives the last user profile slot's label the user clicked.
   *
   * @return the user profile label
   */
  public static Label getLabelAssociatedToLastUserButtonPressed() {
    return labelAssociatedToLastUserButtonPressed;
  }

  /**
   * Load the opacity status for each user profile.
   *
   * @throws IOException {@inheritDoc}
   */
  public static void loadOpacity() throws IOException {

    // create new JSON file
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // store user information into array
    List<User> userProfiles;
    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
      fr.close();
    } catch (FileNotFoundException e) {
      return;
    }

    for (ToggleButton button : arrayButtons) {
      for (User user : userProfiles) {
        if (button.getId().substring(7).equals(user.getId())) {
          button.setOpacity(user.getOpacity());
        }
      }
    }
  }

  /**
   * Load the opacity status for each user profile's label.
   *
   * @throws IOException {@inheritDoc}
   */
  public static void loadUserLabels() throws IOException {

    // create new JSON file
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // store user information into array
    List<User> userProfiles;
    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
      fr.close();
    } catch (FileNotFoundException e) {
      return;
    }

    for (Label label : userLabels) {
      for (User user : userProfiles) {
        if (label.getId().substring(7).equals(user.getId())) {
          label.setText(user.getName());
        }
      }
    }
  }

  /**
   * Initialize the profile view scene and setup the button and label arrays while retrieving the
   * current user profile.
   */
  public void initialize() {
    initializeButtonArray();
    initializeUserLabelArray();
    getCurrentProfile();
  }

  /** Setup the array for user buttons. */
  public void initializeButtonArray() {
    // store all 6 buttons into an array
    arrayButtons = new ToggleButton[6];
    arrayButtons[0] = btnUserOne;
    arrayButtons[1] = btnUserTwo;
    arrayButtons[2] = btnUserThree;
    arrayButtons[3] = btnUserFour;
    arrayButtons[4] = btnUserFive;
    arrayButtons[5] = btnUserSix;
  }

  /** Setup the array for user labels. */
  public void initializeUserLabelArray() {
    // store all 6 user labels into an array
    userLabels = new Label[6];
    userLabels[0] = lblUserOne;
    userLabels[1] = lblUserTwo;
    userLabels[2] = lblUserThree;
    userLabels[3] = lblUserFour;
    userLabels[4] = lblUserFive;
    userLabels[5] = lblUserSix;
  }

  /** Get the current user profile using the given ID. */
  private void getCurrentProfile() {
    if (currentUserId.equals("Zero")) {
      currentUser = null;
    } else {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      List<User> userProfiles = new ArrayList<User>();

      try {
        // read existing user profiles from JSON file and store into array list
        FileReader fr = new FileReader("profiles/profiles.json");
        userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
        fr.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      // select the current profile that was chosen by the user
      for (User userProfile : userProfiles) {
        if (userProfile.getId().equals(ProfileViewController.getCurrentUserId())) {
          currentUser = userProfile;
        }
      }
    }
  }

  /**
   * Switch from profile view scene to profile creation scene.
   *
   * @param event the event triggered when the create new profile button is clicked
   * @throws IOException {@inheritDoc}
   */
  @FXML
  private void onCreateNewProfile(ActionEvent event) throws IOException {
    // play sound effect when button is clicked
    try {
      player = new MediaUtil(MediaUtil.buttonClickFile);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    player.play();

    // set the root to the profile creation scene when "Create Profile" button is
    // pressed
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.PROFILE_CREATION));

    // Store this scene into the next controller
    ((ProfileCreationController) SceneManager.getLoader(AppUi.PROFILE_CREATION).getController())
        .setPreScene(AppUi.PROFILE_VIEW);

    lastUserButtonPressed.setOpacity(1.0);
    btnCreateNewProfile.setDisable(true);
    btnDeleteProfile.setDisable(false);
  }

  /**
   * Switch from profile view scene to game mode scene.
   *
   * @param event the event triggered when the back button is clicked
   * @throws IOException {@inheritDoc}
   */
  @FXML
  private void onGoBack(ActionEvent event) throws IOException {
    // play sound effect when button is clicked
    try {
      player = new MediaUtil(MediaUtil.buttonClickFile);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    player.play();

    // go to the main menu scene
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_MODE));
    ((GameModeController) SceneManager.getLoader(AppUi.GAME_MODE).getController()).initialize();
  }

  /**
   * Switch from profile view scene to the statistics view page of the current user profile.
   *
   * @param event the event triggered when the view statistics button is clicked
   */
  @FXML
  private void onViewStatistics(ActionEvent event) {
    // play sound effect when button is clicked
    try {
      player = new MediaUtil(MediaUtil.buttonClickFile);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    player.play();

    // change scene to statistics view scene on click of this button
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.STATISTICS_VIEW));

    ((StatisticsViewController) SceneManager.getLoader(AppUi.STATISTICS_VIEW).getController())
        .load();
  }

  /**
   * Select user profile slot 1 as the current profile.
   *
   * @param event the event triggered when user one button is clicked
   */
  @FXML
  private void onSelectUserOne(ActionEvent event) {
    ToggleButton btnClicked = (ToggleButton) event.getSource();
    // keep track of button pressed and associated label
    lastUserButtonPressed = btnClicked;
    labelAssociatedToLastUserButtonPressed = lblUserOne;
    // keep track of user id
    currentUserId = "One";

    // no user profile established --> enable "create profile" button, disable
    // "delete profile"
    // button, disable "view statistics" button
    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
      btnDeleteProfile.setDisable(true);
      btnViewStatistics.setDisable(true);
    }
    // user profile is established --> disable "create profile" button, enable
    // "delete profile"
    // button, enable "view statistics" button
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
      btnDeleteProfile.setDisable(false);
      btnViewStatistics.setDisable(false);
      getCurrentProfile();
    }
  }

  /**
   * Select user profile slot 2 as the current profile.
   *
   * @param event the event triggered when user two button is clicked
   */
  @FXML
  private void onSelectUserTwo(ActionEvent event) {
    ToggleButton btnClicked = (ToggleButton) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserTwo;
    labelAssociatedToLastUserButtonPressed = lblUserTwo;
    // keep track of user id
    currentUserId = "Two";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
      btnDeleteProfile.setDisable(true);
      btnViewStatistics.setDisable(true);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
      btnDeleteProfile.setDisable(false);
      btnViewStatistics.setDisable(false);
      getCurrentProfile();
    }
  }

  /**
   * Select user profile slot 3 as the current profile.
   *
   * @param event the event triggered when user three button is clicked
   */
  @FXML
  private void onSelectUserThree(ActionEvent event) {
    ToggleButton btnClicked = (ToggleButton) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserThree;
    labelAssociatedToLastUserButtonPressed = lblUserThree;
    // keep track of user id
    currentUserId = "Three";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
      btnDeleteProfile.setDisable(true);
      btnViewStatistics.setDisable(true);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
      btnDeleteProfile.setDisable(false);
      btnViewStatistics.setDisable(false);
      getCurrentProfile();
    }
  }

  /**
   * Select user profile slot 4 as the current profile.
   *
   * @param event the event triggered when user four button is clicked
   */
  @FXML
  private void onSelectUserFour(ActionEvent event) {
    ToggleButton btnClicked = (ToggleButton) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserFour;
    labelAssociatedToLastUserButtonPressed = lblUserFour;
    // keep track of user id
    currentUserId = "Four";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
      btnDeleteProfile.setDisable(true);
      btnViewStatistics.setDisable(true);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
      btnDeleteProfile.setDisable(false);
      btnViewStatistics.setDisable(false);
      getCurrentProfile();
    }
  }

  /**
   * Select user profile slot 5 as the current profile.
   *
   * @param event the event triggered when user five button is clicked
   */
  @FXML
  private void onSelectUserFive(ActionEvent event) {
    ToggleButton btnClicked = (ToggleButton) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserFive;
    labelAssociatedToLastUserButtonPressed = lblUserFive;
    // keep track of user id
    currentUserId = "Five";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
      btnDeleteProfile.setDisable(true);
      btnViewStatistics.setDisable(true);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
      btnDeleteProfile.setDisable(false);
      btnViewStatistics.setDisable(false);
      getCurrentProfile();
    }
  }

  /**
   * Select user profile slot 6 as the current profile.
   *
   * @param event the event triggered when user six button is clicked
   */
  @FXML
  private void onSelectUserSix(ActionEvent event) {
    ToggleButton btnClicked = (ToggleButton) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserSix;
    labelAssociatedToLastUserButtonPressed = lblUserSix;
    // keep track of user id
    currentUserId = "Six";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
      btnDeleteProfile.setDisable(true);
      btnViewStatistics.setDisable(true);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
      btnDeleteProfile.setDisable(false);
      btnViewStatistics.setDisable(false);
      getCurrentProfile();
    }
  }

  /**
   * Select to play with a guest profile.
   *
   * @param event the event triggered when guest button is clicked
   */
  @FXML
  private void onSelectGuest(ActionEvent event) throws IOException {
    // keep track of button pressed
    lastUserButtonPressed = btnGuest;
    // user id 0 for guest
    currentUserId = "Zero";
    getCurrentProfile();

    ToggleButton button = (ToggleButton) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.GAME_MODE));
    ((GameModeController) SceneManager.getLoader(AppUi.GAME_MODE).getController()).initialize();
  }
}
