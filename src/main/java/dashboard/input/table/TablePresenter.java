package dashboard.input.table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
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

  private GridPane coefs;


  private LinearSystem system;

  private boolean pressCtrl = false;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    n = dimension.get(0);
    m = dimension.get(1);

    system = new LinearSystem();

    createTable();
  }

  private void createTable() {
    coefs = new GridPane();

    coefs.setHgap(3);
    coefs.setVgap(3);
    ColumnConstraints constraints = new ColumnConstraints();
    constraints.setHalignment(HPos.CENTER);
    coefs.getColumnConstraints().addAll(constraints, constraints);
    
    for (int i = 0; i < n + 1; i++) {
      for (int j = 0; j < m + 1; j++) {
        if (i == 0) {
          if (j == n) {
            continue;
          }

          Label label = new Label("x" + (j + 1));
          coefs.add(label, j, i);
          continue;
        }

        TextField textField = new TextField();
        textField.setPadding(new Insets(5));
        textField.setPrefWidth(columnWidth);
        textField.setPrefHeight(rowHeight);
        coefs.add(textField, j, i);
      }
    }

    pane.getChildren().add(coefs);
  }


}
