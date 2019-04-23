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

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class SimplexPresenter implements Initializable {
  final private int cellWidth = 50;
  final private int cellHeight = 35;

  public AnchorPane pane;

  private int n = 3;
  private int m = 3;

  private GridPane table;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    createSimplexPane();
  }

  private void createSimplexPane() {
    table = new GridPane();

    table.setHgap(n);
    table.setVgap(m);
    ColumnConstraints constraints = new ColumnConstraints();
    constraints.setHalignment(HPos.CENTER);
    table.getColumnConstraints().addAll(constraints, constraints);

    for (int i = 0; i < n + 2; i++) {
      for (int j = 0; j < m + 2; j++) {
        if (i == 0) {
          String columnName = j == 0 ? "" : "x" + j;
          if (j == m + 1){
            columnName = "";
          }

          Label label = new Label(columnName);

          label.setAlignment(Pos.CENTER);
          label.setPrefWidth(cellWidth);
          label.setPrefHeight(cellHeight);

          table.add(label, j, i);
          continue;
        }
        if (j == 0){
          String rowName = i == n + 1 ? "" : "x" + i;

          Label label = new Label(rowName);

          label.setAlignment(Pos.CENTER);
          label.setPrefWidth(cellWidth);
          label.setPrefHeight(cellHeight);

          table.add(label, j, i);
          continue;
        }

        TextField textField = new TextField();

        textField.setPadding(new Insets(5));
        textField.setPrefWidth(cellWidth);
        textField.setPrefHeight(cellHeight);

        table.add(textField, j, i);
      }
    }

    pane.getChildren().add(table);
  }
}
