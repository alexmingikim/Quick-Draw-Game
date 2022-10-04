package nz.ac.auckland.se206;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class SettingsController {
  @FXML private Button btnGoBack;

  @SuppressWarnings("rawtypes")
  @FXML
  private ChoiceBox accuracyChoice;

  @SuppressWarnings("rawtypes")
  @FXML
  private ChoiceBox wordsChoice;

  @SuppressWarnings("rawtypes")
  @FXML
  private ChoiceBox timeChoice;

  @SuppressWarnings("rawtypes")
  @FXML
  private ChoiceBox confidenceChoice;

  private String[] accuracyDifficulties = {"Easy", "Medium", "Hard"};

  private String[] otherDifficulties = {"Easy", "Medium", "Hard", "Master"};

  @SuppressWarnings("unchecked")
  public void initialize() {
    // set the difficulty levels for all difficulty settings
    accuracyChoice.setItems(FXCollections.observableArrayList(accuracyDifficulties));
    wordsChoice.setItems(FXCollections.observableArrayList(otherDifficulties));
    timeChoice.setItems(FXCollections.observableArrayList(otherDifficulties));
    confidenceChoice.setItems(FXCollections.observableArrayList(otherDifficulties));

    // set the default difficulty to "Easy"
    accuracyChoice.getSelectionModel().select(0);
    wordsChoice.getSelectionModel().select(0);
    timeChoice.getSelectionModel().select(0);
    confidenceChoice.getSelectionModel().select(0);
  }

  @FXML
  private void onGoBack(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneButtonIsIn = button.getScene();
    sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.MAIN_MENU));
  }
}
