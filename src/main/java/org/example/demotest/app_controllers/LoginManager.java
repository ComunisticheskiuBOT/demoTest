package org.example.demotest.app_controllers;

import java.io.IOException;
import java.util.logging.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;

public class LoginManager {
    private Scene scene;

    public LoginManager(Scene scene) {
        this.scene = scene;
    }

    public void authenticated(String sessionID) {
        showMainView(sessionID);
    }

    public void logout() {
        showLoginScreen();
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/login.fxml")
            );
            scene.setRoot((Parent) loader.load());
            LoginController controller =
                    loader.<LoginController>getController();
            controller.initManager(this);
        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showMainView(String sessionID) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/mainview.fxml")
            );
            scene.setRoot((Parent) loader.load());
            MainViewController controller =
                    loader.<MainViewController>getController();
            controller.initSessionID(this, sessionID);
            controller.initUsersWindow(new MainViewManager(scene));
            controller.initWorkersWindow(new MainViewManager(scene));
            controller.initOrdersWindow(new MainViewManager(scene));
            controller.initCustomersWindow(new MainViewManager(scene));
            controller.initProductsWindow(new MainViewManager(scene));
            controller.initComponentsWindow(new MainViewManager(scene));
            controller.initMaterialsWindow(new MainViewManager(scene));
            controller.initTransportsWindow(new MainViewManager(scene));

        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
