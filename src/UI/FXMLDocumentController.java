
package UI;

import Com.Commands;
import Com.Log;
import Com.Read;
import Com.Refresh;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import Com.Serial;
import Handler.Delay;
import Handler.Popup;
import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
/**
 *
 * @author steven.youhana
 */
public class FXMLDocumentController implements Initializable {
    Serial serial;
    String port = null;
    ObservableList<String> availablePorts = FXCollections.observableArrayList();
    Log log = new Log();
    Popup popup;
    
    @FXML
    private ComboBox<String> cboComs;
    
    @FXML
    private Label lblPort;
    
    @FXML
    private TextField txtBaud;
    
    @FXML
    private TextField txtCommand;
    @FXML
    private TextField txtOutput;
    
    @FXML
    private void onAction() {
        System.out.println("ON> "+serial.TX(Commands.ONKYO_ON));
        lblPort.setText("ON");
    }
    @FXML
    private void offAction() {
        System.out.println("OFF> "+serial.TX(Commands.ONKYO_OFF));
        lblPort.setText("OFF");
    }
    @FXML
    private void push() {
        log.l("PUSH");
        String selectedPort = cboComs.getSelectionModel().getSelectedItem();
        try {
            log.l("COM: "+selectedPort);
            log.l("BAUD: "+txtBaud.getText());
            if (selectedPort == null || selectedPort.isEmpty() || selectedPort.equals("choose..")) {
                log.l("NO PORT SELECTED");
                popup.infoAlert("Port error!", "Select a port");
                return;
            }
            if (txtBaud.getText().isEmpty()) {
                log.l("NO BAUD SET");
                popup.infoAlert("Baudrate error!", "Set baudrate");
                return;
            }
            serial = new Serial(selectedPort,
                    Integer.parseInt(txtBaud.getText()));
            serial.pushCommand(txtCommand.getText());
        } 
        catch (NumberFormatException nfe) {
            log.l("push error nfe: "+nfe.getStackTrace());
            popup.infoAlert("Baudrate error!", "integer expected");
        }
        catch(Exception e) {
            log.l("push error: "+e.getStackTrace()+e.getCause());
            popup.infoAlert("Error!", e.getStackTrace().toString());
        }
        finally {
            serial.closePort(3);
            log.l("finally reached");
            log.l(serial.TX(txtCommand.getText()).toString());
            
        }
    }
    public void populatePorts() {
        for (SerialPort port : serial.getAvailabePorts()) {
            availablePorts.add(port.getSystemPortName());
        }
        Collections.reverse(availablePorts);
        cboComs.setItems(availablePorts);
    }
    public void clearPorts() {
        Platform.runLater(() -> {
            availablePorts.clear();
        });
        
    }
    void refreshPorts() {
//        log.l("refreshPorts()");
        Refresh refresh = new Refresh();
        Delay delay = new Delay();
        delay.by(1000, () -> {
            refresh.start();
        });
//        refresh.start();
        if (refresh.getNewPorts() != null) {
            if (cboComs.getItems().size() != refresh.getNewPorts().length) {
                Platform.runLater(() -> {
                    log.l(refresh.getNewPorts()[0].getPortDescription());
                    clearPorts();
                    for (SerialPort port : refresh.getNewPorts()) {
                        availablePorts.add(port.getSystemPortName());
                    }
                });
            }
            cboComs.setItems(availablePorts);
            
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        serial = new Serial();
//        popup = new Popup();
        serial = null;
        class Manage {
            private Serial serial;
            Manage(Serial serial) {
                this.serial = serial;
            }
            public boolean isInit() {
                log.l(this.serial != null);
                log.l(this.serial.getPort() != null);
                return this.serial != null && this.serial.getPort() != null 
                        ?true :false; 
            }
        }
       
        
        new Thread(() -> {
            while (!Comms.MainWindowClosed) {
                refreshPorts();
            }     
        }).start();

        System.out.println("FXMLDocumentController initialize");
        serial = null;
    }    
    
}
