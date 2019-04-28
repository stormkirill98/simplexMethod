package dashboard;

import dashboard.input.InputView;
import dashboard.output.OutputView;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import logic.Algorithm;
import logic.Coefficient;
import logic.Function;
import logic.Limit;
import logic.enums.TypeProblem;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardPresenter implements Initializable {

  public Pane input;
  public Pane output;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    InputView inputView = new InputView();
    inputView.getViewAsync(input.getChildren()::add);

    OutputView outputView = new OutputView();
    outputView.getViewAsync(output.getChildren()::add);

    Algorithm algorithm = new Algorithm();

    //FAILURE
    /*Function function = new Function(TypeProblem.MIN);
    function.addCoefficient(new Coefficient( -1.0, 1));
    function.addCoefficient(new Coefficient( -10.0, 2));
    function.addCoefficient(new Coefficient( 1.0, 3));
    function.addCoefficient(new Coefficient( -5.0, 4));
    //function.addCoefficient(new Coefficient( 3.0, 5));
    algorithm.setFunction(function);

    Limit limit = new Limit();
    limit.addCoefficient(new Coefficient( 1.0, 1));
    limit.addCoefficient(new Coefficient( 2.0, 2));
    limit.addCoefficient(new Coefficient( -1.0, 3));
    limit.addCoefficient(new Coefficient( -1.0, 4));
    //limit.addCoefficient(new Coefficient( 2.0, 5));
    limit.addCoefficient(new Coefficient( 1.0, -1));
    algorithm.addLimit(limit);

    limit = new Limit();
    limit.addCoefficient(new Coefficient( -1.0, 1));
    limit.addCoefficient(new Coefficient( 2.0, 2));
    limit.addCoefficient(new Coefficient( 3.0, 3));
    limit.addCoefficient(new Coefficient( 1.0, 4));
    //limit.addCoefficient(new Coefficient( 0.0, 5));
    limit.addCoefficient(new Coefficient( 2.0, -1));
    algorithm.addLimit(limit);

    limit = new Limit();
    limit.addCoefficient(new Coefficient( 1.0, 1));
    limit.addCoefficient(new Coefficient( 5.0, 2));
    limit.addCoefficient(new Coefficient( 1.0, 3));
    limit.addCoefficient(new Coefficient( -1.0, 4));
    //limit.addCoefficient(new Coefficient( 0.0, 5));
    limit.addCoefficient(new Coefficient( 5.0, -1));
    algorithm.addLimit(limit);*/

    /*//SUCCESS
    Function function = new Function(TypeProblem.MIN);
    function.addCoefficient(new Coefficient( -1.0, 1));
    function.addCoefficient(new Coefficient( -4.0, 2));
    function.addCoefficient(new Coefficient( -5.0, 3));
    function.addCoefficient(new Coefficient( -9.0, 4));
    function.addCoefficient(new Coefficient( 3.0, 5));
    algorithm.setFunction(function);

    Limit limit = new Limit();
    limit.addCoefficient(new Coefficient( -1.0, 1));
    limit.addCoefficient(new Coefficient( 1.0, 2));
    limit.addCoefficient(new Coefficient( 0.0, 3));
    limit.addCoefficient(new Coefficient( 1.0, 4));
    limit.addCoefficient(new Coefficient( 2.0, 5));
    limit.addCoefficient(new Coefficient( 1.0, -1));
    algorithm.addLimit(limit);

    limit = new Limit();
    limit.addCoefficient(new Coefficient( 1.0, 1));
    limit.addCoefficient(new Coefficient( 1.0, 2));
    limit.addCoefficient(new Coefficient( 2.0, 3));
    limit.addCoefficient(new Coefficient( 3.0, 4));
    limit.addCoefficient(new Coefficient( 0.0, 5));
    limit.addCoefficient(new Coefficient( 5.0, -1));
    algorithm.addLimit(limit);*/

    //SUCCESS
    Function function = new Function(TypeProblem.MIN);
    function.addCoefficient(new Coefficient( -1.0, 1));
    function.addCoefficient(new Coefficient( -1.0, 2));
    function.addCoefficient(new Coefficient( -1.0, 3));
    function.addCoefficient(new Coefficient( -1.0, 4));
    algorithm.setFunction(function);

    Limit limit = new Limit();
    limit.addCoefficient(new Coefficient( 1.0, 1));
    limit.addCoefficient(new Coefficient( 3.0, 2));
    limit.addCoefficient(new Coefficient( 7.0, 3));
    limit.addCoefficient(new Coefficient( -1.0, 4));
    limit.addCoefficient(new Coefficient( 6.0, -1));
    algorithm.addLimit(limit);

    limit = new Limit();
    limit.addCoefficient(new Coefficient( 1.0, 1));
    limit.addCoefficient(new Coefficient( -1.0, 2));
    limit.addCoefficient(new Coefficient( -1.0, 3));
    limit.addCoefficient(new Coefficient( 3.0, 4));
    limit.addCoefficient(new Coefficient( 2.0, -1));
    algorithm.addLimit(limit);

    System.out.println(algorithm + "\n");

    algorithm.searchStartVector();

    System.out.println(algorithm + "\n");
  }
}
