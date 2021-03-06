package dashboard.output.simplex;

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
import logic.Simplex;
import logic.Utilit;
import logic.enums.End;
import logic.enums.Stage;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static logic.Utilit.convertDecimalToFraction;
import static logic.Utilit.subscript;

@SuppressWarnings("Duplicates")
public class SimplexPresenter implements Initializable {
  final private int cellWidth = 50;
  final private int cellHeight = 35;

  public AnchorPane simplexPane;

  @Inject
  private ArrayList<Object> inputData;

  private int n = 0;
  private int m = 0;

  private int numberStep = 0;

  private Simplex simplex;
  private int step = 0;

  private GridPane table;


  //анимация выбора неподходящего элемента
  int countBlinking = 0;
  int[] indexesBadBaseElement = new int[]{-1, -1};
  protected AnimationTimer animationBlinkBaseEl = new AnimationTimer(){
    private long lastUpdate = 0 ;

    @Override
    public void handle(long now) {
      if (now - lastUpdate <= 100_000_000) {
        return;
      }
      if (countBlinking > 5){
        countBlinking  = 0;
        animationBlinkBaseEl.stop();
        return;
      }

      blinkBadBaseElement(indexesBadBaseElement[0],
                          indexesBadBaseElement[1]);

      lastUpdate = now ;
      countBlinking++;
    }
  };

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    numberStep = (Integer) inputData.get(0);
    simplex = (Simplex) inputData.get(1);
    step = simplex.getStep();

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

    simplexPane.getChildren().add(table);
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
    TextField textField = new TextField(convertDecimalToFraction(value));

    textField.setPadding(new Insets(5));
    textField.setPrefWidth(cellWidth);
    textField.setPrefHeight(cellHeight);
    textField.setFocusTraversable(false);

    listenerToFields(textField);

    textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        //TODO: добавить кнопку для сбрасывания базового элемента на автоматический
        if (step != simplex.getStep()){
          return;
        }

        if (simplex.getStage() == Stage.END){
          return;
        }

        if (simplex.getStage() == Stage.ART_BASIS){
          if (simplex.endArtBasis() == End.SUCCESS_ART_BASIS
                  || simplex.endArtBasis() == End.FAILURE){
            return;
          }
        } else {
          if (simplex.end() == End.SUCCESS_ALL
                  || simplex.end() == End.FAILURE){
            return;
          }
        }

        if (newValue){
          int[] indexes = getIndexField(textField);

          if (elementCanBase(textField)){
            clearBaseElement();
            textField.setId("base-element");

            simplex.setIndexesBaseElement(indexes);
          } else {
            TextField field = getCellByRowColumnIndex(indexesBadBaseElement[0] + 1,
                                                    indexesBadBaseElement[1] + 1);
            if (field != null) {
              field.setId("");
            }

            indexesBadBaseElement = indexes;
            countBlinking = 0;
            animationBlinkBaseEl.start();
          }
        }
      }
    });

    return textField;
  }

  private void blinkBadBaseElement(int i, int j){
    TextField textField = getCellByRowColumnIndex(i + 1, j + 1);
    if (textField == null){
      return;
    }

    String id = textField.getId();
    if (id == null || id.equals("")){
      textField.setId("bad-base-element");
    } else {
      textField.setId("");
    }
  }

  private void clearBaseElement(){
    for (int i = 1; i < n + 1; i++) {
      for (int j = 1; j < m + 1; j++) {
        TextField field = getCellByRowColumnIndex(i, j);
        field.setId("");
      }
    }
  }

  private int[] getIndexField(TextField field){
    for (int i = 1; i < n + 1; i++) {
      for (int j = 1; j < m + 1; j++) {
        TextField textField = getCellByRowColumnIndex(i, j);
        if (textField.equals(field)){
          return new int[]{i - 1, j - 1};
        }
      }
    }

    return new int[] {-1, -1};
  }

  private boolean elementCanBase(TextField textField){
    int[] indexes = getIndexField(textField);

    return simplex.canBaseElement(indexes[0], indexes[1]);
  }

  private void setBaseElement(int[] indexes){
    if (indexes == null){
      return;
    }
    if (indexes[0] == -1 || indexes[1] == -1){
      return;
    }
    TextField base = getCellByRowColumnIndex(indexes[0] + 1, indexes[1] + 1);
    base.setId("base-element");
  }

  private void highlightLastRow(End end){
    String id = end == End.SUCCESS_ALL || end == End.SUCCESS_ART_BASIS
            ? "success-last-row"
            : "failure-last-row";
    for (int j = 0; j < m; j++){
      TextField field = getCellByRowColumnIndex(n, j + 1);
      if (field == null){
        continue;
      }
      field.setId(id);
    }
  }

  private void highlightBadColumn(int index){
    for (int i = 0; i < n; i++){
      TextField field = getCellByRowColumnIndex(i + 1, index + 1);
      if (field == null){
        continue;
      }
      field.setId("failure-column");
    }
  }

  public TextField getCellByRowColumnIndex(int row, int column) {
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
