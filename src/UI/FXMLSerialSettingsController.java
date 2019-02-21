package UI;


import Com.Log;
import Com.Serial;
import Handler.Popup;
import com.fazecast.jSerialComm.SerialPort;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class FXMLSerialSettingsController implements Initializable {
    Log log = new Log();
    Popup popup = new Popup();
    private static final FXMLDocumentController MAIN_CTRL = new FXMLDocumentController();
    public FXMLSerialSettingsController() {}
    @FXML
    Button set;
    @FXML
    TextField txtBRate;
    @FXML
    TextField txtDBits;
    @FXML
    TextField txtSBits;
    @FXML
    ComboBox cboParity;
    @FXML
    ComboBox cboFCtrl;
    @FXML
    public void set() {
        try {
            log.l("SEL P: "+MAIN_CTRL.selectedPort);
            Serial.comPort = SerialPort.getCommPort(MAIN_CTRL.selectedPort);
             if (!txtBRate.getText().isEmpty())
                 Serial.comPort.setBaudRate(Integer.parseInt(txtBRate.getText()));
             if (!txtDBits.getText().isEmpty())
                 Serial.comPort.setNumDataBits(Integer.parseInt(txtDBits.getText()));
             if  (!txtSBits.getText().isEmpty())
                 Serial.comPort.setNumStopBits(Integer.parseInt(txtSBits.getText()));
             Serial.comPort.setParity(Integer.parseInt(
                     cboParity.getSelectionModel().getSelectedItem().toString()));
             Serial.comPort.setFlowControl(flowControl(
                     cboFCtrl.getSelectionModel().getSelectedItem().toString()));

            SerialSettings.getInstance().hide();
            MAIN_CTRL.connect();
        }
       catch (NumberFormatException nfe) {
           if (txtBRate.getText().isEmpty()) 
               txtBRate.setStyle("-fx-control-inner-background: #f2d91f;");
           popup.infoAlert("Incorrect entries", Arrays.toString(nfe.getStackTrace()));
       }
       catch (Exception e) {
           popup.errorMessage(Popup.SYSTEM_ERROR, e.toString());
       }
    }
    int flowControl(String fc) {
        switch (fc) {
            case "XON/XOFF": return SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED |
                    SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
            case "RTS/CTS": return SerialPort.FLOW_CONTROL_RTS_ENABLED | 
                    SerialPort.FLOW_CONTROL_CTS_ENABLED;
            case "DSR/DTR": return SerialPort.FLOW_CONTROL_DSR_ENABLED |
                    SerialPort.FLOW_CONTROL_DTR_ENABLED;
            default: return 0;
        }
    }
    int parity(String par) {
        switch (par) {
            case "Odd": return SerialPort.ODD_PARITY;
            case "Even": return SerialPort.EVEN_PARITY;
            case "Mark": return SerialPort.MARK_PARITY;
            case "Space": return SerialPort.SPACE_PARITY;
            default: return 0;
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cboFCtrl.getItems().addAll("None", "XON/XOFF", "RTS/CTS", "DSR/DTR");
        cboParity.getItems().addAll("None", "Odd", "Even", "Mark", "Space");
        cboFCtrl.getSelectionModel().selectFirst();
        cboParity.getSelectionModel().selectFirst();
    }
}
