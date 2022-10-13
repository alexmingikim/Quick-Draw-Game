package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SettingsController.Difficulty;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.util.MediaUtil;

public class HiddenWordModeController {

  static final int NUMBER_EASY_WORDS = 145;

  static final int NUMBER_MEDIUM_WORDS = 132;

  static final int NUMBER_HARD_WORDS = 68;

  static final int TOTAL_NUMBER_WORDS = NUMBER_EASY_WORDS + NUMBER_MEDIUM_WORDS + NUMBER_HARD_WORDS;

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

  private int predictionRank = 1;

  private int prevPredictionRank = 0;

  private Timeline timeline;

  private MediaUtil player;

  // mouse coordinates
  private double currentX;
  private double currentY;

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

  public void startNewGame() throws ModelException, IOException, WordNotFoundException {
    // clear the predictions board and change message when new game is started
    lblStatus.setText("---------- Press Start to Begin ----------");
    predictionsTextFlow.getChildren().clear();

    // reset the timer and clear the canvas
    setCounter();
    lblTimer.setText(String.valueOf(counter));
    onClear();

    // initialise to get new category and make the start button visible
    initialize();
    subInitialize();
    btnStart.setVisible(true);
    blankStatus = true;
    try {
      updatePrediction();
    } catch (TranslateException e) {
      e.printStackTrace();
    }
  }

