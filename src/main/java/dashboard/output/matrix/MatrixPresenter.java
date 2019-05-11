package dashboard.output.matrix;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import logic.Utilit;
import logic.enums.End;
import logic.enums.Stage;
import logic.gauss.LinearSystem;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static logic.Utilit.subscript;

@SuppressWarnings("Duplicates")
public class MatrixPresenter implements Initializable {
  final private int cellWidth = 50;
  final private int cellHeight = 35;

  public AnchorPane pane;

  private int n = 0;
  private int m = 0;

  @Inject
  private ArrayList<Object> inputData;

  private LinearSystem system;
  private List<Integer> orderVars;

  private GridPane table;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    system = (LinearSystem) inputData.get(0);
    orderVars = system.getOrderColumn();

    n = system.size();
    m = system.getEquation(0).size();

    createMatrixPane();
  }

  private void createMatrixPane() {
    table = new GridPane();

    table.setHgap(3);
    table.setVgap(3);
    ColumnConstraints constraints = new ColumnConstraints();
    constraints.setHalignment(HPos.CENTER);
    table.getColumnConstraints().addAll(constraints, constraints);

    //fill grid scrollPane labels and fields
    for (int i = 0; i < n + 1; i++) {
      for (int j = 0; j < m; j++) {
        if (i == 0){
          if (j == m - 1){
            continue;
          }

          Label label = createLabel(orderVars.get(j));
          table.add(label, j, i);
          continue;
        }

        TextField textField = createTextField(system.getEquation(i - 1).get(j));
        table.add(textField, j, i);
      }
    }

    pane.getChildren().add(table);
  }

  private Label createLabel(int index){
    Label label = new Label("");

    label.setAlignment(Pos.CENTER);
    label.setPrefWidth(cellWidth);
    label.setPrefHeight(cellHeight);

    String name = "X" + Utilit.subscript(String.valueOf(index));

    label.setText(name);

    return label;
  }

  private TextField createTextField(Double value){
    TextField textField = new TextField(String.format("%.2f", value));

    textField.setPadding(new Insets(5));
    textField.setPrefWidth(cellWidth);
    textField.setPrefHeight(cellHeight);
    textField.setFocusTraversable(false);

    listenerToFields(textField);

    return textField;
  }

  private void listenerToFields(TextField field){
    final String[] buf = new String[1];
    buf[0] = "";
    //не изменять значения в выводе
    field.textProperty().addListener((observable, oldValue, newValue) -> {
      //TODO: KoctyLb
      if (buf[0].equals(oldValue)){
        return;
      }
      buf[0] = newValue;
      field.setText(oldValue);
    });
  }
}
