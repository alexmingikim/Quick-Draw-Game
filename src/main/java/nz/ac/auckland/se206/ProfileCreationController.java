package nz.ac.auckland.se206;

import java.io.FileWriter;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class ProfileCreationController {

	@FXML
	private TextField usernameTextField;

	@FXML
	private PasswordField passwordTextField;

	@FXML
	private Button confirmButton;

	@FXML
	private Button cancelButton;

	private AppUi preScene;

	public void setPreScene(AppUi preScene) {
		this.preScene = preScene;
	}

	@FXML
	private void onConfirm(ActionEvent event) throws IOException {
		// User Profile Save Format (Case Sensitive)
		// username|password|games_won|games_lost|words_in_previous_runs|avg_time|fastest_win
		String username = usernameTextField.getText();
		String password = passwordTextField.getText();

		// Appending new user profile to the text file database with all profiles
		FileWriter writer = null;
		try {
			writer = new FileWriter("src/main/resources/profiles.txt", true);
			writer.write(username + "|" + password + "|" + 0 + "|" + 0 + "|" + "" + "|" + 0 + "|" + 0 + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
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