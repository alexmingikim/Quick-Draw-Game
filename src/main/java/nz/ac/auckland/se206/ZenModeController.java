package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class ZenModeController {

  @FXML private Canvas canvas;

  @FXML private Label lblProfileName;

  @FXML private Label lblCategory;

  @FXML private Label lblStatus;

  @FXML private Label predictionsLabel;

  @FXML private Label predictionsTitleLabel;

  @FXML private Button btnNewWord;

  @FXML private Button btnTextToSpeech;

  @FXML private Button btnBack;

  @FXML private Button btnClear;

  @FXML private Button btnSaveDrawing;

  @FXML private ToggleButton togBtnBlackPaint;

  @FXML private ToggleButton togBtnRedPaint;

  @FXML private ToggleButton togBtnGreenPaint;

  @FXML private ToggleButton togBtnBluePaint;

  @FXML private ToggleButton togBtnErase;

  @FXML private TextFlow predictionsTextFlow;

  private GraphicsContext graphic;

  private DoodlePrediction model;

  private User currentProfile = ProfileViewController.getCurrentUser();

  private boolean blankStatus = true;

  private String category;

  private TextToSpeech textToSpeech = new TextToSpeech();

  private Stage stage;

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
    // initialise graphics and the prediction model
    graphic = canvas.getGraphicsContext2D();
    model = new DoodlePrediction();
  }

  /**
   * This method changes the username display based on the current user profile. The username
   * becomes "Guest" if there is no user profile selected. A new game is also started if the canvas
   * is blank.
   *
   * @throws IOException {@inheritDoc}
   */
  public void subInitialize() throws IOException {
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
      lblCategory.setText(
          "Category: " + category.substring(0, 1).toUpperCase() + category.substring(1));
    }

    enablePen();
    togBtnBlackPaint.setSelected(true);
  }

  /**
   * Select a random word depending on the user profile's difficulty
   *
   * @return the random selected word
   * @throws IOException {@inheritDoc}
   */
  private String selectRandomCategory() throws IOException {
    // execute different methods depending on guest or profile account
    if (currentProfile == null) {
      return selectCategoryGuest();
    } else {
      return selectCategoryProfile();
    }
  }

  /**
   * Select a random category based on the difficulty setting of the guest profile.
   *
   * @return the random selected word
   * @throws IOException {@inheritDoc}
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
   * Select a random category based on the difficulty setting of the current profile.
   *
   * @return the random selected word
   * @throws IOException {@inheritDoc}
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
   * Generates new category for the user to draw.
   *
   * @throws IOException {@inheritDoc}
   */
  @FXML
  private void onNewWord() throws IOException {
    category = selectRandomCategory();
    lblCategory.setText(
        "Category: " + category.substring(0, 1).toUpperCase() + category.substring(1));
    enablePen();
  }

  /** Speaks the category chosen when TTS button is clicked */
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

  /** Allows user to start drawing. Default colour is black */
  private void enablePen() {
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

  /** Enables user to erase what they have drawn. */
  @FXML
  private void onErase() {
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 20.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // set the brush to clear markings on the canvas
          graphic.clearRect(x, y, 20, 20);
        });
  }

  /**
   * Transitions back to game mode scene from Zen mode scene.
   *
   * @param event when "Back" button is clicked
   */
  @FXML
  private void onBack(ActionEvent event) {
    Button btnSceneIsIn = (Button) event.getSource();
    Scene scene = btnSceneIsIn.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.GAME_MODE));
  }

  /** Clears the canvas to blank state. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    blankStatus = true;
  }

  /** Enables user to save the drawing they have drawn. */
  @FXML
  private void onSaveDrawing() {
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

  /** Enables user to draw with black paint */
  @FXML
  private void onSetBlackPaint() {
    enablePen();
    graphic.setStroke(Color.BLACK);

    // status of other toggle buttons should be off
    togBtnRedPaint.setSelected(false);
    togBtnGreenPaint.setSelected(false);
    togBtnBluePaint.setSelected(false);
  }

  /** Enables user to draw with red paint */
  @FXML
  private void onSetRedPaint() {
    enablePen();
    graphic.setStroke(Color.RED);

    // status of other toggle buttons should be off
    togBtnBlackPaint.setSelected(false);
    togBtnGreenPaint.setSelected(false);
    togBtnBluePaint.setSelected(false);
  }

  /** Enables user to draw with green paint */
  @FXML
  private void onSetGreenPaint() {
    // green
    enablePen();
    graphic.setStroke(Color.GREEN);

    // status of other toggle buttons should be off
    togBtnRedPaint.setSelected(false);
    togBtnBlackPaint.setSelected(false);
    togBtnBluePaint.setSelected(false);
  }

  /** Enables user to draw with blue paint */
  @FXML
  private void onSetBluePaint() {
    // blue
    enablePen();
    graphic.setStroke(Color.BLUE);

    // status of other toggle buttons should be off
    togBtnRedPaint.setSelected(false);
    togBtnGreenPaint.setSelected(false);
    togBtnBlackPaint.setSelected(false);
  }
}
