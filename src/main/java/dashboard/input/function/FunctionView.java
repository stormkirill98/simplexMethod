package dashboard.input.function;

import com.airhacks.afterburner.views.FXMLView;
import javafx.scene.Parent;

import java.util.function.Function;

public class FunctionView extends FXMLView {
  public FunctionView(Function<String, Object> injectionContext) {
    super(injectionContext);
  }

  @Override
  public Parent getView() {
    return super.getView();
  }
}
