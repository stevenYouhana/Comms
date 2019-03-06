
package Test;

import Com.Log;
import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

public class SimpleSerial {
    static volatile String input = "";
    static Log log = new Log();
    synchronized static void setInput(String input) {
        SimpleSerial.input = input;
    }
    
    public static void main(String[] args) throws IOException {
        new Thread(() -> {
            log.l("Scanner thread.........");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                setInput(input = scanner.nextLine());
            }
        }).start();
        
        new Thread(() -> {
            SerialPort port = SerialPort.getCommPort("COM2");
            port.setBaudRate(9600);
            byte[] bytes = null;
            InputStream in;
            String output = "";
            log.l("new THREAD");
            while (!port.isOpen()) {
//                if (input.contains(input))
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SimpleSerial.class.getName()).log(Level.SEVERE, null, ex);
                }
                log.l(input);
                if (input.equals("OPEN PORT")) {
                    log.l("OPEN PORT");
                    port.openPort();
                }
//                setInput("");
                log.l("check port stat: "+port.isOpen());
                while (port.isOpen()) {
                   in = port.getInputStream();
                   log.l(input);
                    try {
                        if(port.getInputStream().available() > 0) {
                            for (int i=0; i<in.available(); i++) {
                              output += (char)in.read();
                              log.l("FOR LOOP");
                            }
                        }
                        if (input.equals("CLOSE PORT")) {
                            log.l("CLOSE PORT");
                            port.getOutputStream().write(input.getBytes());
                            port.closePort();
                            break;
                        }
                        port.getOutputStream().write(input.getBytes());
                        Thread.sleep(500);
//                        log.l("sending: "+input);
                        log.l("RX: "+output);
                        port.getOutputStream().flush();
                        output = "";
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }).start();
    }
}