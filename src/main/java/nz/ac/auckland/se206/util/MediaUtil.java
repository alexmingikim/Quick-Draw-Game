package nz.ac.auckland.se206.util;

import java.net.URISyntaxException;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;

public class MediaUtil {
  private Media mediaFile;
  private MediaPlayer player;
  public static String buttonClickFile = "mixkit-typewriter-soft-click-1125.mp3";
  public static String winGameFile = "mixkit-fantasy-game-success-notification-270.mp3";
  public static String loseGameFile = "mixkit-retro-arcade-lose-2027.mp3";

  public MediaUtil(String fileName) throws URISyntaxException {
    this.mediaFile = new Media(App.class.getResource("/sounds/" + fileName).toURI().toString());
    this.player = new MediaPlayer(mediaFile);
  }

  public void play() {
    //    player.play();

    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // global variable textToSpeech initialized at the beginning
            player.play();
            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }
}
