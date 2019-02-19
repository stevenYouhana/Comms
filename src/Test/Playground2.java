
package Test;

import Com.Log;
import Com.Read;
import Com.Serial;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class Playground2 {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    StampedLock lock = new StampedLock();
    static Log log = new Log();
    public static void main(String[] args) {
        Serial serial = new Serial("COM1", 9600);
        Read reader = new Read(serial.getPort());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        StampedLock lock = new StampedLock();
        executor.submit(() -> {
            long stamp = lock.tryOptimisticRead();

            try {
            System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
            sleep(1);
            System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
            sleep(2);
            System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
            }
//            catch (Exception e) {
//
//            }
            finally {
//                serial.closePort(0);
                lock.unlock(stamp);
            }
        });

        executor.submit(() -> {
            long stamp = lock.writeLock();
            try {
                serial.pushCommand("push command");
                System.out.println("Write Lock acquired");
                sleep(2);
            } 
            catch(Exception e) {

            }
            finally {
                lock.unlock(stamp);
                
                System.out.println("Write done");
            }
        });
//        }
        stop(executor);
//        serial.closePort(0);
    }
    
    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static void stop(ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("termination interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("killing non-finished tasks");
            }
            executor.shutdownNow();
        }
    }

}
