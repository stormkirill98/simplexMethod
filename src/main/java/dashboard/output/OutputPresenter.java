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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import logic.Algorithm;
import logic.Function;
import logic.Simplex;
import logic.enums.End;
import logic.enums.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@SuppressWarnings("ALL")
public class OutputPresenter implements Initializable {
  public AnchorPane pane;
  public VBox vBox;
  public ScrollPane scrollPane;

  public Button start;


  public HBox hBox = new HBox();

  private int countSimplexes = 0;
  int countSimplexInCurrentRow = 0;
  private double widthPane = 0;

  private int n = 0;
  private int m = 0;

  private double[][] tableLimits;
  private FunctionDao functionDao;

  private Algorithm algorithm;
  private Function function;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    widthPane = 0.6 * screen.getWidth();
    double height = screen.getHeight() - 35;
    scrollPane.setPrefViewportHeight(height);
    scrollPane.setPrefViewportWidth(widthPane);


    MyEventBus.register(this);

    start.setOnAction(event -> {
      clear();
      countSimplexInCurrentRow = 0;
      countSimplexes = 0;
      vBox.getChildren().add(hBox);

      if (tableLimits == null) {
        return;
      }
      algorithm = new Algorithm(tableLimits);

      if (functionDao == null) {
        return;
      }
      function = new Function(functionDao.getTypeProblem());
      function.setCoefficients(functionDao.getCoefs());

      algorithm.setFunction(function);

      System.out.println(algorithm);

      try {
        goAlgorithm();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  private void goAlgorithm() throws InterruptedException {
    algorithm.createArtBasis();
    createSimplex(algorithm.getSimplex());
    Thread.sleep(150);

    End end = End.CONTINUE;

    //искусственный базис
    while (end == End.CONTINUE) {
      end = algorithm.makeStep();
      createSimplex(algorithm.getSimplex());

      Thread.sleep(150);
    }
    if (end == End.FAILURE) {
      return;
    }


    //simplex-method
    algorithm.setStage(Stage.SIMPLEX);

    algorithm.recountLastRow();//вывести симплекс
    createSimplex(algorithm.getSimplex());

    Thread.sleep(150);

    end = End.CONTINUE;
    while (end == End.CONTINUE) {
      end = algorithm.makeStep();
      createSimplex(algorithm.getSimplex());

      Thread.sleep(150);
    }

    if (end == End.FAILURE) {
      return;
    }
  }

  private void createSimplex(Simplex simplex) {
    if (simplex == null) {
      return;
    }
    ArrayList<Object> dataTo = new ArrayList<>();
    dataTo.add(countSimplexes++);
    dataTo.add(simplex);

    makeNewRow(simplex.getCountCols());
    SimplexView simplexView = new SimplexView((f) -> dataTo);
    simplexView.getViewAsync(hBox.getChildren()::add);

    countSimplexInCurrentRow++;
  }

  private void makeNewRow(int countSimplexCol) {
    double simplexWidth = ((countSimplexCol + 2) * 50 + (countSimplexCol + 1) * 3);//TODO: the chances are optimize

    //is it possible to add simplex in this row
    if (simplexWidth * (countSimplexInCurrentRow + 1) > widthPane) {
      Separator separator = new Separator();
      separator.setPrefWidth(widthPane);
      separator.setOrientation(Orientation.HORIZONTAL);
      separator.setPadding(new Insets(10, 0, 5, 0));
      vBox.getChildren().add(separator);

      hBox = new HBox();
      vBox.getChildren().add(hBox);
      countSimplexInCurrentRow = 0;
    }
  }

  private void clear() {
    for (Node node : vBox.getChildren()) {
      try {
        HBox hBox = (HBox) node;
        hBox.getChildren().clear();
      } catch (ClassCastException e) {
      }
    }

    vBox.getChildren().clear();
  }

  @Subscribe
  public void receiveDimension(Dimension dimension) {
    System.out.println("get dimension");
    this.n = dimension.getN();
    this.m = dimension.getM();
  }

  @Subscribe
  public void receiveTableLimits(TableLimits tableLimits) {
    System.out.println("get limits");
    this.tableLimits = tableLimits.getTable();
  }

  @Subscribe
  public void receiveFunction(FunctionDao functionDao) {
    System.out.println("get function");
    this.functionDao = functionDao;
  }
}
