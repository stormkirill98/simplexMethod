package dashboard.input;

import dashboard.input.table.TableView;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static logic.Utilit.isNatural;

@SuppressWarnings("Duplicates")
public class InputPresenter implements Initializable {
  public TextField n;
  public TextField m;

  public HBox function;
  public ChoiceBox typeFunction;

  public Pane tablePane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    createTable(3, 3);
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

      createTable(Integer.valueOf(newValue),
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

      createTable(Integer.valueOf(n.getText()),
              Integer.valueOf(newValue));

    });
  }

  private void createTable(int n, int m) {
    ArrayList<Integer> list = new ArrayList<>();
    list.add(n);
    list.add(m);


    TableView tableView = new TableView((f) -> list);
    tablePane.getChildren().clear();
    tableView.getViewAsync(tablePane.getChildren()::add);
  }
}
