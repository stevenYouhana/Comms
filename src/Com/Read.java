package Com;

import Handler.Popup;
import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
public class Read {
    SerialPort port;
    StringBuffer string;
    static Log log = new Log();
    Popup popup = new Popup();
    
    public Read(SerialPort port) {
        this.port = port;
    }
    
    public StringBuffer output() {
        string = new StringBuffer();
            if (!port.isOpen()) port.openPort();
            InputStream in = port.getInputStream();
            try {
                for (int j = 0; j < 1000; ++j) {
                    if (in != null && in.available() > 0) {
                        string.append((char)in.read());
                        in.close();
                    }
                }
            } catch (Exception e) {
                popup.errorMessage("Exception",
                        this.getClass().getName()+"::output()"+e.getMessage());
                e.printStackTrace();
            }
        return string;
    }
}
