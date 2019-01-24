
package Handler.MultiThread;

import Com.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ATask {
    Log log = new Log();
    
    private void task1() {
        log.l("task1");
    }
    private void task2() {
        log.l("task2");
    }
    
    
    PerformTask task = new PerformTask();
    ExecuteTask et = new ExecuteTask();
    
    public void handle() {
        task.setOnRunning( (successEvent) -> {
            task1();
        });
        task.setOnSucceeded( (successEvent) -> {
            task2();
        });
        et.execute(task);
    }
    
}
