
package Com;

import com.fazecast.jSerialComm.SerialPort;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Refresh extends Thread {
    private SerialPort[] ports;
    Log log = new Log();
    public Refresh() {
        
    }
    public void refresh() {
        log.l("refresh()");
        if (SerialPort.getCommPorts().length != Serial.numbetOfPorts) {
            log.l("refresh() if: TRUE");
            ports = SerialPort.getCommPorts();
        }
    }
    public SerialPort[] getNewPorts() {
        return ports;
    }
    @Override
    public void start() {
        log.l("start Refresh");
            FutureTask<Void> task = new FutureTask<>(() -> {
                refresh();
                return null;
            });
              Executor executor = Executors.newSingleThreadScheduledExecutor();              
              executor.execute(task);
              //while(true) {
                try {
                    log.l("try task.get");
                    task.get(3, TimeUnit.SECONDS);
                }
                catch(InterruptedException | ExecutionException | TimeoutException ex) {
                    log.l("Refresh.start.task err ex "+ex.getStackTrace());
                }
              //}
        }
    
}
