package org.example.demotest.app_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.demotest.dto.ServiceRequestClient;
import org.example.demotest.entities.Client;
import org.example.demotest.entities.Reputation;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.example.demotest.app_controllers.LoginController.sessionID;

@Controller
public class ClientAppController {

    @Autowired
    private ApplicationContext applicationContext;

    private TextFormatter<String> createAlphaFilter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("[a-zA-Zа-яА-ЯёЁ]+")) {
                return change;
            }
            return null;
        });
    }

    private TextFormatter<String> createNumericFilter() {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        });
    }

    private TextFormatter<String> createPhoneFilter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.length() > 12) {
                return null;
            }

            if (newText.startsWith("8")) {
                newText = "+7" + newText.substring(1);
                change.setText(newText);
                change.setRange(0, change.getControlText().length());
            }

            if (newText.matches("(\\+7\\d*|8\\d*)")) {
                return change;
            }

            return null;
        });
    }
    private RestTemplate restTemplate = new RestTemplate();
    private String url = "http://localhost:8080/client-api/v1/clients";

    //Таблица
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Long> ClientId;
    @FXML private TableColumn<Client, String> CompanyName;
    @FXML private TableColumn<Client, String> ContactPerson;
    @FXML private TableColumn<Client, String> PhoneNumber;
    @FXML private TableColumn<Client, String> Email;
    @FXML private TableColumn<Client, String> Address;
    @FXML private TableColumn<Client, Reputation> Reputation;

    //Поля для создания пользователя
    @FXML private TextField companyName;
    @FXML private TextField contactPerson;
    @FXML private TextField phoneNumber;
    @FXML private TextField email;
    @FXML private TextField address;
    @FXML private ComboBox<Reputation> reputationComboBox;

    //Поля для удаления клиента
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter;
    @FXML private TextField companyNameFilter;
    @FXML private TextField contactPersonFilter;
    @FXML private ComboBox<String> reputationFilter;
    ObservableList<Client> observableClientList = FXCollections.observableArrayList();


    public void initialize() {
        ClientId.setCellValueFactory(new PropertyValueFactory<>("ClientId"));
        CompanyName.setCellValueFactory(new PropertyValueFactory<>("CompanyName"));
        ContactPerson.setCellValueFactory(new PropertyValueFactory<>("ContactPerson"));
        PhoneNumber.setCellValueFactory(new PropertyValueFactory<>("PhoneNumber"));
        Email.setCellValueFactory(new PropertyValueFactory<>("Email"));
        Address.setCellValueFactory(new PropertyValueFactory<>("Address"));
        Reputation.setCellValueFactory(new PropertyValueFactory<>("Reputation"));
        reputationComboBox.getItems().setAll(org.example.demotest.entities.Reputation.values());

        companyNameFilter.setTextFormatter(createAlphaFilter());
        contactPersonFilter.setTextFormatter(createAlphaFilter());
        phoneNumber.setTextFormatter(createPhoneFilter());

        setupFilters();
        loadClients();
    }

    @FXML
    private void handleUpdateClients(){
        loadClients();
    }

    @FXML
    private void handleCleanButton(){
        companyName.setText(null);
        contactPerson.setText(null);
        phoneNumber.setText(null);
        email.setText(null);
        address.setText(null);
        reputationComboBox.getSelectionModel().clearSelection();
    }

    private void loadClients() {
        try {
            Client[] clients = restTemplate.getForObject(url, Client[].class);
            if (clients != null && clients.length > 0) { // Проверяем, что массив не пустой
                observableClientList.setAll(Arrays.asList(clients));
                clientTable.setItems(observableClientList);
            } else {
                clientTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке сотрудников: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddClient(ActionEvent event) {
        try {
            ServiceRequestClient newClient = ServiceRequestClient.builder()
                    .companyName(companyName.getText())
                    .contactPerson(contactPerson.getText())
                    .phoneNumber(phoneNumber.getText())
                    .email(email.getText())
                    .address(address.getText())
                    .reputation(reputationComboBox.getValue())
                    .build();
            Client clientCreated = restTemplate.postForObject(url, newClient, Client.class);
            if (clientCreated != null) {
                observableClientList.add(clientCreated); // Используйте observableUserList
                clientTable.setItems(observableClientList);
                System.out.println("Пользователь добавлен: " + clientCreated);
            }
        } catch (Exception e) {
            // Логирование ошибки и/или уведомление пользователя
            e.printStackTrace();

            System.out.println("Ошибка при добавлении сотрудника: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        companyNameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        contactPersonFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        reputationFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = idFilter.getText();
        String companyName = companyNameFilter.getText().toLowerCase();
        String contactPerson = contactPersonFilter.getText().toLowerCase();
        String reputationFilterValue = reputationFilter.getValue();

        clientTable.setItems(observableClientList.filtered(client -> {
            boolean matchesId = id.isEmpty() || String.valueOf(client.getClientId()).contains(id);
            boolean matchesCompanyName = client.getCompanyName().toLowerCase().contains(companyName);
            boolean matchesContactPerson = client.getContactPerson().toLowerCase().contains(contactPerson);
            boolean matchesReputation = "All".equals(reputationFilterValue) || client.getReputation().name().equalsIgnoreCase(reputationFilterValue);

            return matchesId && matchesCompanyName && matchesContactPerson && matchesReputation;
        }));
    }

    @Autowired
    private ClientService clientService;

    @FXML
    private void handleDeleteClient() {
        Long idText = Long.valueOf(deleteIdField.getText());

        if (idText != null) {
            // Найдем пользователя по Id
            Client client = clientService.findClientById(idText);
            if (client != null) {
                clientService.deleteClient(client.getClientId());
                loadClients();
            } else {
                System.out.println("Клиент с указанным ID не найден");
            }
        } else {
            // Обработка ситуации, если оба поля пусты
            System.out.println("Введите ID для удаления клиента");
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(String.valueOf(sessionID));
    }
}
