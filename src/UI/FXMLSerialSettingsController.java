package UI;


import Com.Log;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class FXMLSerialSettingsController implements Initializable {
    Log log = new Log();
    public FXMLSerialSettingsController() {}
    @FXML
    Button set;
    @FXML
    public void set() {
        log.l("set adv settings");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
}
