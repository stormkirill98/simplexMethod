package dashboard.input;

import com.airhacks.afterburner.views.FXMLView;
import javafx.scene.Parent;

import java.util.function.Function;

public class InputView extends FXMLView {
  public InputView(Function<String, Object> injectionContext) {
    super(injectionContext);
  }

  @Override
  public Parent getView() {
    return super.getView(); //To change body of generated methods, choose Tools | Templates.
  }

}
