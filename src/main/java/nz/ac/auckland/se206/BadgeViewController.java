package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class BadgeViewController {

  static final int TOTAL_BADGES = 10;

  @FXML private Button btnGoBack;

  @FXML private Button viewStatisticsButton;

  @FXML private Label usernameLabel;

  @FXML private Label oneBadgeLabel;

  @FXML private Label twoBadgeLabel;

  @FXML private Label threeBadgeLabel;

  @FXML private Label fourBadgeLabel;

  @FXML private Label fiveBadgeLabel;

  @FXML private Label sixBadgeLabel;

  @FXML private Label sevenBadgeLabel;

  @FXML private Label eightBadgeLabel;

  @FXML private Label nineBadgeLabel;

  @FXML private Label tenBadgeLabel;

  private Label[] displayBadges;

  private User currentProfile = ProfileViewController.getCurrentUser();

  private List<Badge> allBadges;

  public void initialize() {
    // set up array to store display labels and retrieve current profile
    createGroup();
    subInitialize();
    // retrieve all badges
    getBadges();
    setUpBadges();
  }

  public void subInitialize() {
    currentProfile = ProfileViewController.getCurrentUser();
    if (currentProfile != null) {
      // set up display for username and update badge completion
      usernameLabel.setText(currentProfile.getName());
      updateBadges();
    }
  }

  private void createGroup() {
    // store all display badge labels into a label array for easier iterations to
    // change all labels when needed.
    // Current total of unique badges is 10, more badges can be added for new modes
    displayBadges = new Label[TOTAL_BADGES];
    displayBadges[0] = oneBadgeLabel;
    displayBadges[1] = twoBadgeLabel;
    displayBadges[2] = threeBadgeLabel;
    displayBadges[3] = fourBadgeLabel;
    displayBadges[4] = fiveBadgeLabel;
    displayBadges[5] = sixBadgeLabel;
    displayBadges[6] = sevenBadgeLabel;
    displayBadges[7] = eightBadgeLabel;
    displayBadges[8] = nineBadgeLabel;
    displayBadges[9] = tenBadgeLabel;
  }

  private void getBadges() {
    // initializing utilities to read and store the profiles
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    allBadges = new ArrayList<Badge>();

    try {
      // read all badges from JSON file and store into array list
      FileReader fr = new FileReader("src/main/resources/badges.json");
      allBadges = gson.fromJson(fr, new TypeToken<List<Badge>>() {}.getType());
      fr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setUpBadges() {
    // string concatenation to build the message to display in label
    StringBuilder sb = new StringBuilder();
    Font font = Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14);

    for (int i = 0; i < TOTAL_BADGES; i++) {
      sb.setLength(0);
      // display the name and the description of the badge in the label
      sb.append(allBadges.get(i).getName())
          .append(System.lineSeparator())
          .append(allBadges.get(i).getDescription());
      displayBadges[i].setText(sb.toString());
      displayBadges[i].setFont(font);
    }
  }

  private void updateBadges() {}

  @FXML
  private void onGoBack(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.PROFILE_VIEW));
  }

  @FXML
  private void onViewStatistics(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    Scene scene = btnClicked.getScene();
    scene.setRoot(SceneManager.getUiRoot(SceneManager.AppUi.STATISTICS_VIEW));
  }
}
