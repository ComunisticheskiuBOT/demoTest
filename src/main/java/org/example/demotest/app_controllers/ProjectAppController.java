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
import org.example.demotest.dto.ServiceRequestProject;
import org.example.demotest.entities.MaterialType;
import org.example.demotest.entities.Project;
import org.example.demotest.entities.Role;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.EmployeeService;
import org.example.demotest.services.ProjectService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ProjectAppController {
    private final Logger logger = Logger.getLogger(ProjectAppController.class.getName());
    private final ApplicationContext applicationContext;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    public ProjectAppController(ApplicationContext applicationContext, EmployeeService employeeService, ProjectService projectService){
        this.projectService = projectService;
        this.employeeService = employeeService;
        this.applicationContext = applicationContext;
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

    private TextFormatter<String> createDurationFilter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.isEmpty()) {
                return change;
            }

            if (newText.startsWith("PT")) {
                String remainingText = newText.substring(2);

                if (remainingText.matches("\\d*[MHDY]?")) {
                    return change;
                }
            } else {
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
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/project-api/v1/projects";

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

    private Long userPassport;
    private Role userRole;

    public void initialize(Long userPassport) {
        this.userPassport = userPassport;
        this.userRole = employeeService
                .findEmployeeByPassportNumber(userPassport)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с данным номером паспорта не найден"))
                .getRole();

        ProjectId.setCellValueFactory(new PropertyValueFactory<>("ProjectId"));
        EngineerId.setCellValueFactory(new PropertyValueFactory<>("EngineerId"));
        ProjectName.setCellValueFactory(new PropertyValueFactory<>("ProjectName"));
        Drawing.setCellValueFactory(new PropertyValueFactory<>("Drawing"));
        MateriaLType.setCellValueFactory(new PropertyValueFactory<>("MaterialType"));
        ExpectedTime.setCellValueFactory(new PropertyValueFactory<>("ExpectedTime"));
        ProjectDescription.setCellValueFactory(new PropertyValueFactory<>("ProjectDescription"));

        if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {

            materialTypeField.getItems().setAll(org.example.demotest.entities.MaterialType.values());
            projectNameField.setTextFormatter(createAlphaFilter());
            engineerIdField.setTextFormatter(createNumericFilter());
            expectedTimeField.setTextFormatter(createDurationFilter());
        }

        setupFilters();
        loadProjects();
    }

    @FXML
    private void handleUpdateProjects(){
        loadProjects();
    }

    @FXML
    private void handleCleanButton(){
        if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
            engineerIdField.setText("");
            projectNameField.setText("");
            drawingField.setText("");
            materialTypeField.getSelectionModel().clearSelection();
            expectedTimeField.setText("");
            projectDescriptionField.setText("");
        }
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
            logger.log(Level.WARNING,"Ошибка при загрузке проектов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProject(ActionEvent event) {
        try {
            if(userRole == org.example.demotest.entities.Role.ADMIN) {


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
            }
        } catch (Exception e) {
            logger.log(Level.WARNING,"Ошибка при добавлении проекта: " + e.getMessage());
        }
    }

    private Project selectedProject;

    @FXML
    private void handleTableClick(MouseEvent event) {
        selectedProject = projectTable.getSelectionModel().getSelectedItem();
        try {
            if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
                if (selectedProject != null) {
                    engineerIdField.setText(String.valueOf(selectedProject.getEngineerId()));
                    projectNameField.setText(selectedProject.getProjectName());
                    drawingField.setText(selectedProject.getDrawing());
                    projectDescriptionField.setText(selectedProject.getProjectDescription());
                    expectedTimeField.setText(String.valueOf(selectedProject.getExpectedTime()));
                    materialTypeField.setValue(selectedProject.getMaterialType());
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, длительность): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных сотрудника: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditProject(ActionEvent event) {
        if (selectedProject == null) {
            System.out.println("Не выбран проект для изменения.");
            return;
        }

        try {
            if(userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
                ServiceRequestProject updatedProject = new ServiceRequestProject();

                if (!engineerIdField.getText().isEmpty()) {
                    updatedProject.setEngineerId(Long.valueOf(engineerIdField.getText()));
                }
                if (!projectNameField.getText().isEmpty()) {
                    updatedProject.setProjectDescription(projectNameField.getText());
                }
                if (!drawingField.getText().isEmpty()) {
                    updatedProject.setDrawing(drawingField.getText());
                }
                if (!projectDescriptionField.getText().isEmpty()) {
                    updatedProject.setProjectDescription(projectDescriptionField.getText());
                }
                if (!expectedTimeField.getText().isEmpty()) {
                    updatedProject.setExpectedTime(Duration.parse(expectedTimeField.getText()));
                }
                if (materialTypeField.getValue() != null) {
                    updatedProject.setMaterialType(materialTypeField.getValue());
                }

                Optional<Project> updated = projectService.updatedProject(selectedProject.getProjectId(), updatedProject);

                if (updated.isPresent()) {
                    System.out.println("Проект успешно обновлен: " + updated.get().getProjectName());
                } else {
                    System.out.println("Проект с ID " + selectedProject.getProjectId() + " не найден.");
                }

                loadProjects();
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например длительность): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных проекта: " + e.getMessage());
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

            return matchesId && matchesProjectName && matchesDrawing && matchesMaterialTypeFilterValue;
        }));
    }

    @FXML
    private void handleDeleteProject() {
        if(userRole == org.example.demotest.entities.Role.ADMIN) {

            Long idText = Long.valueOf(deleteIdField.getText());

            Project project = projectService.findProjectById(idText);
            if (project != null) {
                projectService.deleteProject(project.getProjectId());
                loadProjects();
            } else {
                System.out.println("Проект с указанным ID не найден");
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
