package dashboard;

import dashboard.input.InputView;
import dashboard.light.LightView;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javax.inject.Inject;

public class DashboardPresenter implements Initializable {

    public Pane input;

    @FXML
    Label message;

    @FXML
    Pane lightsBox;

    @Inject
    private String prefix;

    @Inject
    private String happyEnding;

    @Inject
    private LocalDate date;

    private String theVeryEnd;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //fetched from dashboard.properties
        this.theVeryEnd = rb.getString("theEnd");

        InputView inputView = new InputView();
        inputView.getViewAsync(input.getChildren()::add);
    }

    public void createLights() {
        for (int i = 0; i < 255; i++) {
            final int red = i;
            LightView view = new LightView((f) -> red);
            view.getViewAsync(lightsBox.getChildren()::add);
        }
    }

    public void launch() {
        message.setText("Date: " + date + " -> " + prefix + happyEnding + theVeryEnd
        );
    }
}
