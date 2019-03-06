
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
    
    public static void main(String[] args) throws IOException {
        new Thread(() -> {
            Log log = new Log();
            SerialPort port = SerialPort.getCommPort("COM2");
            port.setBaudRate(9600);
            Scanner scanner = new Scanner(System.in);        
            byte[] bytes = null;
            InputStream in;
            String input = "";
            String output = "";
            log.l("new THREAD");
            while (!port.isOpen()) {
                log.l("!port.isOpen()");
                input = "";
                output = "";
                input += scanner.nextLine();    //RUN SEPARATELY******+
                if (input.equals("OPEN PORT")) {
                    log.l("OPEN PORT");
                    port.openPort();
                }
                log.l("check port stat: "+port.isOpen());
                while (port.isOpen()) {
                   in = port.getInputStream();
                   log.l("while (port.isOpen())");
                   input = "";
                    try {
                        if(port.getInputStream().available() > 0) {
                            for (int i=0; i<in.available(); i++) {
                              output += (char)in.read();
                              log.l("FOR LOOP");
                            }
                        }
                        input += scanner.nextLine();
                        if (input.equals("CLOSE PORT")) {
                            log.l("CLOSE PORT");
                            port.getOutputStream().write(input.getBytes());
                            port.closePort();
                            Platform.exit();
                            System.exit(0);
                        }
                        Thread.sleep(200);
                        log.l("sending: "+input);
                        log.l("RX: "+output);
//                        port.getOutputStream().flush();
                        output = "";
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    log.l("END WHILE OPEN");
                }
            }
            
        }).start();
    }
}