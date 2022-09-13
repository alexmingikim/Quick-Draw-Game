package nz.ac.auckland.se206;

import java.io.FileWriter;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileCreationController {

	@FXML
	private TextField usernameTextField;

	@FXML
	private PasswordField passwordTextField;

	@FXML
	private Button confirmButton;

	@FXML
	private Button cancelButton;

	@FXML
	private void onConfirm() {
		// User Profile Save Format (Case Sensitive)
		// username|password|games_won|games_lost|words_in_previous_runs|avg_time|fastest_win
		String username = usernameTextField.getText();
		String password = passwordTextField.getText();

		// Appending new user profile to the database with all profiles
		try {
			FileWriter writer = new FileWriter("/resources/profiles.txt");
			writer.append(username + "|" + password + "|" + 0 + "|" + 0 + "|" + "" + "|" + 0 + "|" + 0);
			writer.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	@FXML
	private void onCancel() {

	}
}