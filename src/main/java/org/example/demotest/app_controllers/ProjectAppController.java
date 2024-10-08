package org.example.demotest.app_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.demotest.dto.ServiceRequestProject;
import org.example.demotest.entities.MaterialType;
import org.example.demotest.entities.Project;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;

import static org.example.demotest.app_controllers.LoginController.sessionID;

@Controller
public class ProjectAppController {

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

    private TextFormatter<String> createDurationFilter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.isEmpty()) {
                return change;
            }

            if (newText.startsWith("PT")) {
                String remainingText = newText.substring(2);

                if (remainingText.matches("\\d*[MHDY]?")) {
                    return change; // Разрешаем ввод
                }
            } else {
                // Если строка не начинается с "PT", добавляем его
                if (newText.matches("\\d+")) {
                    String formattedText = "PT" + newText;

                    change.setText(formattedText);
                    change.setRange(0, change.getControlText().length());
                    return change;
                }
            }

            return null;
        });
    }



    
    private RestTemplate restTemplate = new RestTemplate();
    private String url = "http://localhost:8080/project-api/v1/projects";

    //Таблица
    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, Long> ProjectId;
    @FXML private TableColumn<Project, Long> EngineerId;
    @FXML private TableColumn<Project, String> ProjectName;
    @FXML private TableColumn<Project, String> Drawing;
    @FXML private TableColumn<Project, MaterialType> MateriaLType;
    @FXML private TableColumn<Project, Duration> ExpectedTime;
    @FXML private TableColumn<Project, String> ProjectDescription;

    //Поля для создания пользователя
    @FXML private TextField engineerIdField;
    @FXML private TextField projectNameField;
    @FXML private TextField drawingField;
    @FXML private TextField expectedTimeField;
    @FXML private TextField projectDescriptionField;
    @FXML private ComboBox<MaterialType> materialTypeField;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter;
    @FXML private TextField projectNameFilter;
    @FXML private TextField drawingFilter;
    @FXML private ComboBox<String> materialTypeFilter;
    ObservableList<Project> observableProjectsList = FXCollections.observableArrayList();


    public void initialize() {
        ProjectId.setCellValueFactory(new PropertyValueFactory<>("ProjectId"));
        EngineerId.setCellValueFactory(new PropertyValueFactory<>("EngineerId"));
        ProjectName.setCellValueFactory(new PropertyValueFactory<>("ProjectName"));
        Drawing.setCellValueFactory(new PropertyValueFactory<>("Drawing"));
        MateriaLType.setCellValueFactory(new PropertyValueFactory<>("MaterialType"));
        ExpectedTime.setCellValueFactory(new PropertyValueFactory<>("ExpectedTime"));
        ProjectDescription.setCellValueFactory(new PropertyValueFactory<>("ProjectDescription"));
        materialTypeField.getItems().setAll(org.example.demotest.entities.MaterialType.values());

        projectNameField.setTextFormatter(createAlphaFilter());
        engineerIdField.setTextFormatter(createNumericFilter());
        expectedTimeField.setTextFormatter(createDurationFilter());

        setupFilters();
        loadProjects();
    }

    @FXML
    private void handleUpdateProjects(){
        loadProjects();
    }

    @FXML
    private void handleCleanButton(){
        engineerIdField.setText(null);
        projectNameField.setText(null);
        drawingField.setText(null);
        deleteIdField.setText(null);
        materialTypeField.getSelectionModel().clearSelection();
        expectedTimeField.setText(null);
        projectDescriptionField.setText(null);
    }

    private void loadProjects() {
        try {
            Project[] projects = restTemplate.getForObject(url, Project[].class);
            if (projects != null && projects.length > 0) { // Проверяем, что массив не пустой
                observableProjectsList.setAll(Arrays.asList(projects));
                projectTable.setItems(observableProjectsList);
            } else {
                projectTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке проектов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProject(ActionEvent event) {
        try {
            // Проверка на существование ID сотрудника

            Long employeeId = Long.valueOf(engineerIdField.getText());
            if (!projectService.engineerExists(employeeId)) { // Проверка существования
                System.out.println("Сотрудник с указанным ID не найден.");
                return;
            }

            ServiceRequestProject newProject = ServiceRequestProject.builder()
                    .engineerId(Long.valueOf(engineerIdField.getText()))
                    .projectName(projectNameField.getText())
                    .drawing(drawingField.getText())
                    .materialType(materialTypeField.getValue())
                    .expectedTime(Duration.parse(expectedTimeField.getText()))
                    .projectDescription(projectDescriptionField.getText())
                    .build();
            Project projectCreated = restTemplate.postForObject(url, newProject, Project.class);
            if (projectCreated != null) {
                observableProjectsList.add(projectCreated); // Используйте observableUserList
                projectTable.setItems(observableProjectsList);
                System.out.println("Проект добавлен: " + projectCreated);
            }
        } catch (Exception e) {
            // Логирование ошибки и/или уведомление пользователя
            e.printStackTrace();

            System.out.println("Ошибка при добавлении проекта: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        projectNameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        drawingFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        materialTypeFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

    }

    private void applyFilters() {
        String id = idFilter.getText();
        String projectName = projectNameFilter.getText().toLowerCase();
        String drawing = drawingFilter.getText().toLowerCase();
        String materialTypeFilterValue = materialTypeFilter.getValue();

        projectTable.setItems(observableProjectsList.filtered(project -> {
            boolean matchesId = id.isEmpty() || String.valueOf(project.getProjectId()).contains(id);
            boolean matchesProjectName = projectName.isEmpty() || project.getProjectName().toLowerCase().contains(projectName);
            boolean matchesDrawing = drawing.isEmpty() || project.getDrawing().toLowerCase().contains(drawing);
            boolean matchesMaterialTypeFilterValue = materialTypeFilterValue == null || "ALL".equals(materialTypeFilterValue) || project.getMaterialType().name().equalsIgnoreCase(materialTypeFilterValue);

            // Возвращаем результат проверки всех фильтров
            return matchesId && matchesProjectName && matchesDrawing && matchesMaterialTypeFilterValue;
        }));
    }


    @Autowired
    private ProjectService projectService;
    @FXML
    private void handleDeleteProject() {
        Long idText = Long.valueOf(deleteIdField.getText());

        if (idText != null) {
            // Найдем пользователя по Id
            Project project = projectService.findProjectById(idText);
            if (project != null) {
                projectService.deleteProject(project.getProjectId());
                loadProjects();
            } else {
                System.out.println("Проект с указанным ID не найден");
            }
        } else {
            // Обработка ситуации, если оба поля пусты
            System.out.println("Введите ID для удаления проекта");
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(String.valueOf(sessionID));
    }
}
