package dashboard;

import dashboard.input.InputView;
import dashboard.output.OutputView;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardPresenter implements Initializable {

  public Pane input;
  public Pane output;

  private String theVeryEnd;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    this.theVeryEnd = rb.getString("theEnd");

    InputView inputView = new InputView();
    inputView.getViewAsync(input.getChildren()::add);

    OutputView outputView = new OutputView();
    outputView.getViewAsync(output.getChildren()::add);
  }
}
