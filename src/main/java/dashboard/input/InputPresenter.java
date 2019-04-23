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
  public TextField n;
  public TextField m;

  public Pane tablePane;
  public Pane functionPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    createFunctionPane(Integer.valueOf(m.getText()));
    createTablePane(Integer.valueOf(n.getText()),
            Integer.valueOf(m.getText()));
    initInputDimension();
  }

  private void initInputDimension() {
    n.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!isNatural(newValue)) {
        n.setText(oldValue);
        return;
      }

      if (newValue.isEmpty()) {
        newValue = "0";
      }

      createTablePane(Integer.valueOf(newValue),
              Integer.valueOf(m.getText()));

    });

    m.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!isNatural(newValue)) {
        m.setText(oldValue);
        return;
      }

      if (newValue.isEmpty()) {
        newValue = "0";
      }

      createFunctionPane(Integer.valueOf(newValue));
      createTablePane(Integer.valueOf(n.getText()),
              Integer.valueOf(newValue));

    });
  }

  private void createTablePane(int n, int m) {
    ArrayList<Integer> list = new ArrayList<>();
    list.add(n);
    list.add(m);


    TableView tableView = new TableView((f) -> list);
    tablePane.getChildren().clear();
    tableView.getViewAsync(tablePane.getChildren()::add);
  }

  private void createFunctionPane(int m) {
    FunctionView functionView = new FunctionView((f) -> m);
    functionPane.getChildren().clear();
    functionView.getViewAsync(functionPane.getChildren()::add);
  }
}
