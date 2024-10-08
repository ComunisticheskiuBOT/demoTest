package org.example.demotest.app_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.demotest.dto.ServiceRequestDepartment;
import org.example.demotest.dto.ServiceRequestOrder;
import org.example.demotest.entities.Department;
import org.example.demotest.entities.Order;
import org.example.demotest.entities.OrderStatus;
import org.example.demotest.entities.Status;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.DepartmentService;
import org.example.demotest.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.example.demotest.app_controllers.LoginController.sessionID;

@Controller
public class OrderAppController {

    @Autowired
    private ApplicationContext applicationContext;

    private TextFormatter<String> createNumericFilter() {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        });
    }

    private TextFormatter<String> createDateFilter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.length() > 10) {
                return null;
            }

            if (newText.matches("^\\d{0,2}$") ||             // День
                    newText.matches("^\\d{2}-$") ||              // День-
                    newText.matches("^\\d{2}-(0[1-9]?|1[0-2]?)$") || // День-месяц
                    newText.matches("^\\d{2}-\\d{2}-$") ||       // День-месяц-
                    newText.matches("^\\d{2}-\\d{2}-\\d{0,4}$")) {  // День-месяц-год
                return change;
            }

            return null;
        });
    }

    private RestTemplate restTemplate = new RestTemplate();
    private String url = "http://localhost:8080/order-api/v1/orders";

    //Таблица
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Long> id;
    @FXML private TableColumn<Order, Long> client_id;
    @FXML private TableColumn<Order, String> project_id;
    @FXML private TableColumn<Order, Date> order_date;
    @FXML private TableColumn<Order, Date> completion_date;
    @FXML private TableColumn<Order, OrderStatus> order_status;
    @FXML private TableColumn<Order, String> order_description;

    //Поля для создания пользователя
    @FXML private TextField clientIdField;
    @FXML private TextField projectIdField;
    @FXML private TextField dateOfOrderField;
    @FXML private TextField dateOfExecutionField;
    @FXML private ComboBox<OrderStatus> statusComboBoxField;
    @FXML private TextField orderDescription;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter;
    @FXML private TextField clientFilter;
    @FXML private TextField projectFilter;
    @FXML private ComboBox<OrderStatus> statusFilter;

    ObservableList<Order> observableOrderList = FXCollections.observableArrayList();


    public void initialize() {
        id.setCellValueFactory(new PropertyValueFactory<>("Id"));
        client_id.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        project_id.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        order_date.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        completion_date.setCellValueFactory(new PropertyValueFactory<>("completionDate"));
        order_status.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        order_description.setCellValueFactory(new PropertyValueFactory<>("orderDescription"));
        statusComboBoxField.getItems().setAll(org.example.demotest.entities.OrderStatus.values());

        clientIdField.setTextFormatter(createDateFilter());
        projectIdField.setTextFormatter(createDateFilter());
        dateOfOrderField.setTextFormatter(createDateFilter());
        dateOfExecutionField.setTextFormatter(createDateFilter());

        setupFilters();
        loadOrders();
    }

    @FXML
    private void handleUpdateOrders(){
        loadOrders();
    }

    @FXML
    private void handleCleanButton(){
        clientIdField.setText(null);
        projectIdField.setText(null);
        dateOfOrderField.setText(null);
        dateOfExecutionField.setText(null);
        statusComboBoxField.getSelectionModel().clearSelection();
        orderDescription.setText(null);
    }

    private void loadOrders() {
        try {
            Order[] orders = restTemplate.getForObject(url, Order[].class);
            if (orders != null && orders.length > 0) { // Проверяем, что массив не пустой
                observableOrderList.setAll(Arrays.asList(orders));
                orderTable.setItems(observableOrderList);
            } else {
                orderTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке отделов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddOrder(ActionEvent event) {
        try {
            Long clientID= Long.valueOf(clientIdField.getText());
            if (!orderService.clientExists(clientID)) { // Проверка существования
                System.out.println("Клиент с указанным ID не найден.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date parsedDateOfOrder = sdf.parse(dateOfOrderField.getText());
            Date parsedDateExecution = sdf.parse(dateOfExecutionField.getText());
            ServiceRequestOrder newOrder = ServiceRequestOrder.builder()
                    .clientId(Long.valueOf(clientIdField.getText()))
                    .projectId(Long.valueOf(projectIdField.getText()))
                    .dateOfOrder(parsedDateOfOrder)
                    .dateOfExecution(parsedDateExecution)
                    .orderStatus(statusComboBoxField.getValue())
                    .orderDescription(orderDescription.getText())
                    .build();
            System.out.println("Заказ добавлен: " + newOrder);
            Order orderCreated = restTemplate.postForObject(url, newOrder, Order.class);
            if (orderCreated != null) {
                observableOrderList.add(orderCreated); // Используйте observableUserList
                orderTable.setItems(observableOrderList);
                System.out.println("Заказ добавлен: " + orderCreated);
            }
        } catch (Exception e) {
            // Логирование ошибки и/или уведомление пользователя
            e.printStackTrace();

            System.out.println("Ошибка при добавлении заказа: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        clientFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        projectFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = idFilter.getText();
        String clientID = clientFilter.getText().toLowerCase();
        String projectID = projectFilter.getText().toLowerCase();

        orderTable.setItems(observableOrderList.filtered(order -> {
            boolean matchesId = id.isEmpty() || String.valueOf(order.getOrderId()).contains(id);
            boolean matchesClientID = String.valueOf(order.getOrderId()).contains(clientID);
            boolean matchesProjectID = String.valueOf(order.getOrderId()).contains(projectID);

            return matchesId && matchesClientID && matchesProjectID;
        }));
    }

    @Autowired
    private OrderService orderService;

    @FXML
    private void handleDeleteOrder() {
        Long idText = Long.valueOf(deleteIdField.getText());

        if (idText != null) {
            // Найдем отдел по Id
            Order order = orderService.findOrderById(idText);
            if (order != null) {
                orderService.deleteOrder(order.getOrderId());
                loadOrders();
            } else {
                System.out.println("Заказ с указанным ID не найден");
            }
        } else {
            // Обработка ситуации, если оба поля пусты
            System.out.println("Введите ID для удаления заказа");
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(String.valueOf(sessionID));
    }
}
