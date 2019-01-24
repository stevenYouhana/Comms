
package Handler.MultiThread;

import Com.Log;
import javafx.concurrent.Task;

public class PerformTask extends Task<Void> {
    Log log = new Log();
    @Override
    protected Void call() throws Exception {
        log.l("call()");
        return null;
    }
    
}
