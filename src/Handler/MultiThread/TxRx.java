
package Handler.MultiThread;

import Com.Read;
import Com.Serial;
import static Handler.MultiThread.TaskManager.log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class TxRx extends TaskManager {
    final ExecutorService executor = Executors.newFixedThreadPool(2);
    ReentrantLock lock = new ReentrantLock();
    Serial serial;
    String command = "";
    String output = "";
    StringBuffer buffer = new StringBuffer();
    Read reader;
    public static final Object outputLock = new Object();

    public TxRx(Serial serial, String command) {
        this.serial = serial;
        this.command = command;
    }

    public void pushAndRead() {
        log.l("pushAndRead()");
        executor.submit(push());
        executor.submit(read());
        TaskManager.stop(executor);
    }

    Runnable read() {
        reader = new Read(serial.getPort());
        return () -> {
            lock.lock();
            try {
                sleep(1);
                buffer.append(reader.output());
            } finally {
                serial.getPort().closePort();
            }
        };
    }

    Runnable push() {
        serial.open();
        return () -> {
            serial.pushCommand(command);
            lock.unlock();
        };
    }
    public StringBuffer output() {
        return buffer;
    }
    
}
