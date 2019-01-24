
package Com;

import Handler.Delay;
import UI.Comms;
import com.fazecast.jSerialComm.SerialPort;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Refresh {
    private SerialPort[] ports = null;
    FutureTask<Void> task;
    Executor executor;
    Delay delay;
    Log log = new Log();
    public Refresh() {
        
    }
    public void newPorts() {
//        log.l("refresh()");
        if (Serial.availablePorts.length == 0) {
            log.l("numberOfPorts == 0");
            ports = SerialPort.getCommPorts();
        }
        else if (SerialPort.getCommPorts().length != Serial.availablePorts.length) {
            log.l("refresh() if: TRUE");
            ports = SerialPort.getCommPorts();
            Serial.availablePorts = SerialPort.getCommPorts();
        }
        else {
            log.l("refresh() else");
        }
    }
    public SerialPort[] getNewPorts() {
        return ports;
    }
    
}
