package Com;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Read {
    SerialPort port;
    String string = null;
    static Log log = new Log();
    public Read(SerialPort port) {
        this.port = port;
//        log.l("Read(port){} "+port.getSystemPortName());
    }
    
    public String output() {
        string = "";
            if (!port.isOpen()) port.openPort();
            InputStream in = port.getInputStream();
            try {
                for (int j = 0; j < 1000; ++j) {
//                    log.l("reading from Read::String output() PRE IF");
                    if (in != null && in.available() > 0) {
//                        log.l("reading from Read::String output() POST IF");
                        string += (char)in.read();
                        in.close();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace(); 
            }
  
        return string;
    }
    public static void testReader() {
        SerialPort port = SerialPort.getCommPorts()[2];
        port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        port.openPort();
        log.l(port.getSystemPortName());
//        try {
//            if (port.getInputStream().available() > 0) {
                Scanner scanner = new Scanner(port.getInputStream());
                log.l("testReader PRE WHILE");
                while(scanner.hasNextLine()) {
                    log.l("in while");
                    try {
                        //                    String line = scanner.nextLine();
                        log.l("...");
                        log.l(scanner.nextLine());
                    } catch(Exception e) {
                        port.closePort();
                    }
                }
                
                scanner.close();
                log.l("scanner closed");
//            }
//        } catch (IOException ex) {
//            port.closePort();
//            Logger.getLogger(Read.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }
    public static void main(String[] args) {
        Log log = new Log();
        log.l("Reader main()");
//        testReader();
    
    
        Thread thread = new Thread(){
            @Override public void run() {
                SerialPort chosenPort = SerialPort.getCommPorts()[1];
                chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                
//                chosenPort.setBaudRate(9600);
//                chosenPort.setNumDataBits(8);
//                chosenPort.setNumStopBits(1);
//                chosenPort.setParity(0);
//                chosenPort.setFlowControl(0);
                
                chosenPort.closePort();
                chosenPort.openPort();
                log.l(chosenPort.getSystemPortName()+" open: "+chosenPort.isOpen());
                    Scanner scanner = new Scanner(chosenPort.getInputStream());
//                    while(chosenPort.isOpen()) {
//                        log.l("port is open");
//                    }
                    log.l("while pre");
                    byte[] buffer = new byte[2000];
                    for (int i=0; i<2000; i++) {
                        log.l("buffer: "+(char)buffer[i]);
                    }
                    chosenPort.readBytes(buffer, 2000);
                    while(scanner.hasNextLine()) {
                        log.l("while post");
                        
                        try {
                            String line = scanner.nextLine();
                            log.l(scanner.nextLine());
                        } catch(Exception e) { chosenPort.closePort(); }
                    }
                scanner.close();
            }
        };
        thread.start();
    
    
    }
    
    
    
}
