package dashboard.input.table;

import com.airhacks.afterburner.views.FXMLView;
import javafx.scene.Parent;

import java.util.function.Function;

public class TableView extends FXMLView {
  public TableView(Function<String, Object> injectionContext) {
    super(injectionContext);
  }

  @Override
  public Parent getView() {
    return super.getView(); //To change body of generated methods, choose Tools | Templates.
  }

}
