package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import nz.ac.auckland.se206.SceneManager.AppUi;

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

  public static String getCurrentUserId() {
    return currentUserId;
  }

  public static User getCurrentUser() {
    return currentUser;
  }

  public static ToggleButton getLastUserButtonPressed() {
    return lastUserButtonPressed;
  }

  public static Label getLabelAssociatedToLastUserButtonPressed() {
    return labelAssociatedToLastUserButtonPressed;
  }

  // load opacity status
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

  // load opacity status
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

  public void initialize() {
    initializeButtonArray();
    initializeUserLabelArray();
    getCurrentProfile();
  }

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

  @FXML
  private void onCreateNewProfile(ActionEvent event) throws IOException {
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

  @FXML
  private void onGoBack(ActionEvent event) throws IOException {
    // go to the main menu scene
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MAIN_MENU));
    ((MainMenuController) SceneManager.getLoader(AppUi.MAIN_MENU).getController()).subInitialize();
  }

  @FXML
  private void onViewStatistics(ActionEvent event) {
    // change scene to statistics view scene on click of this button
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.STATISTICS_VIEW));

    ((StatisticsViewController) SceneManager.getLoader(AppUi.STATISTICS_VIEW).getController())
        .load();
  }

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

  @FXML
  private void onSelectGuest(ActionEvent event) throws IOException {
    // keep track of button pressed
    lastUserButtonPressed = btnGuest;
    // user id 0 for guest
    currentUserId = "Zero";
    getCurrentProfile();

    ToggleButton button = (ToggleButton) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.MAIN_MENU));
    ((MainMenuController) SceneManager.getLoader(AppUi.MAIN_MENU).getController()).subInitialize();
  }
}
