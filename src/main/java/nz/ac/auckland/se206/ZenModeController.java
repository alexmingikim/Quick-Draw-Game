package nz.ac.auckland.se206;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.TextFlow;

public class ZenModeController {

  @FXML private Canvas canvas;

  @FXML private Label lblProfileName;

  @FXML private Label lblCategory;

  @FXML private Label lblStatus;

  @FXML private Label predictionsLabel;

  @FXML private Label predictionsTitleLabel;

  @FXML private Button btnNewWord;

  @FXML private Button btnTextToSpeech;

  @FXML private Button btnBack;

  @FXML private Button btnClear;

  @FXML private Button btnSaveDrawing;

  @FXML private ToggleButton togBtnBlackPaint;

  @FXML private ToggleButton togBtnRedPaint;

  @FXML private ToggleButton togBtnGreenPaint;

  @FXML private ToggleButton togBtnBluePaint;

  @FXML private ToggleButton togBtnErase;

  @FXML private TextFlow predictionsTextFlow;

  public void startNewGame() {
    // TODO Auto-generated method stub

  }
}
