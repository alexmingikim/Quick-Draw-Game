package nz.ac.auckland.se206;

import static nz.ac.auckland.se206.ml.DoodlePrediction.printPredictions;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;

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

  @FXML private Canvas canvas;

  @FXML private Label categoryLabel;

  @FXML private Label predictionsLabel;

  @FXML private Label statusLabel;

  @FXML private Label timer;

  @FXML private Button penButton;

  @FXML private Button eraserButton;

  @FXML private Button startButton;

  @FXML private Button saveDrawingButton;

  @FXML private Button startNewGameButton;

  private int counter = 60;

  private String category;

  private GraphicsContext graphic;

  private DoodlePrediction model;

  private Timeline timeline;

  private Stage stage;

  private TextToSpeech textToSpeech = new TextToSpeech();

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
  @FXML
  public void initialize() throws ModelException, IOException {
    // create a new menu instance and set a random category
    MainMenuController menu = new MainMenuController();
    category = menu.selectRandomCategory();
    categoryLabel.setText("Category: " + category);

    // initialise graphics and the prediction model
    graphic = canvas.getGraphicsContext2D();
    model = new DoodlePrediction();

    // set buttons to not visible
    startNewGameButton.setVisible(false);
    saveDrawingButton.setVisible(false);
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
  }

  /**
   * This method executes when the user clicks the "Predict" button. It gets the current drawing,
   * queries the DL model and prints on the console the top 5 predictions of the DL model and the
   * elapsed time of the prediction in milliseconds.
   *
   * @throws TranslateException If there is an error in reading the input/output of the DL model.
   */
  private void onPredict() throws TranslateException {
    System.out.println("==== PREDICTION  ====");
    System.out.println("Top 5 predictions");

    final long start = System.currentTimeMillis();

    printPredictions(model.getPredictions(getCurrentSnapshot(), 5));

    System.out.println("prediction performed in " + (System.currentTimeMillis() - start) + " ms");
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  private BufferedImage getCurrentSnapshot() {
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
   * Save the current snapshot on a bitmap file.
   *
   * @return The file of the saved image.
   * @throws IOException If the image cannot be saved.
   */
  private File saveCurrentSnapshotOnFile() throws IOException {
    // You can change the location as you see fit.
    final File tmpFolder = new File("tmp");

    if (!tmpFolder.exists()) {
      tmpFolder.mkdir();
    }

    // We save the image to a file in the tmp folder.
    final File imageToClassify =
        new File(tmpFolder.getName() + "/snapshot" + System.currentTimeMillis() + ".bmp");

    // Save the image to a file.
    ImageIO.write(getCurrentSnapshot(), "bmp", imageToClassify);

    return imageToClassify;
  }

  @FXML
  private void onStart() {
    // change message
    statusLabel.setText("---------- Game in Progress ----------");

    // create a timeline instance for timer countdown and to update predictions
    // every second
    timeline =
        new Timeline(
            (new KeyFrame(Duration.seconds(1), e -> decreaseTime())),
            (new KeyFrame(
                Duration.seconds(1),
                e -> {
                  try {
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
    timeline.setCycleCount(60);
    timeline.play();

    // enable user to draw
    onPen();

    canvas.setDisable(false);
    // make the start button not visible
    startButton.setVisible(false);
  }

  @FXML
  private void onStartNewGame() throws ModelException, IOException {
    // clear the predictions board and change message when new game is started
    predictionsLabel.setText("==== PREDICTIONS ====");
    statusLabel.setText("---------- Press Start to Begin----------");

    // reset the timer and clear the canvas
    counter = 60;
    timer.setText("60");
    onClear();

    // initialise to get new category and make the start button visible
    initialize();
    startButton.setVisible(true);
  }

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

  @FXML
  private void onPen() {
    // save coordinates when mouse is pressed on the canvas
    canvas.setOnMousePressed(
        e -> {
          currentX = e.getX();
          currentY = e.getY();
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

  @FXML
  private void onErase() {
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 5.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // set the brush to clear markings on the canvas
          graphic.clearRect(x, y, 10, 10);
        });
  }

  // helper methods
  private void updatePrediction() throws TranslateException {
    // get all predictions
    List<Classifications.Classification> predictions =
        model.getPredictions(getCurrentSnapshot(), 10);

    // set the labels
    predictionsLabel.setText("==== PREDICTIONS ====\n");
    predictionsLabel.setText(predictionsLabel.getText() + "Top 10 Predictions\n");

    final StringBuilder sb = new StringBuilder();
    int i = 1;

    // for all predictions, print its ranking and if a prediction is in top 3 and
    // matches with the category, call the player win method
    for (final Classifications.Classification prediction : predictions) {
      if (prediction.getClassName().equals(category.replace(" ", "_"))
          && (i == 1 || i == 2 || i == 3)) {
        setWin();
      }

      sb.append(i)
          .append(" : ")
          .append(prediction.getClassName())
          .append(" : ")
          .append(String.format("%.2f%%", 100 * prediction.getProbability()))
          .append(System.lineSeparator());
      i++;
    }

    // using string builder, add all the predictions
    predictionsLabel.setText(predictionsLabel.getText() + sb.toString());
  }

  private void setWin() {
    // stop game and print message
    canvas.setDisable(true);
    timeline.stop();
    statusLabel.setText("Congratulations! You Won! The AI guessed your drawing in time!");

    // text to speech for win message
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            textToSpeech.speak("Congratulations! You Won! The AI guessed your drawing in time!");

            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();

    // make buttons visible
    startNewGameButton.setVisible(true);
    saveDrawingButton.setVisible(true);
  }

  private void setLose() {
    // stop game and print message
    canvas.setDisable(true);
    statusLabel.setText(
        "You Lost. Unfortunately the AI was not able to guess your drawing in time");

    // text to speech for lose message
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            textToSpeech.speak(
                "You Lost. Unfortunately the AI was not able to guess your drawing in time");

            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();

    // make buttons visible
    startNewGameButton.setVisible(true);
    saveDrawingButton.setVisible(true);
  }

  private void decreaseTime() {
    counter--;
    timer.setText(String.valueOf(counter));
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }
}
