package Com;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;

public class Read {
    SerialPort port;
    String string = null;
    Log log = new Log();
    public Read(SerialPort port) {
        this.port = port;
//        log.l("Read(port){} "+port.getSystemPortName());
    }
    
    public String output() {
//        log.l("output()");
        string = "";
//        synchronized(UI.FXMLDocumentController.serialLock) {
            if (!port.isOpen()) port.openPort();
            InputStream in = port.getInputStream();
            try {
//                log.l("output() "+port.getSystemPortName());
                for (int j = 0; j < 1000; ++j) {
                    if (in != null && in.available() > 0) {
                        string += (char)in.read();
                        in.close();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace(); 
            }
//        }
        return string;
    }
}
