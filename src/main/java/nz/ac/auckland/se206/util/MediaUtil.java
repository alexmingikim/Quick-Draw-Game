package nz.ac.auckland.se206.util;

import java.net.URISyntaxException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;

public class MediaUtil {
  private Media mediaFile;
  private MediaPlayer player;
  // create strings for names of the sound files
  public static String buttonClickFile = "mixkit-typewriter-soft-click-1125.mp3";
  public static String winGameFile = "mixkit-fantasy-game-success-notification-270.mp3";
  public static String loseGameFile = "mixkit-retro-arcade-lose-2027.mp3";
  public static String fastTickingFile = "mixkit-fast-wall-clock-ticking-1063.mp3";

  /**
   * Constructor for the mediautil class
   *
   * @param fileName string input referencing the file name of the sound to be played
   * @throws URISyntaxException throws error if string cannot be parsed as a URI reference
   */
  public MediaUtil(String fileName) throws URISyntaxException {
    this.mediaFile = new Media(App.class.getResource("/sounds/" + fileName).toURI().toString());
    this.player = new MediaPlayer(mediaFile);
  }

  /** Plays the associated mp3 file */
  public void play() {
    player.play();
  }

  /** Stops playing the mp3 file */
  public void stop() {
    player.stop();
  }
}
