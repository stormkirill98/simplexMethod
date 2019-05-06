package dashboard.input;

import dashboard.input.function.FunctionView;
import dashboard.input.table.TableView;
import events.*;
import events.domain.Dimension;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import logic.Function;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static logic.Utilit.isNatural;

@SuppressWarnings("Duplicates")
public class InputPresenter implements Initializable {
  public TextField amountLimits;
  public TextField amountVar;

  public Pane tablePane;
  public Pane functionPane;

  @Inject
  private ArrayList<Object> inputData;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initInputDimension();

    Function function = (Function) inputData.get(0);
    double[][] limits = (double[][]) inputData.get(1);
    if (limits != null){
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
      int m = Integer.valueOf(amountVar.getText());

      createTablePane(n, m, null);
      changeSizeTextField(amountLimits);

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

      List<Object> toFunctionPane = new ArrayList<>();
      toFunctionPane.add(Integer.valueOf(amountVar.getText()));
      toFunctionPane.add(null);
      createFunctionPane(toFunctionPane);

      changeSizeTextField(amountVar);

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

  private void changeSizeTextField(TextField textField) {
    if (textField == null) {
      return;
    }

    Text text = new Text(textField.getText());
    text.setFont(textField.getFont());
    new Scene(new Group(text));
    text.applyCss();

    double lengthText = text.getLayoutBounds().getWidth() + 21;

    if (lengthText < textField.getMinWidth()) {
      lengthText = textField.getMinWidth();
    }

    if (lengthText > textField.getMaxWidth()) {
      lengthText = textField.getMaxWidth();
    }

    textField.setPrefWidth(lengthText);
  }
}
