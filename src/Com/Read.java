package Com;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;

public class Read {
    SerialPort port;
    String string = null;
    Log log = new Log();
    public Read(SerialPort port) {
        this.port = port;
    }
    
    public String output() {
        log.l("output()");
        string = "";
        InputStream in = port.getInputStream();
        try {
            for (int j = 0; j < 1000; ++j) {
                if (in != null && in.available() > 0) {
                    string += (char)in.read();
                    in.close();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        return string;
    }
}
