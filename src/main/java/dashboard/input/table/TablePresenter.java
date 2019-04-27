package dashboard.input.table;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class TablePresenter implements Initializable {
  final private int cellWidth = 50;
  final private int cellHeight = 35;

  public AnchorPane pane;
  public ScrollPane scrollPane;

  @Inject
  private ArrayList<Integer> dimension;

  private int n;
  private int m;

  private GridPane table;



  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    double width = screen.getWidth();
    double height = screen.getHeight() - 35;
    scrollPane.setPrefViewportWidth(0.365 * width);
    scrollPane.setPrefViewportHeight(0.778 * height);


    n = dimension.get(0);
    m = dimension.get(1);


    createTablePane();
    fillTable();
  }

  private void createTablePane() {
    table = new GridPane();

    table.setHgap(3);
    table.setVgap(3);
    ColumnConstraints constraints = new ColumnConstraints();
    constraints.setHalignment(HPos.CENTER);
    table.getColumnConstraints().addAll(constraints, constraints);

    for (int i = 0; i < n + 1; i++) {
      for (int j = 0; j < m + 1; j++) {
        if (i == 0) {
          String columnName = j == m ? "" : "x" + (j + 1);

          Label label = new Label(columnName);

          label.setAlignment(Pos.CENTER);
          label.setPrefWidth(cellWidth);
          label.setPrefHeight(cellHeight);

          table.add(label, j, i);
          continue;
        }

        TextField textField = new TextField();

        textField.setPrefWidth(cellWidth);
        textField.setPrefHeight(cellHeight);

        table.add(textField, j, i);
      }
    }

    pane.getChildren().add(table);
  }

  public void fillTable(){
    ObservableList<Node> cells = table.getChildren();
    if (cells == null) {
      return;
    }

    try {
      for (int i = 1; i < n + 1; i++) {
        for (int j = 0; j < m + 1; j++) {
          TextField cell;
          try {
            cell = (TextField) cells.get(i * (m + 1) + j);
          } catch (ClassCastException e) {
            System.out.println("TablePresenter fillTable " + e);
            continue;
          }

          cell.setText(String.format("%.2f", 2.0));
        }
      }
    } catch (IndexOutOfBoundsException e){
      e.printStackTrace();
    }
  }

  public Node getNodeByRowColumnIndex (int row, int column) {
    ObservableList<Node> childrens = table.getChildren();

    for (Node node : childrens) {
      if(GridPane.getRowIndex(node) == row
              && GridPane.getColumnIndex(node) == column) {
        return node;
      }
    }

    return null;
  }
}
