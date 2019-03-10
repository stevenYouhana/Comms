package UI;


import Com.Log;
import Com.Serial;
import Handler.Delay;
import Handler.Popup;
import Handler.Tasks.Connectable;
import Handler.Tasks.SerialSession;
import Handler.Tasks.SerialTask;
import Handler.Tasks.TaskManager;
import static UI.FXMLDocumentController.selectedPort;
import com.fazecast.jSerialComm.SerialPort;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FXMLSerialSettingsController extends FXMLDocumentController
        implements Initializable, Connectable {
    Log log = new Log();
    Delay delay = new Delay();
    Popup popup = new Popup();
    private final SerialTask.Advanced_Setting adv_set = new SerialTask.Advanced_Setting();
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
    
    
    public FXMLSerialSettingsController() {}
    
    @Override
    public void session() {
//        if (txtBRate.getText().isEmpty())
////            return;
//        if (txtDBits.getText().isEmpty())
////            return;
//        if (txtSBits.getText().isEmpty())
////            return;
        int baud = Integer.parseInt(txtBRate.getText());
        srTask = new SerialTask(selectedPort, baud);
        
        adv_set.setAdvancedVariables(baud, 
                Integer.parseInt(txtDBits.getText()), 
                Integer.parseInt(txtSBits.getText()),
                parity(cboParity.getSelectionModel().getSelectedItem().toString()),
                flowControl(
                        cboFCtrl.getSelectionModel().getSelectedItem().toString())
        );
//        if (!srl_tasks.isTerminated()) {
//            TaskManager.stop(srl_tasks);
//            log.l("connect: if (!srl_tasks.isTerminated()) ");
//            srl_tasks = Executors.newFixedThreadPool(3);
//        }
//        srl_tasks.submit(() -> {
//            log.l("ADV TEST: "+srTask.toString());
//            srTask.session();
//        });
//        srl_tasks.submit(() -> Mediator.run());
    }
    public void set() {
        log.l("Set for "+FXMLDocumentController.selectedPort);
        try {
                this.session();
                log.l("delay to close");
                delay.by(50, () -> Platform.runLater( 
                        SerialSettings.getInstance()::hide));
          }
        catch (NumberFormatException nfe) {
            if (txtBRate.getText().isEmpty())
                FXMLDocumentController.Holder.noFieldSelection(txtBRate);
            nfe.printStackTrace();
            popup.infoAlert("Incorrect entries", Arrays.toString(nfe.getStackTrace()));
        }
        catch (Exception e) {
            e.printStackTrace();
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
