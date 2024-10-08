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
import org.example.demotest.entities.Product;
import org.example.demotest.entities.ProductType;
import org.example.demotest.entities.Project;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.example.demotest.app_controllers.LoginController.sessionID;

@Controller
public class ProductAppController {

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

    private RestTemplate restTemplate = new RestTemplate();
    private String url = "http://localhost:8080/product-api/v1/products";

    //Таблица
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Long> ProductId;
    @FXML private TableColumn<Product, Long> ProjectId;
    @FXML private TableColumn<Product, String> ProductName;
    @FXML private TableColumn<Product, ProductType> ProducTType;
    @FXML private TableColumn<Product, Integer> Quantity;
    @FXML private TableColumn<Product, Double> Weight;
    @FXML private TableColumn<Product, Integer> Cost;

    //Поля для создания пользователя
    @FXML private TextField projectIdField;
    @FXML private TextField productNameField;
    @FXML private ComboBox<ProductType> productTypeField;
    @FXML private TextField quantityField;
    @FXML private TextField weightField;
    @FXML private TextField costField;

    //Поля для удаления пользователя
    @FXML private TextField deleteIdField;

    //Фильтры
    @FXML private TextField idFilter;
    @FXML private TextField productNameFilter;
    @FXML private ComboBox<String> productTypeFilter;
    ObservableList<Product> observableProductsList = FXCollections.observableArrayList();


    public void initialize() {
        ProductId.setCellValueFactory(new PropertyValueFactory<>("ProductId"));
        ProjectId.setCellValueFactory(cellData -> {
            Project project = cellData.getValue().getProject();
            return new SimpleObjectProperty<>(project != null ? project.getProjectId() : null);
        });


        ProductName.setCellValueFactory(new PropertyValueFactory<>("ProductName"));
        ProducTType.setCellValueFactory(new PropertyValueFactory<>("ProductType"));
        Quantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        Weight.setCellValueFactory(new PropertyValueFactory<>("Weight"));
        Cost.setCellValueFactory(new PropertyValueFactory<>("Cost"));
        productTypeField.getItems().setAll(ProductType.values());

        productNameField.setTextFormatter(createAlphaFilter());
        projectIdField.setTextFormatter(createNumericFilter());
        quantityField.setTextFormatter(createNumericFilter());
        weightField.setTextFormatter(createNumeric10Filter());
        costField.setTextFormatter(createNumeric10Filter());

        setupFilters();
        loadProducts();
    }

    @FXML
    private void handleUpdateProducts() { loadProducts(); }

    @FXML
    private void handleCleanButton(){
        projectIdField.setText(null);
        productNameField.setText(null);
        deleteIdField.setText(null);
        productTypeField.getSelectionModel().clearSelection();
        quantityField.setText(null);
        weightField.setText(null);
        costField.setText(null);
    }

    private void loadProducts() {
        try {
            Product[] products = restTemplate.getForObject(url, Product[].class);
            if (products != null && products.length > 0) {
                observableProductsList.setAll(Arrays.asList(products));
                productTable.setItems(observableProductsList);
            } else {
                productTable.setItems(FXCollections.emptyObservableList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке продуктов: " + e.getMessage());
        }
    }



    @FXML
    private void handleAddProduct(ActionEvent event) {
        try {
            // Проверка на существование ID сотрудника

            Long projectIdValue = Long.valueOf(projectIdField.getText());
            // Получите объект Project из службы, а не только его ID
            Project project = productService.getProjectById(projectIdValue);

            if (project == null) {
                System.out.println("Проект с указанным ID не найден.");
                return;
            }

            ServiceRequestProduct newProduct = ServiceRequestProduct.builder()
                    .project(project)
                    .productName(productNameField.getText())
                    .productType(productTypeField.getValue())
                    .quantity(Integer.valueOf(quantityField.getText()))
                    .weight(Double.valueOf(weightField.getText()))
                    .cost(Integer.valueOf(costField.getText()))
                    .build();
            System.out.println("Отправляемый продукт: " + newProduct);
            Product productCreated = restTemplate.postForObject(url, newProduct, Product.class);
            if (productCreated != null) {
                observableProductsList.add(productCreated); // Используйте observableUserList
                productTable.setItems(observableProductsList);
                System.out.println("Продукт добавлен: " + productCreated);
            }
        } catch (Exception e) {
            // Логирование ошибки и/или уведомление пользователя
            e.printStackTrace();

            System.out.println("Ошибка при добавлении продукта: " + e.getMessage());
        }
    }

    private void setupFilters() {
        idFilter.setTextFormatter(createNumericFilter());

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        productNameFilter.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        productTypeFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

    }

    private void applyFilters() {
        String id = idFilter.getText();
        String productName = productNameFilter.getText().toLowerCase();
        String productTypeFilterValue = productTypeFilter.getValue();

        productTable.setItems(observableProductsList.filtered(product -> {
            boolean matchesId = id.isEmpty() || String.valueOf(product.getProductId()).contains(id);
            boolean matchesProductName = productName.isEmpty() || product.getProductName().toLowerCase().contains(productName);
            boolean matchesProductTypeFilterValue = productTypeFilterValue == null || "ALL".equals(productTypeFilterValue) || product.getProductType().name().equalsIgnoreCase(productTypeFilterValue);

            // Возвращаем результат проверки всех фильтров
            return matchesId && matchesProductName && matchesProductTypeFilterValue;
        }));
    }


    @Autowired
    private ProductService productService;
    @FXML
    private void handleDeleteProduct() {
        Long idText = Long.valueOf(deleteIdField.getText());

        if (idText != null) {
            // Найдем пользователя по Id
            Product product = productService.findProductById(idText);
            if (product != null) {
                productService.deleteProduct(product.getProductId());
                loadProducts();
            } else {
                System.out.println("Продукт с указанным ID не найден");
            }
        } else {
            // Обработка ситуации, если оба поля пусты
            System.out.println("Введите ID для удаления продукта");
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        LoginManager loginManager = new LoginManager(stage, applicationContext);
        loginManager.showMainView(String.valueOf(sessionID));
    }
}
