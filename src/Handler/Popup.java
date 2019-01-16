package Handler;

import javafx.scene.control.Alert;

public class Popup {
    private Alert alert;
    private final String INFO_ALERT_TITLE = "Info";
    public void infoAlert(String headerText, String context) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(INFO_ALERT_TITLE);
        alert.setHeaderText(headerText);
        alert.setContentText(context);
        alert.show();
    }
    
}
