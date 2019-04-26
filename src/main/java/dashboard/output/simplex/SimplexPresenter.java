package dashboard.output.simplex;

import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import logic.Utilit;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class SimplexPresenter implements Initializable {
  final private int cellWidth = 50;
  final private int cellHeight = 35;

  public AnchorPane pane;

  private int n = 3;
  private int m = 3;
  @Inject
  private int numberStep;

  private GridPane table;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    createSimplexPane();
  }

  //create panel with view of simplex
  private void createSimplexPane() {
    table = new GridPane();

    table.setHgap(n);
    table.setVgap(m);
    ColumnConstraints constraints = new ColumnConstraints();
    constraints.setHalignment(HPos.CENTER);
    table.getColumnConstraints().addAll(constraints, constraints);

    //fill grid pane labels and fields
    for (int i = 0; i < n + 2; i++) {
      for (int j = 0; j < m + 2; j++) {
        if (i == 0 || j == 0) {
          Label label = createLabel(i, j, numberStep);
          table.add(label, j, i);
          continue;
        }

        TextField textField = createTextField();
        table.add(textField, j, i);
      }
    }

    pane.getChildren().add(table);
  }

  private Label createLabel(int i, int j, int numberStep) {
    Label label = new Label("");

    label.setAlignment(Pos.CENTER);
    label.setPrefWidth(cellWidth);
    label.setPrefHeight(cellHeight);

    if (i == 0 && j == 0){
      String name = "X̅" + Utilit.superscript(String.valueOf(numberStep));

      label.setText(name);

      return label;

    }
    if (i == 0) {
      String columnName = "x" + j;
      if (j == m + 1) {
        columnName = "";
      }

      label.setText(columnName);

      return label;
    }
    if (j == 0) {
      String rowName = i == n + 1 ? "" : "x" + i;

      label.setText(rowName);

      return label;
    }

    return label;
  }

  private TextField createTextField(){
    TextField textField = new TextField();

    textField.setPadding(new Insets(5));
    textField.setPrefWidth(cellWidth);
    textField.setPrefHeight(cellHeight);

    return textField;
  }
}
