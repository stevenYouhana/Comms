
package Handler;

import Com.Log;
import java.util.Timer;
import java.util.concurrent.Callable;
import javafx.application.Platform;

public class Delay {
    Timer timer = new Timer();
    Log log = new Log();
    Popup popup = new Popup();
        
    public void by(int ms, Callable<Boolean> func) {
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                log.l("delaying by "+ms);
                try {   
                    log.l("running "+func);
                    func.call();
                } catch (Exception ex) {
                    log.l("Err delay run");
                }
            }
        }, ms);
    }
    public void by(int ms, Runnable func) {
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                log.l("delaying by "+ms);
//                try {
                    func.run();
//                } catch (Exception ex) {
////                    System.out.println("delay by"+ex.getStackTrace());
//                    Platform.runLater(() -> popup.infoAlert("Error!", "delay by(): "));
//                    log.l("Err delay run");
//                    ex.getStackTrace();
//                }
            }
        }, ms);
    }
}
