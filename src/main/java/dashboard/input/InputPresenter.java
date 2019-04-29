package dashboard.input;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import dashboard.input.function.FunctionView;
import dashboard.input.table.TableView;
import events.*;
import events.domain.Dimension;
import javafx.event.EventType;
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

    //TODO:не забыть удалить
    amountVar.setText("4");
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

      int n = Integer.valueOf(newValue);
      int m = Integer.valueOf(amountLimit.getText());

      createTablePane(n, m);

      MyEventBus.post(new Dimension(n, m));
    });

    amountLimit.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!isNatural(newValue)) {
        amountLimit.setText(oldValue);
        return;
      }

      if (newValue.isEmpty()) {
        newValue = "0";
      }

      int n = Integer.valueOf(amountVar.getText());
      int m = Integer.valueOf(newValue);

      createTablePane(n, m);
      createFunctionPane(m);

      MyEventBus.post(new Dimension(n, m));
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
