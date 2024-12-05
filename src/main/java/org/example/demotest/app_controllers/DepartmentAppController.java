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
import org.example.demotest.dto.ServiceRequestDepartment;
import org.example.demotest.entities.Department;
import org.example.demotest.entities.enums.Role;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.DepartmentService;
import org.example.demotest.services.EmployeeService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class DepartmentAppController {
    private final Logger logger = Logger.getLogger(DepartmentAppController.class.getName());

    private final EmployeeService employeeService;

    private final ApplicationContext applicationContext;

    private final DepartmentService departmentService;

    public DepartmentAppController(ApplicationContext applicationContext, EmployeeService employeeService, DepartmentService departmentService){
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.applicationContext = applicationContext;
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
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d*")) {
                return change;
            }
            return null;
        });
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/department-api/v1/departments";

    //Таблица
    @FXML private TableView<Department> departmentTable;
    @FXML private TableColumn<Department, Long> DepartmentId;
    @FXML private TableColumn<Department, String> DepartmentName, Location, Description;

    //Поля для создания пользователя
    @FXML private TextField departmentName, locAtion, description;

    //Поля для удаления клиента
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter, departmentNameFilter, locationFilter;
    ObservableList<Department> observableDepartmentList = FXCollections.observableArrayList();

    private Long userPassport;
    private Role userRole;

    public void initialize(Long userPassport) {
        this.userPassport = userPassport;
        this.userRole = employeeService
                .findEmployeeByPassportNumber(userPassport)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с данным номером паспорта не найден"))
                .getRole();

        DepartmentId.setCellValueFactory(new PropertyValueFactory<>("DepartmentId"));
        DepartmentName.setCellValueFactory(new PropertyValueFactory<>("DepartmentName"));
        Location.setCellValueFactory(new PropertyValueFactory<>("Location"));
        Description.setCellValueFactory(new PropertyValueFactory<>("Description"));

        departmentNameFilter.setTextFormatter(createAlphaFilter());
        locationFilter.setTextFormatter(createAlphaFilter());

        setupFilters();
        loadDepartments();
    }

    @FXML
    private void handleUpdateDepartments(){
        loadDepartments();
    }

    @FXML
    private void handleCleanButton(){
        departmentName.setText("");
        locAtion.setText("");
        description.setText("");
    }

    private void loadDepartments() {
        try {
            Department[] departments = restTemplate.getForObject(url, Department[].class);
            if (departments != null && departments.length > 0) { // Проверяем, что массив не пустой
                observableDepartmentList.setAll(Arrays.asList(departments));
                departmentTable.setItems(observableDepartmentList);
            } else {
                departmentTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке отделов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddDepartment(ActionEvent event) {
        try {
            if(userRole == Role.ADMIN) {

                ServiceRequestDepartment newClient = ServiceRequestDepartment.builder()
                        .departmentName(departmentName.getText())
                        .location(locAtion.getText())
                        .description(description.getText())
                        .build();
                Department departmentCreated = restTemplate.postForObject(url, newClient, Department.class);
                if (departmentCreated != null) {
                    observableDepartmentList.add(departmentCreated); // Используйте observableUserList
                    departmentTable.setItems(observableDepartmentList);
                    System.out.println("Отдел добавлен: " + departmentCreated);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при добавлении отдела: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        departmentNameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        locationFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = idFilter.getText();
        String departmentName = departmentNameFilter.getText().toLowerCase();
        String location = locationFilter.getText().toLowerCase();

        departmentTable.setItems(observableDepartmentList.filtered(department -> {
            boolean matchesId = id.isEmpty() || String.valueOf(department.getDepartmentId()).equals(id);
            boolean matchesDepartmentName = department.getDepartmentName().toLowerCase().contains(departmentName);
            boolean matchesLocation = department.getLocation().toLowerCase().contains(location);

            return matchesId && matchesDepartmentName && matchesLocation;
        }));
    }

    @FXML
    private void handleDeleteDepartment() {
        if (userRole == Role.ADMIN) {

            Long idText = Long.valueOf(deleteIdField.getText());

            Department department = departmentService.findDepartmentById(idText);
            if (department != null) {
                departmentService.deleteDepartment(department.getDepartmentId());
                loadDepartments();
            } else {
                System.out.println("Клиент с указанным ID не найден");
            }
        }
    }
    private Department selectedDepartment;

    @FXML
    private void handleTableClick(MouseEvent event) {
        selectedDepartment = departmentTable.getSelectionModel().getSelectedItem();
        try {
            if (userRole == Role.ADMIN || userRole == Role.MODERATOR) {
                if (selectedDepartment != null) {
                    departmentName.setText(selectedDepartment.getDepartmentName());
                    locAtion.setText(selectedDepartment.getLocation());
                    description.setText(selectedDepartment.getDescription());
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных департамента: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditDepartment(ActionEvent event) {
        if (selectedDepartment == null) {
            System.out.println("Не выбран отдел для изменения.");
            return;
        }

        try {
            if (userRole == Role.ADMIN || userRole == Role.MODERATOR) {
                ServiceRequestDepartment updatedDepartment = getServiceRequestDepartment();

                Optional<Department> updated = departmentService.updatedDepartment(selectedDepartment.getDepartmentId(), updatedDepartment);

                if (updated.isPresent()) {
                    System.out.println("Отдел успешно обновлен: " + updated.get().getDepartmentName());
                } else {
                    System.out.println("Отдел с ID " + selectedDepartment.getDepartmentId() + " не найден.");
                }

                loadDepartments();
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, паспорт или зарплата): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных сотрудника: " + e.getMessage());
        }
    }

    private ServiceRequestDepartment getServiceRequestDepartment() {
        ServiceRequestDepartment updatedDepartment = new ServiceRequestDepartment();

        if (!description.getText().isEmpty()) {
            updatedDepartment.setDescription(description.getText());
        }
        if (!departmentName.getText().isEmpty()) {
            updatedDepartment.setDepartmentName(departmentName.getText());
        }
        if (!locAtion.getText().isEmpty()) {
            updatedDepartment.setLocation(locAtion.getText());
        }
        return updatedDepartment;
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(userPassport);
    }
}
