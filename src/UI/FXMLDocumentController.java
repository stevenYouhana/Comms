
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
import Handler.MultiThread.TxRx;
import Handler.Popup;
import com.fazecast.jSerialComm.SerialPort;
import java.util.Collections;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
//            log.l("set output to "+txrx.output());
            delay = new Delay();
            delay.by(2000, () -> {
                synchronized(TxRx.outputLock) {
                    log.l("set output to "+txrx.output());
                    txtOutput.setText(txrx.output());
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
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        executor = Executors.newFixedThreadPool(9);

        // Runnable, return void, nothing, submit and run the task async
        executor.submit(() -> {
            log.l("first task (Runnable)");
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
        });
        
        Future<Void> atClose = executor.submit(() -> {
            log.l("atClose()");
            while (!Comms.MainWindowClosed) {
                
                synchronized(closingLock) {
                    log.l("wait()");
                    closingLock.wait();
                }
                
            }
            log.l("shuting all threads down");
            synchronized(closingLock) {
                if (serial != null) {
                    log.l("about to close serial");
                    if (serial.getPort() != null && serial.getPort().isOpen()) {
                        log.l("closing com port in atClose()");
                        serial.getPort().closePort();
                    }
                }
            }
            executor.shutdown();
            Platform.exit();
            return null;
        });
        
        log.l("Before Future Result");
        log.l("After Future Result");
        System.out.println("FXMLDocumentController initialize");
    }    
    
}
