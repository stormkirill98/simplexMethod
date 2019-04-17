package dashboard.input.table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.w3c.dom.ranges.Range;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static logic.Utilit.isNatural;

@SuppressWarnings("Duplicates")
public class TablePresenter implements Initializable {
  final private int columnWidth = 50;
  final private int rowHeight = 35;

  public AnchorPane pane;

  @Inject
  private ArrayList<Integer> dimension;

  private TableView table;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    createTable();
  }

  private void createTable() {
    int n = dimension.get(0);
    int m = dimension.get(1);

    table = new TableView();
    final ObservableList<String> data = FXCollections.observableArrayList();

    table.setEditable(true);
    table.setPrefWidth(columnWidth * (n + 1) + 2);
    table.setPrefHeight(rowHeight * (m + 1) - 2);

    for (int i = 0; i < n + 1; i++){
      String name = i != n ? "x" + (i + 1)
              : "";
      TableColumn column = new TableColumn(name);
      column.setCellFactory(TextFieldTableCell.forTableColumn());
      column.setPrefWidth(columnWidth);
      column.setSortable(false);

      table.getColumns().add(column);
    }

    for (int i = 0; i < m; i++) {
      data.add("");
    }
    table.setItems(data);

    pane.getChildren().add(table);
  }

  private Node getCell(int i, int j){
    return (Node)table.queryAccessibleAttribute(
            AccessibleAttribute.CELL_AT_ROW_COLUMN,
            i, j);
  }

  private void setOnClickTab(){
    pane.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
      }
    });
  }
}
