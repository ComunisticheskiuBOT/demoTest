package org.example.demotest.app_controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainViewManager {
    private Scene scene;

    public MainViewManager(Scene scene) {
        this.scene = scene;
    }


    public void users(){
        showUserWindow();
    }
    private void showUserWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/user_add.fxml")
            );
            scene.setRoot((Parent) loader.load());
            UserAppController controller =
                    loader.<UserAppController>getController();
//            controller.initButtonBack(this);
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
