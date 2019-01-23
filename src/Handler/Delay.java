
package Handler;

import Com.Log;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Delay {
    Timer timer = new Timer();
    Log log = new Log();

        
    public void by(int sec, Callable<Boolean> func) {
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                log.l("delaying by "+sec);
                try {   
                    log.l("running "+func);
                    func.call();
                } catch (Exception ex) {
                    log.l("Err delay run");
                }
            }
        }, sec);
    }
    public void by(int sec, Runnable func) {
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                log.l("delaying by "+sec);
                try {
                    func.run();
                } catch (Exception ex) {
                    log.l("Err delay run");
                }
            }
        }, sec);
    }
}
