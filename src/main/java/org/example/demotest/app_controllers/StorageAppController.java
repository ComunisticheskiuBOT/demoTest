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
import org.example.demotest.dto.ServiceRequestStorage;
import org.example.demotest.entities.*;
import org.example.demotest.entities.enums.Role;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.managers.MainViewManager;
import org.example.demotest.services.EmployeeService;
import org.example.demotest.services.MeasurementService;
import org.example.demotest.services.ProductService;
import org.example.demotest.services.StorageService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.demotest.app_controllers.EmployeeAppController.DateFilter;
import static org.example.demotest.app_controllers.EmployeeAppController.NumericFilter;

@Controller
public class StorageAppController {
    private final Logger logger = Logger.getLogger(StorageAppController.class.getName());

    private final ApplicationContext applicationContext;
    private final EmployeeService employeeService;
    private final ProductService productService;
    private final MeasurementService measurementService;

    private final StorageService storageService;

    public StorageAppController(EmployeeService employeeService, StorageService storageService, MeasurementService measurementService, ProductService productService, ApplicationContext applicationContext){
        this.employeeService = employeeService;
        this.storageService = storageService;
        this.productService = productService;
        this.measurementService = measurementService;
        this.applicationContext = applicationContext;
    }

    private TextFormatter<String> createNumericFilter() {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        });
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

    private TextFormatter<String> createDateFilter() {
        return DateFilter();
    }
    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/storage-api/v1/storages";

    //Таблица
    @FXML private TableView<Storage> storageTable;
    @FXML private TableColumn<Storage, Long> StorageId;
    @FXML private TableColumn<Storage, String> StorageName;
    @FXML private TableColumn<Storage, Long> ProductId;
    @FXML private TableColumn<Storage, String> ProductName;
    @FXML private TableColumn<Storage, Integer> Quantity;
    @FXML private TableColumn<Storage, String> MeasurementName;
    @FXML private TableColumn<Storage, Date> ArrivalDate;

    //Поля для создания пользователя
    @FXML private TextField storageNameField, measurementField, productIdField, arrivalDateField, quantityField;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter, productIdFilter, measurementFilter, productNameFilter, storageNameFilter;
    ObservableList<Storage> observableStoragesList = FXCollections.observableArrayList();

    private Long userPassport;
    private Role userRole;

    public void initialize(Long userPassport) {
        this.userPassport = userPassport;
        this.userRole = employeeService
                .findEmployeeByPassportNumber(userPassport)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с данным номером паспорта не найден"))
                .getRole();

        StorageId.setCellValueFactory(new PropertyValueFactory<>("StorageId"));
        ProductId.setCellValueFactory(cellData -> {
            Product product = cellData.getValue().getProduct();
            return new SimpleObjectProperty<>(product != null ? product.getProductId() : null);
        });
        ArrivalDate.setCellValueFactory(new PropertyValueFactory<>("ArrivalDate"));
        Quantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        if(userRole == Role.ADMIN || userRole == Role.MODERATOR) {
            storageNameField.setTextFormatter(createAlphaFilter());
            productIdField.setTextFormatter(createNumericFilter());
            arrivalDateField.setTextFormatter(createDateFilter());
        }

        setupFilters();
        loadStorages();
    }

    @FXML
    private void handleUpdateStorages() { loadStorages(); }

    @FXML
    private void handleCleanButton(){
        if(userRole == Role.ADMIN || userRole == Role.MODERATOR) {
            storageNameField.setText("");
            productIdField.setText("");

            arrivalDateField.setText("");
            if (userRole == Role.ADMIN) deleteIdField.setText(null);
            quantityField.setText("");
        }
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
            logger.log(Level.SEVERE,"Ошибка при загрузке складов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddStorages(ActionEvent event) {
        try {
            if(userRole == Role.ADMIN) {

                Long productIdValue = Long.valueOf(productIdField.getText());
                Product product = storageService.getProductById(productIdValue);

                if (product == null) {
                    System.out.println("Продукт с указанным ID не найден.");
                    return;
                }

                Long measurementIdValue = Long.valueOf(productIdField.getText());
                Measurement measurement = storageService.getMeasurementById(measurementIdValue);

                if (measurement == null) {
                    System.out.println("Единица измерения с указанным ID не найден.");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date arrivalDate = sdf.parse(arrivalDateField.getText());

                ServiceRequestStorage newStorage = ServiceRequestStorage.builder()
                        .storageName(String.valueOf(storageNameField))
                        .product(product)
                        .productName(String.valueOf(product.getProductName()))
                        .arrivalDate(arrivalDate)
                        .measurement(measurement)
                        .quantity(Integer.valueOf(quantityField.getText()))
                        .build();
                Storage storageCreated = restTemplate.postForObject(url, newStorage, Storage.class);
                if (storageCreated != null) {
                    observableStoragesList.add(storageCreated); // Используйте observableUserList
                    storageTable.setItems(observableStoragesList);
                    System.out.println("Склад добавлен: " + storageCreated);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при добавлении склада: " + e.getMessage());
        }
    }

    private Storage selectedStorage;

    @FXML
    private void handleTableClick(MouseEvent event) {
        selectedStorage = storageTable.getSelectionModel().getSelectedItem();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (userRole == Role.ADMIN || userRole == Role.MODERATOR) {
                if (selectedStorage != null) {
                    storageNameField.setText(String.valueOf(selectedStorage.getStorageName()));
                    productIdField.setText(String.valueOf(selectedStorage.getProduct().getProductId()));
                    quantityField.setText(String.valueOf(selectedStorage.getQuantity()));
                    measurementField.setText(String.valueOf(selectedStorage.getMeasurements()));
                    arrivalDateField.setText(sdf.format(selectedStorage.getArrivalDate()));
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например количество): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных склада: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditStorage(ActionEvent event) {
        if (selectedStorage == null) {
            System.out.println("Не выбран склад для изменения.");
            return;
        }

        try {
            if(userRole == Role.ADMIN || userRole == Role.MODERATOR) {
                ServiceRequestStorage updatedStorage = new ServiceRequestStorage();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date parsedArrivalDate = sdf.parse(arrivalDateField.getText());

                if (!storageNameField.getText().isEmpty()) {
                    updatedStorage.setStorageId(Long.valueOf(storageNameField.getText()));
                }
                if (!productIdField.getText().isEmpty()) {
                    updatedStorage.setProduct(productService.findProductById(Long.valueOf(productIdField.getText())));
                }
                if (!quantityField.getText().isEmpty()) {
                    updatedStorage.setQuantity(Integer.valueOf(quantityField.getText()));
                }
                if (!measurementField.getText().isEmpty()) {
                    updatedStorage.setMeasurement(measurementService.findMeasurementByName(String.valueOf(measurementField.getText())));
                }
                if (arrivalDateField.getText() != null) {
                    updatedStorage.setArrivalDate(parsedArrivalDate);
                }

                Optional<Storage> updated = storageService.updatedStorage(selectedStorage.getStorageId(), updatedStorage);

                if (updated.isPresent()) {
                    System.out.println("Склад успешно обновлен: " + updated.get().getStorageId());
                } else {
                    System.out.println("Склад с ID " + selectedStorage.getStorageId() + " не найден.");
                }

                loadStorages();
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например количество): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных склада: " + e.getMessage());
        }
    }

    private void setupFilters() {
        measurementFilter.setTextFormatter(createAlphaFilter());
        productNameFilter.setTextFormatter(createAlphaFilter());
        storageNameFilter.setTextFormatter(createAlphaFilter());
        productIdFilter.setTextFormatter(NumericFilter(1000));

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        productIdFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        measurementFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        productNameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        storageNameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String id = idFilter.getText();
        String productId = productIdFilter.getText();

        storageTable.setItems(observableStoragesList.filtered(storage -> {
            boolean matchesId = id.isEmpty() || String.valueOf(storage.getStorageId()).contains(id);
            boolean matchesProductId = productId.isEmpty() || String.valueOf(storage.getProductId()).contains(productId);
            return matchesId && matchesProductId;
        }));
    }

    @FXML
    private void handleDeleteStorage() {
        if(userRole == Role.ADMIN) {
            Long idText = Long.valueOf(deleteIdField.getText());

            Storage storage = storageService.findStorageById(idText);
            if (storage != null) {
                storageService.deleteStorage(storage.getStorageId());
                loadStorages();
            } else {
                System.out.println("Склад указанным ID не найден");
            }
        }
    }
    @FXML
    public void handleMeasurementButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        MainViewManager mainViewManager = new MainViewManager(stage, applicationContext);
        mainViewManager.measurements(userPassport, userRole);
    }
    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(userPassport);
    }
}
