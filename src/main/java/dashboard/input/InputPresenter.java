package dashboard.input;

import dashboard.input.function.FunctionView;
import dashboard.input.table.TableView;
import events.MyEventBus;
import events.domain.Dimension;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
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

import static logic.Utilit.isNatural;

@SuppressWarnings("Duplicates")
public class InputPresenter implements Initializable {
  private final int fieldHeight = 40;

  public TextField amountLimits;
  public TextField amountVar;

  public Pane tablePane;
  public Pane functionPane;

  public CheckBox setBasisElement;
  public HBox basisElement;
  public ScrollPane scrollPane;

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
        basisElement.setDisable(false);
      } else {
        basisElement.setDisable(true);
      }
    });

    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    double width = screen.getWidth();
    scrollPane.setPrefViewportWidth(0.215 * width);
    scrollPane.setPrefViewportHeight(fieldHeight);
  }

  private void initInputBasisElement() {
    basisElement.getChildren().clear();

    String str = amountVar.getText();
    int countVar = str.isEmpty() ? 0 : Integer.valueOf(str);

    Label startBkt = new Label("(");
    basisElement.getChildren().add(startBkt);
    for (int i = 0; i < countVar; i++) {
      TextField field = new TextField();
      field.setPrefWidth(fieldHeight);
      field.setText("0");


      basisElement.getChildren().add(field);

      if (i != countVar - 1) {
        Label separator = new Label("; ");
        basisElement.getChildren().add(separator);
      }
    }

    Label endBkt = new Label(")");
    basisElement.getChildren().add(endBkt);
  }

  //TODO:лагает при отменненных изменениях
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

      createTablePane(n, m, null);

      MyEventBus.post(new Dimension(n, m));//TODO: проверить на заполненность если это конечно нужно
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

  //TODO:удалить изменение размера и ограничить размерность, чтобы не зависала программа
}
