package Com;

import Handler.Delay;
import Handler.Popup;
import com.fazecast.jSerialComm.*;
import javafx.application.Platform;

public class Serial {
    Log log = new Log();
    public static SerialPort[] availablePorts;
    public static SerialPort comPort;
    int baudRate;
    private static final int DEFAULT_BAUD = 9600;
    byte[] command;
    Popup popup;
    Delay delay;
    public volatile static String output = "";
    static {
        availablePorts = SerialPort.getCommPorts();
    }
    
    public Serial(String port, int baudRate) {
        comPort = selectedCom(port);
        this.baudRate = baudRate;
        comPort.setBaudRate(baudRate);
        comPort.setNumDataBits(8);
        comPort.setNumStopBits(1);
        comPort.setParity(0);
        comPort.setFlowControl(0);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        comPort.openPort();
        log.l("Serial main constructor set");
        log.l("Serial constructor: isOpen(): "+comPort.getSystemPortName()+" "+comPort.openPort());    
        popup = new Popup();
    }
    public Serial(String port) {
        this(port, DEFAULT_BAUD);
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
//        } catch (NullPointerException npe) {
//            log.l("pushCommand ERR npe: " + npe.getCause());
        }
        catch (Exception e) {
            Platform.runLater(
                    () -> popup.infoAlert("Serial error!", "Access to "
                            +comPort.getSystemPortName()+" is denied")
            );
            log.l("comport: "+comPort.isOpen() + comPort.getSystemPortName());
            log.l("comport OPS: "+comPort.getOutputStream());
            log.l("pushCommand() ERR e: "+e.getCause());
        }
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
