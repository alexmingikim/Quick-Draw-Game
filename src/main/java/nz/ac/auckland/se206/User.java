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
  private String fastestWonGame;
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

  public String getFastestWonGame() {
    return fastestWonGame;
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

  public String getWords() {
    return wordsEncountered;
  }

  public void updateWords(String word) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.wordsEncountered).append(word).append(",");
    this.wordsEncountered = sb.toString();
  }

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

  public void updateTimeLost() {
    // first update total time then use that stat to calculate the rest of the time
    // related statistics
    this.totalGameTime = Integer.toString((Integer.parseInt(this.totalGameTime) + 60));
    this.averageDrawingTime =
        Integer.toString(
            Integer.parseInt(this.totalGameTime) / Integer.parseInt(this.noOfGamesPlayed));
  }

  public void incrementNoOfGamesPlayed() {
    this.noOfGamesPlayed = Integer.toString((Integer.parseInt(this.noOfGamesPlayed) + 1));
  }

  public void chooseWonOrLost(boolean status) {
    // Increment games won or games lost depending on boolean parameter
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
