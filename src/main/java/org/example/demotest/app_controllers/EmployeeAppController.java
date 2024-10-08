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

import static org.example.demotest.app_controllers.LoginController.sessionID;

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
    private String url = "http://localhost:8080/employee-api/v1/employees";

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
    @FXML private TableColumn<Employee, Status> Status;
    @FXML private TableColumn<Employee, String> Salary;
    @FXML private TableColumn<Employee, String> Comments;

    //Поля для создания пользователя
    @FXML private TextField secondNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField birthDateField;
    @FXML private TextField passportNumberField;
    @FXML private TextField addressField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField hireDateField;
    @FXML private TextField terminationDateField;
    @FXML private TextField departmentIdField;
    @FXML private TextField positionField;
    @FXML private ComboBox<Status> statusComboBox;
    @FXML private TextField salaryField;
    @FXML private TextField commentsField;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter;
    @FXML private TextField surnameFilter;
    @FXML private TextField nameFilter;
    @FXML private TextField patronymicFilter;
    @FXML private TextField birthDateFilter;
    @FXML private ComboBox<String> statusFilter;
    ObservableList<Employee> observableEmployeeList = FXCollections.observableArrayList();


    public void initialize() {
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
        Status.setCellValueFactory(new PropertyValueFactory<>("Status"));
        Salary.setCellValueFactory(new PropertyValueFactory<>("Salary"));
        Comments.setCellValueFactory(new PropertyValueFactory<>("Comments"));
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

        setupFilters();
        loadEmployees();
    }

    @FXML
    private void handleUpdateEmployees(){
        loadEmployees();
    }

    @FXML
    private void handleCleanButton(){
        secondNameField.setText(null);
        firstNameField.setText(null);
        lastNameField.setText(null);
        birthDateField.setText(null);
        passportNumberField.setText(null);
        addressField.setText(null);
        phoneNumberField.setText(null);
        emailField.setText(null);
        passwordField.setText(null);
        hireDateField.setText(null);
        terminationDateField.setText(null);
        deleteIdField.setText(null);
        departmentIdField.setText(null);
        positionField.setText(null);
        statusComboBox.getSelectionModel().clearSelection();
        salaryField.setText(null);
        commentsField.setText(null);
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
            Long departmentIdValue = Long.valueOf(departmentIdField.getText());
            // Получите объект Project из службы, а не только его ID
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
        } catch (Exception e) {
            // Логирование ошибки и/или уведомление пользователя
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
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        birthDateFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = idFilter.getText();
        String surname = surnameFilter.getText().toLowerCase();
        String name = nameFilter.getText().toLowerCase();
        String patronymic = patronymicFilter.getText().toLowerCase();
        String statusFilterValue = statusFilter.getValue();
        String birthDateText = birthDateFilter.getText();

        employeeTable.setItems(observableEmployeeList.filtered(employee -> {
            boolean matchesId = id.isEmpty() || String.valueOf(employee.getId()).contains(id);
            boolean matchesSurname = surname.isEmpty() || employee.getSecondName().toLowerCase().contains(surname);
            boolean matchesName = name.isEmpty() || employee.getFirstName().toLowerCase().contains(name);
            boolean matchesPatronymic = patronymic.isEmpty() || employee.getLastName().toLowerCase().contains(patronymic);
            boolean matchesStatus = statusFilterValue == null || "ALL".equals(statusFilterValue) || employee.getStatus().name().equalsIgnoreCase(statusFilterValue);

            // Фильтр по дате рождения
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

            // Возвращаем результат проверки всех фильтров
            return matchesId && matchesSurname && matchesName && matchesPatronymic && matchesStatus && matchesBirthDate;
        }));
    }


    @Autowired
    private EmployeeService employeeService;

    @FXML
    private void handleDeleteEmployee() {
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

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(String.valueOf(sessionID));
    }
}
