package org.example.demotest.managers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.demotest.app_controllers.*;
import org.example.demotest.entities.Role;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainViewManager {
    private final Stage stage;
    private final ApplicationContext applicationContext;


    public MainViewManager(Stage stage, ApplicationContext applicationContext) {
        this.stage = stage;
        this.applicationContext = applicationContext;
    }

    public void employees(Long userPassport, Role role){
        showEmployeeWindow(userPassport, role);
    }
    public void clients(Long userPassport, Role role){
        showClientWindow(userPassport, role);
    }
    public void departments(Long userPassport, Role role){
        showDepartmentWindow(userPassport, role);
    }
    public void orders(Long userPassport, Role role){
        showOrderWindow(userPassport, role);
    }
    public  void projects(Long userPassport, Role role) { showProjectWindow(userPassport, role); }
    public  void products(Long userPassport, Role role) { showProductWindow(userPassport, role); }
    public  void qualities(Long userPassport, Role role) { showQualityWindow(userPassport, role); }
    public void storages(Long userPassport, Role role) { showStorageWindow(userPassport, role); }

    private void showEmployeeWindow(Long userPassport, Role role) {

        try {
            if(role == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/AdminInterface/employee_add.fxml")
                );
                employeeWindow(userPassport, loader, 800);
            }
            else if(role == Role.MODERATOR) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/ModeratorInterface/employee_add.fxml")
                );
                employeeWindow(userPassport, loader, 800);
            }
            else if(role == Role.USER) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/UserInterface/employee_check.fxml")
                );
                employeeWindow(userPassport, loader, 500);
            }

        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void employeeWindow(Long userPassport, FXMLLoader loader, int w) throws IOException {
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 1480, w);
        stage.setTitle("Панель управления сотрудниками");
        stage.setScene(scene);
        stage.show();
        EmployeeAppController controller = loader.getController();
        loader.setControllerFactory(applicationContext::getBean);

        controller.initialize(userPassport);
    }

    private void showClientWindow(Long userPassport, Role role) {

        try {
            if (role == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/AdminInterface/client_add.fxml")
                );
                clientWindow(loader, userPassport);
            }
            else if (role == Role.MODERATOR) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/ModeratorInterface/client_add.fxml")
                );
                clientWindow(loader, userPassport);
            }
            else if (role == Role.USER) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/UserInterface/client_check.fxml")
                );
                clientWindow(loader, userPassport);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clientWindow(FXMLLoader loader,Long userPassport) throws IOException {
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Панель управления клиентами");
        stage.setScene(scene);
        stage.show();
        ClientAppController controller = loader.getController();
        loader.setControllerFactory(applicationContext::getBean);

        controller.initialize(userPassport);
    }

    private void showDepartmentWindow(Long userPassport, Role role) {

        try {
            if (role == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/AdminInterface/department_add.fxml")
                );
                departmentWindow(loader, userPassport);
            }
            else if (role == Role.MODERATOR) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/ModeratorInterface/department_add.fxml")
                );
                departmentWindow(loader, userPassport);
            }
            else if (role == Role.USER) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/UserInterface/department_check.fxml")
                );
                departmentWindow(loader, userPassport);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void departmentWindow(FXMLLoader loader, Long userPassport) throws IOException {
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Панель управления отделами");
        stage.setScene(scene);
        stage.show();
        DepartmentAppController controller = loader.getController();
        loader.setControllerFactory(applicationContext::getBean);
        controller.initialize(userPassport);
    }

    private void showOrderWindow(Long userPassport, Role role) {

        try {
            if (role == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/AdminInterface/order_add.fxml")
                );
                orderWindow(loader, userPassport);
            }
            else if (role == Role.MODERATOR) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/ModeratorInterface/order_add.fxml")
                );
                orderWindow(loader, userPassport);
            }
            else if (role == Role.USER) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/UserInterface/order_check.fxml")
                );
                orderWindow(loader, userPassport);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void orderWindow(FXMLLoader loader, Long userPassport) throws IOException {
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Панель управления заказами");
        stage.setScene(scene);
        stage.show();
        OrderAppController controller = loader.getController();
        loader.setControllerFactory(applicationContext::getBean);
        controller.initialize(userPassport);
    }

    private void showProjectWindow(Long userPassport, Role role) {

        try {
            if (role == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/AdminInterface/project_add.fxml")
                );
                projectWindow(loader, userPassport);
            } else if (role == Role.MODERATOR) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/ModeratorInterface/project_add.fxml")
                );
                projectWindow(loader, userPassport);
            } else if (role == Role.USER) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/UserInterface/project_check.fxml")
                );
                projectWindow(loader, userPassport);
            }

        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void projectWindow(FXMLLoader loader, Long userPassport) throws IOException {
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Панель управления проектами");
        stage.setScene(scene);
        stage.show();
        ProjectAppController controller = loader.getController();
        loader.setControllerFactory(applicationContext::getBean);
        controller.initialize(userPassport);
    }

    private void showProductWindow(Long userPassport, Role role) {

        try {
            if (role == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/AdminInterface/product_add.fxml")
                );
                productWindow(userPassport, loader);
            } else if (role == Role.MODERATOR) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/ModeratorInterface/product_add.fxml")
                );
                productWindow(userPassport, loader);
            }
            else if (role == Role.USER) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/UserInterface/product_check.fxml")
                );
                productWindow(userPassport, loader);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void productWindow(Long userPassport, FXMLLoader loader) throws IOException {
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Панель управления продуктами");
        stage.setScene(scene);
        stage.show();
        ProductAppController controller = loader.getController();
        loader.setControllerFactory(applicationContext::getBean);
        controller.initialize(userPassport);
    }

    private void showStorageWindow(Long userPassport, Role role) {

        try {
            if (role == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/AdminInterface/storage_add.fxml")
                );
                storageWindow(userPassport, loader);
            } else if (role == Role.MODERATOR) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/ModeratorInterface/storage_add.fxml")
                );
                storageWindow(userPassport, loader);

            } else if (role == Role.USER) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/UserInterface/storage_check.fxml")
                );
                storageWindow(userPassport, loader);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void storageWindow(Long userPassport, FXMLLoader loader) throws IOException {
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 500);
        stage.setTitle("Панель управления складом");
        stage.setScene(scene);
        stage.show();
        StorageAppController controller = loader.getController();
        loader.setControllerFactory(applicationContext::getBean);
        controller.initialize(userPassport);
    }

    private void showQualityWindow(Long userPassport, Role role) {

        try {
            if (role == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/AdminInterface/quality_add.fxml")
                );
                qualityWindow(userPassport, loader);
            }
            else if (role == Role.MODERATOR) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/ModeratorInterface/quality_add.fxml")
                );
                qualityWindow(userPassport, loader);
            }
            else if (role == Role.USER) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/UserInterface/quality_check.fxml")
                );
                qualityWindow(userPassport, loader);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void qualityWindow(Long userPassport, FXMLLoader loader) throws IOException {
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Панель управления качеством");
        stage.setScene(scene);
        stage.show();
        QualityAppController controller = loader.getController();
        loader.setControllerFactory(applicationContext::getBean);
        controller.initialize(userPassport);
    }
}
