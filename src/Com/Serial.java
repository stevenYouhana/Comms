package Com;

import Handler.Delay;
import Handler.IDelay;
import Handler.Popup;
import com.fazecast.jSerialComm.*;
import java.io.IOException;

public class Serial {
    Log log = new Log();
    public static SerialPort[] availablePorts;
    public static SerialPort comPort;
    int baudRate;
    public final int DEFAULT_BAUD = 9600;
    byte[] command;
    Popup popup;
    Delay delay;
    public volatile static String output = "";
    
    static {
        availablePorts = SerialPort.getCommPorts();
    }
    
    public Serial(String port, int baudRate) {
        this.comPort = selectedCom(port);
        this.baudRate = baudRate;
        comPort.setNumDataBits(8);
        comPort.setNumStopBits(1);
        comPort.setParity(0);
        comPort.setFlowControl(0);
        
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        comPort.openPort();
        log.l("Serial main constructor set");
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
        delay = new Delay();
        delay.by(sec, () -> {
            comPort.closePort();
        });
    }
    
    public void open() {
        comPort.openPort();
    }
    public void pushCommand(String command) {
        log.l("pushCommand(String command)"+" open: "+comPort.isOpen());
        try {
        if (!comPort.openPort()) return;
        comPort.openPort();
        log.l("port opened and pushing from pushCommand(String command)");
        
        comPort.getOutputStream().write(command.getBytes());
//            comPort.getOutputStream().write(Integer.parseInt(command));
//        comPort.writeBytes(command.getBytes(), command.length());
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
        return comPort.getSystemPortName();
    }
    
}
