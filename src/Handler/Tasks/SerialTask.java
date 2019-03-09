package Handler.Tasks;


import Com.Log;
import Handler.Delay;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerialTask {
    static SerialPort port;
//    public static final ReentrantLock readLock = new ReentrantLock();
    public static boolean readLock = true;
    public static Object lock = new Object();
    private String output = "";
    static StringBuffer buffer = new StringBuffer();
//    static volatile String input = "";
    Log log = new Log();
    Delay delay = new Delay();
    public SerialTask(String portName) {
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(9600);
        port.openPort();
    }
    public String read() {
        return output;
    }
    public boolean portIsOpen() {
        return port.isOpen();
    }
    public synchronized void resetOuput() {
        output = "";
    }
    public void output() {
//        InputStream in;
        log.l("output()");
//        while (port.isOpen()) {
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() { 
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }
            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != 
                    SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
                readLock = false;
                resetOuput();
                log.l("SerialPort.LISTENING_EVENT_DATA_AVAILABLE");
                    synchronized (lock) {
                        lock.notifyAll();
                        log.l("notified");
                        try {
                            byte[] newData = new byte[port.bytesAvailable()];
                            int numRead = port.readBytes(newData, newData.length);
                            System.out.println("Read " + numRead + " bytes.");
                            for (int i=0; i<newData.length; i++) {
                                output += (char)newData[i];
                                buffer.append((char)newData[i]);
                                log.l("FOR LOOP");
                            }
                        }
                        finally {
                                    readLock = false;
                                    try {
                                        log.l("     serialEvent wait");
                                        Thread.sleep(100);
                                        lock.wait();
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                        Logger.getLogger(SerialTask.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                
                        }
                }
            }
        });
//        in = port.getInputStream();
//            try {
//                if(port.getInputStream().available() > 0) {
//                    for (int i=0; i<in.available(); i++) {
//                        output += (char)in.read();
//                        log.l("FOR LOOP");
//                    }
//                }    
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
//        }
//        return output;
    }
    public void stopPort() {
        port.closePort();
        log.l("stopPort()");
    }
    
    public void session() {
        log.l("session()");
        do {
//            log.l("port.isOpen() "+port.getSystemPortName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                Logger.getLogger(SerialTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            log.l(read());
        } while ((port.isOpen()));
    }
    public void pushCommand(String command) throws IOException {
        log.l("pushCommand(String)");
        port.getOutputStream().write(command.getBytes());
    }
    
}

