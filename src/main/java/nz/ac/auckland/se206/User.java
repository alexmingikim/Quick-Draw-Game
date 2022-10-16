package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.SettingsController.Difficulty;

public class User {

  private String id;
  private String name;
  private double opacity;
  private String noOfGamesPlayed;
  private String noOfGamesWon;
  private String noOfGamesLost;
  private int winStreak;
  private String averageDrawingTime;
  private String totalGameTime;
  private String fastestWonGameTime;
  private String fastestWonGame;
  private String wordsEncountered;
  private Difficulty accuracy;
  private Difficulty words;
  private Difficulty time;
  private Difficulty confidence;
  private List<Integer> badges = new ArrayList<Integer>();

  /**
   * Creates a new user based on the input attributes
   *
   * @param id user id
   * @param name user name
   * @param opacity opacity of user profile button
   * @param noOfGamesPlayed number of games user has played
   * @param noOfGamesWon number of games user has won
   * @param noOfGamesLost number of games user has lost
   * @param averageDrawingTime average time user has spent on a game
   * @param totalGameTime total time user has spent drawing
   * @param fastestWonGameTime fastest time of a won game
   * @param fastestWonGame string of fastest won game
   * @param wordsEncountered list of words which user has encountered
   */
  public User(
      String id,
      String name,
      double opacity,
      String noOfGamesPlayed,
      String noOfGamesWon,
      String noOfGamesLost,
      String averageDrawingTime,
      String totalGameTime,
      String fastestWonGameTime,
      String fastestWonGame,
      String wordsEncountered) {
    super();
    this.id = id;
    this.name = name;
    this.opacity = opacity;
    this.noOfGamesPlayed = noOfGamesPlayed;
    this.noOfGamesWon = noOfGamesWon;
    this.noOfGamesLost = noOfGamesLost;
    this.averageDrawingTime = averageDrawingTime;
    this.totalGameTime = totalGameTime;
    this.fastestWonGameTime = fastestWonGameTime;
    this.fastestWonGame = fastestWonGame;
    this.wordsEncountered = wordsEncountered;
    this.winStreak = 0;
    this.accuracy = Difficulty.EASY;
    this.words = Difficulty.EASY;
    this.time = Difficulty.EASY;
    this.confidence = Difficulty.EASY;
  }

  /**
   * Get the name of the current user profile.
   *
   * @return name of the user
   */
  public String getName() {
    return name;
  }

  /**
   * Get the number of games played of the current user profile.
   *
   * @return number of games played
   */
  public String getNoOfGamesPlayed() {
    return noOfGamesPlayed;
  }

  /**
   * Get the number of games won of the current user profile.
   *
   * @return number of games won
   */
  public String getNoOfGamesWon() {
    return noOfGamesWon;
  }

  /**
   * Get the number of games lost of the current user profile.
   *
   * @return number of games lost
   */
  public String getNoOfGamesLost() {
    return noOfGamesLost;
  }

  /**
   * Get the win streak of the current user profile.
   *
   * @return win streak
   */
  public int getWinStreak() {
    return winStreak;
  }

  /**
   * Get the average drawing time of the current user profile.
   *
   * @return average drawing time
   */
  public String getAverageDrawingTime() {
    return averageDrawingTime;
  }

  /**
   * Get the total game time of the current user profile.
   *
   * @return total game time
   */
  public String getTotalGameTime() {
    return totalGameTime;
  }

  /**
   * Get the fastest won game time of the current user profile.
   *
   * @return fastest won game time
   */
  public String getFastestWonGameTime() {
    return fastestWonGameTime;
  }

  /**
   * Get the fastest won game's word on the current user profile.
   *
   * @return fastest won game's word
   */
  public String getFastestWonGame() {
    return fastestWonGame;
  }

  /**
   * Get all the words encountered on the current user profile.
   *
   * @return words encountered
   */
  public String getWordsEncountered() {
    return wordsEncountered;
  }

  /**
   * Get the opacity of the current user profile.
   *
   * @return opacity
   */
  public double getOpacity() {
    return opacity;
  }

  /**
   * Get the ID of the current user profile.
   *
   * @return ID
   */
  public String getId() {
    return id;
  }

