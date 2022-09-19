package nz.ac.auckland.se206;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class ProfileCreationController {

  @FXML private TextField usernameTextField;

  @FXML private Button confirmButton;

  @FXML private Button cancelButton;

  private AppUi preScene;

  public void setPreScene(AppUi preScene) {
    this.preScene = preScene;
  }

  @FXML
  private void onConfirm(ActionEvent event) throws IOException {
    // save JSON files onto folder
    // if folder does not exist, create one
    try {
      Path path = Paths.get("profiles");
      Files.createDirectories(path);
    } catch (FileAlreadyExistsException e) {

    }

    // User Profile Save Format (Case Sensitive)
    // username|password|games_won|games_lost|words_in_previous_runs|avg_time|fastest_win
    String username = usernameTextField.getText();

    // Generate error if username field or password field are blank
    if (usernameTextField.getText().isBlank()) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Empty username");
      alert.setHeaderText("Please insert a valid username");
      alert.showAndWait();
    }

    // Returning to previous scene
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(preScene));
  }

  @FXML
  private void onCancel(ActionEvent event) throws IOException {
    // Returning to previous scene
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(preScene));
  }
}
