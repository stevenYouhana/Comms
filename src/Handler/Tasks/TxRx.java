
package Handler.Tasks;

import Com.Read;
import Com.Serial;
import static Handler.Tasks.TaskManager.log;
import com.fazecast.jSerialComm.SerialPort;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class TxRx extends TaskManager {
    ReentrantLock lock = new ReentrantLock();
    Serial serial;
    String command = "";
    static StringBuffer buffer = new StringBuffer();
    Read reader;

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
                sleep(500);
                buffer.append(reader.output()).append("\n");
        };
    }

    Runnable push() {
        return () -> {
            try {
            serial.pushCommand(command);
            } finally {
                lock.unlock();
            }
        };
    }
    public SerialPort getThePort() {
            return serial.getPort();
    }
    public StringBuffer output() {
        return buffer;
    }
    
}
