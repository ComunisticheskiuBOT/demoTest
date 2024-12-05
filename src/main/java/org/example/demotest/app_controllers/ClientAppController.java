package org.example.demotest.app_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.demotest.dto.ServiceRequestClient;
import org.example.demotest.entities.Client;
import org.example.demotest.entities.enums.Reputation;
import org.example.demotest.entities.enums.Role;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.ClientService;
import org.example.demotest.services.EmployeeService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.demotest.app_controllers.EmployeeAppController.PhoneFilter;

@Controller
public class ClientAppController {
    private final Logger logger = Logger.getLogger(ClientAppController.class.getName());
    private final ApplicationContext applicationContext;
    private final ClientService clientService;
    private final EmployeeService employeeService;

    public ClientAppController(ApplicationContext applicationContext, ClientService clientService, EmployeeService employeeService) {
        this.applicationContext = applicationContext;
        this.clientService = clientService;
        this.employeeService = employeeService;
    }

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
        return PhoneFilter();
    }
    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/client-api/v1/clients";

    //Таблица
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Long> ClientId;
    @FXML private TableColumn<Client, String> CompanyName, ContactPerson, PhoneNumber, Email, Address;
    @FXML private TableColumn<Client, Reputation> Reputation;

    //Поля для создания пользователя
    @FXML private TextField companyName, contactPerson, phoneNumber, email, address;
    @FXML private ComboBox<Reputation> reputationComboBox;

    //Поля для удаления клиента
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter, companyNameFilter, contactPersonFilter;
    @FXML private ComboBox<String> reputationFilter;
    ObservableList<Client> observableClientList = FXCollections.observableArrayList();

    private Long userPassport;
    private Role userRole;

    public void initialize(Long userPassport) {
        this.userPassport = userPassport;
        this.userRole = employeeService
                .findEmployeeByPassportNumber(userPassport)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с данным номером паспорта не найден"))
                .getRole();

        ClientId.setCellValueFactory(new PropertyValueFactory<>("ClientId"));
        CompanyName.setCellValueFactory(new PropertyValueFactory<>("CompanyName"));
        ContactPerson.setCellValueFactory(new PropertyValueFactory<>("ContactPerson"));
        PhoneNumber.setCellValueFactory(new PropertyValueFactory<>("PhoneNumber"));
        Email.setCellValueFactory(new PropertyValueFactory<>("Email"));
        Address.setCellValueFactory(new PropertyValueFactory<>("Address"));
        Reputation.setCellValueFactory(new PropertyValueFactory<>("Reputation"));

        if(userRole == Role.ADMIN || userRole == Role.MODERATOR) {
            reputationComboBox.getItems().setAll(org.example.demotest.entities.enums.Reputation.values());
            companyNameFilter.setTextFormatter(createAlphaFilter());
            contactPersonFilter.setTextFormatter(createAlphaFilter());
            phoneNumber.setTextFormatter(createPhoneFilter());
        }

        setupFilters();
        loadClients();
    }

    @FXML
    private void handleUpdateClients(){
        loadClients();
    }

    @FXML
    private void handleCleanButton(){
        if (userRole == Role.ADMIN || userRole == Role.MODERATOR) {
            companyName.setText("");
            contactPerson.setText("");
            phoneNumber.setText("");
            email.setText("");
            address.setText("");
            reputationComboBox.getSelectionModel().clearSelection();
        }
    }

    private void loadClients() {
        try {
            Client[] clients = restTemplate.getForObject(url, Client[].class);
            if (clients != null && clients.length > 0) {
                observableClientList.setAll(Arrays.asList(clients));
                clientTable.setItems(observableClientList);
            } else {
                clientTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при загрузке сотрудников: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddClient(ActionEvent event) {
        try {
            if(userRole == Role.ADMIN) {

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
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при добавлении сотрудника: " + e.getMessage());
        }
    }

    private Client selectedClient;

    @FXML
    private void handleTableClick(MouseEvent event) {
        selectedClient = clientTable.getSelectionModel().getSelectedItem();
        try {
            if (userRole == Role.ADMIN || userRole == Role.MODERATOR) {
                if (selectedClient != null) {
                    companyName.setText(selectedClient.getCompanyName());
                    contactPerson.setText(selectedClient.getContactPerson());
                    phoneNumber.setText(selectedClient.getPhoneNumber());
                    email.setText(String.valueOf(selectedClient.getEmail()));
                    address.setText(selectedClient.getAddress());
                    reputationComboBox.setValue(selectedClient.getReputation());
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, номер телефона): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных сотрудника: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditClient(ActionEvent event) {
        if (selectedClient == null) {
            System.out.println("Не выбран сотрудник для изменения.");
            return;
        }

        try {
            if(userRole == Role.ADMIN || userRole == Role.MODERATOR) {
                ServiceRequestClient updatedClient = getServiceRequestClient();

                Optional<Client> updated = clientService.updatedClient(selectedClient.getClientId(), updatedClient);

                if (updated.isPresent()) {
                    System.out.println("Клиент успешно обновлен: " + updated.get().getCompanyName());
                } else {
                    System.out.println("Клиент с ID " + selectedClient.getClientId() + " не найден.");
                }

                loadClients();
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, номер телефона): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных клиента: " + e.getMessage());
        }
    }

    private ServiceRequestClient getServiceRequestClient() {
        ServiceRequestClient updatedClient = new ServiceRequestClient();

        if (!companyName.getText().isEmpty()) {
            updatedClient.setCompanyName(companyName.getText());
        }
        if (!contactPerson.getText().isEmpty()) {
            updatedClient.setContactPerson(contactPerson.getText());
        }
        if (!phoneNumber.getText().isEmpty()) {
            updatedClient.setPhoneNumber(phoneNumber.getText());
        }
        if (!email.getText().isEmpty()) {
            updatedClient.setEmail(email.getText());
        }
        if (!address.getText().isEmpty()) {
            updatedClient.setAddress(address.getText());
        }
        if (reputationComboBox.getValue() != null) {
            updatedClient.setReputation(reputationComboBox.getValue());
        }

        return updatedClient;
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        companyNameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        contactPersonFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        reputationFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = safeGetText(idFilter);
        String companyName = safeGetText(companyNameFilter);
        String contactPerson = safeGetText(contactPersonFilter);
        String reputationFilterValue = reputationFilter.getValue();

        clientTable.setItems(observableClientList.filtered(client -> {
            boolean matchesId = id.isEmpty() || String.valueOf(client.getClientId()).contains(id);
            boolean matchesCompanyName = client.getCompanyName().toLowerCase().contains(companyName);
            boolean matchesContactPerson = client.getContactPerson().toLowerCase().contains(contactPerson);
            boolean matchesReputation = "ALL".equals(reputationFilterValue) || client.getReputation().name().equalsIgnoreCase(reputationFilterValue);

            return matchesId && matchesCompanyName && matchesContactPerson && matchesReputation;
        }));
    }
    private String safeGetText(TextField textField) {
        String text = textField.getText();
        return text != null ? text.trim().toLowerCase() : "";
    }

    @FXML
    private void handleDeleteClient() {
        if (userRole == Role.ADMIN) {
            Long idText = Long.valueOf(deleteIdField.getText());

            Client client = clientService.findClientById(idText);
            if (client != null) {
                clientService.deleteClient(client.getClientId());
                loadClients();
            } else {
                System.out.println("Клиент с указанным ID не найден");
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
