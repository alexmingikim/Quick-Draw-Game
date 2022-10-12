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
    RESULTS
  }

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();
  private static HashMap<AppUi, FXMLLoader> loaderMap = new HashMap<AppUi, FXMLLoader>();

  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  public static void addLoader(AppUi appUi, FXMLLoader loader) {
    loaderMap.put(appUi, loader);
  }

  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi);
  }

  public static FXMLLoader getLoader(AppUi appUi) {
    return loaderMap.get(appUi);
  }
}
