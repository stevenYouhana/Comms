package Handler;

import Com.Log;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Popup {
    Log log = new Log();
    private Alert alert;
    private final String INFO_ALERT = "Info";
    private final String ERROR_MESSAGE = "Error";
    
    public void infoAlert(String headerText, String context) {
        log.l("infoAlert()");
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(INFO_ALERT);
        alert.setHeaderText(headerText);
        alert.setContentText(context);
        alert.show();
    }
    public void errorMessage(String headerText, String context) {
        log.l("errorMessage()");
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ERROR_MESSAGE);
        alert.setHeaderText(headerText);
        alert.setContentText(context);
        alert.show();
    }
    public void close() {
        if (this.alert != null) this.alert.close();
    }
}
