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
import nz.ac.auckland.se206.SceneManager.AppUi;

public class ProfileViewController {

  @FXML Button btnCreateNewProfile;

  @FXML Button btnViewStatistics;

  @FXML Button btnDeleteProfile;

  @FXML Button btnGoBack;

  @FXML private Button btnUserOne;
  @FXML private Button btnUserTwo;
  @FXML private Button btnUserThree;
  @FXML private Button btnUserFour;
  @FXML private Button btnUserFive;
  @FXML private Button btnUserSix;
  @FXML private Button btnGuest;

  @FXML private Label lblUserOne;
  @FXML private Label lblUserTwo;
  @FXML private Label lblUserThree;
  @FXML private Label lblUserFour;
  @FXML private Label lblUserFive;
  @FXML private Label lblUserSix;

  private static Button[] arrayButtons;

  static Button lastUserButtonPressed;
  static Label labelAssociatedToLastUserButtonPressed;

  String currentUserSelected;
  static String currentUserId;

  @FXML
  private void initialize() {
    initializeButtonArray();
  }

  public void initializeButtonArray() {
    arrayButtons = new Button[6];
    arrayButtons[0] = btnUserOne;
    arrayButtons[1] = btnUserTwo;
    arrayButtons[2] = btnUserThree;
    arrayButtons[3] = btnUserFour;
    arrayButtons[4] = btnUserFive;
    arrayButtons[5] = btnUserSix;
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

  public static String getCurrentUserId() {
    return currentUserId;
  }

  public static Button getLastUserButtonPressed() {
    return lastUserButtonPressed;
  }

  @FXML
  private void onGoBack(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.MAIN_MENU));
  }

  @FXML
  private void onViewStatistics(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.STATISTICS_VIEW));
  }

  @FXML
  private void onUserOne(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    // keep track of button pressed and associated label
    lastUserButtonPressed = btnClicked;
    labelAssociatedToLastUserButtonPressed = lblUserOne;
    // keep track of user id
    currentUserId = "One";

    // no user profile established --> enable "create profile" button, disable "delete profile"
    // button, disable "view statistics" button
    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
      btnDeleteProfile.setDisable(true);
      btnViewStatistics.setDisable(true);
    }
    // user profile is established --> disable "create profile" button, enable "delete profile"
    // button, enable "view statistics" button
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
      btnDeleteProfile.setDisable(false);
      btnViewStatistics.setDisable(false);
    }
  }

  @FXML
  private void onUserTwo(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
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
    }
  }

  @FXML
  private void onUserThree(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
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
    }
  }

  @FXML
  private void onUserFour(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
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
    }
  }

  @FXML
  private void onUserFive(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
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
    }
  }

  @FXML
  private void onUserSix(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
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
    }
  }

  @FXML
  private void onGuest(ActionEvent event) {
    // user id 0 for guest
    currentUserId = "Zero";
  }

  // load opacity status
  public static void loadOpacity() throws IOException {

    // create new JSON file
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // store user information into array
    List<User> userProfiles = new ArrayList<User>();
    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
      fr.close();
    } catch (FileNotFoundException e) {
      return;
    }

    for (Button button : arrayButtons) {
      for (User user : userProfiles) {
        if (button.getId().substring(7).equals(user.getId())) {
          button.setOpacity(user.getOpacity());
        }
      }
    }
  }

  public static Label getLabelAssociatedToLastUserButtonPressed() {
    return labelAssociatedToLastUserButtonPressed;
  }
}
