package org.example.demotest.app_controllers;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.managers.MainViewManager;
import org.springframework.stereotype.Component;

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

    private MainViewManager mainViewManager;

    public void initialize() {}

    public void initSessionID(final LoginManager loginManager, String sessionID) {
        sessionLabel.setText(sessionID);
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                loginManager.logout();
            }
        });
    }

    public void initWorkersWindow(final MainViewManager mainViewManager) {
        workersButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.employees();
            }
        });
    }

    public void initOrdersWindow(final MainViewManager mainViewManager) {
        ordersButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.orders();
            }
        });
    }

    public void initDepartmentsWindow(final MainViewManager mainViewManager) {
        departmentButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.departments();
            }
        });
    }

    public void initClientsWindow(final MainViewManager mainViewManager) {
        clientButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.clients();
            }
        });
    }

    public void initProjectsWindow(final MainViewManager mainViewManager) {
        projectsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.projects();
            }
        });
    }

    public void initQualitiesWindow(final MainViewManager mainViewManager) {
        qualitiesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.qualities();
            }
        });
    }

    public void initProductsWindow(final MainViewManager mainViewManager) {
        productsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.products();
            }
        });
    }

    public void initStoragesWindow(final MainViewManager mainViewManager) {
        storagesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.storages();
            }
        });
    }
}
