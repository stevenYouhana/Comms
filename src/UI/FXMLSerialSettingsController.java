package UI;


import Com.Log;
import Com.Serial;
import Handler.Delay;
import Handler.Popup;
import Handler.Tasks.SerialSession;
import com.fazecast.jSerialComm.SerialPort;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FXMLSerialSettingsController implements Initializable {
    Log log = new Log();
    Delay delay = new Delay();
    Popup popup = new Popup();
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
            SerialSession.connect(
                    FXMLDocumentController.selectedPort, Integer.parseInt(
                    txtBRate.getText()), FXMLDocumentController.getInstance());
            Serial.comPort = SerialPort.getCommPort(FXMLDocumentController.selectedPort);
            if (!txtBRate.getText().isEmpty())
                Serial.comPort.setBaudRate(Integer.parseInt(txtBRate.getText()));
            if (!txtDBits.getText().isEmpty())
                Serial.comPort.setNumDataBits(Integer.parseInt(txtDBits.getText()));
            if (!txtSBits.getText().isEmpty())
                Serial.comPort.setNumStopBits(Integer.parseInt(txtSBits.getText()));
                Serial.comPort.setParity(parity(
                        cboParity.getSelectionModel().getSelectedItem().toString()));
                Serial.comPort.setFlowControl(flowControl(
                        cboFCtrl.getSelectionModel().getSelectedItem().toString()));
                    log.l("delay to close");
                    delay.by(50, () -> Platform.runLater( 
                            SerialSettings.getInstance()::hide));
        }
        catch (NumberFormatException nfe) {
            if (txtBRate.getText().isEmpty())
                FXMLDocumentController.Holder.noFieldSelection(txtBRate);
            log.l(Arrays.toString(nfe.getStackTrace()));
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
