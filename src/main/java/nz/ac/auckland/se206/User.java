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

  public String getName() {
    return name;
  }

  public String getNoOfGamesPlayed() {
    return noOfGamesPlayed;
  }

  public String getNoOfGamesWon() {
    return noOfGamesWon;
  }

  public String getNoOfGamesLost() {
    return noOfGamesLost;
  }

  public String getAverageDrawingTime() {
    return averageDrawingTime;
  }

  public String getTotalGameTime() {
    return totalGameTime;
  }

  public String getFastestWonGameTime() {
    return fastestWonGameTime;
  }

  public String getWordsEncountered() {
    return wordsEncountered;
  }

  public double getOpacity() {
    return opacity;
  }

  public String getId() {
    return id;
  }
}
