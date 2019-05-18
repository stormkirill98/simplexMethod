package dashboard.output;

import com.google.common.eventbus.Subscribe;
import dashboard.output.matrix.MatrixView;
import dashboard.output.simplex.SimplexView;
import events.MyEventBus;
import events.domain.BasisElement;
import events.domain.Dimension;
import events.domain.FunctionDao;
import events.domain.TableLimits;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import logic.gauss.Gauss;
import logic.gauss.LinearSystem;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static logic.Utilit.isZero;

@SuppressWarnings("ALL")
public class OutputPresenter implements Initializable {
  public ScrollPane scrollPane;

  public VBox simplexesVBox;
  public HBox simplexesRow = new HBox();

  public Button start;
  public CheckBox stepByStep;
  public Button next;
  public Button back;

  private int step = 0;
  private double widthPane = 0;

  private int n = 3;
  private int m = 3;

  private double[][] tableLimits;
  private FunctionDao functionDao;
  private List<Double> basisElement = null;

  private Algorithm algorithm;
  private Function function;

  private End end = End.CONTINUE;
  private Stage stage;

  private boolean printAnswer = false;

  private boolean noEndDirectGauss = true;
  private boolean noEndReversGauss = true;

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

    resetVars();

    simplexesVBox.getChildren().add(simplexesRow);

