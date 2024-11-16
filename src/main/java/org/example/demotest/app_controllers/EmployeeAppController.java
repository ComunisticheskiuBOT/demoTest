package org.example.demotest.app_controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.demotest.dto.ServiceRequestEmployee;
import org.example.demotest.entities.Role;
import org.example.demotest.entities.Department;
import org.example.demotest.entities.Employee;
import org.example.demotest.entities.Status;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;

@Controller
public class EmployeeAppController {

    @Autowired
    private ApplicationContext applicationContext;

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
    private TextFormatter<String> createNumeric10Filter() {
        return Numeric10Filter();
    }

    static TextFormatter<String> Numeric10Filter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.length() > 10) {
                return null;
            }

            if (newText.matches("\\d*")) {
                return change;
            }

            return null;
        });
    }

    private TextFormatter<String> createPhoneFilter() {
        return PhoneFilter();
    }

    static TextFormatter<String> PhoneFilter() {
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

    private TextFormatter<String> createDateFilter() {
        return DateFilter();
    }

    static TextFormatter<String> DateFilter() {
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

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/employee-api/v1/employees";

    //Таблица
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Long> id;
    @FXML private TableColumn<Employee, String> second_name;
    @FXML private TableColumn<Employee, String> first_name;
    @FXML private TableColumn<Employee, String> last_name;
    @FXML private TableColumn<Employee, Date> birth_date;
    @FXML private TableColumn<Employee, String> passport_number;
    @FXML private TableColumn<Employee, String> adDress;
    @FXML private TableColumn<Employee, String> phone_number;
    @FXML private TableColumn<Employee, String> Email;
    @FXML private TableColumn<Employee, String> pass_word;
    @FXML private TableColumn<Employee, Date> hire_date;
    @FXML private TableColumn<Employee, Date> termination_date;
    @FXML private TableColumn<Employee, Long> department_id;
    @FXML private TableColumn<Employee, String> Position;
    @FXML private TableColumn<Employee, Role> Role;
    @FXML private TableColumn<Employee, Status> Status;
    @FXML private TableColumn<Employee, String> Salary, Comments;

    //Заголовки для создания пользователя
    @FXML private Label surnameLabel, nameLabel, secondNameLabel, dateBirthLabel, passportLabel, addressLabel, numberLabel,
                        mailLabel, passwordLabel, dateAcceptanceLabel, dateDismissalLabel, idLabel, postLabel, roleLabel,
                        statusLabel, salaryLabel, descLabel;

    //Поля для создания пользователя
    @FXML private TextField secondNameField, firstNameField, lastNameField, birthDateField, passportNumberField,
                            addressField, phoneNumberField, emailField, hireDateField, terminationDateField,
                            departmentIdField, positionField, salaryField, commentsField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private ComboBox<Status> statusComboBox;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField, idFilter, surnameFilter, nameFilter, patronymicFilter, birthDateFilter;
    @FXML private ComboBox<String> roleFilter, statusFilter;

    //Кнопки
    @FXML private Button deleteEmployeeButton, addEmployeeButton, cleanButton, updateEmployeeButton, handleBackButton;
    ObservableList<Employee> observableEmployeeList = FXCollections.observableArrayList();

    @Autowired
    private EmployeeService employeeService;

    private Long userPassport;
    private Role userRole;

    public void initialize(Long userPassport) {
        this.userPassport = userPassport;
        this.userRole = employeeService.findEmployeeByPassportNumber(userPassport).get().getRole();

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        first_name.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        second_name.setCellValueFactory(new PropertyValueFactory<>("secondName"));
        last_name.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        birth_date.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        passport_number.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        adDress.setCellValueFactory(new PropertyValueFactory<>("address"));
        phone_number.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        Email.setCellValueFactory(new PropertyValueFactory<>("Email"));
        pass_word.setCellValueFactory(new PropertyValueFactory<>("passWord"));
        hire_date.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        termination_date.setCellValueFactory(new PropertyValueFactory<>("terminationDate"));
        department_id.setCellValueFactory(cellData -> {
            Department department = cellData.getValue().getDepartment();
            return new SimpleObjectProperty<>(department != null ? department.getDepartmentId() : null);
        });

        Position.setCellValueFactory(new PropertyValueFactory<>("Position"));
        Role.setCellValueFactory(new PropertyValueFactory<>("Role"));
        Status.setCellValueFactory(new PropertyValueFactory<>("Status"));
        Salary.setCellValueFactory(new PropertyValueFactory<>("Salary"));
        Comments.setCellValueFactory(new PropertyValueFactory<>("Comments"));

        if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
            statusComboBox.getItems().setAll(org.example.demotest.entities.Status.values());
            firstNameField.setTextFormatter(createAlphaFilter());
            secondNameField.setTextFormatter(createAlphaFilter());
            lastNameField.setTextFormatter(createAlphaFilter());
            positionField.setTextFormatter(createAlphaFilter());
            birthDateField.setTextFormatter(createDateFilter());
            passportNumberField.setTextFormatter(createNumeric10Filter());
            departmentIdField.setTextFormatter(createNumericFilter());
            salaryField.setTextFormatter(createNumeric10Filter());
            phoneNumberField.setTextFormatter(createPhoneFilter());
            hireDateField.setTextFormatter(createDateFilter());
            terminationDateField.setTextFormatter(createDateFilter());
        }

        setupFilters();
        loadEmployees();
    }

    @FXML
    private void handleUpdateEmployees(){
        loadEmployees();
    }

    @FXML
    private void handleCleanButton(){
        TextField[] textFields = {
                secondNameField, firstNameField, lastNameField, birthDateField, passportNumberField,
                addressField, phoneNumberField, emailField, passwordField, hireDateField, terminationDateField,
                deleteIdField, departmentIdField, positionField, salaryField, commentsField
        };
        for (TextField textField : textFields) {
            textField.setText(null);
        }
        roleComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
    }

    private void loadEmployees() {
        try {
            Employee[] employees = restTemplate.getForObject(url, Employee[].class);
            if (employees != null && employees.length > 0) { // Проверяем, что массив не пустой
                observableEmployeeList.setAll(Arrays.asList(employees));
                employeeTable.setItems(observableEmployeeList);
            } else {
                employeeTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке сотрудников: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddEmployee(ActionEvent event) {
        try {
            if(userRole == org.example.demotest.entities.Role.ADMIN) {
                Long departmentIdValue = Long.valueOf(departmentIdField.getText());
                Department department = employeeService.getDepartmentById(departmentIdValue);

                if (department == null) {
                    System.out.println("Отдел с указанным ID не найден.");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date parsedBirthDate = sdf.parse(birthDateField.getText());
                Date parsedHireDate = sdf.parse(hireDateField.getText());
                Date parsedTerminationDate = terminationDateField.getText().isEmpty()
                        ? null
                        : sdf.parse(terminationDateField.getText());

                ServiceRequestEmployee newEmployee = ServiceRequestEmployee.builder()
                        .firstName(firstNameField.getText())
                        .secondName(secondNameField.getText())
                        .lastName(lastNameField.getText())
                        .birthDate(parsedBirthDate)
                        .phoneNumber(phoneNumberField.getText())
                        .address(addressField.getText())
                        .passportNumber(Long.valueOf(passportNumberField.getText()))
                        .email(emailField.getText())
                        .password(passwordField.getText())
                        .hireDate(parsedHireDate)
                        .terminationDate(parsedTerminationDate)
                        .department(department)
                        .position(positionField.getText())
                        .role(roleComboBox.getValue())
                        .status(statusComboBox.getValue())
                        .salary(Integer.valueOf(salaryField.getText()))
                        .comments(commentsField.getText())
                        .build();
                Employee employeeCreated = restTemplate.postForObject(url, newEmployee, Employee.class);
                if (employeeCreated != null) {
                    observableEmployeeList.add(employeeCreated); // Используйте observableUserList
                    employeeTable.setItems(observableEmployeeList);
                    System.out.println("Пользователь добавлен: " + employeeCreated);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Ошибка при добавлении сотрудника: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());
        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        surnameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        nameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        patronymicFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        roleFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        birthDateFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = idFilter.getText();
        String surname = surnameFilter.getText().toLowerCase();
        String name = nameFilter.getText().toLowerCase();
        String patronymic = patronymicFilter.getText().toLowerCase();
        String statusFilterValue = statusFilter.getValue();
        String roleFilterValue = roleFilter.getValue();
        String birthDateText = birthDateFilter.getText();

        employeeTable.setItems(observableEmployeeList.filtered(employee -> {
            boolean matchesId = id.isEmpty() || String.valueOf(employee.getId()).contains(id);
            boolean matchesSurname = surname.isEmpty() || employee.getSecondName().toLowerCase().contains(surname);
            boolean matchesName = name.isEmpty() || employee.getFirstName().toLowerCase().contains(name);
            boolean matchesPatronymic = patronymic.isEmpty() || employee.getLastName().toLowerCase().contains(patronymic);
            boolean matchesRoles = roleFilterValue == null || "ALL".equals(roleFilterValue) || employee.getRole().name().equalsIgnoreCase(roleFilterValue);
            boolean matchesStatus = statusFilterValue == null || "ALL".equals(statusFilterValue) || employee.getStatus().name().equalsIgnoreCase(statusFilterValue);

            boolean matchesBirthDate = birthDateText.isEmpty();
            if (!matchesBirthDate) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date parsedFilterDate = sdf.parse(birthDateText);
                    matchesBirthDate = sdf.format(employee.getBirthDate()).equals(sdf.format(parsedFilterDate));
                } catch (Exception e) {
                    System.out.println("Неправильный формат даты: " + birthDateText);
                    matchesBirthDate = false;
                }
            }

            return matchesId && matchesSurname && matchesName && matchesPatronymic && matchesRoles && matchesStatus && matchesBirthDate;
        }));
    }

    @FXML
    private void handleDeleteEmployee() {
        if(userRole == org.example.demotest.entities.Role.ADMIN) {
            Long idText = Long.valueOf(deleteIdField.getText());

            if (idText != null) {
                // Найдем пользователя по Id
                Employee employee = employeeService.findEmployeeById(idText);
                if (employee != null) {
                    employeeService.deleteEmployee(employee.getId());
                    loadEmployees();
                } else {
                    System.out.println("Пользователь с указанным ID не найден");
                }
            } else {
                // Обработка ситуации, если оба поля пусты
                System.out.println("Введите ID для удаления сотрудника");
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
