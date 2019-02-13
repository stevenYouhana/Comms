
package Handler.Tasks;

import Com.Log;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.concurrent.Task;


public class TaskManager {
    static Log log = new Log();
    final ExecutorService executor = Executors.newFixedThreadPool(5);
    
    ArrayList<Task> tasks = new ArrayList<>(5);
//    Set<Task> tasks = new HashSet<>();
//    LinkedList<Task> tasks = new LinkedList<>();
    
    public void add(Task task) {
        tasks.add(task);
    }
        public void sleep(int seconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(seconds);
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
