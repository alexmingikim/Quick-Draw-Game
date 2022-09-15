package nz.ac.auckland.se206;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class MainMenuController {

	@FXML
	private Button newGameButton;

	@FXML
	private Button createProfileButton;

	private TextToSpeech textToSpeech = new TextToSpeech();

	static FXMLLoader fxmlLoader;

	@FXML
	private void onStartNewGame(ActionEvent event) throws IOException {
		// set the root to the canvas if the start new game button is pressed
		Button button = (Button) event.getSource();
		Scene sceneButtonIsIn = button.getScene();
		sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.CANVAS));

		// text to speech for starting the game
		Task<Void> backgroundTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				textToSpeech.speak("Press the start button to begin");

				return null;
			}
		};

		Thread backgroundThread = new Thread(backgroundTask);
		backgroundThread.start();
	}

	@FXML
	private void onCreateNewProfile(ActionEvent event) throws IOException {
		// set the root to the profile creation scene when create profile button is
		// pressed
		Button button = (Button) event.getSource();
		Scene sceneButtonIsIn = button.getScene();
		sceneButtonIsIn.setRoot(SceneManager.getUiRoot(AppUi.PROFILE_CREATION));

		// Store this scene into the next controller
		((ProfileCreationController) SceneManager.getLoader(AppUi.PROFILE_CREATION).getController())
				.setPreScene(AppUi.MAIN_MENU);
	}

	public String selectRandomCategory() throws IOException {
		// get a list of all the categories
		ArrayList<String> categoryList = new ArrayList<String>();

		// create fields
		String line;
		String[] category;
		String difficulty;
		BufferedReader br;

		try {
			// read from the csv file
			br = new BufferedReader(new FileReader("src/main/resources/category_difficulty.csv"));

			// get all categories that are rated E
			while ((line = br.readLine()) != null) {
				category = line.split(",");
				difficulty = category[1];

				if (difficulty.equals("E")) {
					categoryList.add(category[0]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// return random category from list
		Random random = new Random();
		int index = random.nextInt(categoryList.size());
		return categoryList.get(index);
	}
}
