package dashboard;

import dashboard.input.InputView;
import dashboard.output.OutputView;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import logic.Function;
import logic.enums.TypeProblem;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardPresenter implements Initializable {

  public Pane input;
  public Pane output;

  @Inject
  private Stage primaryStage;

  private FileChooser fileChooser = new FileChooser();

  private Function function = null;
  private double[][] limits = null;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    List<Object> toInput = new ArrayList<>();
    toInput.add(null);
    toInput.add(null);

    InputView inputView = new InputView((f) -> toInput);
    inputView.getViewAsync(input.getChildren()::add);

    OutputView outputView = new OutputView();
    outputView.getViewAsync(output.getChildren()::add);
  }

  public void openFile(ActionEvent event) throws IOException {
    File file = fileChooser.showOpenDialog(primaryStage);
    if (file == null){
      return;
    }

    String[] strings = Files.lines(Paths.get(file.getAbsolutePath())).toArray(String[]::new);
    //убираем лишние пробелы
    for (int i = 0; i < strings.length; i++) {
      String str = strings[i];
      //TODO: удалять пустые строки

      str = str.replaceAll("  ", " ");
      if (str.substring(str.length() - 1).equals(" ")){
        str = str.substring(0, str.length() - 1);
      }

      strings[i] = str;
    }

    int countLimits = Integer.valueOf(strings[0].split("x")[0]);
    int countVar = Integer.valueOf(strings[0].split("x")[1]);

    String functionString = strings[1];
    this.function = readFunction(functionString, countVar);

    this.limits = readLimits(strings, countLimits, countVar);

    List<Object> toInput = new ArrayList<>();
    toInput.add(function);
    toInput.add(limits);
    input.getChildren().clear();
    InputView inputView = new InputView((f) -> toInput);
    inputView.getViewAsync(input.getChildren()::add);
  }

  public Function readFunction(String str, int countVar){
    if (str == null || str.isEmpty()){
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

  public double[][] readLimits(String[] strings, int countLimits,  int countVar){
    double[][] limits = new double[countLimits][countVar + 1];

    for (int i = 2; i < strings.length; i++){
      String str = strings[i];

      int j = 0;
      for (String num : str.split(" ")) {
        limits[i - 2][j++] = Double.valueOf(num);
      }
    }

    return limits;
  }

  public void save(ActionEvent event) {

  }
}
