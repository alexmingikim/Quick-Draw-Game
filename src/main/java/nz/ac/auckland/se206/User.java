package nz.ac.auckland.se206;

public class User {

  private String id;
  private String name;
  private double opacity;
  private String noOfGamesPlayed;
  private String noOfGamesWon;
  private String noOfGamesLost;
  private String averageDrawingTime;
  private String totalGameTime;
  private String fastestWonGameTime;
  private String wordsEncountered;

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
    this.wordsEncountered = wordsEncountered;
  }

  public double getOpacity() {
    return opacity;
  }

  public String getId() {
    return id;
  }

  public String getWords() {
    return wordsEncountered;
  }

  public String getName() {
    return name;
  }

  public void updateWords(String word) {
    this.wordsEncountered = this.wordsEncountered + "," + word;
  }

  public void updateTotalTime(int time) {
    this.totalGameTime = Integer.toString((Integer.parseInt(this.totalGameTime) + time));
    this.averageDrawingTime =
        Integer.toString(
            Integer.parseInt(this.totalGameTime) / Integer.parseInt(this.noOfGamesPlayed));

    if (this.fastestWonGameTime.equals("-") || time < Integer.parseInt(this.fastestWonGameTime)) {
      this.fastestWonGameTime = Integer.toString(time);
    }
  }

  public void incrementNoOfGamesPlayed() {
    this.noOfGamesPlayed = Integer.toString((Integer.parseInt(this.noOfGamesPlayed) + 1));
  }

  public void gameWonOrLost(boolean status) {
    if (status) {
      this.noOfGamesWon = Integer.toString((Integer.parseInt(this.noOfGamesWon) + 1));
    } else {
      this.noOfGamesLost = Integer.toString((Integer.parseInt(this.noOfGamesLost) + 1));
    }
  }

  public void resetWords() {
    this.wordsEncountered = "";
  }
}
