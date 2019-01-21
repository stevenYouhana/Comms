package UI;

import Com.Log;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Comms extends Application {
    Log log = new Log();
    public static boolean MainWindowClosed = false;
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("For Serial");
        stage.show();
        stage.setOnCloseRequest((WindowEvent event) -> {
            log.l("main terminated");            
            MainWindowClosed = true;
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        System.out.println("Comms main");
        launch(args);
    }
    public static Thread getCurrentThread() {
        return Thread.currentThread();
    }
    
}
