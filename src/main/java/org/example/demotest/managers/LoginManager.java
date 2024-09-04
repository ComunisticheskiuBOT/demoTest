package org.example.demotest.managers;

import java.io.IOException;
import java.util.logging.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import org.example.demotest.app_controllers.LoginController;
import org.example.demotest.app_controllers.MainViewController;

public class LoginManager {
    private Stage stage;


    public LoginManager(Stage stage) {
        this.stage = stage;
    }

    public void authenticated(String sessionID) {
        showMainView(sessionID);
    }

    public void logout() {
        showLoginScreen();
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/login.fxml"));
            Parent root = loader.load();

            if (stage == null) {
                throw new IllegalStateException("Stage is not initialized");
            }

            Scene scene = new Scene(root, 250, 150);
            stage.setTitle("Авторизация");
            stage.setScene(scene);
            stage.show();

            LoginController controller = loader.getController();
            controller.initManager(this);
        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, "Error loading FXML", ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void showMainView(String sessionID) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/mainview.fxml")
            );

            Parent root = loader.load();
            Scene scene = new Scene(root, 250, 500);
            stage.setTitle("Панель управления");
            stage.setScene(scene);
            stage.show();

            MainViewController controller = loader.getController();

            controller.initSessionID(this, sessionID);
            controller.initUsersWindow(new MainViewManager(stage));
            controller.initWorkersWindow(new MainViewManager(stage));
            controller.initOrdersWindow(new MainViewManager(stage));
            controller.initCustomersWindow(new MainViewManager(stage));
            controller.initProductsWindow(new MainViewManager(stage));
            controller.initComponentsWindow(new MainViewManager(stage));
            controller.initMaterialsWindow(new MainViewManager(stage));
            controller.initTransportsWindow(new MainViewManager(stage));

        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
