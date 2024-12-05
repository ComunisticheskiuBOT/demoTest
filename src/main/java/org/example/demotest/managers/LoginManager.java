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
    public Stage stage;
    public ApplicationContext applicationContext;

    public LoginManager(Stage stage, ApplicationContext applicationContext) {
        this.stage = stage;
        this.applicationContext = applicationContext;
    }

    public void authenticated(Long userPassport) {
        showMainView(userPassport);
    }

    public void logout() {
        showLoginScreen();
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/CommonInterface/login.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            if (stage == null) {
                throw new IllegalStateException("Stage is not initialized");
            }

            Scene scene = new Scene(root, 300, 150);
            stage.setTitle("Авторизация");
            stage.setScene(scene);
            stage.show();

            LoginController controller = loader.getController();

            controller.initManager(this);
            controller.initConnected(new ConnectorManager(stage, applicationContext));
        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, "Error loading FXML", ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void showMainView(Long userPassport) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/GUI/CommonInterface/mainview.fxml")
            );

            Parent root = loader.load();
            Scene scene = new Scene(root, 300, 500);
            stage.setTitle("Панель управления");
            stage.setScene(scene);
            stage.show();

            MainViewController controller = loader.getController();
            loader.setControllerFactory(applicationContext::getBean);
            controller.initSessionLogin(this, userPassport, applicationContext);
            controller.initWorkersWindow(new MainViewManager(stage, applicationContext),userPassport, applicationContext);
            controller.initOrdersWindow(new MainViewManager(stage, applicationContext),userPassport, applicationContext);
            controller.initDepartmentsWindow(new MainViewManager(stage, applicationContext),userPassport, applicationContext);
            controller.initClientsWindow(new MainViewManager(stage, applicationContext),userPassport, applicationContext);
            controller.initProjectsWindow(new MainViewManager(stage, applicationContext),userPassport, applicationContext);
            controller.initProductsWindow(new MainViewManager(stage, applicationContext),userPassport, applicationContext);
            controller.initQualitiesWindow(new MainViewManager(stage, applicationContext),userPassport, applicationContext);
            controller.initStoragesWindow(new MainViewManager(stage, applicationContext),userPassport, applicationContext);
            //controller.initMeasurementsWindow(new MainViewManager(stage, applicationContext),userPassport, applicationContext);
        } catch (IOException ex) {
            Logger.getLogger(LoginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
