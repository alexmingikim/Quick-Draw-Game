package nz.ac.auckland.se206.util;

import java.net.URISyntaxException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;

public class MediaUtil {
  private Media mediaFile;
  private MediaPlayer player;
  public static String buttonClickFile = "mixkit-typewriter-soft-click-1125.mp3";
  public static String winGameFile = "mixkit-fantasy-game-success-notification-270.mp3";
  public static String loseGameFile = "mixkit-retro-arcade-lose-2027.mp3";
  public static String fastTickingFile = "mixkit-fast-wall-clock-ticking-1063.mp3";

  public MediaUtil(String fileName) throws URISyntaxException {
    this.mediaFile = new Media(App.class.getResource("/sounds/" + fileName).toURI().toString());
    this.player = new MediaPlayer(mediaFile);
  }

  /** Plays the mp3 file associated */
  public void play() {
    player.play();
  }

  /** Stops playing the mp3 file */
  public void stop() {
    player.stop();
  }
}