  /**
   * Get all the words encountered on the current user profile.
   *
   * @return words encountered
   */
  public String getWords() {
    return wordsEncountered;
  }

  /**
   * Get the difficulty settings of the current user profile.
   *
   * @return difficulty settings
   */
  public Difficulty[] getDifficulties() {
    return new Difficulty[] {this.accuracy, this.words, this.time, this.confidence};
  }

  /**
   * Get all the badges on the current user profile.
   *
   * @return badges
   */
  public List<Integer> getBadges() {
    return badges;
  }

  /**
   * Set the accuracy difficulty of the current user profile.
   *
   * @param accuracy user chosen accuracy difficulty
   */
  public void setAccuracy(Difficulty accuracy) {
    this.accuracy = accuracy;
  }

  /**
   * Set the words difficulty of the current user profile.
   *
   * @param words user chosen words difficulty
   */
  public void setWords(Difficulty words) {
    this.words = words;
  }

  /**
   * Set the time difficulty of the current user profile.
   *
   * @param time user chosen time difficulty
   */
  public void setTime(Difficulty time) {
    this.time = time;
  }

  /**
   * Set the confidence difficulty of the current user profile.
   *
   * @param confidence user chosen confidence difficulty
   */
  public void setConfidence(Difficulty confidence) {
    this.confidence = confidence;
  }

  /**
   * Update the words encountered of the current user profile.
   *
   * @param word new encountered word
   */
  public void updateWords(String word) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.wordsEncountered).append(word).append(",");
    this.wordsEncountered = sb.toString();
  }

  /**
   * Update the time related stats of the current user profile when game was won.
   *
   * @param time the time taken for the game to be won
   * @param word the word from the won game
   */
  public void updateTimeWon(int time, String word) {
    // first update total time then use that stat to calculate the rest of the time
    // related statistics
    this.totalGameTime = Integer.toString((Integer.parseInt(this.totalGameTime) + time));
    this.averageDrawingTime =
        Integer.toString(
            Integer.parseInt(this.totalGameTime) / Integer.parseInt(this.noOfGamesPlayed));

    if (this.fastestWonGameTime.equals("-") || time < Integer.parseInt(this.fastestWonGameTime)) {
      this.fastestWonGameTime = Integer.toString(time);
      this.fastestWonGame = word;
    }
  }

  /** Update the time related stats of the current user profile when gamee was lost. */
  public void updateTimeLost(int time) {
    // first update total time then use that stat to calculate the rest of the time
    // related statistics
    this.totalGameTime = Integer.toString((Integer.parseInt(this.totalGameTime) + time));
    this.averageDrawingTime =
        Integer.toString(
            Integer.parseInt(this.totalGameTime) / Integer.parseInt(this.noOfGamesPlayed));
  }

  /** Update the badges achieved on the current user profile. */
  public void updateBadges(int badgeIndex) {
    this.badges.add(badgeIndex);
  }

  /** Increment the number of games played on the current user profile. */
  public void incrementNoOfGamesPlayed() {
    this.noOfGamesPlayed = Integer.toString((Integer.parseInt(this.noOfGamesPlayed) + 1));
  }

  /** Increment the win streak of the current user profile. */
  public void incrementWinStreak() {
    this.winStreak++;
  }

  /**
   * Increment either game won or lost depending on results.
   *
   * @param status the result of the game
   */
  public void chooseWonOrLost(boolean status) {
    // Increment games won or games lost depending on boolean parameter
    if (status) {
      this.noOfGamesWon = Integer.toString((Integer.parseInt(this.noOfGamesWon) + 1));
    } else {
      this.noOfGamesLost = Integer.toString((Integer.parseInt(this.noOfGamesLost) + 1));
    }
  }

  /** Reset all words encountered for this current user profile. */
  public void resetWords() {
    this.wordsEncountered = "";
  }

  /** Reset the win streak if the user loses for the current user profile. */
  public void resetWinStreak() {
    this.winStreak = 0;
  }

  /** Set the opacity of the current user profile. */
  public void setOpacity(double opacity) {
    this.opacity = opacity;
  }
}
