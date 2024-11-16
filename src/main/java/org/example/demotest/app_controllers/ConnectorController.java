package org.example.demotest.app_controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.demotest.managers.ConnectorManager;
import org.example.demotest.managers.LoginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class ConnectorController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DriverManagerDataSource dataSource;

    @FXML private TextField url;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button connectorButton;
    @FXML private Button toLogin;


    public void initLoginWindow(final ConnectorManager connectorManager) {
        toLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                connectorManager.login();
            }
        });
    }

    @FXML
    public void initialize() {
        connectorButton.setOnAction(event -> updateDataSource());
    }

    public void updateDataSource() {
        dataSource.setUrl(url.getText());
        dataSource.setUsername(username.getText());
        dataSource.setPassword(password.getText());

        System.out.println("Параметры подключения к базе данных обновлены: " + dataSource.getUrl());
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showLoginScreen();
    }
}