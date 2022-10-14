package nz.ac.auckland.se206;

public class Badge {

  private int id;
  private String name;
  private String description;

  /**
   * gets the indexed ID of the badge
   *
   * @return the ID of the badge
   */
  public int getId() {
    return this.id;
  }

  /**
   * gets the name of the badge
   *
   * @return the name of the badge
   */
  public String getName() {
    return this.name;
  }

  /**
   * gets the description of the badge
   *
   * @return the description of the badge
   */
  public String getDescription() {
    return this.description;
  }
}
