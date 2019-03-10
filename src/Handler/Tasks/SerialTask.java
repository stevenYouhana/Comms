package Handler.Tasks;


import Com.Log;
import Handler.Delay;
import UI.Mediator;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.IOException;
import javafx.scene.control.TextArea;

public class SerialTask {

    static SerialPort port;
    public final static Object lock = new Object();
    static StringBuffer buffer = new StringBuffer();
    Delay delay = new Delay();
    Log log = new Log();
    
    public static class Advanced_Setting {
        public void setAdvancedVariables(int baud, int dataBits, int stopBits,
                int parity, int flowControl) {
            port.setComPortParameters(baud, dataBits, stopBits, parity);
        }
        public void setTimeOut(int mode, int read, int write) {
            port.setComPortTimeouts(mode, read, write);
        }
    }
    
    public SerialTask(String portName, int baudRate) {
        port = SerialPort.getCommPort(portName);
        port.setComPortParameters(baudRate, 8, 1, 0);
        port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        port.openPort();
    }
    public SerialTask(String portName) {
        this(portName, 9600);
    }
    public boolean portIsOpen() {
        return port.isOpen();
    }
    public synchronized void resetOuput() {
        log.l("resetOutput()");
//        output = "";
        buffer.delete(0, buffer.length());
    }
    public void session() {
        log.l("output()");
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() { 
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }
            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != 
                    SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
                log.l("SerialPort.LISTENING_EVENT_DATA_AVAILABLE");
                synchronized (lock) {
                    byte[] newData = new byte[port.bytesAvailable()];
                    port.readBytes(newData, newData.length);
                    for (int i=0; i<newData.length; i++) {
                        buffer.append((char)newData[i]);
                        Mediator.output += (char)newData[i];
                    }
                    lock.notifyAll();
                }
            }
        });
    }
    public void stopPort() {
        port.closePort();
        log.l("stopPort()");
    }
    
    public void pushCommand(String command) throws IOException {
        log.l("pushCommand(String)");
        port.getOutputStream().write(command.getBytes());
    }
    @Override
    public String toString() {
        return port.getDescriptivePortName()+"\n"+port.getSystemPortName()
                +"\nbaud: "+port.getBaudRate()
                +"\ndata bits: "+port.getNumDataBits()
                +"\nstop bits: "+port.getNumStopBits()
                +"\nparity: "+port.getParity()
                +"\nflow control: "+port.getFlowControlSettings();
    }
}

