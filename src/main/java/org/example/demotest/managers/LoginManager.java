package org.example.demotest.managers;

import java.io.IOException;
import java.util.logging.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import org.example.demotest.app_controllers.LoginController;
import org.example.demotest.app_controllers.MainViewController;
import org.springframework.context.ApplicationContext;

public class LoginManager {
    private Stage stage;
    private ApplicationContext applicationContext;

    public LoginManager(Stage stage, ApplicationContext applicationContext) {
        this.stage = stage;
        this.applicationContext = applicationContext;
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
            loader.setControllerFactory(applicationContext::getBean);
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
            loader.setControllerFactory(applicationContext::getBean);

            controller.initSessionID(this, sessionID);
            controller.initWorkersWindow(new MainViewManager(stage, applicationContext));
            controller.initOrdersWindow(new MainViewManager(stage, applicationContext));
            controller.initDepartmentsWindow(new MainViewManager(stage, applicationContext));
            controller.initClientsWindow(new MainViewManager(stage, applicationContext));
            controller.initProjectsWindow(new MainViewManager(stage, applicationContext));
            controller.initProductsWindow(new MainViewManager(stage, applicationContext));
            controller.initQualitiesWindow(new MainViewManager(stage, applicationContext));

        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
