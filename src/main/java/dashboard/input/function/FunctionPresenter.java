package dashboard.input.function;

import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class FunctionPresenter implements Initializable {
  final private int cellWidth = 40;
  final private int cellHeight = 35;


  public HBox box;
  public ScrollPane scrollPane;

  @Inject
  private int n;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    double width = screen.getWidth();
    scrollPane.setPrefViewportWidth(0.365 * width);
    scrollPane.setPrefViewportHeight(cellHeight);

    createFunctionPane();
  }

  private void createFunctionPane(){
    for (int i = 0; i < n + 1; i++){
      String labelName = "+x" + (i + 1);
      if (i == 0)
        labelName = "x" + (i + 1);
      if (i == n)
        labelName = "=";
      Label label = new Label(labelName);

      label.setAlignment(Pos.CENTER);
      label.setPrefHeight(cellHeight);

      TextField textField = new TextField();

      textField.setPadding(new Insets(5));
      textField.setPrefWidth(cellWidth);
      textField.setPrefHeight(cellHeight);

      box.getChildren().add(label);
      box.getChildren().add(textField);
    }
  }
}
