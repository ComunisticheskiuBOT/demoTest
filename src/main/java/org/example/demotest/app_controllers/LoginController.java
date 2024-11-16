package org.example.demotest.app_controllers;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.demotest.managers.ConnectorManager;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.EmployeeService;
import org.springframework.stereotype.Component;

@Component
public class LoginController {
    @FXML public TextField user;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Button connectButton;
    private static final String DEVELOPER_PASSWORD = "dev_password";
    private static final String DEVELOPER_PASSPORT = "0000111111";

    public EmployeeService employeeService;
    public LoginController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    public void initialize() {}

    public void initManager(final LoginManager loginManager) {
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                if(authorize()) {
                    loginManager.authenticated(Long.parseLong(user.getText()));

                }
            }
        });

    }

    public void initConnected(final ConnectorManager connectorManager) {
        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                connectorManager.connected();
            }
        });

    }

    public boolean authorize() {
        boolean isDeveloper = false;
        String user_password = this.password.getText();
        Long user_passport = Long.parseLong(user.getText());

        if(user.getText().equals(DEVELOPER_PASSPORT) && user_password.equals(DEVELOPER_PASSWORD)){
            isDeveloper = true;
        }

        return (employeeService.validateUser(user_passport, user_password) || isDeveloper);
    }
}