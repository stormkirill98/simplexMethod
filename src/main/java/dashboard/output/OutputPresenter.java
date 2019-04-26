package dashboard.output;

import dashboard.output.simplex.SimplexView;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import java.net.URL;
import java.util.ResourceBundle;

public class OutputPresenter implements Initializable {
  public VBox vBox;
  public ScrollPane pane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    double width = screen.getWidth();
    double height = screen.getHeight() - 35;
    pane.setPrefViewportHeight(height);
    pane.setPrefViewportWidth(0.6 * width);

    HBox hBox = null;
    for (int i = 0; i < 3; i++) {
      if (i % 3 == 0) {
        hBox = new HBox();
        vBox.getChildren().add(hBox);
      }
      int finalI = i;
      SimplexView simplexView = new SimplexView((f) -> finalI);
      simplexView.getView(hBox.getChildren()::add);
    }
  }
}
