package org.example.demotest.app_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.demotest.entities.Role;
import org.example.demotest.entities.User;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.*;
import java.util.Arrays;

import static org.example.demotest.app_controllers.LoginController.sessionID;

@Component
public class UserAppController {

    private TextFormatter<String> createAlphaFilter() {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("[a-zA-Zа-яА-ЯёЁ]+")) {
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
    private RestTemplate restTemplate = new RestTemplate();
    private String url = "http://localhost:8080/user-api/v1/users";

    //Таблица
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> id;
    @FXML private TableColumn<User, String> second_name;
    @FXML private TableColumn<User, String> first_name;
    @FXML private TableColumn<User, String> last_name;
    @FXML private TableColumn<User, Role> role;
    @FXML private TableColumn<User, String> user_description;
    @FXML private TableColumn<User, Long> employee_id;
    @FXML private TableColumn<User, String> user_password;

    //Поля для создания пользователя
    @FXML private TextField secondName;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private TextField userDescription;
    @FXML private TextField employeeId;
    @FXML private PasswordField userPassword;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField;
    @FXML private TextField deleteEmployeeIdField;
    @FXML private Button deleteUserButton;

    //Фильтры
    @FXML private TextField idFilter;
    @FXML private TextField employeeIdFilter;
    @FXML private TextField surnameFilter;
    @FXML private TextField nameFilter;
    @FXML private TextField patronymicFilter;
    @FXML private ComboBox<String> roleFilter;
    ObservableList<User> observableUserList = FXCollections.observableArrayList();

    @Autowired
    private UserService userService;

    public void initialize() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        first_name.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        second_name.setCellValueFactory(new PropertyValueFactory<>("second_name"));
        last_name.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        role.setCellValueFactory(new PropertyValueFactory<>("role"));
        user_description.setCellValueFactory(new PropertyValueFactory<>("user_description"));
        employee_id.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        user_password.setCellValueFactory(new PropertyValueFactory<>("userPassword"));
        roleComboBox.getItems().setAll(Role.values());

        employeeId.setTextFormatter(createNumericFilter());
        firstName.setTextFormatter(createAlphaFilter());
        secondName.setTextFormatter(createAlphaFilter());
        lastName.setTextFormatter(createAlphaFilter());

        setupFilters();
        loadUsers();
    }

    private void loadUsers() {
        try {
            User[] users = restTemplate.getForObject(url, User[].class);
            if (users != null && users.length > 0) { // Проверяем, что массив не пустой
                observableUserList.setAll(Arrays.asList(users));
                userTable.setItems(observableUserList);
            } else {
                userTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке пользователей: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddUser(ActionEvent event) {
        User newUser = User.builder()
                .employeeId(Long.parseLong(employeeId.getText()))
                .first_name(firstName.getText())
                .second_name(secondName.getText())
                .last_name(lastName.getText())
                .role(roleComboBox.getValue())
                .user_description(userDescription.getText())
                .userPassword(userPassword.getText())
                .build();

        try {
            User createdUser = restTemplate.postForObject(url, newUser, User.class);
            if (createdUser != null) {
                observableUserList.add(createdUser); // Используйте observableUserList
                userTable.setItems(observableUserList);
                System.out.println("Пользователь добавлен: " + createdUser);
            }
        } catch (Exception e) {
            // Логирование ошибки и/или уведомление пользователя
            e.printStackTrace();
            System.out.println("Ошибка при добавлении пользователя: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());
        employeeIdFilter.setTextFormatter(createNumericFilter());

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        employeeIdFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        surnameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        nameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        patronymicFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        roleFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = idFilter.getText();
        String employeeId = employeeIdFilter.getText();
        String surname = surnameFilter.getText().toLowerCase();
        String name = nameFilter.getText().toLowerCase();
        String patronymic = patronymicFilter.getText().toLowerCase();
        String roleFilterValue = roleFilter.getValue();

        userTable.setItems(observableUserList.filtered(user -> {
            boolean matchesId = id.isEmpty() || String.valueOf(user.getId()).contains(id);
            boolean matchesEmployeeId = employeeId.isEmpty() || String.valueOf(user.getEmployeeId()).contains(employeeId);
            boolean matchesSurname = user.getSecond_name().toLowerCase().contains(surname);
            boolean matchesName = user.getFirst_name().toLowerCase().contains(name);
            boolean matchesPatronymic = user.getLast_name().toLowerCase().contains(patronymic);
            boolean matchesRole = "All".equals(roleFilterValue) || user.getRole().name().equalsIgnoreCase(roleFilterValue);

            return matchesId && matchesEmployeeId && matchesSurname && matchesName && matchesPatronymic && matchesRole;
        }));
    }

    @FXML
    private void handleDeleteUser() {
        String idText = deleteIdField.getText();
        String employeeIdText = deleteEmployeeIdField.getText();

        if (idText != null && !idText.isEmpty()) {
            Long id = Long.parseLong(idText);
            userService.deleteUserById(id);
        } else if (employeeIdText != null && !employeeIdText.isEmpty()) {
            Long employeeId = Long.parseLong(employeeIdText);
            userService.deleteUserByEmployeeId(employeeId);
        } else {
            // Обработка ситуации, если оба поля пусты
            System.out.println("Введите ID или Рабочий ID для удаления пользователя");
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage);
        loginManager.showMainView(String.valueOf(sessionID));
    }
}
