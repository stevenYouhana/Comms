
package UI;

import Com.Log;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import Com.Serial;
import Handler.Delay;
import Handler.Tasks.TaskManager;
import Handler.Tasks.SerialSession;
import Handler.Popup;
import com.fazecast.jSerialComm.SerialPort;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
/**
 *
 * @author steven.youhana
 */
public class FXMLDocumentController implements Initializable {
    private static FXMLDocumentController instance = null;
    public static Serial serial;
    String port;
    ObservableList<String> availablePorts = FXCollections.observableArrayList();
    Log log = new Log();
    Popup popup = new Popup();
    ExecutorService executor;
    Delay delay;
    SerialSession serialSession;

    public volatile static String selectedPort = null;
    public FXMLDocumentController() { instance = this; }
    
    public static class Holder {
        public static  final String CBO_MSG = "select.."; 
        public static  final String BTN_CONNECT = "Connect";
        public static final String BTN_DISCONNECT = "Disconnet";
        static final String CSS_NO_ENTRY = "-fx-background-color: #f2d91f;";
        static final String CSS_RESET = "-fx-control-inner-background: #ffffff";
        public static void noFieldSelection(TextField txt) {
            txt.setStyle(CSS_NO_ENTRY);
        }
        public static void noCboSelection(ComboBox cbo) {
            cbo.setStyle(CSS_NO_ENTRY);
        }
        public static void txtFieldReset(TextField txt) {
            txt.setStyle(CSS_RESET);
        }
        public static void reset_fields(ComboBox cbo, TextField txt) {
            cbo.setStyle(CSS_RESET);
            txt.setStyle(CSS_RESET);
        }
    }
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
    private ToggleGroup grpEndType;
    @FXML
    private RadioButton radLF;
    @FXML
    private RadioButton radCRLF;
    String LfCr = null;
    @FXML
    public void connect() {
        selectedPort = cboComs.getSelectionModel().getSelectedItem();
        try {
            log.l("COM: "+selectedPort);
            log.l("BAUD: "+txtBaud.getText());
            if (selectedPort == null 
                    || selectedPort.isEmpty() 
                    || selectedPort.equals(Holder.CBO_MSG)) {
                if (btnConnect.getText().equals(Holder.BTN_DISCONNECT)) {
                    popup.errorMessage("Lost connection!", 
                        "Port is either closed or disconnected. Closing session");
                    setDisconnectedState();
                    return;
                }
                Holder.noCboSelection(cboComs);
                popup.infoAlert("Port error!", "Select a port");
                return;
            }
            else if (txtBaud.getText().isEmpty()) {
                Holder.noFieldSelection(txtBaud);
                popup.infoAlert("Baudrate error!", "Set baudrate");
                return;
            }
            else {
                Holder.reset_fields(cboComs, txtBaud);
                SerialSession.connect(
                        selectedPort, Integer.parseInt(txtBaud.getText()), instance);
            }
        }
        catch (NullPointerException npe) {
            log.l("push error nfe: "+Arrays.toString(npe.getStackTrace()));
            popup.infoAlert("Null", npe.getMessage());
        }
        catch (NumberFormatException nfe) {
            log.l("push error nfe: "+Arrays.toString(nfe.getStackTrace()));
            popup.infoAlert("Baudrate error!", "Integer expected");
        }
        catch (Exception e) {
            log.l("Connetion error: "+Arrays.toString(e.getStackTrace())+e.getCause());
            popup.infoAlert("Connetion Error!", Arrays.toString(e.getStackTrace()));
        }
    }
    public void setConnectedState() {
        btnConnect.setText(Holder.BTN_DISCONNECT);
        txtBaud.setDisable(true);
        cboComs.setDisable(true);
        txtCommand.setDisable(false);
        btnPush.setDisable(false);
        txtBaud.setText(String.valueOf(serial.getPort().getBaudRate()));
        advSettings.setDisable(true);
    }
    public void setDisconnectedState() {
        btnConnect.setText(Holder.BTN_CONNECT);
        txtBaud.setDisable(false);
        cboComs.setDisable(false);
        txtCommand.setDisable(true);
        btnPush.setDisable(true);
        advSettings.setDisable(false);
        log.l("closing: "+serial.getPort().getSystemPortName());
        Platform.runLater(serial.getPort()::closePort);
        log.l(serial.getPort().getSystemPortName()+" isOpen(): "+serial.getPort().isOpen());
    }
    @FXML
    private void push() throws InterruptedException {
        log.l("PUSH");
        try {
            log.l("sel Port: "+selectedPort);
            if (!Serial.comPort.isOpen() || Serial.comPort == null || 
                    selectedPort == null || selectedPort.equals(Holder.CBO_MSG)) {
                popup.errorMessage("Lost connection!", 
                        "Port is either closed or disconnected. Closing session");
                setDisconnectedState();
                return;
            }
            serialSession = new SerialSession(
                    serial, finalizeCommand(txtCommand.getText(), LfCr));
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
    private String finalizeCommand(String command, String LF_CR) {
        //          CR = \r; LF = \n; CRLF = \r\n
        return LF_CR.equals("CR/LF")? command+"\r\n": command+'\n';
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
    public static FXMLDocumentController getInstance() {
        return instance == null? instance = new FXMLDocumentController(): instance;
    }
    public SerialSession getTxRx() {
        return serialSession;
    }
    public ExecutorService getExecutor() {
        return executor;
    }
    public void goToAdvSettings() {
        if (SerialSettings.showing == true) return;
        log.l("not showing -- should start");
        try {
        if (cboComs
                .getSelectionModel()
                .getSelectedItem()
                .equals(
                        Holder.CBO_MSG))
            Holder.noCboSelection(cboComs);
        setting.start(stage);
        } catch (NullPointerException npe) {
            if (cboComs.getSelectionModel().getSelectedIndex() == -1) 
                Holder.noCboSelection(cboComs);
            npe.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void clearOutput() {
        log.l("clearing output");
        txtOutput.clear();
        serialSession.output().delete(0, serialSession.output().length());
    }
    public Button getBtnCon() {
        return btnConnect;
    }
    public TextField getTxtCmd() {
        return txtCommand;
    }
    public TextField getTxtBaud() {
        return txtBaud;
    }
    public Button getBtnPush() {
        return btnPush;
    }
    public ComboBox getCboComs() {
        return cboComs;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stage = new Stage();
        setting = SerialSettings.getInstance();
        radLF.setToggleGroup(grpEndType);
        radCRLF.setToggleGroup(grpEndType);
        grpEndType.selectedToggleProperty().addListener((
                ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) -> {
                    RadioButton rb1 = (RadioButton)grpEndType.getSelectedToggle();
                    if (rb1 != null) {
                        LfCr = rb1.getText();
                    } 
                });
        radLF.setSelected(true);
        txtCommand.setDisable(true);
        btnPush.setDisable(true);
        
        ExecutorService executor = Executors.newSingleThreadExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    return t;
                }
            }
        );
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
