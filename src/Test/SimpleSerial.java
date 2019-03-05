
package Test;

import Com.Log;
import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleSerial {
    
    public static void main(String[] args) throws IOException {
        Log log = new Log();
        SerialPort port = SerialPort.getCommPort("COM2");
        port.setBaudRate(9600);
        port.openPort();
        Scanner scanner = new Scanner(System.in);
        String input = "";
        
        byte[] bytes = null;
        InputStream in = port.getInputStream();
        
        new Thread(() -> {
            log.l("new THREAD");
            String output = "";
            while (port.isOpen()) {
                try {
                    if(port.getInputStream().available() > 0) {
                        for (int i=0; i<in.available(); i++) {
                          output += (char)in.read();
                       }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                    log.l("RX: "+output);
                    port.getOutputStream().flush();
                    output = "";
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (IOException ex) {
                    Logger.getLogger(SimpleSerial.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
        }).start();
        log.l("into the while");
        while (port.isOpen()) {
            input += scanner.nextLine();
            if (input.endsWith("0"))
            {
                log.l("TX");
                port.getOutputStream().write(input.getBytes());
                input = "";
            }
        }
    }
}