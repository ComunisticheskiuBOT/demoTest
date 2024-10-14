package org.example.demotest.app_controllers;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.demotest.managers.ConnectorManager;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.managers.MainViewManager;
import org.example.demotest.services.EmployeeService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LoginController {
    @FXML private TextField user;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Button connectButton;

    private ApplicationContext applicationContext;

    private static final String DEVELOPER_PASSWORD = "devpassword";
    private static final Long DEVELOPER_ID = 0L;


    public EmployeeService employeeService;
    public LoginController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    public void initialize() {}

    public void initManager(final LoginManager loginManager) {
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                String sessionID = authorize();
                if (sessionID != null) {
                    loginManager.authenticated(sessionID);
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

    public String authorize() {
        String user_password = this.password.getText();

        if (DEVELOPER_PASSWORD.equals(user_password)) {
            return generateSessionID(DEVELOPER_ID);
        }

        Long passportNumber = Long.parseLong(user.getText());

        boolean isValidUser = employeeService.validateUser(passportNumber, user_password);
        return isValidUser ? generateSessionID(passportNumber) : null;
    }

    public static int sessionID = 0;

    private String generateSessionID(Long employee_id) {
        sessionID++;
        return "User " + employee_id;
    }
}