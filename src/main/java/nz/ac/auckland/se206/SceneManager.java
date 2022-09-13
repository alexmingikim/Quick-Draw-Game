package nz.ac.auckland.se206;

import java.util.HashMap;

import javafx.scene.Parent;

public class SceneManager {

	public enum AppUi {
		MAIN_MENU, CANVAS, PROFILE_CREATION
	}

	private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();

	public static void addUi(AppUi appUi, Parent uiRoot) {
		sceneMap.put(appUi, uiRoot);
	}

	public static Parent getUiRoot(AppUi appUi) {
		return sceneMap.get(appUi);
	}
}
