package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
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
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SettingsController.Difficulty;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.util.MediaUtil;

/**
 * This is the controller of the canvas. You are free to modify this class and the corresponding
 * FXML file as you see fit. For example, you might no longer need the "Predict" button because the
 * DL model should be automatically queried in the background every second.
 *
 * <p>!! IMPORTANT !!
 *
 * <p>Although we added the scale of the image, you need to be careful when changing the size of the
 * drawable canvas and the brush size. If you make the brush too big or too small with respect to
 * the canvas size, the ML model will not work correctly. So be careful. If you make some changes in
 * the canvas and brush sizes, make sure that the prediction works fine.
 */
public class CanvasController {

  static final int NUMBER_EASY_WORDS = 145;

  static final int NUMBER_MEDIUM_WORDS = 132;

  static final int NUMBER_HARD_WORDS = 68;

  static final int TOTAL_NUMBER_WORDS = NUMBER_EASY_WORDS + NUMBER_MEDIUM_WORDS + NUMBER_HARD_WORDS;

  @FXML private Canvas canvas;

  @FXML private Label categoryLabel;

  @FXML private Label predictionsLabel;

  @FXML private Label predictionsTitleLabel;

  @FXML private Label statusLabel;

  @FXML private Label timerLabel;

  @FXML private Label profileUsernameLabel;

  @FXML private Button penButton;

  @FXML private Button eraserButton;

  @FXML private Button startButton;

  @FXML private Button backButton;

  @FXML private Button clearButton;

  @FXML private TextFlow predictionsTextFlow;

  private int counter = 60;

  private String category;

  private GraphicsContext graphic;

  private DoodlePrediction model;

  private Timeline timeline;

  private Stage stage;

  private TextToSpeech textToSpeech = new TextToSpeech();

  private User currentProfile = ProfileViewController.getCurrentUser();

  private MediaUtil player;

  private boolean blankStatus = true;

  private int predictionRank = 1;

  private int prevPredictionRank = 0;

  private List<Integer> newBadges = new ArrayList<Integer>();

  // mouse coordinates
  private double currentX;
  private double currentY;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {
    subInitialize();

    // initialise graphics and the prediction model
    graphic = canvas.getGraphicsContext2D();
    model = new DoodlePrediction();

    // set buttons to not visible or disabled
    clearButton.setDisable(true);
    penButton.setDisable(true);
    eraserButton.setDisable(true);
  }

  /**
   * This method changes the username display based on the current user profile. The username
   * becomes "Guest" if there is no user profile selected. A new game is also started if the canvas
   * is blank.
   *
   * @throws IOException if an input or output exception occurs
   */
  public void subInitialize() throws IOException {
    newBadges.clear();
    // Changes the profile display name
    currentProfile = ProfileViewController.getCurrentUser();
    if (currentProfile == null) {
      profileUsernameLabel.setText("Guest");
    } else {
      profileUsernameLabel.setText(currentProfile.getName());
    }

    if (blankStatus == true) {
      // set a random category according to difficulty
      category = selectRandomCategory();
      categoryLabel.setText(
          "Category: " + category.substring(0, 1).toUpperCase() + category.substring(1));
      // set the time limit according to difficulty
      setCounter();
      timerLabel.setText(String.valueOf(counter));
    }
  }

  /**
   * This method is used to start a new game when the current game is finished.
   *
   * @throws ModelException if the prediction model has an error
   * @throws IOException if the input and output of the file has an error
   */
  public void startNewGame() throws ModelException, IOException {
    // clear the predictions board and change message when new game is started
    statusLabel.setText("---------- Press Start to Begin ----------");
    predictionsTextFlow.getChildren().clear();

    // reset the timer and clear the canvas
    setCounter();
    timerLabel.setText(String.valueOf(counter));
    onClear();

    // initialise to get new category and make the start button visible
    initialize();
    startButton.setVisible(true);
    blankStatus = true;
    try {
      updatePrediction();
    } catch (TranslateException e) {
      e.printStackTrace();
    }
  }

