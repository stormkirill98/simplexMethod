package dashboard.input;

import dashboard.input.function.FunctionView;
import dashboard.input.table.TableView;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static logic.Utilit.isNatural;

@SuppressWarnings("Duplicates")
public class InputPresenter implements Initializable {
  public TextField amountVar;
  public TextField amountLimit;

  public Pane tablePane;
  public Pane functionPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    createFunctionPane(Integer.valueOf(amountLimit.getText()));
    createTablePane(Integer.valueOf(amountVar.getText()),
            Integer.valueOf(amountLimit.getText()));
    initInputDimension();
  }

  private void initInputDimension() {
    amountVar.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!isNatural(newValue)) {
        amountVar.setText(oldValue);
        return;
      }

      if (newValue.isEmpty()) {
        newValue = "0";
      }

      createTablePane(Integer.valueOf(newValue),
              Integer.valueOf(amountLimit.getText()));


    });

    amountLimit.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!isNatural(newValue)) {
        amountLimit.setText(oldValue);
        return;
      }

      if (newValue.isEmpty()) {
        newValue = "0";
      }

      createFunctionPane(Integer.valueOf(newValue));
      createTablePane(Integer.valueOf(amountVar.getText()),
              Integer.valueOf(newValue));

    });
  }

  private void createTablePane(int amountLimits, int amountVar) {
    ArrayList<Integer> list = new ArrayList<>();
    list.add(amountLimits);
    list.add(amountVar);

    TableView tableView = new TableView((f) -> list);
    tablePane.getChildren().clear();
    tableView.getViewAsync(tablePane.getChildren()::add);
  }

  private void createFunctionPane(int amountVar) {
    FunctionView functionView = new FunctionView((f) -> amountVar);
    functionPane.getChildren().clear();
    functionView.getViewAsync(functionPane.getChildren()::add);
  }
}
