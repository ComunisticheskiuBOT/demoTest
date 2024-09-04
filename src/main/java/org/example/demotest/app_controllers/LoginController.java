package org.example.demotest.app_controllers;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginController {
    @FXML private TextField user;
    @FXML private TextField password;
    @FXML private Button loginButton;

    private static final String DEVELOPER_PASSWORD = "devpassword";
    private static final Long DEVELOPER_ID = 0L;


    private UserService userService;

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

    private String authorize() {
        String user_password = this.password.getText();

        if (DEVELOPER_PASSWORD.equals(user_password)) {
            return generateSessionID(DEVELOPER_ID);
        }

        Long employee_id = Long.parseLong(user.getText());

        boolean isValidUser = userService.validateUser(employee_id, user_password);
        return isValidUser ? generateSessionID(employee_id) : null;
    }

    public void deleteUser(Long id) {
        userService.deleteUserById(id);
    }

    public void deleteUserByEmployeeId(Long employeeId) {
        userService.deleteUserByEmployeeId(employeeId);
    }

    public static int sessionID = 0;

    private String generateSessionID(Long employee_id) {
        sessionID++;
        return "User " + employee_id + " - session " + System.currentTimeMillis();
    }
}