  /** Save the final snapshot of the canvas after the game has ended. */
  public void saveDrawing() {
    // create a new file choose instance and prompt the user to select a location
    // and name with a suggested default name
    FileChooser saveFile = new FileChooser();
    saveFile.setTitle("Save File");
    FileChooser.ExtensionFilter extensionFilter =
        new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
    saveFile.getExtensionFilters().add(extensionFilter);
    saveFile.setInitialFileName(category);
    File file = saveFile.showSaveDialog(stage);

    // if the file is not null, render and save the image
    if (file != null) {
      try {
        WritableImage writableImage = new WritableImage(390, 250);
        canvas.snapshot(null, writableImage);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
        ImageIO.write(renderedImage, "png", file);
      } catch (IOException e) {
        // otherwise print exception message
        e.printStackTrace();
      }
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

  /** Set the value of the timer depending on the the user profile's difficulty setting. */
  private void setCounter() {
    // execute different methods depending on guest or profile account
    if (currentProfile == null) {
      setCounterGuest();
    } else {
      setCounterProfile();
    }
  }

  /**
   * Select a random word depending on the user profile's difficulty
   *
   * @return the random selected word
   * @throws IOException if a file input or output error occurs
   */
  private String selectRandomCategory() throws IOException {
    // execute different methods depending on guest or profile account
    if (currentProfile == null) {
      return selectCategoryGuest();
    } else {
      return selectCategoryProfile();
    }
  }

  /** Set the initial timer value depending on current profile difficulty. */
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

  /** Set the initial timer value depending on selected guest difficulty. */
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

  /**
   * Retrieve all categories present in the resource file.
   *
   * @return the array of categories with their corresponding difficulty
   */
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

  /**
   * Select a random category based on the difficulty setting of the current profile.
   *
   * @return the random selected word
   * @throws IOException if a file input and output error occurs
   */
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

  /**
   * Select a random category based on the difficulty setting of the guest profile.
   *
   * @return the random selected word
   * @throws IOException if a file input and output error occurs
   */
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

  /**
   * Check if the current profile has encountered every single word in one or more of the categoryr
   * difficulties.
   */
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
   * Check if the fastest won game on this profile meets the requirements for any of the Speed Demon
   * badges.
   */
  private void checkSpeedDemonQualifications() {
    int fastestGame = Integer.parseInt(currentProfile.getFastestWonGameTime());
    // Check if game won is under 30 seconds
    if (fastestGame < 30 && !currentProfile.getBadges().contains(0)) {
      currentProfile.updateBadges(0);
      newBadges.add(0);
    }
    // Check if game won is under 10 seconds
    if (fastestGame < 10 && !currentProfile.getBadges().contains(1)) {
      currentProfile.updateBadges(1);
      newBadges.add(1);
    }
    // Check if game won is under 5 seconds
    if (fastestGame < 5 && !currentProfile.getBadges().contains(2)) {
      currentProfile.updateBadges(2);
      newBadges.add(2);
    }
  }

  /**
   * Check if the winning streak on this profile meets the requirements for any of the Winning
   * Streak badges.
   */
  private void checkWinningStreakQualifications() {
    int winStreak = currentProfile.getWinStreak();
    // Check if the winning streak on this profile is over 2
    if (winStreak >= 2 && !currentProfile.getBadges().contains(3)) {
      currentProfile.updateBadges(3);
      newBadges.add(3);
    }
    // Check if the winning streak on this profile is over 5
    if (winStreak >= 5 && !currentProfile.getBadges().contains(4)) {
      currentProfile.updateBadges(4);
      newBadges.add(4);
    }
    // Check if the winning streak on this profile is over 10
    if (winStreak >= 10 && !currentProfile.getBadges().contains(5)) {
      currentProfile.updateBadges(5);
      newBadges.add(5);
    }
  }

  /**
   * Check if the total number of games on this profile meets the requirement for any of the Veteran
   * badges.
   */
  private void checkVeteranQualifications() {
    int totalGames = Integer.parseInt(currentProfile.getNoOfGamesPlayed());
    // Check if total number of games on this profile is over 5
    if (totalGames >= 5 && !currentProfile.getBadges().contains(6)) {
      currentProfile.updateBadges(6);
      newBadges.add(6);
    }
    // Check if total number of games on this profile is over 10
    if (totalGames >= 10 && !currentProfile.getBadges().contains(7)) {
      currentProfile.updateBadges(7);
      newBadges.add(7);
    }
    // Check if total number of games on this profile is over 20
    if (totalGames >= 20 && !currentProfile.getBadges().contains(8)) {
      currentProfile.updateBadges(8);
      newBadges.add(8);
    }
  }

  /**
   * Check if the current game is eligible to receive the Challenger badge which has the requirement
   * of winning a game with a master difficulty setting.
   */
  private void checkChallengerQualifications() {
    // Check if the current game being played has a master difficulty setting
    for (Difficulty difficulty : currentProfile.getDifficulties()) {
      if (difficulty == Difficulty.MASTER && !currentProfile.getBadges().contains(9)) {
        currentProfile.updateBadges(9);
        newBadges.add(9);
      }
    }
  }

  /** Update and save the new changes to the current profile to the local json file. */
  private void updateProfile() {
    // initializing utilities to read and store the profiles
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    List<User> userProfiles;
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

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    blankStatus = true;
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

  /**
   * This method is executed when the start button is pressed. The timer begins and the AI predicts
   * what is drawn.
   */
  @FXML
  private void onStart() {
    // change message
    statusLabel.setText("Hmmmmmmmm............");

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
    onPen();

    // Set start and back buttons to invisible and other buttons enabled
    clearButton.setDisable(false);
    canvas.setDisable(false);
    penButton.setDisable(false);
    eraserButton.setDisable(false);
    startButton.setVisible(false);
    backButton.setVisible(false);
  }

  /**
   * This method is executed whenever the user clicks and drags on the canvas to draw an image. The
   * pen options are set according to the standard measurements.
   */
  @FXML
  private void onPen() {
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

  /**
   * This method is executed when the user clicks and drags on the canvas and the eraser button is
   * clicked. The eraser size option is set to be bigger than the pen for easier erasing.
   */
  @FXML
  private void onErase() {
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 10.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // set the brush to clear markings on the canvas
          graphic.clearRect(x, y, 10, 10);
        });
  }

  /** This method plays the audio to inform users what the current category is. */
  @FXML
  private void onPlayTextToSpeech() {
    // text to speech: says the current category being played
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // global variable textToSpeech initialized at the beginning
            textToSpeech.speak(category);
            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }

  /**
   * Retrieves the predictions for the current snapshot of the canvas and displays the top 10
   * predictions as well as an indicator for whether the AI is close to guessing the current word.
   *
   * @throws TranslateException if the model translation has an error
   */
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
        statusLabel.setText("Getting further...");
      } else {
        statusLabel.setText("Getting closer!!!");
      }

      // statusLabel.setText(predictionRank + " " + prevPredictionRank);
      prevPredictionRank = predictionRank;

      // set the labels
      predictionsTitleLabel.setText("ROBO'S PREDICTIONS");
      predictionsLabel.setText("Top 10 Predictions\n");

      // clear the text flow
      predictionsTextFlow.getChildren().clear();

      // set the font
      Font font;

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
            statusLabel.setText("Almost there!! You're doing great!");
          } else if (i <= 5) {
            statusLabel.setText("So close!!! You can do it!");
          }
        } else {
          font = Font.font("Courier New", FontWeight.NORMAL, FontPosture.REGULAR, 16);
        }

        int winCondition = 0;
        Difficulty accuracyDifficulty;
        Difficulty confidenceDifficulty;

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

        // combining all the predicted words into a list
        if (i != 10) {
          sb.append(i)
              .append("  :  ")
              .append(word.substring(0, 1).toUpperCase() + word.substring(1))
              .append(System.lineSeparator());
          text.setText(sb.toString());
          text.setFont(font);
          predictionsTextFlow.getChildren().add(text);
        } else {
          // special consideration for spacing if it is the 10th word
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

  /**
   * This method is executed when the user wins the game. A sound effect is played for winning and
   * the stats of the game is stored if the current user profile is not the default guest.
   */
  private void setWin() {
    // stop game and print message
    canvas.setDisable(true);
    timeline.stop();
    statusLabel.setText("Congratulations! You Won! Surely, the next Picasso!");

    if (player != null) {
      player.stop();
    }

    // play winning sound effect
    try {
      player = new MediaUtil(MediaUtil.winGameFile);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    player.play();

    // Update profile if it is not a guest profile
    if (currentProfile != null) {
      currentProfile.updateWords(category);
      currentProfile.incrementNoOfGamesPlayed();
      currentProfile.incrementWinStreak();
      currentProfile.chooseWonOrLost(true);
      // Set the time it took to win the game depending on the current profile's time
      // difficulty setting
      switch (currentProfile.getDifficulties()[2]) {
        case EASY:
          currentProfile.updateTimeWon(60 - counter, category);
          break;
        case MEDIUM:
          currentProfile.updateTimeWon(45 - counter, category);
          break;
        case HARD:
          currentProfile.updateTimeWon(30 - counter, category);
          break;
        case MASTER:
          currentProfile.updateTimeWon(15 - counter, category);
          break;
      }
      checkMaxWords();
      // check if the current game allowed the user to earn any badges
      checkSpeedDemonQualifications();
      checkWinningStreakQualifications();
      checkVeteranQualifications();
      checkChallengerQualifications();
      // update the profile with new stats
      updateProfile();
    }

    // make buttons visible or disabled
    clearButton.setDisable(true);
    penButton.setDisable(true);
    eraserButton.setDisable(true);
    backButton.setVisible(true);

    // show results of the game
    ((ResultsController) SceneManager.getLoader(AppUi.RESULTS).getController())
        .setGameResults(true);
    ((ResultsController) SceneManager.getLoader(AppUi.RESULTS).getController())
        .setNewBadges(newBadges);
    switchToResults();
  }

  /**
   * This method is executed when the user loses the game. A sound effect is played for losing and
   * the stats of the game is stored if the current user profile is not the default guest.
   */
  private void setLose() {
    // stop game and print message
    canvas.setDisable(true);
    statusLabel.setText("You Lost. Unfortunately, I was not able to guess your drawing in time.");

    // play losing sound effect
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
      currentProfile.resetWinStreak();
      currentProfile.chooseWonOrLost(false);
      // Set the time it took to lost the game depending on the current profile's time
      // difficulty setting
      switch (currentProfile.getDifficulties()[2]) {
        case EASY:
          currentProfile.updateTimeLost(60);
          break;
        case MEDIUM:
          currentProfile.updateTimeLost(45);
          break;
        case HARD:
          currentProfile.updateTimeLost(30);
          break;
        case MASTER:
          currentProfile.updateTimeLost(15);
          break;
      }
      checkMaxWords();
      // check if the current game allowed the user to earn any badges
      checkVeteranQualifications();
      // update the profile with new stats
      updateProfile();
    }

    // make buttons visible or disabled
    clearButton.setDisable(true);
    penButton.setDisable(true);
    eraserButton.setDisable(true);
    backButton.setVisible(true);

    // show results of the game
    ((ResultsController) SceneManager.getLoader(AppUi.RESULTS).getController())
        .setGameResults(false);
    ((ResultsController) SceneManager.getLoader(AppUi.RESULTS).getController())
        .setNewBadges(newBadges);
    switchToResults();
  }

  /** Switch from canvas scene to results scene. */
  private void switchToResults() {
    Scene scene = startButton.getParent().getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.RESULTS));
    ((ResultsController) SceneManager.getLoader(AppUi.RESULTS).getController()).subInitialize();
  }

  /**
   * Decrease the game timer by 1 second and play a sound effect when the time reaches 10 and below.
   */
  private void decreaseTime() {
    // decrease counter by 1
    counter--;
    timerLabel.setText(String.valueOf(counter));
    // play time ticking sound effects when time is at 10 seconds and below
    if (counter == 10) {
      try {
        player = new MediaUtil(MediaUtil.fastTickingFile);
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
      player.play();
    }
  }

  /**
   * Sets canvas scene onto the current scene.
   *
   * @param stage the stage of the application
   */
  public void setStage(Stage stage) {
    this.stage = stage;
  }
}
