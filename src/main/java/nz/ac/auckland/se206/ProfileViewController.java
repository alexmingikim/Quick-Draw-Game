package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class ProfileViewController {

  @FXML Button btnCreateNewProfile;

  @FXML Button btnViewStatistics;

  @FXML Button btnGoBack;

  @FXML
  private void onCreateNewProfile(ActionEvent event) throws IOException {
    // set the root to the profile creation scene when "Create Profile button is
    // pressed
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.PROFILE_CREATION));

    // Store this scene into the next controller
    ((ProfileCreationController) SceneManager.getLoader(AppUi.PROFILE_CREATION).getController())
        .setPreScene(AppUi.PROFILE_VIEW);
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
}
