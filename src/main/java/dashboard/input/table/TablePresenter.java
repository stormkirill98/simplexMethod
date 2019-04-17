package dashboard.input.table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import logic.Equation;
import logic.LinearSystem;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class TablePresenter implements Initializable {
  final private int columnWidth = 50;
  final private int rowHeight = 35;

  public AnchorPane pane;

  @Inject
  private ArrayList<Integer> dimension;

  private int n;
  private int m;
  private TableView<Equation> table;

  private LinearSystem system;

  private boolean pressCtrl = false;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    n = dimension.get(0);
    m = dimension.get(1);

    system = new LinearSystem();

    createTable();
    //setOnClickTab(pane);
    //setOnClickToCell();

    getCell(0,0);
  }

  private void createTable() {


    table = new TableView();
    final ObservableList<Equation> data = FXCollections.observableArrayList();

    table.setEditable(true);
    table.setPrefWidth(columnWidth * (n + 1) + 2);
    table.setPrefHeight(rowHeight * (m + 1) - 2);

    for (int i = 0; i < n + 1; i++) {
      String name = i != n ? "x" + (i + 1)
              : "";
      TableColumn column = new TableColumn(name);
      column.setCellValueFactory(new PropertyValueFactory<Equation,Double>());
      column.setPrefWidth(columnWidth);
      column.setSortable(false);

      table.getColumns().add(column);
    }

    for (int i = 0; i < m; i++) {
      Equation equation = new Equation();
      equation.add(1.0);
      equation.add(1.0);
      equation.add(1.0);
      data.add(equation);
    }
    table.setItems(data);

    pane.getChildren().add(table);
  }

  private Node getCell(int i, int j) {
    ObservableList list = table.getItems();
    Object o = list.get(i);
    return null;
  }

  private int[] getIndexActiveCell() {
    for (int i = 0; i < n + 1; i++) {
      for (int j = 0; j < m; j++) {
        Node cell = getCell(i, j);
        if (cell.isFocused()) {
          return new int[]{i, j};
        }
      }
    }

    return new int[]{0, 0};
  }

  private void setOnClickToCell() {
    for (int i = 0; i < n + 1; i++) {
      for (int j = 0; j < m; j++) {
        Node cell = getCell(i, j);
        if (cell != null)
          cell.setId("colorCell");
      }
    }
  }

  private void setOnClickTab(Node node) {
    node.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.LEFT) {
          System.out.println("click w");

          int[] indexes = getIndexActiveCell();
          System.out.println(indexes[0] + " " + indexes[1]);
        }
        if (event.getCode() == KeyCode.CONTROL) {
          System.out.println("click ctrl");

          pressCtrl = true;
        }
      }
    });

    node.setOnKeyReleased(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
          pressCtrl = false;
        }
      }
    });


  }
}
