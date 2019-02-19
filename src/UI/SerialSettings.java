package UI;

import Com.Log;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.plugin2.ipc.windows.WindowsEvent;


public class SerialSettings extends Application {
    
    private static SerialSettings instance = null;
    public static boolean showing = false;
    private SerialSettings() {}
    Log log = new Log();
    public static SerialSettings getInstance() {
        return instance == null? instance = new SerialSettings(): instance;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SerialSettings.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Advanced settings");
        stage.setResizable(false);
        log.l("SerialSettings INIT");
        stage.setOnCloseRequest((WindowEvent event) -> {
           showing = true;
           Platform.exit();
//           event.consume();
        });
        stage.show();
    }
    public void hide() {
        this.hide();
    }
}
