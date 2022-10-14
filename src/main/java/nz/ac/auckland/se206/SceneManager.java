package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SceneManager {

  public enum AppUi {
    MAIN_MENU,
    GAME_MODE,
    PROFILE_VIEW,
    PROFILE_CREATION,
    STATISTICS_VIEW,
    BADGE_VIEW,
    SETTINGS,
    CANVAS,
    RESULTS,
    ZEN_MODE,
    HIDDEN_WORD_MODE
  }

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();
  private static HashMap<AppUi, FXMLLoader> loaderMap = new HashMap<AppUi, FXMLLoader>();

  /**
   * Add the loaded UI root of the scene into the scene hashmap.
   *
   * @param appUi the name of the scene
   * @param uiRoot the root of the scene
   */
  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  /**
   * Add the loader of the scene into the loader hashmap.
   *
   * @param appUi the name of the scene
   * @param loader the loader of the scene
   */
  public static void addLoader(AppUi appUi, FXMLLoader loader) {
    loaderMap.put(appUi, loader);
  }

  /**
   * Retrieve the root of the selected scene.
   *
   * @param appUi the name of the scene
   * @return the root of the scene
   */
  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi);
  }

  /**
   * Retrieve the loader of the selected scene.
   *
   * @param appUi the name of the scene.
   * @return the loader of the scene
   */
  public static FXMLLoader getLoader(AppUi appUi) {
    return loaderMap.get(appUi);
  }
}
