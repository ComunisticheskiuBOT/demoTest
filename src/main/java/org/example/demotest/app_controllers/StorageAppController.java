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
import org.example.demotest.dto.ServiceRequestQuality;
import org.example.demotest.dto.ServiceRequestStorage;
import org.example.demotest.entities.*;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.QualityService;
import org.example.demotest.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.example.demotest.app_controllers.LoginController.sessionID;

@Controller
public class StorageAppController {

    @Autowired
    private ApplicationContext applicationContext;

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
    private String url = "http://localhost:8080/storage-api/v1/storages";

    //Таблица
    @FXML private TableView<Storage> storageTable;
    @FXML private TableColumn<Storage, Long> StorageId;
    @FXML private TableColumn<Storage, Long> ProductId;
    @FXML private TableColumn<Storage, Date> ArrivalDate;
    @FXML private TableColumn<Storage, Integer> Quantity;


    //Поля для создания пользователя
    @FXML private TextField storageIdField;
    @FXML private TextField productIdField;
    @FXML private TextField arrivalDateField;
    @FXML private TextField quantityField;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter;
    @FXML private TextField productIdFilter;
    ObservableList<Storage> observableStoragesList = FXCollections.observableArrayList();


    public void initialize() {
        StorageId.setCellValueFactory(new PropertyValueFactory<>("StorageId"));
        ProductId.setCellValueFactory(cellData -> {
            Product product = cellData.getValue().getProduct();
            return new SimpleObjectProperty<>(product != null ? product.getProductId() : null);
        });
        ArrivalDate.setCellValueFactory(new PropertyValueFactory<>("ArrivalDate"));
        Quantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        storageIdField.setTextFormatter(createNumericFilter());
        productIdField.setTextFormatter(createNumericFilter());
        arrivalDateField.setTextFormatter(createDateFilter());

        setupFilters();
        loadStorages();
    }

    @FXML
    private void handleUpdateStorages() { loadStorages(); }

    @FXML
    private void handleCleanButton(){
        storageIdField.setText(null);
        productIdField.setText(null);
        arrivalDateField.setText(null);
        deleteIdField.setText(null);
        quantityField.setText(null);
    }

    private void loadStorages() {
        try {
            Storage[] storages = restTemplate.getForObject(url, Storage[].class);
            if (storages != null && storages.length > 0) {
                observableStoragesList.setAll(Arrays.asList(storages));
                storageTable.setItems(observableStoragesList);
            } else {
                storageTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке складов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddStorages(ActionEvent event) {
        try {
            Long productIdValue = Long.valueOf(productIdField.getText());
            Product product = storageService.getProductById(productIdValue);

            if (product == null) {
                System.out.println("Продукт с указанным ID не найден.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date arrivalDate = sdf.parse(arrivalDateField.getText());

            ServiceRequestStorage newStorage = ServiceRequestStorage.builder()
                    .storageId(Long.valueOf(storageIdField.getText()))
                    .product(product)
                    .arrivalDate(arrivalDate)
                    .quantity(Integer.valueOf(quantityField.getText()))
                    .build();
            Storage storageCreated = restTemplate.postForObject(url, newStorage, Storage.class);
            if (storageCreated != null) {
                observableStoragesList.add(storageCreated); // Используйте observableUserList
                storageTable.setItems(observableStoragesList);
                System.out.println("Склад добавлен: " + storageCreated);
            }
        } catch (Exception e) {
            // Логирование ошибки и/или уведомление пользователя
            e.printStackTrace();

            System.out.println("Ошибка при добавлении склада: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        productIdFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = idFilter.getText();
        String productId = productIdFilter.getText();

        storageTable.setItems(observableStoragesList.filtered(quality -> {
            boolean matchesId = id.isEmpty() || String.valueOf(quality.getStorageId()).contains(id);
            boolean matchesProductId = productId.isEmpty() || String.valueOf(quality.getProductId()).contains(productId);

            // Возвращаем результат проверки всех фильтров
            return matchesId && matchesProductId;
        }));
    }


    @Autowired
    private StorageService storageService;
    @FXML
    private void handleDeleteStorage() {
        Long idText = Long.valueOf(deleteIdField.getText());

        if (idText != null) {
            Storage storage = storageService.findStorageById(idText);
            if (storage != null) {
                storageService.deleteStorage(storage.getStorageId());
                loadStorages();
            } else {
                System.out.println("Склад указанным ID не найден");
            }
        } else {
            System.out.println("Введите ID для склада");
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(String.valueOf(sessionID));
    }
}
