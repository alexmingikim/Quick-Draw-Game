package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    // generate error if username field is blank
    if (usernameTextField.getText().isBlank()) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Empty username");
      alert.setHeaderText("Please insert a valid username");
      alert.showAndWait();
      return;
    }

    // save JSON files onto folder
    // if folder does not exist, create one
    try {
      // create profile folder
      Path path = Paths.get("profiles");
      Files.createDirectories(path);
    } catch (FileAlreadyExistsException e) {

    }

    // keep track of username and user id
    String username = usernameTextField.getText();
    String userId = ProfileViewController.getCurrentUserId();

    usernameTextField.clear();

    // create new JSON file
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    List<User> userProfiles = new ArrayList<User>();
    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
      fr.close();
    } catch (FileNotFoundException e) {

    }

    // add new user profile to array list and store as JSON file
    userProfiles.add(new User(userId, username, "0", "0", "0", "-", "0", "-", ""));
    FileWriter writer = new FileWriter("profiles/profiles.json");
    gson.toJson(userProfiles, writer);
    writer.flush();
    writer.close();

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

    Button userProfileButton = ProfileViewController.getLastUserButtonPressed();
    userProfileButton.setOpacity(0.5);
  }
}
