package org.example.demotest.managers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.demotest.app_controllers.UserAppController;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainViewManager {
    private Stage stage;


    public MainViewManager(Stage stage) {
        this.stage = stage;
    }


    public void users(){
        showUserWindow();
    }
    private void showUserWindow() {
        Button handleBackButton;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/user_add.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Панель управления пользователями");
            stage.setScene(scene);
            stage.show();
            UserAppController controller = loader.getController();
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
