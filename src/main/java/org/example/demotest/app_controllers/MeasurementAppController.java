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
import org.example.demotest.dto.ServiceRequestMeasurement;
import org.example.demotest.entities.Client;
import org.example.demotest.entities.Measurement;
import org.example.demotest.entities.enums.Role;
import org.example.demotest.managers.MainViewManager;
import org.example.demotest.services.EmployeeService;
import org.example.demotest.services.MeasurementService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class MeasurementAppController {
    private final Logger logger = Logger.getLogger(MeasurementAppController.class.getName());
    private final ApplicationContext applicationContext;
    private final MeasurementService measurementService;
    private final EmployeeService employeeService;

    public MeasurementAppController(ApplicationContext applicationContext, MeasurementService measurementService, EmployeeService employeeService) {
        this.applicationContext = applicationContext;
        this.measurementService = measurementService;
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

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/measurement-api/v1/measurements";

    //Таблица
    @FXML private TableView<Measurement> measurementTable;
    @FXML private TableColumn<Client, Long> MeasurementId;
    @FXML private TableColumn<Client, String> Name, Description;

    //Поля для создания пользователя
    @FXML private TextField nameField, descriptionField;

    //Поля для удаления клиента
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter, measurementFilter;
    ObservableList<Measurement> observableMeasurementList = FXCollections.observableArrayList();

    private Long userPassport;
    private Role userRole;

    public void initialize(Long userPassport) {
        this.userPassport = userPassport;
        this.userRole = employeeService
                .findEmployeeByPassportNumber(userPassport)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с данным номером паспорта не найден"))
                .getRole();

        MeasurementId.setCellValueFactory(new PropertyValueFactory<>("MeasurementId"));
        Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        Description.setCellValueFactory(new PropertyValueFactory<>("Description"));

        setupFilters();
        loadMeasurements();
    }

    @FXML
    private void handleUpdateMeasurements(){
        loadMeasurements();
    }

    @FXML
    private void handleCleanButton(){
        if (userRole == Role.ADMIN || userRole == Role.MODERATOR) {
            nameField.setText("");
            deleteIdField.setText("");
        }
    }

    private void loadMeasurements() {
        try {
            Measurement[] measurements = restTemplate.getForObject(url, Measurement[].class);
            if (measurements != null && measurements.length > 0) {
                observableMeasurementList.setAll(Arrays.asList(measurements));
                measurementTable.setItems(observableMeasurementList);
            } else {
                measurementTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при загрузке единиц измерения: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddMeasurement(ActionEvent event) {
        try {
            if(userRole == Role.ADMIN) {

                ServiceRequestMeasurement newMeasurement = ServiceRequestMeasurement.builder()
                        .name(nameField.getText())
                        .description(descriptionField.getText())
                        .build();
                Measurement measurementCreated = restTemplate.postForObject(url, newMeasurement, Measurement.class);
                if (measurementCreated != null) {
                    observableMeasurementList.add(measurementCreated); // Используйте observableUserList
                    measurementTable.setItems(observableMeasurementList);
                    System.out.println("Пользователь добавлен: " + measurementCreated);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при добавлении сотрудника: " + e.getMessage());
        }
    }

    private Measurement selectedMeasurement;

    @FXML
    private void handleTableClick(MouseEvent event) {
        selectedMeasurement = measurementTable.getSelectionModel().getSelectedItem();
        try {
            if (userRole == Role.ADMIN || userRole == Role.MODERATOR) {
                if (selectedMeasurement != null) {
                    nameField.setText(selectedMeasurement.getName());
                    descriptionField.setText(selectedMeasurement.getDescription());
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, номер телефона): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных сотрудника: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditMeasurement(ActionEvent event) {
        if (selectedMeasurement == null) {
            System.out.println("Не выбран сотрудник для изменения.");
            return;
        }

        try {
            if(userRole == Role.ADMIN || userRole == Role.MODERATOR) {
                ServiceRequestMeasurement updatedMeasurement = getServiceRequestMeasurement();

                Optional<Measurement> updated = measurementService.updatedMeasurement(selectedMeasurement.getMeasurementId(), updatedMeasurement);

                if (updated.isPresent()) {
                    System.out.println("Единица измерения успешна обновлена: " + updated.get().getName());
                } else {
                    System.out.println("Клиент с ID " + selectedMeasurement.getMeasurementId() + " не найден.");
                }

                loadMeasurements();
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, номер телефона): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных клиента: " + e.getMessage());
        }
    }

    private ServiceRequestMeasurement getServiceRequestMeasurement() {
        ServiceRequestMeasurement updatedMeasurement = new ServiceRequestMeasurement();

        if (!nameField.getText().isEmpty()) {
            updatedMeasurement.setName(nameField.getText());
        }
        if (!descriptionField.getText().isEmpty()) {
            updatedMeasurement.setDescription(descriptionField.getText());
        }

        return updatedMeasurement;
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());
        measurementFilter.setTextFormatter(createAlphaFilter());

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        measurementFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = safeGetText(idFilter);
        String name = safeGetText(nameField);

        measurementTable.setItems(observableMeasurementList.filtered(measurement -> {
            boolean matchesId = id.isEmpty() || String.valueOf(measurement.getMeasurementId()).contains(id);
            boolean matchesName = name.isEmpty() || measurement.getName().toLowerCase().contains(name);

            return matchesId && matchesName;
        }));
    }
    private String safeGetText(TextField textField) {
        String text = textField.getText();
        return text != null ? text.trim().toLowerCase() : "";
    }

    @FXML
    private void handleDeleteMeasurement() {
        if (userRole == Role.ADMIN) {
            Long idText = Long.valueOf(deleteIdField.getText());

            Measurement measurement = measurementService.findMeasurementById(idText);
            if (measurement != null) {
                measurementService.deleteMeasurement(measurement.getMeasurementId());
                loadMeasurements();
            } else {
                System.out.println("Клиент с указанным ID не найден");
            }
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        MainViewManager mainViewManager = new MainViewManager(stage, applicationContext);
        mainViewManager.storages(userPassport, userRole);
    }
}
