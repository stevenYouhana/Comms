
package Handler.Tasks;

import Com.Read;
import Com.Serial;
import Handler.Popup;
import static Handler.Tasks.TaskManager.log;
import UI.FXMLDocumentController;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class SerialSession extends TaskManager {
    ReentrantLock lock = new ReentrantLock();
    Serial serial;
    String command = "";
    StringBuffer buffer;
    Read reader;
    static Popup popup = new Popup();

    public SerialSession(Serial serial, String command) {
        this.serial = serial;
        this.command = command;
    }
    public static void connect(String port, int baud, FXMLDocumentController cntrl) {
        try {
            log.l("hash code: "+cntrl.toString());
            if (cntrl.getBtnCon().getText().equals(FXMLDocumentController
                    .Holder.BTN_CONNECT)) {
                log.l("connect");
                FXMLDocumentController.selectedPort = port;
                try {
                    log.l("COM: "+port);
                    log.l("BAUD: "+baud);
                    log.l("btn text: "+cntrl.getBtnCon().getText());
                    if (port == null 
                            || port.isEmpty() 
                            || port.equals(FXMLDocumentController.Holder.CBO_MSG)) {
                        popup.infoAlert("Port error!", "Select a port");
                        log.l("cntrl.setDisconnectedState(); CALLED!");
                        cntrl.setDisconnectedState();
                        return;
                    }
                    if (port.isEmpty()) {
                        popup.infoAlert("Baudrate error!", "Set baudrate");
                        cntrl.setDisconnectedState();
                        return;
                    }
                }
                catch (NumberFormatException nfe) {
                    log.l("push error nfe: "+Arrays.toString(nfe.getStackTrace()));
                    popup.infoAlert("Baudrate error!", "Integer expected");
                    cntrl.setDisconnectedState();
                    return;
                }
                catch (Exception e) {
                    log.l("push error: "+Arrays.toString(e.getStackTrace())+e.getCause());
                    popup.infoAlert("Error!", Arrays.toString(e.getStackTrace()));
                    return;
                }
                
                FXMLDocumentController.serial = new Serial(port, baud);
                if (!Serial.comPort.isOpen()) {
                    log.l("SerialSession::connect() if !Serial.comPort.isOpen()");
                    log.l(Serial.comPort.getSystemPortName()+ " is open: "+Serial.comPort.isOpen());
                    popup.errorMessage("Error", 
                            Serial.comPort.getSystemPortName()+ " is busy");
                    return;
                }
                cntrl.setConnectedState();
            }
            else {
                // *******HANDLE LOSS CONNECTION! *********
                log.l("setDisconnectedState() called");
                log.l("btn text: "+cntrl.getBtnCon().getText());
                cntrl.setDisconnectedState();
            }
        } catch (NullPointerException npe) {
            log.l(Arrays.toString(npe.getStackTrace()));
        } catch (Exception e) {
            log.l(Arrays.toString(e.getStackTrace()));
        }
    }
    public void pushAndRead() {
        log.l("pushAndRead()");
        executor.submit(push());
        executor.submit(read());
        TaskManager.stop(executor);
    }

    Runnable read() {
        buffer = new StringBuffer();
        reader = new Read(serial.getPort());
        return () -> {
            lock.lock();
                sleep(500);
                buffer.append(reader.output()).append('\n');
        };
    }

    Runnable push() {
        return () -> {
            try {
            serial.pushCommand(command);
            } finally {
                lock.unlock();
            }
        };
    }
    public StringBuffer output() {
        return buffer;
    }
    
}