  // helper methods
  private void updatePrediction() throws TranslateException {
    if (blankStatus == false) {
      // get top 10 predictions
      List<Classifications.Classification> predictions =
          model.getPredictions(getCurrentSnapshot(), 10);
      // get all predictions
      List<Classifications.Classification> predictionsAll =
          model.getPredictions(getCurrentSnapshot(), TOTAL_NUMBER_WORDS);
      final StringBuilder sb = new StringBuilder();
      int i = 1;

      // get the rank of the prediction
      predictionRank = 1;
      for (Classifications.Classification prediction : predictionsAll) {
        if (prediction.getClassName().equals(category.replace(" ", "_"))) {
          break;
        }
        predictionRank++;
      }

      // set message depending on if user is getting further or closer
      if (predictionRank > prevPredictionRank) {
        lblStatus.setText("Getting further...");
      } else {
        lblStatus.setText("Getting closer!!!");
      }

      // statusLabel.setText(predictionRank + " " + prevPredictionRank);
      prevPredictionRank = predictionRank;

      // set the labels
      predictionsTitleLabel.setText("ROBO'S PREDICTIONS");
      predictionsLabel.setText("Top 10 Predictions\n");

      // clear the text flow
      predictionsTextFlow.getChildren().clear();

      // set the font
      Font font = Font.font("Courier New", FontWeight.NORMAL, FontPosture.REGULAR, 16);

      // for all predictions, print its ranking and if a prediction is in top 3 and
      // matches with the category, call the player win method
      for (final Classifications.Classification prediction : predictions) {
        Text text = new Text();
        sb.setLength(0);

        // change the display format of the prediction word depending on length
        String word = prediction.getClassName().replace("_", " ");

        // set the font to be bold if the guess is the category
        if (prediction.getClassName().equals(category.replace(" ", "_"))) {
          font = Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 16);
          // change message depending on prediction ranking
          if (i > 5) {
            lblStatus.setText("Almost there!! You're doing great!");
          } else if (i <= 5) {
            lblStatus.setText("So close!!! You can do it!");
          }
        } else {
          font = Font.font("Courier New", FontWeight.NORMAL, FontPosture.REGULAR, 16);
        }

        int winCondition = 0;
        Difficulty accuracyDifficulty = null;
        Difficulty confidenceDifficulty = null;

        // Checking for winning condition for each accuracy difficulty setting
        if (currentProfile == null) {
          accuracyDifficulty = SettingsController.getGuestDifficulty()[0];
          confidenceDifficulty = SettingsController.getGuestDifficulty()[3];
        } else {
          accuracyDifficulty = currentProfile.getDifficulties()[0];
          confidenceDifficulty = currentProfile.getDifficulties()[3];
        }
        switch (accuracyDifficulty) {
          case EASY:
            // user wins if the word is within top 3 of the AI's prediction
            if (prediction.getClassName().equals(category.replace(" ", "_"))
                && (i == 1 || i == 2 || i == 3)) {
              winCondition++;
            }
            break;
          case MEDIUM:
            // user wins if the word is within top 2 of the AI's prediction
            if (prediction.getClassName().equals(category.replace(" ", "_"))
                && (i == 1 || i == 2)) {
              winCondition++;
            }
            break;
          case HARD:
            // user wins if the word is within top 1 of the AI's prediction
            if (prediction.getClassName().equals(category.replace(" ", "_")) && (i == 1)) {
              winCondition++;
            }
            break;
          default:
            break;
        }

        // Checking for winning condition for each confidence difficulty setting
        switch (confidenceDifficulty) {
          case EASY:
            // user meets one win condition if the word has at least 1% confidence level
            if (prediction.getProbability() >= 0.01) {
              winCondition++;
            }
            break;
          case MEDIUM:
            // user meets one win condition if the word has at least 10% confidence level
            if (prediction.getProbability() >= 0.1) {
              winCondition++;
            }
            break;
          case HARD:
            // user meets one win condition if the word has at least 25% confidence level
            if (prediction.getProbability() >= 0.25) {
              winCondition++;
            }
            break;
          case MASTER:
            // user meets one win condition if the word has at least 50% confidence level
            if (prediction.getProbability() >= 0.5) {
              winCondition++;
            }
            break;
        }

        if (winCondition == 2) {
          setWin();
        }

        if (i != 10) {
          sb.append(i)
              .append("  :  ")
              .append(word.substring(0, 1).toUpperCase() + word.substring(1))
              .append(System.lineSeparator());
          text.setText(sb.toString());
          text.setFont(font);
          predictionsTextFlow.getChildren().add(text);
        } else {
          sb.append(i)
              .append(" :  ")
              .append(word.substring(0, 1).toUpperCase() + word.substring(1))
              .append(System.lineSeparator());
          text.setText(sb.toString());
          text.setFont(font);
          predictionsTextFlow.getChildren().add(text);
        }
        i++;
      }

    } else {
      predictionsTitleLabel.setText("ROBO'S PREDICTIONS");
      predictionsLabel.setText("Top 10 Predictions\n");
    }
  }

  private void setWin() {
    // stop game and print message
    canvas.setDisable(true);
    timeline.stop();
    lblStatus.setText("Congratulations! You Won! Surely, the next Picasso!");

    if (player != null) {
      player.stop();
    }

    try {
      player = new MediaUtil(MediaUtil.winGameFile);
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    player.play();

    // Update profile if it is not a guest profile
    if (currentProfile != null) {
      currentProfile.updateWords(category);
      currentProfile.incrementNoOfGamesPlayed();
      currentProfile.chooseWonOrLost(true);
      currentProfile.updateTimeWon(60 - counter, category);
      checkMaxWords();
      updateProfile();
    }

    // make buttons visible or disabled
    btnClear.setDisable(true);
    btnDraw.setDisable(true);
    btnErase.setDisable(true);
    btnBack.setVisible(true);

    // show results of the game
    ((ResultsController) SceneManager.getLoader(AppUi.RESULTS).getController())
        .setGameResults(true);
    switchToResults();
  }

  private void switchToResults() {
    Scene scene = btnStart.getParent().getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.RESULTS));
    ((ResultsController) SceneManager.getLoader(AppUi.RESULTS).getController()).subInitialize();
  }

  private void updateProfile() {
    // initializing utilities to read and store the profiles
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    List<User> userProfiles = new ArrayList<User>();
    currentProfile = ProfileViewController.getCurrentUser();

    try {
      // read existing user profiles from JSON file and store into array list
      FileReader fr = new FileReader("profiles/profiles.json");
      userProfiles = gson.fromJson(fr, new TypeToken<List<User>>() {}.getType());
      fr.close();

      // select the current profile that was chosen by the user
      int userIndex = -1;
      for (User userProfile : userProfiles) {
        if (userProfile.getId().equals(currentProfile.getId())) {
          userIndex = userProfiles.indexOf(userProfile);
        }
      }
      userProfiles.set(userIndex, currentProfile);

      // Write any updates from the current game to the json file
      FileWriter fw = new FileWriter("profiles/profiles.json");
      gson.toJson(userProfiles, fw);
      fw.flush();
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void checkMaxWords() {
    // get a list of all the categories and profile's encountered words
    ArrayList<String[]> categoryList = new ArrayList<String[]>(getCategories());
    ArrayList<String> wordsList =
        new ArrayList<String>(Arrays.asList(currentProfile.getWords().split(",")));

    // declare variables to store number of words encountered in each category
    int easyWords = 0;
    int mediumWords = 0;
    int hardWords = 0;

    // find number of words encountered in each category
    for (String[] category : categoryList) {
      if (wordsList.contains(category[0])) {
        switch (category[1]) {
          case "E":
            easyWords++;
            break;
          case "M":
            mediumWords++;
            break;
          case "H":
            hardWords++;
            break;
        }
      }
    }

    // check if the words encountered has reached maximum number of words available
    // per difficulty
    if (easyWords == NUMBER_EASY_WORDS) {
      currentProfile.resetWords();
    }
    if (mediumWords == NUMBER_MEDIUM_WORDS) {
      currentProfile.resetWords();
    }
    if (hardWords == NUMBER_HARD_WORDS) {
      currentProfile.resetWords();
    }
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  public BufferedImage getCurrentSnapshot() {
    final Image snapshot = canvas.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  /**
   * Finds and displays the definition of an input word to user.
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

  @FXML
  private void onStart() {
    // change message
    lblStatus.setText("Hmmmmmmmm............");

    // create a timeline instance for timer countdown and to update predictions
    // every second
    timeline =
        new Timeline(
            (new KeyFrame(Duration.seconds(1), e -> decreaseTime())),
            (new KeyFrame(
                Duration.seconds(1),
                e -> {
                  try {
                    // updating the predictions list every second
                    updatePrediction();
                  } catch (TranslateException e1) {
                    e1.printStackTrace();
                  }
                })));
    KeyFrame kf =
        new KeyFrame(
            Duration.seconds(1),
            event -> {
              if (counter <= 0) {
                timeline.stop();
                setLose();
              }
            });
    timeline.getKeyFrames().addAll(kf, new KeyFrame(Duration.seconds(1)));
    timeline.setCycleCount(counter);
    timeline.play();

    // enable user to draw
    onDraw();

    // Set start and back buttons to invisible and other buttons enabled
    btnClear.setDisable(false);
    canvas.setDisable(false);
    btnDraw.setDisable(false);
    btnErase.setDisable(false);
    btnStart.setVisible(false);
    btnBack.setVisible(false);
  }

  /**
   * This method is called when the "Back" button is pressed.
   *
   * @param event the clicking action from the user
   */
  @FXML
  private void onBack(ActionEvent event) {
    Button btnSceneIsIn = (Button) event.getSource();
    Scene scene = btnSceneIsIn.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_MODE));
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    blankStatus = true;
  }

  @FXML
  private void onErase() {
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 10.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // set the brush to clear markings on the canvas
          graphic.clearRect(x, y, 20, 20);
        });
  }

  @FXML
  private void onDraw() {
    // save coordinates when mouse is pressed on the canvas
    canvas.setOnMousePressed(
        e -> {
          currentX = e.getX();
          currentY = e.getY();
          blankStatus = false;
        });

    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 6;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.BLACK);
          graphic.setLineWidth(size);

          // Create a line that goes from the point (currentX, currentY) and (x,y)
          graphic.strokeLine(currentX, currentY, x, y);

          // update the coordinates
          currentX = x;
          currentY = y;
        });
  }

  private void decreaseTime() {
    counter--;
    lblTimer.setText(String.valueOf(counter));
    if (counter == 10) {
      try {
        player = new MediaUtil(MediaUtil.fastTickingFile);
      } catch (URISyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      player.play();
    }
  }

  private void setLose() {
    // stop game and print message
    canvas.setDisable(true);
    lblStatus.setText("You Lost. Unfortunately, I was not able to guess your drawing in time.");

    try {
      player = new MediaUtil(MediaUtil.loseGameFile);
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    player.play();

    // Update profile if it is not a guest profile
    if (currentProfile != null) {
      currentProfile.updateWords(category);
      currentProfile.incrementNoOfGamesPlayed();
      currentProfile.chooseWonOrLost(false);
      currentProfile.updateTimeLost();
      checkMaxWords();
      updateProfile();
    }

    // make buttons visible or disabled
    btnClear.setDisable(true);
    btnDraw.setDisable(true);
    btnErase.setDisable(true);
    btnBack.setVisible(true);

    // show results of the game
    ((ResultsController) SceneManager.getLoader(AppUi.RESULTS).getController())
        .setGameResults(false);
    switchToResults();
  }
}
