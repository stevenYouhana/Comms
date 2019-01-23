
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

public class Refresh extends Thread {
    private SerialPort[] ports;
    FutureTask<Void> task;
    Executor executor;
    Delay delay;
    Log log = new Log();
    public Refresh() {
        
    }
    private void refresh() {
//        log.l("refresh()");
        if (SerialPort.getCommPorts().length != Serial.numberOfPorts) {
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
        task = new FutureTask<>(() -> {
            refresh();
            return null;
        });
          executor = Executors.newSingleThreadScheduledExecutor();              
          executor.execute(task);
            try {
//                    log.l("try task.get");
                task.get(3, TimeUnit.SECONDS);
            }
            catch(InterruptedException | ExecutionException | TimeoutException ex) {
                log.l("Refresh.start.task err ex "+ex.getMessage());
            }

    }
}
