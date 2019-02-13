
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
import Handler.MultiThread.TaskManager;
import Handler.MultiThread.TxRx;
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

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
/**
 *
 * @author steven.youhana
 */
public class FXMLDocumentController implements Initializable {
    public volatile static Serial serial = new Serial();
    String port = null;
    ObservableList<String> availablePorts = FXCollections.observableArrayList();
    Log log = new Log();
    Popup popup = new Popup();
    ExecutorService executor;
    Delay delay;
    TxRx txrx;
    
    public final static Object closingLock = new Object();
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
    private void push() throws InterruptedException {
        log.l("PUSH");
        selectedPort = cboComs.getSelectionModel().getSelectedItem();
        try {
            log.l("COM: "+selectedPort);
            log.l("BAUD: "+txtBaud.getText());
            if (selectedPort == null || selectedPort.isEmpty() || selectedPort.equals("choose..")) {
                popup.infoAlert("Port error!", "Select a port");
                return;
            }
            if (txtBaud.getText().isEmpty()) {
                popup.infoAlert("Baudrate error!", "Set baudrate");
                return;
            }
            txrx = new TxRx(new Serial(selectedPort,
                  Integer.parseInt(txtBaud.getText())), txtCommand.getText());
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
            delay.by(2000, () -> {
                log.l("set output to "+txrx.output());
                    txtOutput.setText(txrx.output().toString());
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
    });
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
