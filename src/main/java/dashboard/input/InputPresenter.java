package dashboard.input;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

import static logic.Utilit.isNatural;

@SuppressWarnings("Duplicates")
public class InputPresenter implements Initializable {
  public TextField n;
  public TextField m;

  public HBox function;
  public ChoiceBox typeFunction;

  public GridPane coefs;

  private Node inputCoef;
  private Node label;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    inputCoef = getNodeFromCoefs(0, 1);
    label = getNodeFromCoefs(0, 0);
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

      createTableCoefs();

    });

    m.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!isNatural(newValue)) {
        m.setText(oldValue);
        return;
      }

      if (newValue.isEmpty()) {
        newValue = "0";
      }

      createTableCoefs();

    });
  }

  private void createTableCoefs() {
    int n = Integer.valueOf(this.n.getText());
    int m = Integer.valueOf(this.m.getText());

    coefs.getChildren().clear();

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        if (i == 0){
          Node node = label;
          node.accessibleTextProperty().setValue("x" + (i+1));
          coefs.add(node, i, j);
          continue;
        }

        Node node = inputCoef;
        coefs.add(node, i, j);
      }
    }
  }

  private Node getNodeFromCoefs(int col, int row) {
    return coefs.getChildren().get(col * coefs.getColumnConstraints().size() + row);
  }
}
