
package UI;

import Com.Commands;
import Com.Log;
import Com.Refresh;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import Com.Serial;
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
//    @FXML
//    private TextField txtOutput;
//    
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
        } 
        catch (NumberFormatException nfe) {
            log.l("push error nfe: "+nfe.getStackTrace());
            popup.infoAlert("Baudrate error!", "integer expected");
        }
        catch(Exception e) {
            log.l("push error: "+e.getStackTrace()+e.getCause());
            popup.infoAlert("Error!", e.getStackTrace().toString());
        }
            log.l("finally reached");
            log.l(serial.TX(txtCommand.getText()).toString());
            serial.pushCommand(txtCommand.getText());
        
        log.l("PUSH finished");
        
    }
    private String getOutput() {
        String string = null;
        
        
        return string;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        serial = new Serial();
        popup = new Popup();
        ObservableList<String> availablePorts = FXCollections.observableArrayList();
        for (SerialPort port : serial.getAvailabePorts()) {
            availablePorts.add(port.getSystemPortName());
        }
        Collections.reverse(availablePorts);
        cboComs.setItems(availablePorts);
       
        
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Refresh refresh = new Refresh();
            
            //TESTING
            serial = new Serial();
            serial.setComPort("COM6");
            serial.getPort().setBaudRate(9600);
            serial.getPort().openPort();
            if (serial.getPort() != null) {
                while (!Comms.MainWindowClosed) {
                
                    serial.getPort().writeBytes("HELLO PORT".getBytes(), 100);

    //TEST READER
    //                if (serial.getPort() != null && serial.getPort().isOpen()) {
    //                    log.l("reading:");
    //                    log.l(serial.getOutput());
    //                }
                    InputStream in = serial.getPort().getInputStream();
                    try
                    {
                       for (int j = 0; j < 1000; ++j)
                    //       System.out.println("getIS");
                           System.out.print((char)in.read());
                       in.close();
                    } catch (Exception e) { e.printStackTrace(); }

                    log.l("start innit Thread(())");
                    refresh.start();
                    if (refresh.getNewPorts() != null) {
                        if (cboComs.getItems().size() != refresh.getNewPorts().length) {
                            Platform.runLater(() -> {
                                availablePorts.clear();
                                for (SerialPort port : refresh.getNewPorts()) {
                                    availablePorts.add(port.getSystemPortName());
                                }
                            });
                    }
                cboComs.setItems(availablePorts);
                }
            }
        } else log.l("port NULL");
            log.l("main Thread: "+Comms.getCurrentThread().isAlive());
            
        }).start();
        System.out.println("FXMLDocumentController initialize");
        
        serial = null;
    }    
    
}
