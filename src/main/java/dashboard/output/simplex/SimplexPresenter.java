package dashboard.output.simplex;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import logic.Simplex;
import logic.Utilit;
import logic.enums.End;
import logic.enums.Stage;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static logic.Utilit.subscript;

@SuppressWarnings("Duplicates")
public class SimplexPresenter implements Initializable {
  final private int cellWidth = 50;
  final private int cellHeight = 35;

  public AnchorPane pane;

  @Inject
  private ArrayList<Object> inputData;

  private int n = 0;
  private int m = 0;
  private int numberStep = 0;

  private Simplex simplex;

  private GridPane table;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    numberStep = (Integer) inputData.get(0);
    simplex = (Simplex) inputData.get(1);

    n = simplex.getCountRows();
    m = simplex.getCountCols();

    createSimplexPane();

    int indexNoPositiveColumn = simplex.getIndexNoPositiveColumn();
    if (indexNoPositiveColumn >= 0){
      highlightBadColumn(indexNoPositiveColumn);
      return;
    }

    Stage stage = simplex.getStage();
    if (stage == Stage.ART_BASIS && simplex.endArtBasis() == End.FAILURE){
      highlightLastRow(End.FAILURE);
      return;
    } else if (simplex.end() == End.FAILURE){
      highlightLastRow(End.FAILURE);
      return;
    }

    if (stage == Stage.ART_BASIS && simplex.endArtBasis() == End.SUCCESS_ART_BASIS){
      highlightLastRow(End.SUCCESS_ART_BASIS);
      return;
    } else if (simplex.end() == End.SUCCESS_ALL){
      highlightLastRow(End.SUCCESS_ALL);
      return;
    }



    /*if (simplex.endArtBasis() == End.FAILURE ||
        simplex.end() == End.FAILURE){
      highlightLastRow(End.FAILURE);
      return;
    }*/

    int[] indexesBaseElement = simplex.searchBaseElement();
    setBaseElement(indexesBaseElement);
  }

  //create panel with view of simplex
  private void createSimplexPane() {
    table = new GridPane();

    table.setHgap(3);
    table.setVgap(3);
    ColumnConstraints constraints = new ColumnConstraints();
    constraints.setHalignment(HPos.CENTER);
    table.getColumnConstraints().addAll(constraints, constraints);


    //fill grid scrollPane labels and fields
    for (int i = 0; i < n + 1; i++) {
      for (int j = 0; j < m + 1; j++) {
        if (i == 0 && j == 0){
          Label label = createLabel(numberStep);
          table.add(label, j, i);
          continue;
        }
        if (i == 0) {
          if (j == m)
            continue;

          List<Integer> indexesVarCol = simplex.getIndexesVarCol();
          Label label = createLabelCol(indexesVarCol.get(j - 1));
          table.add(label, j, i);
          continue;
        }

        if (j == 0) {
          if (i == n)
            continue;

          List<Integer> indexesVarRow = simplex.getIndexesVarRow();
          Label label = createLabelRow(indexesVarRow.get(i - 1));
          table.add(label, j, i);
          continue;
        }

        TextField textField = createTextField(simplex.getValue(i - 1, j - 1));
        table.add(textField, j, i);
      }
    }

    pane.getChildren().add(table);
  }

  private Label createLabel(int numberStep){
    Label label = new Label("");

    label.setAlignment(Pos.CENTER);
    label.setPrefWidth(cellWidth);
    label.setPrefHeight(cellHeight);

    String name = "X̅" + Utilit.superscript(String.valueOf(numberStep));

    label.setText(name);



    return label;
  }

  private Label createLabelRow(int indexVarRow) {
    Label label = new Label("");

    label.setAlignment(Pos.CENTER);
    label.setPrefWidth(cellWidth);
    label.setPrefHeight(cellHeight);

    String rowName = "x" + subscript(String.valueOf(indexVarRow));

    label.setText(rowName);

    return label;
  }

  private Label createLabelCol(int indexVarCol) {
    Label label = new Label("");

    label.setAlignment(Pos.CENTER);
    label.setPrefWidth(cellWidth);
    label.setPrefHeight(cellHeight);

    String columnName = "x" + subscript(String.valueOf(indexVarCol));

    label.setText(columnName);

    return label;
  }

  private TextField createTextField(Double value){
    TextField textField = new TextField(String.format("%.2f", value));

    textField.setPadding(new Insets(5));
    textField.setPrefWidth(cellWidth);
    textField.setPrefHeight(cellHeight);

    return textField;
  }

  private void setBaseElement(int[] indexes){
    if (indexes == null){
      return;
    }
    if (indexes[0] == -1 || indexes[1] == -1){
      return;
    }
    TextField base = getNodeByRowColumnIndex(indexes[0] + 1, indexes[1] + 1);
    base.setId("base-element");
  }

  private void highlightLastRow(End end){
    String id = end == End.SUCCESS_ALL || end == End.SUCCESS_ART_BASIS
            ? "success-last-row"
            : "failure-last-row";
    for (int j = 0; j < m; j++){
      TextField field = getNodeByRowColumnIndex(n, j + 1);
      if (field == null){
        continue;
      }
      field.setId(id);
    }
  }

  private void highlightBadColumn(int index){
    for (int i = 0; i < n; i++){
      TextField field = getNodeByRowColumnIndex(i + 1, index + 1);
      if (field == null){
        continue;
      }
      field.setId("failure-column");
    }
  }

  public TextField getNodeByRowColumnIndex(int row, int column) {
    ObservableList<Node> childrens = table.getChildren();

    try {
      for (Node node : childrens) {
        if (GridPane.getRowIndex(node) == row
                && GridPane.getColumnIndex(node) == column) {
          return (TextField) node;
        }
      }
    } catch (ClassCastException ignored){ }

    return null;
  }
}
