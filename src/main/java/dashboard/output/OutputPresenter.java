package dashboard.output;

import com.google.common.eventbus.Subscribe;
import dashboard.output.simplex.SimplexView;
import events.MyEventBus;
import events.domain.Dimension;
import events.domain.FunctionDao;
import events.domain.TableLimits;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import logic.Function;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OutputPresenter implements Initializable {
  public VBox vBox;
  public ScrollPane pane;

  private int n = 8;
  private int m = 7;

  private double[][] tableLimits;
  private FunctionDao functionDao;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    double width = 0.6 * screen.getWidth();
    double height = screen.getHeight() - 35;
    pane.setPrefViewportHeight(height);
    pane.setPrefViewportWidth(width);

    MyEventBus.register(this);

    //fillSimplexesForTest(width);
  }

  @Subscribe
  public void getDimnsion(Dimension dimension) {
    this.n = dimension.getN();
    this.m = dimension.getM();
  }

  @Subscribe
  public void getTableLimits(TableLimits tableLimits) {
    this.tableLimits = tableLimits.getTable();
  }

  @Subscribe
  public void getFunction(FunctionDao functionDao) {
    System.out.println(functionDao.getTypeProblem());
    this.functionDao = functionDao;
  }

  private void fillSimplexesForTest(double width) {
    HBox hBox = new HBox();
    vBox.getChildren().add(hBox);
    int countSimplexInCurrentRow = 0;
    for (int i = 0; i < 10; i++) {
      double simplexWidth = ((m + 2) * 50 + (m + 1) * 3);//TODO: the chances are optimize

      //is it possible to add simplex in this row
      if (simplexWidth * (countSimplexInCurrentRow + 1) > width) {
        Separator separator = new Separator();
        separator.setPrefWidth(width);
        separator.setOrientation(Orientation.HORIZONTAL);
        separator.setPadding(new Insets(10, 0, 5, 0));
        vBox.getChildren().add(separator);

        hBox = new HBox();
        vBox.getChildren().add(hBox);
        countSimplexInCurrentRow = 0;
      }

      ArrayList<Integer> dataTo = new ArrayList<>();
      dataTo.add(n);
      dataTo.add(m);
      dataTo.add(i);

      SimplexView simplexView = new SimplexView((f) -> dataTo);
      simplexView.getView(hBox.getChildren()::add);

      countSimplexInCurrentRow++;
    }
  }
}
