
package Handler.Tasks;

import Com.Read;
import Com.Serial;
import static Handler.Tasks.TaskManager.log;
import java.util.concurrent.locks.ReentrantLock;

public class SerialSession extends TaskManager {
    ReentrantLock lock = new ReentrantLock();
    Serial serial;
    String command = "";
    StringBuffer buffer;
    Read reader;

    public SerialSession(Serial serial, String command) {
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
        buffer = new StringBuffer();
        reader = new Read(serial.getPort());
        return () -> {
            lock.lock();
                sleep(500);
                buffer.append(reader.output()).append('\n');
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
    public StringBuffer output() {
        return buffer;
    }
    
}
