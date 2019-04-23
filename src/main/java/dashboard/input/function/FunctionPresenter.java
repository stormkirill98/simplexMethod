package dashboard.input.function;

import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class FunctionPresenter implements Initializable {
  final private int cellWidth = 40;
  final private int cellHeight = 35;


  public VBox box;

  @Inject
  private int n;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    createFunctionPane();
  }

  private void createFunctionPane(){
    HBox hbox = new HBox();
    box.getChildren().add(hbox);
    for (int i = 0; i < n + 1; i++){
      if (i == 9){
        hbox = new HBox();
        hbox.setPadding(new Insets(10,0,0,0));

        box.getChildren().add(hbox);
      }
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

      hbox.getChildren().add(label);
      hbox.getChildren().add(textField);
    }
  }
}
