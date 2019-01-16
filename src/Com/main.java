package Com;

public class main {

    public static void main(String args) {
        Log log = new Log();
        log.l("main...");
        Serial serial = new Serial();
        serial.init();
    }
} 
