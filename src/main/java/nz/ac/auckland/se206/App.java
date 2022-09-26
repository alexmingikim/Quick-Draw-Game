package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {
  // initialise static field
  static FXMLLoader fxmlLoader;

  public static void main(final String[] args) {
    launch();
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
    return fxmlLoader.load();
  }

  // initialise text to speech object
  private TextToSpeech textToSpeech = new TextToSpeech();

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    // load and add all scenes to SceneManager
    SceneManager.addUi(AppUi.MAIN_MENU, loadFxml("main_menu"));
    SceneManager.addLoader(AppUi.MAIN_MENU, fxmlLoader);
    SceneManager.addUi(AppUi.PROFILE_VIEW, loadFxml("profile_view"));
    SceneManager.addLoader(AppUi.PROFILE_VIEW, fxmlLoader);
    SceneManager.addUi(AppUi.PROFILE_CREATION, loadFxml("profile_creation"));
    SceneManager.addLoader(AppUi.PROFILE_CREATION, fxmlLoader);
    SceneManager.addUi(AppUi.STATISTICS_VIEW, loadFxml("statistics_view"));
    SceneManager.addLoader(AppUi.STATISTICS_VIEW, fxmlLoader);

    // load the canvas fxml and adds to appui, also set the stage in
    // CanvasController as the primary stage
    SceneManager.addUi(AppUi.CANVAS, loadFxml("canvas"));
    SceneManager.addLoader(AppUi.CANVAS, fxmlLoader);
    ((CanvasController) fxmlLoader.getController()).setStage(stage);

    // listen for window close and terminate text to speech on close
    stage.setOnCloseRequest(
        e -> {
          Platform.exit();
          textToSpeech.terminate();
        });

    // create a new scene and display to user
    final Scene scene = new Scene(SceneManager.getUiRoot(AppUi.MAIN_MENU), 800, 600);
    stage.setScene(scene);
    stage.show();
  }
}
