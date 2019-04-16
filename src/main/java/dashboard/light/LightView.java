package dashboard.light;

import com.airhacks.afterburner.views.FXMLView;
import java.util.function.Function;
import javafx.scene.Parent;

public class LightView extends FXMLView {

    public LightView(Function<String, Object> injectionContext) {
        super(injectionContext);
    }

    @Override
    public Parent getView() {
        return super.getView(); //To change body of generated methods, choose Tools | Templates.
    }

}
