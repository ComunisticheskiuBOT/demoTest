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

import org.example.demotest.dto.ServiceRequestProduct;
import org.example.demotest.dto.ServiceRequestQuality;
import org.example.demotest.entities.*;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.ProductService;
import org.example.demotest.services.QualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Arrays;
import java.util.Date;

import static org.example.demotest.app_controllers.LoginController.sessionID;

@Controller
public class QualityAppController {

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
    private String url = "http://localhost:8080/quality-api/v1/qualities";

    //Таблица
    @FXML private TableView<Quality> qualityTable;
    @FXML private TableColumn<Quality, Long> QualityId;
    @FXML private TableColumn<Quality, Long> InspectorId;
    @FXML private TableColumn<Quality, Long> ProductId;
    @FXML private TableColumn<Quality, Date> InspectionDate;
    @FXML private TableColumn<Quality, Result> ResulT;
    @FXML private TableColumn<Quality, String> Comments;


    //Поля для создания пользователя
    @FXML private TextField inspectorIdField;
    @FXML private TextField productIdField;
    @FXML private TextField inspectionDateField;
    @FXML private ComboBox<Result> resultTypeField;
    @FXML private TextField commentsTimeField;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter;
    @FXML private TextField inspectorIdFilter;
    @FXML private TextField productIdFilter;
    @FXML private ComboBox<String> resultTypeFilter;
    ObservableList<Quality> observableQualitiesList = FXCollections.observableArrayList();


    public void initialize() {
        QualityId.setCellValueFactory(new PropertyValueFactory<>("QualityId"));
        InspectorId.setCellValueFactory(cellData -> {
            Employee employee = cellData.getValue().getInspector();
            return new SimpleObjectProperty<>(employee != null ? employee.getId() : null);
        });
        ProductId.setCellValueFactory(cellData -> {
            Product product = cellData.getValue().getProduct();
            return new SimpleObjectProperty<>(product != null ? product.getProductId() : null);
        });
        InspectionDate.setCellValueFactory(new PropertyValueFactory<>("InspectionDate"));
        ResulT.setCellValueFactory(new PropertyValueFactory<>("Result"));
        Comments.setCellValueFactory(new PropertyValueFactory<>("Comments"));
        resultTypeField.getItems().setAll(Result.values());

        inspectorIdField.setTextFormatter(createNumericFilter());
        productIdField.setTextFormatter(createNumericFilter());
        inspectionDateField.setTextFormatter(createDateFilter());

        setupFilters();
        loadQualities();
    }

    @FXML
    private void handleUpdateQualities() { loadQualities(); }

    @FXML
    private void handleCleanButton(){
        inspectorIdField.setText(null);
        productIdField.setText(null);
        inspectionDateField.setText(null);
        deleteIdField.setText(null);
        resultTypeField.getSelectionModel().clearSelection();
        commentsTimeField.setText(null);
    }

    private void loadQualities() {
        try {
            Quality[] qualities = restTemplate.getForObject(url, Quality[].class);
            if (qualities != null && qualities.length > 0) {
                observableQualitiesList.setAll(Arrays.asList(qualities));
                qualityTable.setItems(observableQualitiesList);
            } else {
                qualityTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке проверок качества: " + e.getMessage());
        }
    }



    @FXML
    private void handleAddQuality(ActionEvent event) {
        try {
            Long productIdValue = Long.valueOf(productIdField.getText());
            Product product = qualityService.getProductById(productIdValue);

            if (product == null) {
                System.out.println("Продукт с указанным ID не найден.");
                return;
            }

            Long inspectorIdValue = Long.valueOf(inspectorIdField.getText());
            Employee employee = qualityService.getEmployeeById(inspectorIdValue);

            if (employee == null) {
                System.out.println("Сотрудник с указанным ID не найден.");
                return;
            }

            if (employee.getStatus() == Status.NONACTIVE) {
                System.out.println("Инспектор неактивен. Проверку качества создать нельзя.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date inspectionDate = sdf.parse(inspectionDateField.getText());

            ServiceRequestQuality newQuality = ServiceRequestQuality.builder()
                    .product(product)
                    .inspector(employee)
                    .inspectionDate(inspectionDate)
                    .result(resultTypeField.getValue())
                    .comments(commentsTimeField.getText())
                    .build();
            Quality qualityCreated = restTemplate.postForObject(url, newQuality, Quality.class);
            if (qualityCreated != null) {
                observableQualitiesList.add(qualityCreated); // Используйте observableUserList
                qualityTable.setItems(observableQualitiesList);
                System.out.println("Проверка качества продукта добавлена: " + qualityCreated);
            }
        } catch (Exception e) {
            // Логирование ошибки и/или уведомление пользователя
            e.printStackTrace();

            System.out.println("Ошибка при добавлении проверки качества продукта: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        inspectorIdFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        productIdFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        resultTypeFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

    }

    private void applyFilters() {
        String id = idFilter.getText();
        String inspectorId = inspectorIdFilter.getText();
        String productId = productIdFilter.getText();
        String resultTypeFilterValue = resultTypeFilter.getValue();

        qualityTable.setItems(observableQualitiesList.filtered(quality -> {
            boolean matchesId = id.isEmpty() || String.valueOf(quality.getQualityId()).contains(id);
            boolean matchesInspectorId = inspectorId.isEmpty() || String.valueOf(quality.getInspectorId()).contains(inspectorId);
            boolean matchesProductId = productId.isEmpty() || String.valueOf(quality.getInspectorId()).contains(productId);
            boolean matchesResultTypeFilterValue = resultTypeFilterValue == null || "All".equals(resultTypeFilterValue) || quality.getResult().name().equalsIgnoreCase(resultTypeFilterValue);

            // Возвращаем результат проверки всех фильтров
            return matchesId && matchesProductId && matchesInspectorId && matchesResultTypeFilterValue;
        }));
    }


    @Autowired
    private QualityService qualityService;
    @FXML
    private void handleDeleteQuality() {
        Long idText = Long.valueOf(deleteIdField.getText());

        if (idText != null) {
            // Найдем пользователя по Id
            Quality quality = qualityService.findQualityById(idText);
            if (quality != null) {
                qualityService.deleteQuality(quality.getProductId());
                loadQualities();
            } else {
                System.out.println("Проверка качества с указанным ID не найден");
            }
        } else {
            // Обработка ситуации, если оба поля пусты
            System.out.println("Введите ID для проверки качества продукта");
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(String.valueOf(sessionID));
    }
}