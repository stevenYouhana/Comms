package Com;


public class Log {
    public void l(String s) {
        System.out.println(s);
    }
    public void l(boolean bool) {
        System.out.println(bool);
    }

    public void l(Serial serial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}