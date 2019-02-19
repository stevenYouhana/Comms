
package Test;

import Com.Log;
import Com.Serial;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadingPlayground {
    //TimeUnit.SECONDS.sleep(1);
    static Log log = new Log();
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        Operation op = new Operation();
        log.l(Operation.serial.getPort().getSystemPortName());
        op.read();
//        op.push();
    }
}

class Operation {
    static Log log = new Log();
    static ExecutorService ex = Executors.newFixedThreadPool(3);
    public static Serial serial;
    
    public Operation() {
        log.l("init serial");
        serial = new Serial("COM1",9600);
    }
    
    static String input = "";
    
    
    public Serial getSerial() {
        return serial;
    }

    public void push() throws InterruptedException, ExecutionException, TimeoutException {
        Callable<String> pushTask = () -> {
            TimeUnit.SECONDS.sleep(1);
            return "push command";
        };
//        serial.open(); EEROR
        serial.getPort().openPort();
        Future<String> future = ex.submit(pushTask);
        log.l("push future is done? "+future.isDone());
        log.l(future.get(2, TimeUnit.SECONDS));
        
        serial.pushCommand(future.get(2, TimeUnit.SECONDS));
        
        log.l("push future is done? "+future.isDone());
//        shutdown(ex);
//        serial.closePort(1);
    }
    public void read() throws InterruptedException, ExecutionException, TimeoutException {
        Reader reader = new Reader();
        Future future;
//        log.l("push future is done? "+future.isDone());
        while(true) {
            future = ex.submit(reader.readTask());
//            if (!input.isEmpty()) {
//                log.l(future.get(1,TimeUnit.SECONDS));
//                log.l("push future is done? "+future.isDone());
//            }
            log.l(input);
        }
        
        
    }
    
    static class Reader {
        Log log = new Log();
        Runnable readTask = () -> {
            if (serial != null) {
                log.l("serial != null");
                log.l(serial.getPort().getSystemPortName());
                try {
                if (serial.getPort().getInputStream().available() > 0) {
                    log.l("reading");
                    input = serial.getPort().getInputStream().toString();
                }
//                    else input = "";
            
                } catch(IOException ioe) {
                    log.l("readTask ioe: "+ioe.getMessage());
                }
            }
        };
        
        public Runnable readTask() {
            return readTask;
        }
    }
    
    public void shutdown(ExecutorService executor) {
        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");  
        }
    }
}

