package org.example.demotest.app_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.demotest.entities.Employee;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.managers.MainViewManager;
import org.example.demotest.services.EmployeeService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MainViewController {

    @FXML private Button logoutButton;
    @FXML private Button workersButton;
    @FXML private Button ordersButton;
    @FXML private Button clientButton;
    @FXML private Button departmentButton;
    @FXML private Button projectsButton;
    @FXML private Button productsButton;
    @FXML private Button qualitiesButton;
    @FXML private Button storagesButton;
    @FXML private Label  sessionLabel;


    public void initSessionLogin(final LoginManager loginManager, Long userPassport, ApplicationContext applicationContext) {

        Optional<Employee> employeeOptional = applicationContext.getBean(EmployeeService.class).getEmployeeByPassport(userPassport);

        if (employeeOptional.isPresent()) {
            sessionLabel.setText(String.valueOf(userPassport));
        }

        logoutButton.setOnAction(event -> loginManager.logout());
    }

    public void initWorkersWindow(final MainViewManager mainViewManager, Long userPassport, ApplicationContext applicationContext) {
        workersButton.setOnAction(event -> mainViewManager.employees(userPassport, applicationContext.getBean(EmployeeService.class).getEmployeeByPassport(userPassport).get().getRole()));
    }

    public void initOrdersWindow(final MainViewManager mainViewManager, Long userPassport, ApplicationContext applicationContext) {
        ordersButton.setOnAction(event -> mainViewManager.orders(userPassport, applicationContext.getBean(EmployeeService.class).getEmployeeByPassport(userPassport).get().getRole()));
    }

    public void initDepartmentsWindow(final MainViewManager mainViewManager, Long userPassport, ApplicationContext applicationContext) {
        departmentButton.setOnAction(event -> mainViewManager.departments(userPassport, applicationContext.getBean(EmployeeService.class).getEmployeeByPassport(userPassport).get().getRole()));
    }

    public void initClientsWindow(final MainViewManager mainViewManager, Long userPassport, ApplicationContext applicationContext) {
        clientButton.setOnAction(event -> mainViewManager.clients(userPassport, applicationContext.getBean(EmployeeService.class).getEmployeeByPassport(userPassport).get().getRole()));
    }

    public void initProjectsWindow(final MainViewManager mainViewManager, Long userPassport, ApplicationContext applicationContext) {
        projectsButton.setOnAction(event -> mainViewManager.projects(userPassport, applicationContext.getBean(EmployeeService.class).getEmployeeByPassport(userPassport).get().getRole()));
    }

    public void initQualitiesWindow(final MainViewManager mainViewManager, Long userPassport, ApplicationContext applicationContext) {
        qualitiesButton.setOnAction(event -> mainViewManager.qualities(userPassport, applicationContext.getBean(EmployeeService.class).getEmployeeByPassport(userPassport).get().getRole()));
    }

    public void initProductsWindow(final MainViewManager mainViewManager, Long userPassport, ApplicationContext applicationContext) {
        productsButton.setOnAction(event -> mainViewManager.products(userPassport, applicationContext.getBean(EmployeeService.class).getEmployeeByPassport(userPassport).get().getRole()));
    }

    public void initStoragesWindow(final MainViewManager mainViewManager, Long userPassport, ApplicationContext applicationContext) {
        storagesButton.setOnAction(event -> mainViewManager.storages(userPassport, applicationContext.getBean(EmployeeService.class).getEmployeeByPassport(userPassport).get().getRole()));
    }
}