    if (tableLimits == null) {
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

    algorithm = new Algorithm(tableLimits, null, 0, function);
    if (algorithm.getStage() == Stage.END) {
      printError(Error.BAD_INPUT);
      return;
    }



    if (stepByStep.isSelected()) {
      if (basisElement != null) {
        startGauss();
        return;
      }

      algorithm.createArtBasis();
      createSimplex(algorithm.getSimplex());
    } else {
      try {
        if (basisElement != null) {
          goGauss();
        } else {
          goAlgorithm();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  //TODO: шаг назад не работает при гауссе
  public boolean onClickNext() {
    if (stage == Stage.GAUSS) {
      if (!noEndDirectGauss && !noEndReversGauss) {

        Gauss.backSwap();
        if (Gauss.isSwap()) {
          createMatrixPane();
        }

        try {
          Thread.sleep(150);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        stage = Stage.SIMPLEX;

        List<Integer> indexesExpressedVars = Gauss.getIndexesExpressVars();
        double[][] limits = Gauss.getLimits();
        algorithm = new Algorithm(limits, indexesExpressedVars, basisElement.size(), function);
        createSimplex(algorithm.getSimplex());

        return true;
      }
      if (noEndDirectGauss) {
        makeGaussGirectStep();
        return true;
      }
      if (noEndReversGauss) {
        //TODO: можно подсвечивать единичный минор
        makeGaussReversStep();
        return true;
      }
    }

    if (algorithm == null) {
      return false;
    }
    if (end == End.FAILURE) {
      printError(Error.NOT_LIMITED);
      return false;
    }
    if (end == End.SUCCESS_ALL) {
      printAnswer();
      return false;
    }
    if (end == End.SUCCESS_ART_BASIS) {
      algorithm.setStage(Stage.SIMPLEX);
      step = 0;

      algorithm.recountLastRow();

      createSimplex(algorithm.getSimplex());
      end = algorithm.getSimplex().end();
      return true;
    }

    end = algorithm.makeStep();
    createSimplex(algorithm.getSimplex());

    return true;
  }

  public void onClickBack(ActionEvent event) {
    if (algorithm == null) {
      return;
    }

    ObservableList<Node> rowsSimplexes = simplexesVBox.getChildren();
    if (rowsSimplexes == null || rowsSimplexes.size() == 0) {
      return;
    }

    HBox lastRow = null;
    try {
      lastRow = (HBox) rowsSimplexes.get(rowsSimplexes.size() - 1);
    } catch (ClassCastException e) {
      //удалить вывод ошибки или ответа вместе с 2 разделителями
      for (int i = 0; i < 3; i++) {
        rowsSimplexes.remove(rowsSimplexes.size() - 1);
      }
    }
    if (lastRow == null) {
      return;
    }

    ObservableList<Node> simplexesInLastRow = lastRow.getChildren();
    if (simplexesInLastRow == null) {
      return;
    }

    if (simplexesInLastRow.size() == 1) {
      //если осталась одна строка в которой только первоначальный симплекс
      if (rowsSimplexes.size() == 1) {
        return;
      }
      //удаляем последнию строку, в которой один симплекс, и разделитель перед ней
      rowsSimplexes.remove(rowsSimplexes.size() - 1);
      rowsSimplexes.remove(rowsSimplexes.size() - 1);
      //и текущей стркое приваиваем предыдущую
      simplexesRow = (HBox) rowsSimplexes.get(rowsSimplexes.size() - 1);
    } else {
      simplexesInLastRow.remove(simplexesInLastRow.size() - 1);
    }

    algorithm.backStep();
    step--;
    //если вернулись к искуственному симплексу
    if (step == -1) {
      step = algorithm.getSimplex().getStep() + 1;
    }
    stage = algorithm.getStage();

    if (stage == Stage.ART_BASIS) {
      end = algorithm.getSimplex().endArtBasis();
      return;
    }

    if (stage == Stage.SIMPLEX) {
      end = algorithm.getSimplex().end();
      return;
    }

    end = algorithm.getSimplex().endArtBasis();
    if (end == End.SUCCESS_ART_BASIS) {
      return;
    } else {
      end = algorithm.getSimplex().end();
    }
  }

  private void makeGaussGirectStep() {
    if (Gauss.isSwap() == null) {
      List<Integer> indexesExpressedVars = getIndexesExpressedVars();

      Gauss.swap(indexesExpressedVars);
      if (Gauss.isSwap()) {
        createMatrixPane();
        return;
      }
    }

    noEndDirectGauss = Gauss.makeDirectStep();

    createMatrixPane();
  }

  private void makeGaussReversStep() {
    noEndReversGauss = Gauss.makeReversStep();

    createMatrixPane();
  }

  private void createMatrixPane() {
    ArrayList<Object> dataTo = new ArrayList<>();
    dataTo.add(Gauss.getSystem());

    makeNewRow();
    MatrixView matrixView = new MatrixView((f) -> dataTo);
    matrixView.getViewAsync((simplexesRow.getChildren()::add));
  }

  public void changeStepByStep(boolean newValue) {
    if (newValue) {
      next.setDisable(false);
      back.setDisable(false);
    } else {
      next.setDisable(true);
      back.setDisable(true);
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
    step = 0;

    algorithm.recountLastRow();
    createSimplex(algorithm.getSimplex());
    end = algorithm.getSimplex().end();

    Thread.sleep(150);

    while (end == End.CONTINUE) {
      end = algorithm.makeStep();
      createSimplex(algorithm.getSimplex());

      Thread.sleep(150);
    }

    if (end == End.FAILURE) {
      printError(Error.NOT_LIMITED);
      return;
    }

    printAnswer();
  }

  private void startGauss() {
    stage = Stage.GAUSS;

    LinearSystem system = new LinearSystem(tableLimits);

    List<Integer> indexesExpressedVars = getIndexesExpressedVars();

    Gauss.setSystem(system);

    ArrayList<Object> dataTo = new ArrayList<>();
    dataTo.add(system);

    makeNewRow();
    MatrixView matrixView = new MatrixView((f) -> dataTo);
    matrixView.getViewAsync((simplexesRow.getChildren()::add));
  }

  private void goGauss() {
    startGauss();
    try {
      Thread.sleep(150);
      while (onClickNext()) {
        Thread.sleep(150);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private List<Integer> getIndexesExpressedVars() {
    List<Integer> indexesExpressedVars = new ArrayList<>();

    for (int i = 0; i < basisElement.size(); i++) {
      double value = basisElement.get(i);
      if (!isZero(value)) {
        indexesExpressedVars.add(i);
      }
    }

    return indexesExpressedVars;
  }


  //TODO:после гаусса создается симплекс с ПОДСВЕЧЕННОЙ красным последней строкой
  private void createSimplex(Simplex simplex) {
    if (simplex == null) {
      return;
    }
    ArrayList<Object> dataTo = new ArrayList<>();
    dataTo.add(step++);
    dataTo.add(simplex);

    makeNewRow();
    SimplexView simplexView = new SimplexView((f) -> dataTo);
    simplexView.getViewAsync(simplexesRow.getChildren()::add);
  }

  private void makeNewRow() {
    if (stepByStep.isSelected()) {
      double width = 0;
      int count = simplexesRow.getChildren().size();
      for (Node node : simplexesRow.getChildren()) {
        width += node.prefWidth(1);
      }

      //is it possible to add simplex in this row
      width += width / count; // добавляем среднюю ширину элементов в этой строке

      if (width > widthPane) {
        addSeparator();

        simplexesRow = new HBox();
        simplexesVBox.getChildren().add(simplexesRow);
      }
    } else {

    }

  }

  private void addSeparator() {
    Separator separator = new Separator();
    separator.setPrefWidth(widthPane + 50);
    separator.setOrientation(Orientation.HORIZONTAL);
    separator.setPadding(new Insets(10, 0, 5, 0));
    simplexesVBox.getChildren().add(separator);
  }

  private void clear() {
    for (Node node : simplexesVBox.getChildren()) {
      try {
        HBox hBox = (HBox) node;
        hBox.getChildren().clear();
      } catch (ClassCastException e) {
      }
    }

    simplexesVBox.getChildren().clear();
  }

  private void resetVars() {
    step = 0;
    end = End.CONTINUE;

    noEndDirectGauss = true;
    noEndReversGauss = true;
    Gauss.revertVars();
  }

  private void printError(Error error) {
    addSeparator();

    HBox hBox = new HBox();

    String text = error == Error.NOT_LIMITED ? "Not limited" : "Check input";

    Label label = new Label(text);
    label.setFont(new Font(40));

    hBox.getChildren().add(label);
    simplexesVBox.getChildren().add(hBox);

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
    Label pointLabel = new Label();
    
    List<Double> point = algorithm.getPointExtr();
    String str = "Point " + type + " = (";
    for (Double num : point) {
      str += String.format("%.2f; ", num);
    }
    str = str.substring(0, str.length() - 2) + ")";

    pointLabel.setText(str);
    pointLabel.setFont(font);
    pointLabel.setAlignment(Pos.CENTER);

    Label value = new Label();
    String functionValue = type == "min" ? String.format("%.4f", -algorithm.getFunctionExtr())
            : String.format("%.4f", algorithm.getFunctionExtr());
    str = "Function " + type + " value = " + functionValue;
    value.setText(str);
    value.setFont(font);
    value.setAlignment(Pos.CENTER);

    vBox.getChildren().add(pointLabel);
    vBox.getChildren().add(value);
    this.simplexesVBox.getChildren().add(vBox);

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

  @Subscribe
  public void receiveBasisElement(BasisElement basisElement) {
    System.out.println("get basisElement");
    this.basisElement = basisElement.getCoefs();
  }
}