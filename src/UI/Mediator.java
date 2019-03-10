
package UI;

public class Mediator {
    public volatile static String output = "";
    public static synchronized void resetOutput() {
        output = "";
    }
    
    public static  void run() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
            System.out.println("MED>>> "+output);
        }
    }
}
