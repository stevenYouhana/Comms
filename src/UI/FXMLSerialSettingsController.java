package UI;


import Com.Log;
import Com.Serial;
import com.fazecast.jSerialComm.SerialPort;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FXMLSerialSettingsController implements Initializable {
    Log log = new Log();
//    SerialSettings view = new SerialSettings();
    public FXMLSerialSettingsController() {}
    private ArrayList<String> params = new ArrayList<>(5);
    @FXML
    Button set;
    @FXML
    TextField txtBaud;
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
        
       log.l("set adv settings");
        if (!txtBaud.getText().isEmpty())
            Serial.comPort.setBaudRate(Integer.parseInt(txtBaud.getText()));
        if (!txtDBits.getText().isEmpty())
            Serial.comPort.setNumDataBits(Integer.parseInt(txtDBits.getText()));
        if  (!txtSBits.getText().isEmpty())
            Serial.comPort.setNumStopBits(Integer.parseInt(txtSBits.getText()));
        Serial.comPort.setParity(Integer.parseInt(
                cboParity.getSelectionModel().getSelectedItem().toString()));
        Serial.comPort.setFlowControl(flowControl(
                cboFCtrl.getSelectionModel().getSelectedItem().toString()));
       SerialSettings.getInstance().hide();
    }
    int flowControl(String fc) {
        switch (fc) {
            case "XON/XOFF": return SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED;
            case "RTS/CTS": return SerialPort.FLOW_CONTROL_RTS_ENABLED;
            case "DSR/DTR": return SerialPort.FLOW_CONTROL_DSR_ENABLED;
            default: return 0;
        }
    }
    int parity(String par) {
        switch(par) {
            case "None": return SerialPort.NO_PARITY;
            case "Odd": return SerialPort.ODD_PARITY;
            case "Even": return SerialPort.EVEN_PARITY;
            case "Mark": return SerialPort.MARK_PARITY;
            case "Space": return SerialPort.SPACE_PARITY;
            default: return 0;
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
}
