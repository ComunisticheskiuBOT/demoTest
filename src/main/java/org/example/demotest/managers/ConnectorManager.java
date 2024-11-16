package org.example.demotest.managers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.demotest.app_controllers.ConnectorController;
import org.example.demotest.app_controllers.LoginController;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectorManager {
    private Stage stage;
    private ApplicationContext applicationContext;

    public ConnectorManager(Stage stage, ApplicationContext applicationContext) {
        this.stage = stage;
        this.applicationContext = applicationContext;
    }

    public void connected() {
        showConnectorScreen();
    }
    public void login() {
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

            controller.initManager(new LoginManager(stage, applicationContext));
            controller.initConnected(new ConnectorManager(stage, applicationContext));
        } catch (IOException ex) {
            Logger.getLogger(ConnectorManager.class.getName()).log(Level.SEVERE, "Error loading FXML", ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(ConnectorManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void showConnectorScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/CommonInterface/db_connector.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            if (stage == null) {
                throw new IllegalStateException("Stage is not initialized");
            }

            Scene scene = new Scene(root, 300, 150);
            stage.setTitle("Коннектор БД");
            stage.setScene(scene);
            stage.show();

            ConnectorController controller = loader.getController();
            loader.setControllerFactory(applicationContext::getBean);
            controller.initLoginWindow(new ConnectorManager(stage, applicationContext));
        } catch (IOException ex) {
            Logger.getLogger(ConnectorManager.class.getName()).log(Level.SEVERE, "Error loading FXML", ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(ConnectorManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
