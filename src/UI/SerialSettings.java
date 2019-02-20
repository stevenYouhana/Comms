package UI;

import Com.Log;
import Handler.Popup;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.plugin2.ipc.windows.WindowsEvent;


public class SerialSettings extends Application {
    private Stage stage;
    private static SerialSettings instance = null;
    public static boolean showing = false;
    private SerialSettings() {}
    Log log = new Log();
    Popup popup = new Popup();
    public static SerialSettings getInstance() {
        return instance == null? instance = new SerialSettings(): instance;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("SerialSettings.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Advanced settings");
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest((WindowEvent event) -> {
//           stage.close();
            hide();
           event.consume();
        });
        showing = true;
        stage.show();
    }
    public void hide() {
        try {
            showing = false;
            stage.close();
        } catch(NullPointerException npe) {
            popup.errorMessage(Popup.SYSTEM_ERROR, "SerialSettings::hide()\n"+
                    npe.getMessage());
        } catch (Exception e) {
            popup.errorMessage(Popup.SYSTEM_ERROR, "SerialSettings::hide()\n"+
                    e.getMessage());
        }
    }
}
