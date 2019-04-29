package dashboard.input.table;

import events.MyEventBus;
import events.domain.TableLimits;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static logic.Utilit.*;

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

  private GridPane tablePane;

  private double[][] table;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    double width = screen.getWidth();
    double height = screen.getHeight() - 35;
    scrollPane.setPrefViewportWidth(0.365 * width);
    scrollPane.setPrefViewportHeight(0.778 * height);

    n = dimension.get(0);
    m = dimension.get(1);

    table = new double[n][m + 1];

    createTablePane();
    isFilled(tablePane);//для подсвечивания всех незаполненных полей красной подсветкой

    fillTable();
  }

  //TODO: при пересоздании таблицы, можно попробовать сохранить старые значения
  private void createTablePane() {
    tablePane = new GridPane();

    tablePane.setHgap(3);
    tablePane.setVgap(3);
    ColumnConstraints constraints = new ColumnConstraints();
    constraints.setHalignment(HPos.CENTER);
    tablePane.getColumnConstraints().addAll(constraints, constraints);

    for (int i = 0; i < n + 1; i++) {
      for (int j = 0; j < m + 1; j++) {
        if (i == 0) {
          String columnName = j == m ? "" : "x" + subscript(String.valueOf(j + 1));

          Label label = new Label(columnName);

          label.setAlignment(Pos.CENTER);
          label.setPrefWidth(cellWidth);
          label.setPrefHeight(cellHeight);

          tablePane.add(label, j, i);
          continue;
        }

        TextField textField = new TextField();

        int finalJ = j;
        int finalI = i - 1;
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
          if (!validateDouble(textField, oldValue, newValue)) {
            return;
          }

          Double value = strToDouble(newValue);
          table[finalI][finalJ] = value;

          if (isFilled(tablePane)) {
            MyEventBus.post(new TableLimits(table));
          }
        });

        textField.setPrefWidth(cellWidth);
        textField.setPrefHeight(cellHeight);


        tablePane.add(textField, j, i);
      }
    }

    pane.getChildren().add(tablePane);
  }

  public void fillTable() {
    ObservableList<Node> cells = tablePane.getChildren();
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
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
  }

  public TextField getNodeByRowColumnIndex(int row, int column) {
    ObservableList<Node> childrens = tablePane.getChildren();

    for (Node node : childrens) {
      if (GridPane.getRowIndex(node) == row
              && GridPane.getColumnIndex(node) == column) {
        return (TextField) node;
      }
    }

    return null;
  }
}
