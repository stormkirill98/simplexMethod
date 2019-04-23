package dashboard.output;

import dashboard.output.simplex.SimplexView;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class OutputPresenter implements Initializable {
  public AnchorPane pane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    SimplexView simplexView = new SimplexView();
    simplexView.getViewAsync(pane.getChildren()::add);
  }
}
