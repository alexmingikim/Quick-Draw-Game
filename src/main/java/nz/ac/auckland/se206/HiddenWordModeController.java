package nz.ac.auckland.se206;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import nz.ac.auckland.se206.ml.DoodlePrediction;

public class HiddenWordModeController {

  @FXML private Canvas canvas;

  @FXML private Label lblProfileName;

  @FXML private Label lblDefinition;

  @FXML private Label lblStatus;

  @FXML private Label lblTimer;

  @FXML private Label predictionsLabel;

  @FXML private Label predictionsTitleLabel;

  @FXML private TextFlow predictionsTextFlow;

  @FXML private Button btnStart;

  @FXML private Button btnDraw;

  @FXML private Button btnErase;

  @FXML private Button btnClear;

  @FXML private Button btnBack;

  private GraphicsContext graphic;

  private DoodlePrediction model;

  public void subInitialize() {
    // TODO Auto-generated method stub

  }
}
