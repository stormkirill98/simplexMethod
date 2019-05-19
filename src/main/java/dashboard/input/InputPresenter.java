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
    List<Double> inputBasisElement = (List<Double>) inputData.get(2);

    if (limits != null) {
      amountLimits.setText(String.valueOf(limits.length));
      amountVar.setText(String.valueOf(limits[0].length - 1));
    }

    createFunctionPane(Integer.valueOf(amountVar.getText()), function);

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

    fillBasisElement(inputBasisElement);

    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    double width = screen.getWidth();
    scrollPane.setPrefViewportWidth(0.215 * width);
    scrollPane.setPrefViewportHeight(fieldHeight);
  }

  private void fillBasisElement(List<Double> basisElement){
    if (basisElement == null){
      return;
    }

    setBasisElement.setSelected(true);

    ObservableList<Node> childrens = basisElementNode.getChildren();

    int index = 0;
    for (Node children : childrens) {
      try {
        TextField field = (TextField) children;

        field.setText(String.valueOf(basisElement.get(index++)));
      } catch (ClassCastException ignored) {}
    }
  }

  private void initInputBasisElement() {
    basisElementNode.getChildren().clear();

    String str = amountVar.getText();
    int countVar = str.isEmpty() ? 0 : Integer.valueOf(str);
    if (countVar < 3) {
      basisElementNode.getChildren().clear();
      return;
    }

    Label startBkt = new Label("(");
    basisElementNode.getChildren().add(startBkt);
    for (int i = 0; i < countVar; i++) {
      TextField field = new TextField();
      field.setPrefWidth(fieldWidth);
      field.setText("0");

      field.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!validateDouble(field, oldValue, newValue)) {
          return;
        }

        if (setBasisElement.isSelected()) {
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

  private List<Double> createBasisElement() {
    List<Double> basisElement = new ArrayList<>();
    ObservableList<Node> childrens = basisElementNode.getChildren();

    int countLimits = Integer.valueOf(amountLimits.getText());

    int countNoNullCoef = 0;

    for (Node children : childrens) {
      try {
        TextField field = (TextField) children;
        Double value = strToDouble(field.getText());
        if (value == null) {
          return null;
        }

        if (!isZero(value)) {
          countNoNullCoef++;
        }

        basisElement.add(value);
      } catch (ClassCastException ignored) {
      }
    }

    if (countNoNullCoef > countLimits) {
      return null;
    }

    if (countNoNullCoef == 0) {
      return null;
    }

    return basisElement;
  }

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
      String str = amountVar.getText();
      int m = str.isEmpty() ? 0 : Integer.valueOf(str);

      if (n > 16) {
        amountLimits.setText("16");
        return;
      }

      createTablePane(n, m, null);
      initInputBasisElement();

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

      String str = amountLimits.getText();
      int n = str.isEmpty() ? 0 : Integer.valueOf(str);
      int m = Integer.valueOf(newValue);

      if (m > 16) {
        amountVar.setText("16");
        return;
      }

      createTablePane(n, m, null);
      initInputBasisElement();
      createFunctionPane(m, null);

      MyEventBus.post(new Dimension(n, m));
    });
  }

  private void createTablePane(int amountLimits, int amountVar, double[][] limits) {
    if (amountLimits < 2){
      tablePane.getChildren().clear();
      this.amountLimits.setId("text-field-empty");
      return;
    }
    if (amountVar < 3){
      tablePane.getChildren().clear();
      this.amountVar.setId("text-field-empty");
      return;
    }
    this.amountLimits.setId("");
    this.amountVar.setId("");



    ArrayList<Object> list = new ArrayList<>();
    list.add(amountLimits);
    list.add(amountVar);
    list.add(limits);

    TableView tableView = new TableView((f) -> list);
    tablePane.getChildren().clear();
    tableView.getViewAsync(tablePane.getChildren()::add);
  }

  private void createFunctionPane(int amountVar, Function function) {
    if (amountVar < 3){
      functionPane.getChildren().clear();
      this.amountVar.setId("text-field-empty");
      return;
    }
    this.amountVar.setId("");

    List<Object> data = new ArrayList<>();
    data.add(amountVar);
    data.add(function);

    FunctionView functionView = new FunctionView((f) -> data);
    functionPane.getChildren().clear();
    functionView.getViewAsync(functionPane.getChildren()::add);
  }
}
