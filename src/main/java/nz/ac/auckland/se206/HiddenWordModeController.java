package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.TextFlow;
import nz.ac.auckland.se206.ml.DoodlePrediction;

public class HiddenWordModeController {

  @FXML private Canvas canvas;

  @FXML private Label lblProfileName;

  @FXML private Label lblStatus;

  @FXML private Label lblTimer;

  @FXML private Label predictionsLabel;

  @FXML private Label predictionsTitleLabel;

  @FXML private Label lblDefinition;

  @FXML private ScrollPane scrollPaneDefinition;

  @FXML private TextFlow predictionsTextFlow;

  @FXML private Button btnStart;

  @FXML private Button btnDraw;

  @FXML private Button btnErase;

  @FXML private Button btnClear;

  @FXML private Button btnBack;

  private GraphicsContext graphic;

  private DoodlePrediction model;

  private User currentProfile = ProfileViewController.getCurrentUser();

  private boolean blankStatus = true;

  private String category;

  private String definition;

  private int counter = 60;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   * @throws WordNotFoundException
   */
  public void initialize() throws ModelException, IOException, WordNotFoundException {
    // initialise graphics and the prediction model
    graphic = canvas.getGraphicsContext2D();
    model = new DoodlePrediction();

    // set buttons to not visible or disabled
    btnClear.setDisable(true);
    btnDraw.setDisable(true);
    btnErase.setDisable(true);
  }

  public void subInitialize() throws IOException, WordNotFoundException {
    // Changes the profile display name
    currentProfile = ProfileViewController.getCurrentUser();
    if (currentProfile == null) {
      lblProfileName.setText("Guest");
    } else {
      lblProfileName.setText(currentProfile.getName());
    }

    if (blankStatus == true) {
      // set a random category according to difficulty
      category = selectRandomCategory();

      // find and display definition
      findAndDisplayDefinition(category);

      // set the time limit according to difficulty
      setCounter();
      lblTimer.setText(String.valueOf(counter));
    }
  }

  /**
   * Finds the definition of an input word.
   *
   * @param word word whose definition must be found
   */
  private void findAndDisplayDefinition(String word) {
    // assign background thread to find definition
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            definition = DictionaryLookUp.searchWordDefinition(word);
            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();

    backgroundTask.setOnSucceeded(
        event -> {
          lblDefinition.setText(
              "Definition: \n" + definition + "\n\nHint: starts with '" + word.charAt(0) + "'");
        });
  }

  private void setCounter() {
    // execute different methods depending on guest or profile account
    if (currentProfile == null) {
      setCounterGuest();
    } else {
      setCounterProfile();
    }
  }

  private void setCounterGuest() {
    switch (SettingsController.getGuestDifficulty()[2]) {
      case EASY:
        // 60s time limit for timer difficulty easy
        counter = 60;
        break;
      case MEDIUM:
        // 45s time limit for timer difficulty easy
        counter = 45;
        break;
      case HARD:
        // 30s time limit for timer difficulty easy
        counter = 30;
        break;
      case MASTER:
        // 15s time limit for timer difficulty easy
        counter = 15;
        break;
    }
  }

  private void setCounterProfile() {
    switch (currentProfile.getDifficulties()[2]) {
      case EASY:
        // 60s time limit for timer difficulty easy
        counter = 60;
        break;
      case MEDIUM:
        // 45s time limit for timer difficulty easy
        counter = 45;
        break;
      case HARD:
        // 30s time limit for timer difficulty easy
        counter = 30;
        break;
      case MASTER:
        // 15s time limit for timer difficulty easy
        counter = 15;
        break;
    }
  }

  private String selectRandomCategory() throws IOException {
    // execute different methods depending on guest or profile account
    if (currentProfile == null) {
      return selectCategoryGuest();
    } else {
      return selectCategoryProfile();
    }
  }

  private String selectCategoryGuest() throws IOException {
    // get a list of all the categories
    ArrayList<String[]> categoryList = new ArrayList<String[]>(getCategories());
    ArrayList<String> modifiedList = new ArrayList<String>();
    Random random = new Random();

    // modify list of categories depending on difficulty chosen
    switch (SettingsController.getGuestDifficulty()[1]) {
      case EASY:
        // only include words under easy difficulty
        for (String[] category : categoryList) {
          if (category[1].equals("E")) {
            modifiedList.add(category[0]);
          }
        }
        break;
      case MEDIUM:
        // only include words under easy and medium difficulty
        for (String[] category : categoryList) {
          if (category[1].equals("E") || category[1].equals("M")) {
            modifiedList.add(category[0]);
          }
        }
        break;
      case HARD:
        // include words under all difficulties
        for (String[] category : categoryList) {
          modifiedList.add(category[0]);
        }
        break;
      case MASTER:
        // only include words under hard difficulty
        for (String[] category : categoryList) {
          if (category[1].equals("H")) {
            modifiedList.add(category[0]);
          }
        }
        break;
    }

    // return random category from modified list depending on difficulty
    int index = random.nextInt(modifiedList.size());
    return modifiedList.get(index);
  }

  private String selectCategoryProfile() throws IOException {
    // get a list of all the categories
    ArrayList<String> wordsList =
        new ArrayList<String>(Arrays.asList(currentProfile.getWords().split(",")));
    ArrayList<String[]> categoryList = new ArrayList<String[]>(getCategories());
    ArrayList<String> modifiedList = new ArrayList<String>();
    Random random = new Random();

    // modify list of categories depending on difficulty chosen
    switch (currentProfile.getDifficulties()[1]) {
      case EASY:
        // only include words under easy difficulty which the current profile
        // hasn't encountered
        for (String[] category : categoryList) {
          if (category[1].equals("E") && !(wordsList.contains(category[0]))) {
            modifiedList.add(category[0]);
          }
        }
        break;
      case MEDIUM:
        // only include words under easy and medium difficulty which the current profile
        // hasn't encountered
        for (String[] category : categoryList) {
          if ((category[1].equals("E") || category[1].equals("M"))
              && !(wordsList.contains(category[0]))) {
            modifiedList.add(category[0]);
          }
        }
        break;
      case HARD:
        // only all words which the current profile hasn't encountered
        for (String[] category : categoryList) {
          if (!(wordsList.contains(category[0]))) {
            modifiedList.add(category[0]);
          }
        }
        break;
      case MASTER:
        // only include words under hard difficulty which the current profile hasn't
        // encountered
        for (String[] category : categoryList) {
          if (category[1].equals("H") && !(wordsList.contains(category[0]))) {
            modifiedList.add(category[0]);
          }
        }
        break;
    }

    // return random category from modified list depending on difficulty
    int index = random.nextInt(modifiedList.size());
    return modifiedList.get(index);
  }

  private ArrayList<String[]> getCategories() {
    // get a list of all the categories
    ArrayList<String[]> categoryList = new ArrayList<String[]>();
    // Declaring and initializing fields
    String line;
    String[] category;
    try {
      // get all categories from csv file
      BufferedReader br =
          new BufferedReader(new FileReader("src/main/resources/category_difficulty.csv"));
      while ((line = br.readLine()) != null) {
        category = line.split(",");
        categoryList.add(category);
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return categoryList;
  }
}
