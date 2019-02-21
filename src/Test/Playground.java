
package Test;

import Com.Log;
import Com.Serial;
import Handler.Tasks.TaskManager;
import Handler.Tasks.SerialSession;
import com.fazecast.jSerialComm.SerialPort;

public class Playground {
    
    public static void main(String[] args) {
        Log log = new Log();
        Serial serial = new Serial("COM3");
        log.l("PLAYGROUND");
        serial.getPort().setFlowControl(SerialPort.FLOW_CONTROL_CTS_ENABLED);
        log.l("CTS: "+SerialPort.FLOW_CONTROL_CTS_ENABLED);
        log.l("disabled: "+SerialPort.FLOW_CONTROL_DISABLED);
        log.l("DSR: "+SerialPort.FLOW_CONTROL_DSR_ENABLED);
        log.l("DTR: "+SerialPort.FLOW_CONTROL_DTR_ENABLED);
        log.l("RTS: "+SerialPort.FLOW_CONTROL_RTS_ENABLED);
        log.l("XONXOFF IN en: "+SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED);
        log.l("XONXOFF OUT en: "+SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED);
        
//        serial.getPort().set
        serial.getPort().setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED | SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED);
        log.l("get RTS: "+serial.getPort().getDSR());
        log.l("get DSR: "+serial.getPort().getDSR());
        
        log.l("done");
//        log.l("FC: "+serial.getPort().getFlowControlSettings());
    }
}
    
