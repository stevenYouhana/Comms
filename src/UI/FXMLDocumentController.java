
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
import Handler.Tasks.TaskManager;
import Handler.Tasks.TxRx;
import Handler.Popup;
import com.fazecast.jSerialComm.SerialPort;
import java.util.Collections;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.DepthTest;
import javafx.scene.control.Button;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
/**
 *
 * @author steven.youhana
 */
public class FXMLDocumentController implements Initializable {
    public static Serial serial;
    String port;
    ObservableList<String> availablePorts = FXCollections.observableArrayList();
    Log log = new Log();
    Popup popup = new Popup();
    ExecutorService executor;
    Delay delay;
    TxRx txrx;
    private final String BTN_CONNECT = "Connect";
    private final String BTN_DISCONNECT = "Disconnet";
    volatile String selectedPort = null;
    
    @FXML
    private ComboBox<String> cboComs;
    @FXML
    private Label lblPort;
    @FXML
    private TextField txtBaud;
    @FXML
    private TextField txtCommand;
    @FXML
    private TextArea txtOutput;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnPush;
    @FXML
    
    public void connect() {
        if (btnConnect.getText().equals(BTN_CONNECT)) {
            log.l("connect");
            selectedPort = cboComs.getSelectionModel().getSelectedItem().toString();
            try {
                log.l("COM: "+selectedPort);
                log.l("BAUD: "+txtBaud.getText());
                if (selectedPort == null 
                        || selectedPort.isEmpty() 
                        || selectedPort.equals("choose..")) {
                    popup.infoAlert("Port error!", "Select a port");
                    return;
                }
                if (txtBaud.getText().isEmpty()) {
                    popup.infoAlert("Baudrate error!", "Set baudrate");
                    return;
                }
            }
            catch (NumberFormatException nfe) {
                log.l("push error nfe: "+nfe.getStackTrace());
                popup.infoAlert("Baudrate error!", "Integer expected");
            }
            catch(Exception e) {
                log.l("push error: "+e.getStackTrace()+e.getCause());
                popup.infoAlert("Error!", e.getStackTrace().toString());
            }
            serial = new Serial(selectedPort, Integer.parseInt(txtBaud.getText()));
            btnConnect.setText(BTN_DISCONNECT);
            txtBaud.setDisable(true);
            cboComs.setDisable(true);
            txtCommand.setDisable(false);
            btnPush.setDisable(false);
        }
        else {
            Serial.comPort.closePort();
            btnConnect.setText(BTN_CONNECT);
            txtBaud.setDisable(false);
            cboComs.setDisable(false);
            txtCommand.setDisable(true);
            btnPush.setDisable(true);
        }
    }
    
    @FXML
    private void push() throws InterruptedException {
        log.l("PUSH");
        try {
            txrx = new TxRx(serial, txtCommand.getText());
            txrx.pushAndRead();
        }
        catch (NumberFormatException nfe) {
            log.l("push error nfe: "+nfe.getStackTrace());
            popup.infoAlert("Baudrate error!", "Integer expected");
        }
        catch(Exception e) {
            log.l("push error: "+e.getStackTrace()+e.getCause());
            popup.infoAlert("Error!", e.getStackTrace().toString());
        }
        finally {
            delay = new Delay();
            delay.by(1000, () -> {
                try {
                log.l("set output to "+txrx.output());
                    txtOutput.setText(txrx.output().toString());
                } finally {
                    return null;
                }
            });
        }
    }
    private void refreshPorts() {
        clearPorts();
        for (SerialPort port : SerialPort.getCommPorts()) {
          Platform.runLater(() -> {
              availablePorts.add(port.getSystemPortName());
          });
            Serial.availablePorts = SerialPort.getCommPorts();
        }
        Platform.runLater(() -> {
            Collections.reverse(availablePorts); 
        });
        cboComs.setItems(availablePorts);
    }
    private void clearPorts() {
        Platform.runLater(() -> {
            availablePorts.clear();
        });
    }
    public TxRx getTxRx() {
        return txrx;
    }
    public ExecutorService getExecutor() {
        return executor;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ExecutorService executor = Executors.newSingleThreadExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    return t;
                }
            }
        );
        txtCommand.setDisable(true);
        btnPush.setDisable(true);
        
        executor.submit(() -> {
            while (!Comms.MainWindowClosed) {
                if (availablePorts.size() <= 0) {
                    refreshPorts();
                }
                else if (Serial.availablePorts.length != 
                        SerialPort.getCommPorts().length) {
                    log.l("refreshing ports");
                    refreshPorts();
                }
            }
            TaskManager.stop(executor);
        });
        System.out.println("FXMLDocumentController initialize");
    }    
}
