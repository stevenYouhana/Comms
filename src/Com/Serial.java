package Com;

import Handler.Delay;
import Handler.IDelay;
import Handler.Popup;
import com.fazecast.jSerialComm.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Serial {
    Log log = new Log();
    public static SerialPort[] availablePorts;
    SerialPort comPort;
    int baudRate = 0;
    public final int DEFAULT_BAUD = 9600;
    byte[] command;
    Popup popup;
    Delay delay;
    
    static {
        availablePorts = SerialPort.getCommPorts();
    }
    
    public Serial(String port, int baudRate) {
        this.comPort = selectedCom(port);
        this.baudRate = baudRate;
        popup = new Popup();
    }
    public Serial(String port) {
        this(port, 9600);
    }
    public Serial(Runnable task) {
        task.run();
    }
    public Serial() {}
    
    public void setComPort(String port) {
        this.comPort = selectedCom(port);
        log.l("port set to "+port);
        log.l("SET PORT TO: "+comPort.getSystemPortName());
    }
    
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
        if (this.comPort != null) comPort.setBaudRate(baudRate);
    }
    public void setCommand(String command) {
        this.command = command.getBytes();
    }
    public byte[] TX(String command) {
        return command.getBytes();
    }
    public SerialPort getPort() {
        return comPort;
    }
    public void closePort(int sec) {
        log.l("closePort(int sec)");
            delay = new Delay();
            delay.by(sec, () -> {
                comPort.closePort();
                log.l("closePort(int sec)");
            });
    
    }
    public void pushCommand(String command) {
        try {
        comPort.openPort();
        comPort.writeBytes(command.getBytes(), command.length());
        } catch (NullPointerException npe) {
            log.l("pushCommand ERR npe: " + npe.getCause());
        }
        catch (Exception e) {
            log.l("pushCommand() ERR e: "+e.getCause());
            popup.infoAlert("Serial error!", "check if you have selected a valid COM port");
        }
//        finally {
//            if (comPort != null && comPort.isOpen()) closePort(5000);
//        }
    }

    
    private SerialPort selectedCom(String port) {
        SerialPort[] availablePorts = SerialPort.getCommPorts();
        for (SerialPort p : availablePorts) {
            if (p.getSystemPortName().equals(port)) return p;
        }
        return null;
    }
    @Override
    public String toString() {
        return comPort.getPortDescription();
    }
    
}
