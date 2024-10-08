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
import org.example.demotest.entities.Department;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.example.demotest.app_controllers.LoginController.sessionID;

@Controller
public class DepartmentAppController {

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
    private RestTemplate restTemplate = new RestTemplate();
    private String url = "http://localhost:8080/department-api/v1/departments";

    //Таблица
    @FXML private TableView<Department> departmentTable;
    @FXML private TableColumn<Department, Long> DepartmentId;
    @FXML private TableColumn<Department, String> DepartmentName;
    @FXML private TableColumn<Department, String> Location;
    @FXML private TableColumn<Department, String> Description;

    //Поля для создания пользователя
    @FXML private TextField departmentName;
    @FXML private TextField locAtion;
    @FXML private TextField description;

    //Поля для удаления клиента
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter;
    @FXML private TextField departmentNameFilter;
    @FXML private TextField locationFilter;
    ObservableList<Department> observableDepartmentList = FXCollections.observableArrayList();


    public void initialize() {
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
        departmentName.setText(null);
        locAtion.setText(null);
        description.setText(null);
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
            e.printStackTrace();
            System.out.println("Ошибка при загрузке отделов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddDepartment(ActionEvent event) {
        try {
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
        } catch (Exception e) {
            // Логирование ошибки и/или уведомление пользователя
            e.printStackTrace();

            System.out.println("Ошибка при добавлении отдела: " + e.getMessage());
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


    @Autowired
    private DepartmentService departmentService;

    @FXML
    private void handleDeleteDepartment() {
        Long idText = Long.valueOf(deleteIdField.getText());

        if (idText != null) {
            // Найдем отдел по Id
            Department department = departmentService.findDepartmentById(idText);
            if (department != null) {
                departmentService.deleteDepartment(department.getDepartmentId());
                loadDepartments();
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
