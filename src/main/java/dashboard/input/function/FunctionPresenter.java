package dashboard.input.function;

import events.MyEventBus;
import events.domain.FunctionDao;
import events.domain.TableLimits;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import logic.enums.TypeProblem;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static logic.Utilit.*;

public class FunctionPresenter implements Initializable {
  final private int cellWidth = 40;
  final private int cellHeight = 35;


  public HBox box;
  public ScrollPane scrollPane;

  @Inject
  private int n;

  private double[] funcCoefs;
  private TypeProblem typeProblem = TypeProblem.MIN;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
    double width = screen.getWidth();
    scrollPane.setPrefViewportWidth(0.365 * width);
    scrollPane.setPrefViewportHeight(cellHeight);

    funcCoefs = new double[n];

    createFunctionPane();
    isFilled(box);
  }

  private void createFunctionPane(){
    for (int i = 0; i < n; i++){
      String labelName = "x" + subscript(String.valueOf(i + 1)) + "+";
      if (i == 0 || i == n -1)
        labelName = "x" + subscript(String.valueOf(i + 1));

      Label label = new Label(labelName);

      label.setAlignment(Pos.CENTER);
      label.setPrefHeight(cellHeight);

      TextField textField = new TextField();

      int finalI = i;
      textField.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!validateDouble(textField, oldValue, newValue)){
          return;
        }

        Double value = strToDouble(newValue);
        funcCoefs[finalI] = value;

        if (isFilled(box)){
          MyEventBus.post(new FunctionDao(funcCoefs, typeProblem));
        }
      });

      textField.setPadding(new Insets(5));
      textField.setPrefWidth(cellWidth);
      textField.setPrefHeight(cellHeight);

      box.getChildren().add(textField);
      box.getChildren().add(label);
    }

    Label label = new Label("->");
    label.setAlignment(Pos.CENTER);
    label.setPrefHeight(cellHeight);
    box.getChildren().add(label);

    ToggleButton toggleButton = new ToggleButton();
    toggleButton.setText("min");
    toggleButton.setSelected(false);
    toggleButton.setPrefWidth(cellWidth + 10);
    toggleButton.setPrefHeight(cellHeight);
    toggleButton.setAlignment(Pos.CENTER);
    toggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue){
          typeProblem = TypeProblem.MAX;
          toggleButton.setText("max");
        } else {
          typeProblem = TypeProblem.MIN;
          toggleButton.setText("min");
        }

        if (isFilled(box)){
          MyEventBus.post(new FunctionDao(funcCoefs, typeProblem));
        }
      }
    });
    box.getChildren().add(toggleButton);
  }


}
