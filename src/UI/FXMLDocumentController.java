
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
import Handler.Tasks.SerialSession;
import Handler.Popup;
import com.fazecast.jSerialComm.SerialPort;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
    SerialSession serialSession;
    private final String BTN_CONNECT = "Connect";
    private final String BTN_DISCONNECT = "Disconnet";
    volatile static String selectedPort = null;
    Stage stage;
    SerialSettings setting;
    @FXML
    private ComboBox<String> cboComs;
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
    private Hyperlink advSettings;
    @FXML
    public void connect() {
        if (btnConnect.getText().equals(BTN_CONNECT)) {
            log.l("connect");
            selectedPort = cboComs.getSelectionModel().getSelectedItem();
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
                log.l("push error nfe: "+Arrays.toString(nfe.getStackTrace()));
                popup.infoAlert("Baudrate error!", "Integer expected");
            }
            catch (Exception e) {
                log.l("push error: "+Arrays.toString(e.getStackTrace())+e.getCause());
                popup.infoAlert("Error!", Arrays.toString(e.getStackTrace()));
            }
            serial = new Serial(selectedPort, Integer.parseInt(txtBaud.getText()));
            if (!Serial.comPort.isOpen()) {
                popup.errorMessage("Error", Serial.comPort.getSystemPortName()+ " is busy");
                return;
            }
            btnConnect.setText(BTN_DISCONNECT);
            txtBaud.setDisable(true);
            cboComs.setDisable(true);
            txtCommand.setDisable(false);
            btnPush.setDisable(false);
            advSettings.setDisable(true);
        }
        else {
            Serial.comPort.closePort();
            btnConnect.setText(BTN_CONNECT);
            txtBaud.setDisable(false);
            cboComs.setDisable(false);
            txtCommand.setDisable(true);
            btnPush.setDisable(true);
            advSettings.setDisable(false);
        }
    }
    
    @FXML
    private void push() throws InterruptedException {
        log.l("PUSH");
        try {
            serialSession = new SerialSession(serial, txtCommand.getText()+'\n');
            serialSession.pushAndRead();
        }
        catch (NumberFormatException nfe) {
            log.l("push error nfe: "+Arrays.toString(nfe.getStackTrace()));
            popup.infoAlert("Baudrate error!", "Integer expected");
        }
        catch(Exception e) {
            log.l("push error: "+Arrays.toString(e.getStackTrace())+e.getCause());
            popup.infoAlert("Error!", Arrays.toString(e.getStackTrace()));
        }
        finally {
            delay = new Delay();
            delay.by(1000, () -> {
                try {
                log.l("set output to "+serialSession.output());
                    txtOutput.appendText(serialSession.output().toString());
                    txtOutput.setScrollTop(Double.MIN_VALUE);
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
    public SerialSession getTxRx() {
        return serialSession;
    }
    public ExecutorService getExecutor() {
        return executor;
    }
    public void goToAdvSettings() throws Exception {
        if (SerialSettings.showing == true) return;
        log.l("not showing -- should start");
        setting.start(stage);
    }
    public void clearOutput() {
        log.l("clearing output");
        txtOutput.clear();
        serialSession.output().delete(0, serialSession.output().length());
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stage = new Stage();
        setting = SerialSettings.getInstance();
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
        cboComs.setOnAction(e -> {
            log.l("port change");
            selectedPort = cboComs.getSelectionModel().getSelectedItem();
        });
        txtOutput.textProperty().addListener(
                (ObservableValue<?> observable, Object oldValue, Object newValue) -> {
            txtOutput.setScrollTop(Double.MIN_VALUE);
        });
        
        System.out.println("FXMLDocumentController initialize");
    }
}
