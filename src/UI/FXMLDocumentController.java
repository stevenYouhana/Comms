
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
import Handler.MultiThread.ATask;
import Handler.MultiThread.PerformTask;
import Handler.MultiThread.TaskManager;
import Handler.Popup;
import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
/**
 *
 * @author steven.youhana
 */
public class FXMLDocumentController implements Initializable {
    Serial serial = new Serial();
    String port = null;
    ObservableList<String> availablePorts = FXCollections.observableArrayList();
    Log log = new Log();
    Popup popup = new Popup();
    public final static Object closingLock = new Object();
    
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
    private void setParams() {
        log.l("setParams()");
        Delay delay = new Delay();
        synchronized(serial){
            serial = new Serial(() -> {
            if (cboComs.getSelectionModel().getSelectedIndex() != -1) {
                String selectedPort = cboComs.getSelectionModel().getSelectedItem();
                serial = new Serial(selectedPort);
            }
            if (!txtBaud.getText().isEmpty()) {
                try {
                    serial.setBaudRate(Integer.parseInt(txtBaud.getText()));
                    popup.infoAlert("Info!", "Success");
                    delay.by(1000, () -> {
                        log.l("closing");
                        Platform.runLater( () -> {
                            popup.close();
                        });
                    });
                } catch (NumberFormatException nfe) {
                    popup.infoAlert("Baudrate error!", "Integer expected!");
                }
                
            }
            if (serial.getPort() != null) {
                serial.getPort().openPort();
//                serial.getPort().setComPortTimeouts(
//                        SerialPort.TIMEOUT_NONBLOCKING,
//                        20000, 20000);
            }
        });

        }
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
            synchronized(serial) {
                serial = new Serial(selectedPort,
                    Integer.parseInt(txtBaud.getText()));
            serial.pushCommand(txtCommand.getText());
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
        finally {
            if (serial != null) {
                serial.closePort(30000);
                
            log.l("finally reached");
            log.l(serial.TX(txtCommand.getText()).toString());
            
            } else return;
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
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Runnable, return void, nothing, submit and run the task async
        executor.submit(() -> {
            log.l("first task (Runnable)");
//            serial = new Serial();
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

        
        Future<Integer> futureTask1 = executor.submit(() -> {
            System.out.println("Callable READ:");
            Read read;
            while (!Comms.MainWindowClosed) { 
                if (serial != null) {
//                    log.l("serial INIT");
                    if (serial.getPort() != null) {
                        log.l("reading...");
                        read = new Read(serial.getPort());
                        log.l(read.output());
                    }
                }
            }
            return 1 + 1;
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
            futureTask1.cancel(true);
            executor.shutdown();
            Platform.exit();
            return null;
        });
        log.l("Before Future Result");
        log.l("After Future Result");
        System.out.println("FXMLDocumentController initialize");
//        serial = null;
    }    
    
}
