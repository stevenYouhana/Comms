package Handler;

import Com.Log;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Popup {
    Log log = new Log();
    private Alert alert;
    private final String INFO_ALERT_TITLE = "Info";
    public void infoAlert(String headerText, String context) {
        log.l("infoAlert");
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(INFO_ALERT_TITLE);
        alert.setHeaderText(headerText);
        alert.setContentText(context);
        log.l("show ALERT");
        alert.show();
    }
    public void close() {
        if (this.alert != null) this.alert.close();
    }
}
