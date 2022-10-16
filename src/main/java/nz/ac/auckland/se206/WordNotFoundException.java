package nz.ac.auckland.se206;

public class WordNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;
  private String word;

  WordNotFoundException(String word, String message) {
    super(message);
    this.word = word;
  }

  public String getWord() {
    return word;
  }
}
