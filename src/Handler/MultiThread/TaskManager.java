
package Handler.MultiThread;

import Com.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import javafx.concurrent.Task;


public class TaskManager {
    Log log = new Log();
    final ExecutorService es = Executors.newFixedThreadPool(1);
    ArrayList<Task> tasks = new ArrayList<>();
//    Set<Task> tasks = new HashSet<>();
//    LinkedList<Task> tasks = new LinkedList<>();
    public void add(Task task) {
        tasks.add(task);
    }
    public void execute() {
        log.l("execute()");
//        tasks.forEach(task -> {
//            log.l("ex each");
//            es.execute(task);
//        }); 
        es.execute(tasks.get(0));
    }
    public void shutdown() {
        this.shutdown();
    }
}
