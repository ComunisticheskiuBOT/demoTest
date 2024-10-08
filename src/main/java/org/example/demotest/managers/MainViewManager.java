package org.example.demotest.managers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.demotest.app_controllers.*;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainViewManager {
    private Stage stage;
    private ApplicationContext applicationContext;

    public MainViewManager(Stage stage, ApplicationContext applicationContext) {
        this.stage = stage;
        this.applicationContext = applicationContext;
    }

    public void employees(){
        showEmployeeWindow();
    }
    public void clients(){
        showClientWindow();
    }
    public void departments(){
        showDepartmentWindow();
    }
    public void orders(){
        showOrderWindow();
    }
    public  void projects() { showProjectWindow(); }
    public  void products() { showProductWindow(); }
    public  void qualities() { showQualityWindow(); }

    private void showEmployeeWindow() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/employee_add.fxml")
            );
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            stage.setTitle("Панель управления сотрудниками");
            stage.setScene(scene);
            stage.show();
            EmployeeAppController controller = loader.getController();
            loader.setControllerFactory(applicationContext::getBean);

            controller.initialize();
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showClientWindow() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/client_add.fxml")
            );
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 500);
            stage.setTitle("Панель управления клиентами");
            stage.setScene(scene);
            stage.show();
            ClientAppController controller = loader.getController();
            loader.setControllerFactory(applicationContext::getBean);

            controller.initialize();
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showDepartmentWindow() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/department_add.fxml")
            );
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Панель управления отделами");
            stage.setScene(scene);
            stage.show();
            DepartmentAppController controller = loader.getController();
            loader.setControllerFactory(applicationContext::getBean);
            controller.initialize();
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showOrderWindow() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/order_add.fxml")
            );
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Панель управления заказами");
            stage.setScene(scene);
            stage.show();
            OrderAppController controller = loader.getController();
            loader.setControllerFactory(applicationContext::getBean);
            controller.initialize();
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showProjectWindow() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/project_add.fxml")
            );
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Панель управления проектами");
            stage.setScene(scene);
            stage.show();
            ProjectAppController controller = loader.getController();
            loader.setControllerFactory(applicationContext::getBean);
            controller.initialize();
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showProductWindow() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/product_add.fxml")
            );
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Панель управления продуктами");
            stage.setScene(scene);
            stage.show();
            ProductAppController controller = loader.getController();
            loader.setControllerFactory(applicationContext::getBean);
            controller.initialize();
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showQualityWindow() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/UI/quality_add.fxml")
            );
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Панель управления качеством");
            stage.setScene(scene);
            stage.show();
            QualityAppController controller = loader.getController();
            loader.setControllerFactory(applicationContext::getBean);
            controller.initialize();
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
