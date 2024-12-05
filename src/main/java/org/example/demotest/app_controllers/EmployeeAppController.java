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
import org.example.demotest.dto.ServiceRequestEmployee;
import org.example.demotest.entities.enums.Role;
import org.example.demotest.entities.Department;
import org.example.demotest.entities.Employee;
import org.example.demotest.entities.enums.Status;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.EmployeeService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class EmployeeAppController {
    private final Logger logger = Logger.getLogger(EmployeeAppController.class.getName());
    private final ApplicationContext applicationContext;
    private final EmployeeService employeeService;

    public EmployeeAppController(ApplicationContext applicationContext, EmployeeService employeeService){
        this.applicationContext = applicationContext;
        this.employeeService = employeeService;
    }

    private TextFormatter<String> createAlphaFilter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[a-zA-Zа-яА-Я]*")) {
                return change;
            }
            return null;
        });
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

    static TextFormatter<String> NumericFilter(int n) {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.length() > n) {
                return null;
            }

            if (newText.isEmpty() || newText.matches("\\d*")) {
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

            if (newText.isEmpty()) {
                return change;
            }

            if (newText.length() > 12) {
                return null;
            }

            if (newText.startsWith("8")) {
                String formattedText = "+7" + newText.substring(1);
                change.setText(formattedText);
                //change.setRange(0, formattedText.length());
                return change;
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

            if (newText.isEmpty()) {
                return change;
            }

            if (newText.length() > 10) {
                return null;
            }

            if (newText.matches("^\\d{0,2}$") ||                // День
                    newText.matches("^\\d{2}-$") ||             // День-
                    newText.matches("^\\d{2}-(0[1-9]?|1[0-2]?)$") || // День-месяц
                    newText.matches("^\\d{2}-\\d{2}-$") ||      // День-месяц-
                    newText.matches("^\\d{2}-\\d{2}-\\d{0,4}$")) { // День-месяц-год
                return change;
            }
            change.setText("");
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
    @FXML private TableColumn<Employee, String> inn;
    @FXML private TableColumn<Employee, String> snils;
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

    //Поля для создания пользователя
    @FXML private TextField secondNameField, firstNameField, lastNameField, birthDateField, passportNumberField, innField, snilsField,
                            addressField, phoneNumberField, emailField, hireDateField, terminationDateField,
                            departmentIdField, positionField, salaryField, commentsField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private ComboBox<Status> statusComboBox;

    //Поля для удаления/фильтров пользователя
    @FXML private TextField deleteIdField, idFilter, surnameFilter, nameFilter, patronymicFilter, birthDateFilter,
                            passportNumberFilter, innFilter, snilsFilter, idDepartmentFilter, phoneNumberFilter, hireDateFilter, terminationDateFilter, salaryFilter, positionFilter;
    @FXML private ComboBox<String> roleFilter, statusFilter;

    ObservableList<Employee> observableEmployeeList = FXCollections.observableArrayList();

    private Long userPassport;
    private Role userRole;

    public void initialize(Long userPassport) {
        this.userPassport = userPassport;
        this.userRole = employeeService
                .findEmployeeByPassportNumber(userPassport)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с данным номером паспорта не найден"))
                .getRole();

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        first_name.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        second_name.setCellValueFactory(new PropertyValueFactory<>("secondName"));
        last_name.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        birth_date.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        passport_number.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        inn.setCellValueFactory(new PropertyValueFactory<>("inn"));
        snils.setCellValueFactory(new PropertyValueFactory<>("snils"));
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

        if (userRole == org.example.demotest.entities.enums.Role.ADMIN || userRole == org.example.demotest.entities.enums.Role.MODERATOR) {
            statusComboBox.getItems().setAll(org.example.demotest.entities.enums.Status.values());
            firstNameField.setTextFormatter(createAlphaFilter());
            secondNameField.setTextFormatter(createAlphaFilter());
            lastNameField.setTextFormatter(createAlphaFilter());
            positionField.setTextFormatter(createAlphaFilter());
            birthDateField.setTextFormatter(createDateFilter());
            passportNumberField.setTextFormatter(NumericFilter(10));
            innField.setTextFormatter(NumericFilter(12));
            snilsField.setTextFormatter(NumericFilter(11));
            departmentIdField.setTextFormatter(createNumericFilter());
            salaryField.setTextFormatter(createNumericFilter());
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
        if (userRole == org.example.demotest.entities.enums.Role.ADMIN) {
            TextField[] textFields = {
                    secondNameField, firstNameField, lastNameField, birthDateField, passportNumberField, innField, snilsField,
                    addressField, phoneNumberField, emailField, passwordField, hireDateField, terminationDateField,
                    departmentIdField, positionField, salaryField, commentsField, deleteIdField,
                    idFilter, surnameFilter, nameFilter, patronymicFilter, birthDateFilter
            };
            for (TextField textField : textFields) {
                if(textField != null) textField.setText("");
            }
            roleComboBox.getSelectionModel().clearSelection();
            statusComboBox.getSelectionModel().clearSelection();
        } else if (userRole == org.example.demotest.entities.enums.Role.MODERATOR || userRole == org.example.demotest.entities.enums.Role.USER){
            TextField[] textFields = {
                    secondNameField, firstNameField, lastNameField, birthDateField, passportNumberField, innField, snilsField,
                    addressField, phoneNumberField, emailField, passwordField, hireDateField, terminationDateField,
                    departmentIdField, positionField, salaryField, commentsField,
                    idFilter, surnameFilter, nameFilter, patronymicFilter, birthDateFilter,
                    passportNumberFilter, innFilter, snilsFilter, idDepartmentFilter, phoneNumberFilter, hireDateFilter, terminationDateFilter, salaryFilter, positionFilter
            };
            for (TextField textField : textFields) {
                if(textField != null) textField.setText("");
            }
            if (userRole == org.example.demotest.entities.enums.Role.MODERATOR) statusComboBox.getSelectionModel().clearSelection();
        }
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
            logger.log(Level.SEVERE,"Ошибка при загрузке сотрудников: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddEmployee(ActionEvent event) {
        try {
            if(userRole == org.example.demotest.entities.enums.Role.ADMIN) {
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
                        .inn(Long.valueOf(innField.getText()))
                        .snils(Long.valueOf(snilsField.getText()))
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
            logger.log(Level.SEVERE,"Ошибка при добавлении сотрудника: " + e.getMessage());
        }
    }

    private Employee selectedEmployee;

    @FXML
    private void handleTableClick(MouseEvent event) {
        selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (userRole == org.example.demotest.entities.enums.Role.ADMIN || userRole == org.example.demotest.entities.enums.Role.MODERATOR) {
                if (selectedEmployee != null) {
                    departmentIdField.setText(String.valueOf(selectedEmployee.getDepartment().getDepartmentId()));
                    secondNameField.setText(selectedEmployee.getSecondName());
                    firstNameField.setText(selectedEmployee.getFirstName());
                    lastNameField.setText(selectedEmployee.getLastName());
                    passportNumberField.setText(String.valueOf(selectedEmployee.getPassportNumber()));
                    innField.setText(String.valueOf(selectedEmployee.getInn()));
                    snilsField.setText(String.valueOf(selectedEmployee.getSnils()));
                    addressField.setText(selectedEmployee.getAddress());
                    phoneNumberField.setText(selectedEmployee.getPhoneNumber());
                    emailField.setText(selectedEmployee.getEmail());
                    passwordField.setText(selectedEmployee.getPassWord());
                    salaryField.setText(String.valueOf(selectedEmployee.getSalary()));
                    commentsField.setText(selectedEmployee.getComments());
                    birthDateField.setText(sdf.format(selectedEmployee.getBirthDate()));
                    hireDateField.setText(sdf.format(selectedEmployee.getHireDate()));
                    terminationDateField.setText(
                            selectedEmployee.getTerminationDate() == null
                                    ? ""
                                    : sdf.format(selectedEmployee.getTerminationDate())
                    );
                    positionField.setText(selectedEmployee.getPosition());
                    statusComboBox.setValue(selectedEmployee.getStatus());
                    if (userRole == org.example.demotest.entities.enums.Role.ADMIN) roleComboBox.setValue(selectedEmployee.getRole());
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, паспорт или зарплата): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных сотрудника: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditEmployee(ActionEvent event) {
        if (selectedEmployee == null) {
            System.out.println("Не выбран сотрудник для изменения.");
            return;
        }

        try {
            if(userRole == org.example.demotest.entities.enums.Role.ADMIN || userRole == org.example.demotest.entities.enums.Role.MODERATOR) {
                ServiceRequestEmployee updatedEmployee = new ServiceRequestEmployee();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date parsedBirthDate = sdf.parse(birthDateField.getText());
                Date parsedHireDate = sdf.parse(hireDateField.getText());
                Date parsedTerminationDate = (terminationDateField == null || terminationDateField.getText() == null || terminationDateField.getText().isEmpty())
                        ? null
                        : sdf.parse(terminationDateField.getText());

                if (!secondNameField.getText().isEmpty()) {
                    updatedEmployee.setSecondName(secondNameField.getText());
                }
                if (!firstNameField.getText().isEmpty()) {
                    updatedEmployee.setFirstName(firstNameField.getText());
                }
                if (!lastNameField.getText().isEmpty()) {
                    updatedEmployee.setLastName(lastNameField.getText());
                }
                if (!passportNumberField.getText().isEmpty()) {
                    updatedEmployee.setPassportNumber(Long.valueOf(passportNumberField.getText()));
                }
                if (!innField.getText().isEmpty()) {
                    updatedEmployee.setInn(Long.valueOf(innField.getText()));
                }
                if (!snilsField.getText().isEmpty()) {
                    updatedEmployee.setSnils(Long.valueOf(snilsField.getText()));
                }
                if (!addressField.getText().isEmpty()) {
                    updatedEmployee.setAddress(addressField.getText());
                }
                if (!phoneNumberField.getText().isEmpty()) {
                    updatedEmployee.setPhoneNumber(phoneNumberField.getText());
                }
                if (!emailField.getText().isEmpty()) {
                    updatedEmployee.setEmail(emailField.getText());
                }
                if (!salaryField.getText().isEmpty()) {
                    updatedEmployee.setSalary(Integer.valueOf(salaryField.getText()));
                }
                if (!commentsField.getText().isEmpty()) {
                    updatedEmployee.setComments(commentsField.getText());
                }
                if (birthDateField.getText() != null) {
                    updatedEmployee.setBirthDate(parsedBirthDate);
                }
                if (hireDateField.getText() != null) {
                    updatedEmployee.setHireDate(parsedHireDate);
                }
                if (terminationDateField.getText() != null) {
                    updatedEmployee.setTerminationDate(parsedTerminationDate);
                }

                if (statusComboBox.getValue() != null) {
                    updatedEmployee.setStatus(statusComboBox.getValue());
                }

                Optional<Employee> updated = employeeService.updateEmployee(selectedEmployee.getId(), updatedEmployee);

                if (updated.isPresent()) {
                    System.out.println("Сотрудник успешно обновлен: " + updated.get().getFirstName());
                } else {
                    System.out.println("Сотрудник с ID " + selectedEmployee.getId() + " не найден.");
                }

                loadEmployees();
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, паспорт или зарплата): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных сотрудника: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());
        surnameFilter.setTextFormatter(createAlphaFilter());
        nameFilter.setTextFormatter(createAlphaFilter());
        patronymicFilter.setTextFormatter(createAlphaFilter());
        positionFilter.setTextFormatter(createAlphaFilter());
        phoneNumberFilter.setTextFormatter(createPhoneFilter());
        salaryFilter.setTextFormatter(createNumericFilter());
        passportNumberFilter.setTextFormatter(NumericFilter(10));
        innFilter.setTextFormatter(NumericFilter(12));
        snilsFilter.setTextFormatter(NumericFilter(11));
        idDepartmentFilter.setTextFormatter(createNumericFilter());

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        surnameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        nameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        patronymicFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        positionFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        passportNumberFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        innFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        snilsFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        idDepartmentFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        phoneNumberFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        salaryFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        roleFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        birthDateFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        hireDateFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        terminationDateFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = safeGetText(idFilter);
        String surname = safeGetText(surnameFilter);
        String name = safeGetText(nameFilter);
        String patronymic = safeGetText(patronymicFilter);
        String position = safeGetText(positionFilter);
        String phoneNumber = safeGetText(phoneNumberFilter);
        String salary = safeGetText(salaryFilter);
        String passportNumber = safeGetText(passportNumberFilter);
        String inn = safeGetText(innFilter);
        String snils = safeGetText(snilsFilter);
        String departmentId = safeGetText(idDepartmentFilter);

        String roleFilterValue = roleFilter.getValue() != null ? roleFilter.getValue() : "ALL";
        String statusFilterValue = statusFilter.getValue() != null ? statusFilter.getValue() : "ALL";

        String birthDateText = safeGetText(birthDateFilter);
        String hireDateText = safeGetText(hireDateFilter);
        String terminationDateText = safeGetText(terminationDateFilter);

        employeeTable.setItems(observableEmployeeList.filtered(employee -> {
            boolean matchesId = id.isEmpty() || String.valueOf(employee.getId()).contains(id);
            boolean matchesSurname = surname.isEmpty() || employee.getSecondName().toLowerCase().contains(surname);
            boolean matchesName = name.isEmpty() || employee.getFirstName().toLowerCase().contains(name);
            boolean matchesPatronymic = patronymic.isEmpty() || employee.getLastName().toLowerCase().contains(patronymic);
            boolean matchesPosition = position.isEmpty() || String.valueOf(employee.getPosition()).contains(position);
            boolean matchesPhoneNumber = phoneNumber.isEmpty() || String.valueOf(employee.getPhoneNumber()).contains(phoneNumber);
            boolean matchesSalary =  salary.isEmpty() || String.valueOf(employee.getSalary()).contains(salary);
            boolean matchesPassportNumber =  passportNumber.isEmpty() || String.valueOf(employee.getPassportNumber()).contains(passportNumber);
            boolean matchesInn =  inn.isEmpty() || String.valueOf(employee.getInn()).contains(inn);
            boolean matchesSnils =  snils.isEmpty() || String.valueOf(employee.getSnils()).contains(snils);
            boolean matchesDepartment =  departmentId.isEmpty() || String.valueOf(employee.getDepartment().getDepartmentId()).contains(departmentId);

            boolean matchesRoles = "ALL".equals(roleFilterValue) || employee.getRole().name().equalsIgnoreCase(roleFilterValue);
            boolean matchesStatus = "ALL".equals(statusFilterValue) || employee.getStatus().name().equalsIgnoreCase(statusFilterValue);

            boolean matchesBirthDate = birthDateText.isEmpty();
            if (!matchesBirthDate) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date parsedFilterDate = sdf.parse(birthDateText);
                    matchesBirthDate = sdf.format(employee.getBirthDate()).equals(sdf.format(parsedFilterDate));
                } catch (Exception e) {
                    System.out.println("Неправильный формат даты: " + birthDateText);
                }
            }

            boolean matchesHireDate = hireDateText.isEmpty();
            if (!matchesHireDate) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date parsedFilterDate = sdf.parse(hireDateText);
                    matchesHireDate = sdf.format(employee.getHireDate()).equals(sdf.format(parsedFilterDate));
                } catch (Exception e) {
                    System.out.println("Неправильный формат даты: " + hireDateText);
                }
            }

            boolean matchesTerminationDate = terminationDateText.isEmpty();
            if (!matchesTerminationDate) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date parsedFilterDate = sdf.parse(terminationDateText);
                    matchesTerminationDate = sdf.format(employee.getTerminationDate()).equals(sdf.format(parsedFilterDate));
                } catch (Exception e) {
                    System.out.println("Неправильный формат даты: " + terminationDateText);
                }
            }

            return matchesId && matchesSurname && matchesName && matchesPatronymic && matchesRoles && matchesStatus && matchesPosition && matchesPhoneNumber && matchesPassportNumber
                    && matchesInn && matchesSnils && matchesDepartment && matchesSalary && matchesBirthDate && matchesHireDate && matchesTerminationDate;
        }));
    }

    private String safeGetText(TextField textField) {
        String text = textField.getText();
        return text != null ? text.trim().toLowerCase() : "";
    }

    @FXML
    private void handleDeleteEmployee() {
        if(userRole == org.example.demotest.entities.enums.Role.ADMIN) {
            Long idText = Long.valueOf(deleteIdField.getText());

            Employee employee = employeeService.findEmployeeById(idText);
            if (employee != null) {
                employeeService.deleteEmployee(employee.getId());
                loadEmployees();
            } else {
                System.out.println("Пользователь с указанным ID не найден");
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
