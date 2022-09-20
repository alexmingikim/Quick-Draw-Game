package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class ProfileViewController {

  @FXML Button btnCreateNewProfile;

  @FXML Button btnDeleteProfile;

  @FXML Button btnGoBack;

  @FXML Button btnUserOne;

  @FXML Button btnUserTwo;

  @FXML Button btnUserThree;

  @FXML Button btnUserFour;

  @FXML Button btnUserFive;

  @FXML Button btnUserSix;

  @FXML Button btnGuest;

  static Button lastUserButtonPressed;

  String currentUserSelected;
  static String currentUserId;

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

    lastUserButtonPressed.setOpacity(1);
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
  private void onUserOne(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserOne;
    // keep track of user id
    currentUserId = "One";

    // no user profile established --> enable "create profile" button
    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
    }
    // user profile is established --> disable "create profile" button
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
    }
  }

  @FXML
  private void onUserTwo(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserTwo;
    // keep track of user id
    currentUserId = "Two";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
    }
  }

  @FXML
  private void onUserThree(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserThree;
    // keep track of user id
    currentUserId = "Three";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
    }
  }

  @FXML
  private void onUserFour(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserFour;
    // keep track of user id
    currentUserId = "Four";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
    }
  }

  @FXML
  private void onUserFive(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserFive;
    // keep track of user id
    currentUserId = "Five";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
    }
  }

  @FXML
  private void onUserSix(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    // keep track of button pressed
    lastUserButtonPressed = btnUserSix;
    // keep track of user id
    currentUserId = "Six";

    if (btnClicked.getOpacity() == 0.5) {
      btnCreateNewProfile.setDisable(false);
    }
    if (btnClicked.getOpacity() == 1) {
      btnCreateNewProfile.setDisable(true);
    }
  }

  @FXML
  private void onGuest(ActionEvent event) {
    // user id 0 for guest
    currentUserId = "Zero";
  }
}
