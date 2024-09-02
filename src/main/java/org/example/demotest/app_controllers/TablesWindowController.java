package org.example.demotest.app_controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.demotest.DemoTestApplication;
import org.springframework.stereotype.Component;

@Component
public class TablesWindowController {

    @FXML
    private Button btn_back;

    @FXML
    private void handleUsers() {

    }

    @FXML
    private void handleWorkers() {
        // Логика открытия окна работников
    }

    @FXML
    private void handleOrders() {
        // Логика открытия окна заказов
    }

    @FXML
    private void handleCustomers() {
        // Логика открытия окна заказчиков
    }

    @FXML
    private void handleProducts() {
        // Логика открытия окна продукции
    }

    @FXML
    private void handleComponents() {
        // Логика открытия окна составляющих
    }

    @FXML
    private void handleMaterials() {
        // Логика открытия окна материалов
    }

    @FXML
    private void handleTransport() {
        // Логика открытия окна транспорта
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) btn_back.getScene().getWindow();
        stage.close();
        Platform.runLater(() -> {
            try {
                new DemoTestApplication().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
