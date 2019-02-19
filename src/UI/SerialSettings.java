package UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class SerialSettings extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SerialSettings.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Advanced settings");
        stage.setResizable(false);
        stage.show();
    }
    public void hide() {
        this.hide();
    }
}
