package dashboard.input;

import dashboard.input.function.FunctionView;
import dashboard.input.table.TableView;
import events.MyEventBus;
import events.domain.BasisElement;
import events.domain.Dimension;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import logic.Function;
import org.checkerframework.checker.units.qual.A;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static logic.Utilit.*;

@SuppressWarnings("Duplicates")
public class InputPresenter implements Initializable {
  private final int fieldHeight = 35;
  private final int fieldWidth = 50;

  public TextField amountLimits;
  public TextField amountVar;

  public Pane tablePane;
  public Pane functionPane;

  public CheckBox setBasisElement;
  public HBox basisElementNode;
  public ScrollPane scrollPane;

  private List<Double> basisElement = null;

  @Inject
  private ArrayList<Object> inputData;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initInputDimension();

    Function function = (Function) inputData.get(0);
    double[][] limits = (double[][]) inputData.get(1);
    if (limits != null) {
      amountLimits.setText(String.valueOf(limits.length));
      amountVar.setText(String.valueOf(limits[0].length - 1));
    }

    List<Object> toFunctionPane = new ArrayList<>();
    toFunctionPane.add(Integer.valueOf(amountVar.getText()));
    toFunctionPane.add(function);
    createFunctionPane(toFunctionPane);

    createTablePane(Integer.valueOf(amountLimits.getText()),
            Integer.valueOf(amountVar.getText()),
            limits);

    initInputBasisElement();

    setBasisElement.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        basisElementNode.setDisable(false);
        basisElement = createBasisElement();

        MyEventBus.post(new BasisElement(basisElement));
      } else {
        basisElementNode.setDisable(true);
        MyEventBus.post(new BasisElement(null));
      }
    });

    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    double width = screen.getWidth();
    scrollPane.setPrefViewportWidth(0.215 * width);
    scrollPane.setPrefViewportHeight(fieldHeight);
  }

  private void initInputBasisElement() {
    basisElementNode.getChildren().clear();

    String str = amountVar.getText();
    int countVar = str.isEmpty() ? 0 : Integer.valueOf(str);

    Label startBkt = new Label("(");
    basisElementNode.getChildren().add(startBkt);
    for (int i = 0; i < countVar; i++) {
      TextField field = new TextField();
      field.setPrefWidth(fieldWidth);
      field.setText("0");

      field.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!validateDouble(field, oldValue, newValue)){
          return;
        }

        if (setBasisElement.isSelected()){
          basisElement = createBasisElement();
          MyEventBus.post(new BasisElement(basisElement));
        } else {
          basisElement = null;
          MyEventBus.post(new BasisElement(null));
        }
      });

      basisElementNode.getChildren().add(field);

      if (i != countVar - 1) {
        Label separator = new Label("; ");
        basisElementNode.getChildren().add(separator);
      }
    }

    Label endBkt = new Label(")");
    basisElementNode.getChildren().add(endBkt);
  }

  private List<Double> createBasisElement(){
    List<Double> basisElement = new ArrayList<>();
    ObservableList<Node> childrens = basisElementNode.getChildren();

    int countLimits = Integer.valueOf(amountLimits.getText());

    int countNoNullCoef = 0;

    for (int i = 0; i < childrens.size(); i++) {
      try {
        TextField field = (TextField) childrens.get(i);
        Double value = strToDouble(field.getText());
        if (value == null){
          return null;
        }

        if (!isZero(value)) {
          countNoNullCoef++;
        }

        basisElement.add(value);
      } catch (ClassCastException ignored){ }
    }

    if (countNoNullCoef > countLimits){
      return null;
    }

    if (countNoNullCoef == 0){
      return null;
    }

    return basisElement;
  }

  //TODO:лагает при отменненных изменениях
  //TODO:ограничить размерность, чтобы не зависала программа
  private void initInputDimension() {
    amountLimits.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!isNatural(newValue)) {
        amountLimits.setText(oldValue);
        return;
      }

      if (newValue.isEmpty()) {
        newValue = "0";
      }

      int n = Integer.valueOf(newValue);
      int m = Integer.valueOf(amountVar.getText());

      if (n < 2) {
        amountLimits.setText("2");
        return;
      }
      if (n > 16) {
        amountLimits.setText("16");
        return;
      }

      createTablePane(n, m, null);

      MyEventBus.post(new Dimension(n, m));
    });

    amountVar.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!isNatural(newValue)) {
        amountVar.setText(oldValue);
        return;
      }

      if (newValue.isEmpty()) {
        newValue = "0";
      }

      int n = Integer.valueOf(amountLimits.getText());
      int m = Integer.valueOf(newValue);

      if (m < 3) {
        amountVar.setText("3");
        return;
      }
      if (m > 16) {
        amountVar.setText("16");
        return;
      }

      createTablePane(n, m, null);
      initInputBasisElement();

      List<Object> toFunctionPane = new ArrayList<>();
      toFunctionPane.add(Integer.valueOf(amountVar.getText()));
      toFunctionPane.add(null);
      createFunctionPane(toFunctionPane);

      MyEventBus.post(new Dimension(n, m));//TODO: проверить на заполненность если это конечно нужно
    });
  }

  private void createTablePane(int amountLimits, int amountVar, double[][] limits) {
    ArrayList<Object> list = new ArrayList<>();
    list.add(amountLimits);
    list.add(amountVar);
    list.add(limits);

    TableView tableView = new TableView((f) -> list);
    tablePane.getChildren().clear();
    tableView.getViewAsync(tablePane.getChildren()::add);
  }

  private void createFunctionPane(List<Object> data) {
    FunctionView functionView = new FunctionView((f) -> data);
    functionPane.getChildren().clear();
    functionView.getViewAsync(functionPane.getChildren()::add);
  }
}
