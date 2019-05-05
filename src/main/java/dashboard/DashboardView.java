package dashboard;

import com.airhacks.afterburner.views.FXMLView;
import javafx.scene.Parent;

import java.util.function.Function;

public class DashboardView extends FXMLView {
  public DashboardView(Function<String, Object> injectionContext) {
    super(injectionContext);
  }

  @Override
  public Parent getView() {
    return super.getView();
  }
}
