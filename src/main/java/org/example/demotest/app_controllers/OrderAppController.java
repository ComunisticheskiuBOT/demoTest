package org.example.demotest.app_controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.demotest.dto.ServiceRequestOrder;
import org.example.demotest.entities.*;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.demotest.app_controllers.EmployeeAppController.DateFilter;


@Controller
public class OrderAppController {
    private final Logger logger = Logger.getLogger(OrderAppController.class.getName());

    private final EmployeeService employeeService;
    private final ClientService clientService;
    private final ProjectService projectService;
    private final OrderService orderService;
    private final ApplicationContext applicationContext;

    public OrderAppController(ApplicationContext applicationContext, EmployeeService employeeService, ClientService clientService, ProjectService projectService, OrderService orderService){
        this.employeeService = employeeService;
        this.clientService = clientService;
        this.projectService = projectService;
        this.orderService = orderService;
        this.applicationContext = applicationContext;
    }

    private TextFormatter<String> createNumericFilter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d*")) {
                return change;
            }
            return null;
        });
    }

    private TextFormatter<String> createDateFilter() {
        return DateFilter();
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/order-api/v1/orders";

    //Таблица
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Long> id;
    @FXML private TableColumn<Order, Long> client_id;
    @FXML private TableColumn<Order, Long> project_id;
    @FXML private TableColumn<Order, Date> order_date;
    @FXML private TableColumn<Order, Date> completion_date;
    @FXML private TableColumn<Order, OrderStatus> order_status;
    @FXML private TableColumn<Order, String> order_description;

    //Поля для создания пользователя
    @FXML private TextField clientIdField, projectIdField, dateOfOrderField, dateOfExecutionField, orderDescription;
    @FXML private ComboBox<OrderStatus> statusComboBoxField;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter, clientFilter, projectFilter;
    @FXML private ComboBox<OrderStatus> statusFilter;

    ObservableList<Order> observableOrderList = FXCollections.observableArrayList();

    private Long userPassport;
    private Role userRole;

    public void initialize(Long userPassport) {
        this.userPassport = userPassport;
        this.userRole = employeeService
                .findEmployeeByPassportNumber(userPassport)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с данным номером паспорта не найден"))
                .getRole();        id.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        client_id.setCellValueFactory(cellData -> {
            Client client = cellData.getValue().getClient();
            return new SimpleObjectProperty<>(client != null ? client.getClientId() : null);
        });

        project_id.setCellValueFactory(cellData -> {
            Project project = cellData.getValue().getProject();
            return new SimpleObjectProperty<>(project != null ? project.getProjectId() : null);
        });

        order_date.setCellValueFactory(new PropertyValueFactory<>("dateOfOrder"));
        completion_date.setCellValueFactory(new PropertyValueFactory<>("dateOfExecution"));
        order_status.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        order_description.setCellValueFactory(new PropertyValueFactory<>("orderDescription"));

        if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
            statusComboBoxField.getItems().setAll(org.example.demotest.entities.OrderStatus.values());
            statusComboBoxField.getItems().setAll(org.example.demotest.entities.OrderStatus.values());
            clientIdField.setTextFormatter(createDateFilter());
            projectIdField.setTextFormatter(createDateFilter());
            dateOfOrderField.setTextFormatter(createDateFilter());
            dateOfExecutionField.setTextFormatter(createDateFilter());
        }

        setupFilters();
        loadOrders();
    }

    @FXML
    private void handleUpdateOrders(){
        loadOrders();
    }

    @FXML
    private void handleCleanButton(){
        if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
            clientIdField.setText("");
            projectIdField.setText("");
            dateOfOrderField.setText("");
            dateOfExecutionField.setText("");
            statusComboBoxField.getSelectionModel().clearSelection();
            orderDescription.setText("");
        }
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
            logger.log(Level.SEVERE,"Ошибка при загрузке отделов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddOrder(ActionEvent event) {

        try {
            if (userRole == org.example.demotest.entities.Role.ADMIN) {

                Long clientIdValue = Long.valueOf(clientIdField.getText());
                Client client = orderService.getClientById(clientIdValue);

                if (client == null) {
                    System.out.println("Клиент с указанным ID не найден.");
                    return;
                }

                Long projectIdValue = Long.valueOf(projectIdField.getText());
                Project project = orderService.getProjectById(projectIdValue);

                if (project == null) {
                    System.out.println("Проект с указанным ID не найден.");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date parsedDateOfOrder = sdf.parse(dateOfOrderField.getText());
                Date parsedDateExecution = sdf.parse(dateOfExecutionField.getText());
                ServiceRequestOrder newOrder = ServiceRequestOrder.builder()
                        .client(client)
                        .project(project)
                        .dateOfOrder(parsedDateOfOrder)
                        .dateOfExecution(parsedDateExecution)
                        .orderStatus(statusComboBoxField.getValue())
                        .orderDescription(orderDescription.getText())
                        .build();
                Order orderCreated = restTemplate.postForObject(url, newOrder, Order.class);
                if (orderCreated != null) {
                    observableOrderList.add(orderCreated); // Используйте observableUserList
                    orderTable.setItems(observableOrderList);
                    System.out.println("Заказ добавлен: " + orderCreated);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при добавлении заказа: " + e.getMessage());
        }
    }

    private Order selectedOrder;

    @FXML
    private void handleTableClick(MouseEvent event) {
        selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
                if (selectedOrder != null) {
                    clientIdField.setText(String.valueOf(selectedOrder.getClient().getClientId()));
                    projectIdField.setText(String.valueOf(selectedOrder.getProject().getProjectId()));
                    orderDescription.setText(selectedOrder.getOrderDescription());
                    dateOfExecutionField.setText(sdf.format(selectedOrder.getDateOfExecution()));
                    dateOfOrderField.setText(sdf.format(selectedOrder.getDateOfOrder()));
                    statusComboBoxField.setValue(selectedOrder.getOrderStatus());
                }
            }
        }  catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных заказа: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditOrder(ActionEvent event) {
        if (selectedOrder == null) {
            System.out.println("Не выбран заказ для изменения.");
            return;
        }

        try {
            if(userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
                ServiceRequestOrder updatedOrder = new ServiceRequestOrder();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date parsedDateOfExecutionField = sdf.parse(dateOfExecutionField.getText());
                Date parsedDateOfOrderField = sdf.parse(dateOfOrderField.getText());

                if (!clientIdField.getText().isEmpty()) {
                    updatedOrder.setClient(clientService.findClientById(Long.valueOf(clientIdField.getText())));
                }
                if (!projectIdField.getText().isEmpty()) {
                    updatedOrder.setProject(projectService.findProjectById(Long.valueOf(projectIdField.getText())));
                }
                if (!dateOfOrderField.getText().isEmpty()) {
                    updatedOrder.setDateOfOrder(parsedDateOfExecutionField);
                }
                if (!dateOfExecutionField.getText().isEmpty()) {
                    updatedOrder.setDateOfExecution(parsedDateOfOrderField);
                }
                if (!orderDescription.getText().isEmpty()) {
                    updatedOrder.setOrderDescription(orderDescription.getText());
                }

                Optional<Order> updated = orderService.updateOrder(selectedOrder.getOrderId(), updatedOrder);

                if (updated.isPresent()) {
                    System.out.println("Заказ успешно обновлен: " + updated.get().getOrderId());
                } else {
                    System.out.println("Заказ с ID " + selectedOrder.getOrderId() + " не найден.");
                }

                loadOrders();
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, паспорт или зарплата): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных сотрудника: " + e.getMessage());
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
        String clientID = clientFilter.getText();
        String projectID = projectFilter.getText();

        orderTable.setItems(observableOrderList.filtered(order -> {
            boolean matchesId = id.isEmpty() || String.valueOf(order.getOrderId()).contains(id);
            boolean matchesClientID = clientID.isEmpty() || String.valueOf(order.getOrderId()).contains(clientID);
            boolean matchesProjectID = String.valueOf(order.getOrderId()).contains(projectID);

            return matchesId && matchesClientID && matchesProjectID;
        }));
    }

    @FXML
    private void handleDeleteOrder() {
        if (userRole == org.example.demotest.entities.Role.ADMIN) {

            Long idText = Long.valueOf(deleteIdField.getText());

            Order order = orderService.findOrderById(idText);
            if (order != null) {
                orderService.deleteOrder(order.getOrderId());
                loadOrders();
            } else {
                System.out.println("Заказ с указанным ID не найден");
            }
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(userPassport);
    }
}
