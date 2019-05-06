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
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import logic.Algorithm;
import logic.Function;
import logic.Simplex;
import logic.enums.End;
import logic.enums.Error;
import logic.enums.Stage;
import logic.enums.TypeProblem;

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
  public CheckBox stepByStep;
  public Button next;
  public Button prev;

  private int step = 0;
  int countSimplexInCurrentRow = 0;
  private double widthPane = 0;

  private int n = 3;
  private int m = 3;

  private double[][] tableLimits;
  private FunctionDao functionDao;

  private Algorithm algorithm;
  private Function function;

  private End end = End.CONTINUE;
  private Stage stage = Stage.ART_BASIS;
  private boolean printAnswer = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    widthPane = 0.6 * screen.getWidth();
    double height = screen.getHeight() - 120;
    scrollPane.setPrefViewportHeight(height);
    scrollPane.setPrefViewportWidth(widthPane);

    MyEventBus.register(this);

    start.setOnAction(event -> {
      onClickStart();
    });
    next.setOnAction(event -> {
      onClickNext();
    });
    stepByStep.selectedProperty().addListener((observable, oldValue, newValue) -> changeStepByStep(newValue));
  }

  private void onClickStart() {
    clear();
    countSimplexInCurrentRow = 0;
    step = 0;
    end = End.CONTINUE;

    vBox.getChildren().add(hBox);

    if (tableLimits == null) {
      return;
    }
    algorithm = new Algorithm(tableLimits);
    if (algorithm.getStage() == Stage.END) {
      printError(Error.BAD_INPUT);
      return;
    }

    if (functionDao == null) {
      return;
    }
    function = new Function(functionDao.getTypeProblem());
    function.setCoefficients(functionDao.getCoefs());
    if (function.getType() == TypeProblem.MAX) {
      function.reverseType();
    }

    algorithm.setFunction(function);

    System.out.println(algorithm);

    if (stepByStep.isSelected()) {
      algorithm.createArtBasis();
      createSimplex(algorithm.getSimplex());
    } else {
      try {
        goAlgorithm();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void onClickNext() {
    if (algorithm == null){
      return;
    }
    if (end == End.FAILURE) {
      printError(Error.NOT_LIMITED);
      return;
    }
    if (end == End.SUCCESS_ALL) {
      printAnswer();
      return;
    }
    if (end == End.SUCCESS_ART_BASIS) {
      algorithm.setStage(Stage.SIMPLEX);
      step = 0;

      algorithm.recountLastRow();
      createSimplex(algorithm.getSimplex());
      end = algorithm.getSimplex().end();
      return;
    }

    end = algorithm.makeStep();
    createSimplex(algorithm.getSimplex());
  }

  public void changeStepByStep(boolean newValue) {
    if (newValue) {
      next.setDisable(false);
      prev.setDisable(false);
    } else {
      next.setDisable(true);
      prev.setDisable(true);
    }
  }

  private void goAlgorithm() throws InterruptedException {
    algorithm.createArtBasis();
    createSimplex(algorithm.getSimplex());
    Thread.sleep(150);

    end = End.CONTINUE;
    //искусственный базис
    while (end == End.CONTINUE) {
      end = algorithm.makeStep();
      createSimplex(algorithm.getSimplex());

      Thread.sleep(150);
    }
    if (end == End.FAILURE) {
      printError(Error.NOT_LIMITED);
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
      printError(Error.NOT_LIMITED);
      return;
    }
  }

  private void createSimplex(Simplex simplex) {
    if (simplex == null) {
      return;
    }
    ArrayList<Object> dataTo = new ArrayList<>();
    dataTo.add(step++);
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
      addSeparator();

      hBox = new HBox();
      vBox.getChildren().add(hBox);
      countSimplexInCurrentRow = 0;
    }
  }

  private void addSeparator() {
    Separator separator = new Separator();//TODO: не на всю ширину
    separator.setPrefWidth(widthPane);
    separator.setOrientation(Orientation.HORIZONTAL);
    separator.setPadding(new Insets(10, 0, 5, 0));
    vBox.getChildren().add(separator);
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

  private void printError(Error error) {
    addSeparator();

    HBox hBox = new HBox();

    String text = error == Error.NOT_LIMITED ? "Not limited" : "Check input";

    Label label = new Label(text);
    label.setFont(new Font(40));

    hBox.getChildren().add(label);
    vBox.getChildren().add(hBox);

    addSeparator();
  }

  private void printAnswer() {
    if (printAnswer) {
      return;
    }

    addSeparator();

    VBox vBox = new VBox();

    Font font = new Font(30);

    String type = "";
    if (function.isReverse()) {
      type = function.getType() == TypeProblem.MIN ? "max" : "min";
    } else {
      type = function.getType().toString().toLowerCase();
    }
    Label point = new Label();
    String str = "Point " + type + " = " + algorithm.getPointExtr();
    point.setText(str);
    point.setFont(font);
    point.setAlignment(Pos.CENTER);

    Label value = new Label();
    String functionValue = type == "min" ? String.format("%.4f", -algorithm.getFunctionExtr())
                                         : String.format("%.4f", algorithm.getFunctionExtr());
    str = "Function " + type + " value = " + functionValue;
    value.setText(str);
    value.setFont(font);
    value.setAlignment(Pos.CENTER);

    vBox.getChildren().add(point);
    vBox.getChildren().add(value);
    this.vBox.getChildren().add(vBox);

    addSeparator();
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
