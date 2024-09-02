package org.example.demotest.app_controllers;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class MainViewController {

    @FXML private Button logoutButton;
    @FXML private Button usersButton;
    @FXML private Button workersButton;
    @FXML private Button ordersButton;
    @FXML private Button customersButton;
    @FXML private Button productsButton;
    @FXML private Button componentsButton;
    @FXML private Button materialsButton;
    @FXML private Button transportsButton;
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

    public void initUsersWindow(final MainViewManager mainViewManager) {
        usersButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.users();
            }
        });
    }

    public void initWorkersWindow(final MainViewManager mainViewManager) {
        workersButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.users();
            }
        });
    }

    public void initOrdersWindow(final MainViewManager mainViewManager) {
        ordersButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.users();
            }
        });
    }

    public void initCustomersWindow(final MainViewManager mainViewManager) {
        customersButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.users();
            }
        });
    }

    public void initProductsWindow(final MainViewManager mainViewManager) {
        productsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.users();
            }
        });
    }

    public void initComponentsWindow(final MainViewManager mainViewManager) {
        componentsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.users();
            }
        });
    }

    public void initMaterialsWindow(final MainViewManager mainViewManager) {
        materialsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.users();
            }
        });
    }

    public void initTransportsWindow(final MainViewManager mainViewManager) {
        transportsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                mainViewManager.users();
            }
        });
    }
}
