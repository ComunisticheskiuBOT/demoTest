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
import org.example.demotest.dto.ServiceRequestProduct;
import org.example.demotest.entities.*;
import org.example.demotest.managers.LoginManager;
import org.example.demotest.services.EmployeeService;
import org.example.demotest.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Double.*;
import static org.example.demotest.app_controllers.EmployeeAppController.Numeric10Filter;

@Controller
public class ProductAppController {
    private final Logger logger = Logger.getLogger(ProductAppController.class.getName());
    private final ApplicationContext applicationContext;
    private final EmployeeService employeeService;
    private final ProductService productService;

    public ProductAppController(EmployeeService employeeService, ProductService productService, ApplicationContext applicationContext){
        this.employeeService = employeeService;
        this.productService = productService;
        this.applicationContext = applicationContext;
    }

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
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d*")) {
                return change;
            }
            return null;
        });
    }

    private TextFormatter<String> createNumeric10Filter() {
        return Numeric10Filter();
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/product-api/v1/products";

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

    private Long userPassport;
    private Role userRole;

    public void initialize(Long userPassport) {
        this.userPassport = userPassport;
        this.userRole = employeeService
                .findEmployeeByPassportNumber(userPassport)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с данным номером паспорта не найден"))
                .getRole();

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

        if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
            productTypeField.getItems().setAll(ProductType.values());
            productNameField.setTextFormatter(createAlphaFilter());
            projectIdField.setTextFormatter(createNumericFilter());
            quantityField.setTextFormatter(createNumericFilter());
            weightField.setTextFormatter(createNumeric10Filter());
            costField.setTextFormatter(createNumeric10Filter());
        }

        setupFilters();
        loadProducts();
    }

    @FXML
    private void handleUpdateProducts() { loadProducts(); }

    @FXML
    private void handleCleanButton(){
        if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
            projectIdField.setText("");
            productNameField.setText("");
            deleteIdField.setText("");
            productTypeField.getSelectionModel().clearSelection();
            quantityField.setText("");
            weightField.setText("");
            costField.setText("");
        }
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
            logger.log(Level.SEVERE,"Ошибка при загрузке продуктов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProduct(ActionEvent event) {
        try {
            if (userRole == org.example.demotest.entities.Role.ADMIN) {
                Long projectIdValue = Long.valueOf(projectIdField.getText());
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
                        .weight(valueOf(weightField.getText()))
                        .cost(Integer.valueOf(costField.getText()))
                        .build();
                System.out.println("Отправляемый продукт: " + newProduct);
                Product productCreated = restTemplate.postForObject(url, newProduct, Product.class);
                if (productCreated != null) {
                    observableProductsList.add(productCreated); // Используйте observableUserList
                    productTable.setItems(observableProductsList);
                    System.out.println("Продукт добавлен: " + productCreated);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при добавлении продукта: " + e.getMessage());
        }
    }

    private Product selectedProduct;

    @FXML
    private void handleTableClick(MouseEvent event) {
        selectedProduct = productTable.getSelectionModel().getSelectedItem();
        try {
            if (userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
                if (selectedProduct != null) {
                    projectIdField.setText(String.valueOf(selectedProduct.getProjectId()));
                    productNameField.setText(String.valueOf(selectedProduct.getProductName()));
                    quantityField.setText(String.valueOf(selectedProduct.getQuantity()));
                    weightField.setText(String.valueOf(selectedProduct.getWeight()));
                    costField.setText(String.valueOf(selectedProduct.getCost()));
                    productTypeField.setValue(selectedProduct.getProductType());
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, стоимость, масса или количество): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных продукта: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditProduct(ActionEvent event) {
        if (selectedProduct == null) {
            System.out.println("Не выбран сотрудник для изменения.");
            return;
        }

        try {
            if(userRole == org.example.demotest.entities.Role.ADMIN || userRole == org.example.demotest.entities.Role.MODERATOR) {
                ServiceRequestProduct updatedProduct = new ServiceRequestProduct();
                if (!productNameField.getText().isEmpty()) {
                    updatedProduct.setProductName(productNameField.getText());
                }
                if (!quantityField.getText().isEmpty()) {
                    updatedProduct.setQuantity(Integer.valueOf(quantityField.getText()));
                }
                if (!weightField.getText().isEmpty()) {
                    updatedProduct.setWeight(valueOf(weightField.getText()));
                }
                if (!costField.getText().isEmpty()) {
                    updatedProduct.setCost(Integer.valueOf(costField.getText()));
                }
                if (productTypeField.getValue() != null) {
                    updatedProduct.setProductType(productTypeField.getValue());
                }

                Optional<Product> updated = productService.updatedProduct(selectedProduct.getProductId(), updatedProduct);

                if (updated.isPresent()) {
                    System.out.println("Продукт успешно обновлен: " + updated.get().getProductName());
                } else {
                    System.out.println("Продукт с ID " + selectedProduct.getProductId() + " не найден.");
                }

                loadProducts();
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ошибка в числовом формате (например, стоимость, масса или количество): " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Ошибка при изменении данных продукта: " + e.getMessage());
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

            return matchesId && matchesProductName && matchesProductTypeFilterValue;
        }));
    }

    @FXML
    private void handleDeleteProduct() {
        if(userRole == org.example.demotest.entities.Role.ADMIN) {
            Long idText = Long.valueOf(deleteIdField.getText());

            Product product = productService.findProductById(idText);
            if (product != null) {
                productService.deleteProduct(product.getProductId());
                loadProducts();
            } else {
                System.out.println("Продукт с указанным ID не найден");
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
