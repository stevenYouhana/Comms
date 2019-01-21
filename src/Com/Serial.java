package Com;

import Handler.Popup;
import com.fazecast.jSerialComm.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;

public class Serial {
    Log log = new Log();
    SerialPort[] comPorts;
    SerialPort comPort;
    int baudRate;
    byte[] command;
    public static int numbetOfPorts = 0;
    Popup popup;
    
    
    public Serial(String port, int baudRate) {
        this.comPort = selectedCom(port);
        this.baudRate = baudRate;
        popup = new Popup();
    }
    public Serial() {}
    
    public void setComPort(String port) {
        this.comPort = selectedCom(port);
        log.l("port set to "+port);
        log.l("SET PORT TO: "+comPort.getSystemPortName());
    }
    public SerialPort[] getAvailabePorts() {
        this.comPorts = SerialPort.getCommPorts();
        numbetOfPorts = comPorts.length;
        return comPorts;
    }
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
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
    private void closePort(int sec) {
        log.l("about to close");
        Timer timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                log.l("closing port"); 
                comPort.closePort();
            }
        }, sec);
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
        finally {
            if (comPort != null && comPort.isOpen()) closePort(5);
        }
    }
    public String getOutput() {
//        OutputStream output = new OutputStream() {
//        private StringBuilder string = new StringBuilder();
//        @Override
//        public void write(int b) throws IOException {
//            this.string.append((char) b );
//        }
//        public String toString(){
//            return this.string.toString();
//        }
//    };
        byte[] bytes = null;
        comPort.readBytes(bytes, 1000);
        return bytes.toString();
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
    public void init() {
        log.l("Com project:");
        log.l(comPort.getDescriptivePortName());
        log.l(comPort.getPortDescription());
        log.l(comPort.getSystemPortName());
        byte[] buffer = {'a','b','c'};
       
        String line = "from java Com class";
        log.l("writing "+buffer);
        comPort.setBaudRate(baudRate);
        comPort.openPort();
        int count = 0;

        while (count < 100) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                log.l("Thread interrupted: "+ie.getStackTrace());
            }
            log.l("writing");
           
            comPort.writeBytes(line.getBytes(), line.getBytes().length);
            
            count++;
        }
        comPort.closePort();
    }
}
