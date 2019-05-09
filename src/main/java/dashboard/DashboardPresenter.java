package dashboard;

import com.google.common.eventbus.Subscribe;
import dashboard.input.InputView;
import dashboard.output.OutputView;
import events.MyEventBus;
import events.domain.Dimension;
import events.domain.FunctionDao;
import events.domain.TableLimits;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.Function;
import logic.enums.TypeProblem;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DashboardPresenter implements Initializable {

  public Pane input;
  public Pane output;

  @Inject
  private Stage primaryStage;

  private FileChooser fileChooser = new FileChooser();

  String[] saveToFile = null;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    List<Object> toInput = new ArrayList<>();
    toInput.add(null);
    toInput.add(null);

    InputView inputView = new InputView((f) -> toInput);
    inputView.getViewAsync(input.getChildren()::add);

    OutputView outputView = new OutputView();
    outputView.getViewAsync(output.getChildren()::add);

    MyEventBus.register(this);

    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
    fileChooser.getExtensionFilters().add(extFilter);

    initUserDirectory(fileChooser);
  }

  public void initUserDirectory(FileChooser fileChooser) {
    String userDirectoryString;
    userDirectoryString = System.getProperty("directory");

    if (userDirectoryString == null) {
      userDirectoryString = "C:\\";
      addNewProperty();
    }

    File userDirectory = new File(userDirectoryString);
    if (!userDirectory.canRead()) {
      userDirectory = new File("C:\\");
    }
    fileChooser.setInitialDirectory(userDirectory);
  }

  public void addNewProperty() {
    Properties properties = System.getProperties();

    properties.put("directory", "C:\\");

    System.setProperties(properties);
  }

  public void setUserDirectory(String filePath){
    String directory = filePath.substring(0, filePath.lastIndexOf("\\"));

    System.setProperty("directory", directory);

    File userDirectory = new File(directory);
    if (!userDirectory.canRead()) {
      userDirectory = new File("C:\\");
    }
    fileChooser.setInitialDirectory(userDirectory);
  }

  public void openFile(ActionEvent event) throws IOException {
    File file = fileChooser.showOpenDialog(primaryStage);
    if (file == null) {
      return;
    }

    //очищаем вывод
    output.getChildren().clear();
    OutputView outputView = new OutputView();
    outputView.getViewAsync(output.getChildren()::add);

    setUserDirectory(file.getPath());

    String[] inputData = Files.lines(Paths.get(file.getAbsolutePath())).toArray(String[]::new);

    //удалить пустые стркои
    List<String> strs = new ArrayList<>(Arrays.asList(inputData));
    strs.removeIf(s -> s.equals(""));
    String[] strings = strs.toArray(new String[strs.size()]);

    //убираем лишние пробелы
    //заменяем запятые на точки
    for (int i = 0; i < strings.length; i++) {
      String str = strings[i];

      str = str.replaceAll("  ", " ");
      if (str.substring(str.length() - 1).equals(" ")) {
        str = str.substring(0, str.length() - 1);
      }

      str = str.replaceAll(",", ".");

      strings[i] = str;
    }

    int countLimits = Integer.valueOf(strings[0].split("x")[0]);
    int countVar = Integer.valueOf(strings[0].split("x")[1]);

    //TODO: иногда почему-то не считывается функция
    String functionString = strings[1];
    Function function = readFunction(functionString, countVar);

    double[][] limits = readLimits(strings, countLimits, countVar);

    List<Object> toInput = new ArrayList<>();
    toInput.add(function);
    toInput.add(limits);
    input.getChildren().clear();
    InputView inputView = new InputView((f) -> toInput);
    inputView.getViewAsync(input.getChildren()::add);
  }

  public Function readFunction(String str, int countVar) {
    if (str == null || str.isEmpty()) {
      return null;
    }

    double[] coefs = new double[countVar];

    String coefsStr = str.split("->")[0];

    String type = str.split("->")[1];
    type = type.toLowerCase();

    TypeProblem typeProblem = type.contains("max") ? TypeProblem.MAX : TypeProblem.MIN;
    Function function = new Function(typeProblem);

    int i = 0;
    for (String s : coefsStr.split(" ")) {
      coefs[i++] = Double.valueOf(s);
    }

    function.setCoefficients(coefs);

    return function;
  }

  public double[][] readLimits(String[] strings, int countLimits, int countVar) {
    double[][] limits = new double[countLimits][countVar + 1];

    for (int i = 2; i < strings.length; i++) {
      String str = strings[i];

      int j = 0;
      for (String num : str.split(" ")) {
        limits[i - 2][j++] = Double.valueOf(num);
      }
    }

    return limits;
  }

  public void save(ActionEvent event) {
    if (saveToFile == null) {
      return;
    }

    File file = fileChooser.showSaveDialog(primaryStage);
    if (file == null) {
      return;
    }

    setUserDirectory(file.getPath());

    try {
      PrintWriter writer;
      writer = new PrintWriter(file);
      for (String s : saveToFile) {
        writer.println(s);
      }
      writer.close();
    } catch (IOException ignored) {
    }
  }

  @Subscribe
  public void receiveDimension(Dimension dimension) {
    int n = dimension.getN();
    int m = dimension.getM();

    saveToFile = new String[n + 2];

    saveToFile[0] = n + "x" + m;
  }

  @Subscribe
  public void receiveTableLimits(TableLimits tableLimits) {
    if (saveToFile == null) {
      return;
    }

    double[][] limits = tableLimits.getTable();
    for (int i = 0; i < limits.length; i++) {
      saveToFile[i + 2] = "";
      for (int j = 0; j < limits[i].length; j++) {
        saveToFile[i + 2] += String.format("%.2f", limits[i][j]) + " ";
      }
    }
  }

  @Subscribe
  public void receiveFunction(FunctionDao functionDao) {
    if (saveToFile == null) {
      return;
    }

    saveToFile[1] = "";

    double[] coefs = functionDao.getCoefs();
    for (double coef : coefs) {
      saveToFile[1] += String.format("%.2f", coef) + " ";
    }
    saveToFile[1] += "-> " + functionDao.getTypeProblem().toString().toLowerCase();
  }
}
