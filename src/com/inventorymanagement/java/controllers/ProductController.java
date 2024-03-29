
package com.inventorymanagement.java.controllers;

import com.inventorymanagement.java.dao.Main_DAO;
import com.inventorymanagement.java.dao.components.CategoriesDB;
import com.inventorymanagement.java.dao.components.HistoryDB;
import com.inventorymanagement.java.dao.components.ProductsDB;
import com.inventorymanagement.java.models.Category;
import com.inventorymanagement.java.models.History;
import com.inventorymanagement.java.models.Product;
import com.inventorymanagement.java.models.User;
import com.inventorymanagement.java.utils.Alerts;
import com.inventorymanagement.java.utils.LayoutsActions;
import com.inventorymanagement.java.utils.observeUserData.Observer;
import com.inventorymanagement.java.utils.observeUserData.UserData;
import com.inventorymanagement.java.utils.validators.EmailValidation;
import com.inventorymanagement.java.utils.validators.FacadeValidator;
import com.inventorymanagement.java.utils.validators.UserNameValidation;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProductController extends LayoutsActions implements Initializable, Observer {
    User userData = UserData.getState();
    ProductsDB productsDB = Main_DAO.getInstance().getProducts();
    CategoriesDB categoriesDB = Main_DAO.getInstance().getCategories();
    HistoryDB historyDB =  Main_DAO.getInstance().getHistory();
    List<Product> getProductsList = null;
    ObservableList<RecursiveProduct> productList = null;
    FacadeValidator validator;
    @FXML
    private MenuItem menuEditBtn;
    @FXML
    private MenuItem menuDeleteBtn;
    @FXML
    private JFXButton addBtn;

    @FXML
    private JFXTextField searchField;
    @FXML
    private TreeTableColumn<RecursiveProduct, String> col_description;
    @FXML
    private TreeTableColumn<RecursiveProduct, String> col_quantity;

    @FXML
    private StackPane primaryPane;
    @FXML
    private TreeTableColumn<RecursiveProduct, String> col_category;
    @FXML
    private TreeTableColumn<RecursiveProduct, String> col_price;
    @FXML
    private TreeTableColumn<RecursiveProduct, String> col_name;
    @FXML
    private TreeTableColumn<RecursiveProduct, String> col_id;
    @FXML
    private JFXTreeTableView<RecursiveProduct> tableView;

    private void setButtonsVisibility(){
        categoryButton.setVisible(userData != null ? userData.getRole().equals("admin") : false);
        usersButton.setVisible(userData != null ? userData.getRole().equals("admin"): false);
        historyButton.setVisible(userData != null ? userData.getRole().equals("admin"): false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserData.addObserver(this);
        setButtonsVisibility();

        productList = FXCollections.observableArrayList();
        getProductsList = productsDB.getAll();

        validator = new FacadeValidator(new EmailValidation(), new UserNameValidation());


        // setting btn events
        btnEvents();
        // setting table values
        setTable();
        // setting stage draggable
        setStageDraggable();
    }

    @Override
    public void update(){
        this.userData = UserData.getState();
    }

    public void refreshAction() {
        // event for refresh
        productList.removeAll(productList);
        getProductsList = productsDB.getAll();

        getProductsList.forEach(product -> {
            productList.add(new RecursiveProduct(String.valueOf(product.getId()), product.getProductName(),
                    String.valueOf(product.getPrice()), product.getProductDescription(),
                    String.valueOf(product.getNoInStock()), product.getProductCategory()));
        });
    }

    // setting btn events
    private void btnEvents() {
        // event for add btn
        addBtn.setOnMouseClicked(event -> {

            BoxBlur blur = new BoxBlur(3.0, 3.0, 3);
            mainPane.setEffect(blur);

            JFXDialogLayout content = new JFXDialogLayout();
            JFXDialog dialog = new JFXDialog(primaryPane, content, JFXDialog.DialogTransition.TOP);
            content.setAlignment(Pos.CENTER);
            content.setHeading(new Text("Add Product"));
            dialog.setOverlayClose(false);
            VBox box = new VBox();
            box.setSpacing(15);
            box.setAlignment(Pos.CENTER);

            JFXTextField productNameField = new JFXTextField();
            productNameField.setPromptText("Product Name");
            productNameField.setLabelFloat(true);

            JFXTextField productDescriptionField = new JFXTextField();
            productDescriptionField.setPromptText("Product Description");
            productDescriptionField.setLabelFloat(true);

            JFXTextField productPriceField = new JFXTextField();
            productPriceField.setPromptText("Product Price");
            productPriceField.setLabelFloat(true);

            JFXTextField numberInStockField = new JFXTextField();
            numberInStockField.setPromptText("Number In Stock");
            numberInStockField.setLabelFloat(true);

            JFXComboBox<String> categoryComboList = new JFXComboBox<>();
            categoryComboList.setPromptText("Category");
            categoryComboList.setPrefWidth(400.0);
            categoryComboList.setLabelFloat(true);

            ObservableList<Category> departmentsList = FXCollections.observableArrayList(categoriesDB.getAll());
            departmentsList.forEach(category -> {
                categoryComboList.getItems().add(category.getCategoryName());
            });

            box.getChildren().addAll(productNameField, productDescriptionField, productPriceField, numberInStockField, categoryComboList);
            box.setSpacing(30.0);
            content.setBody(box);

            JFXButton saveBtn = new JFXButton("Save");
            JFXButton cancelBtn = new JFXButton("Cancel");

            saveBtn.getStyleClass().add("dial-btn");
            cancelBtn.getStyleClass().add("dial-btn");


            saveBtn.setOnAction(event1 -> {
                if (productNameField.getText().isEmpty() || productNameField.getText().trim().isEmpty()) {
                    Alerts.jfxAlert("Error", "Product Name Field cannot be empty");
                    return;
                }

                if (productDescriptionField.getText().isEmpty() || productDescriptionField.getText().trim().isEmpty()) {
                    Alerts.jfxAlert("Error", "Description Field cannot be empty");
                    return;
                }

                if (productPriceField.getText().isEmpty() || productPriceField.getText().trim().isEmpty()) {
                    Alerts.jfxAlert("Error", "Product Price Field cannot be empty");
                    return;
                }

                if (numberInStockField.getText().isEmpty() || numberInStockField.getText().trim().isEmpty()) {
                    Alerts.jfxAlert("Error", "Number In Stock Field cannot be empty");
                    return;
                }

                if (!validator.isNumber(numberInStockField.getText())) {
                    Alerts.jfxAlert("Error", "Number In Field Must Be a Number");
                    return;
                }

                if (!validator.isDouble(productPriceField.getText())) {
                    Alerts.jfxAlert("Error", "Product Price Field Must Be a Number");
                    return;
                }

                if (categoryComboList.getSelectionModel().isEmpty()) {
                    Alerts.jfxAlert("Error", "Category must be selected");
                    return;
                }

                History history = new History(
                        0,
                        Double.parseDouble(productPriceField.getText()),
                        productNameField.getText(),
                        categoryComboList.getSelectionModel().getSelectedItem(),
                        productDescriptionField.getText(),
                        "added", LocalDateTime.now().toString()
                );

                if (historyDB.create(history) != 1) {
                    Alerts.jfxAlert("Error", "An error occurred");
                    return;
                }

                Product product = new Product(
                        0,
                        Integer.parseInt(numberInStockField.getText()),
                        Double.parseDouble(productPriceField.getText()),
                        productNameField.getText(),
                        productDescriptionField.getText(),
                        categoryComboList.getSelectionModel().getSelectedItem()
                );

                if (productsDB.create(product) != 1) {
                    Alerts.jfxAlert("Error", "An error occurred");
                    return;
                }



                refreshAction();
                dialog.close();
            });

            cancelBtn.setOnAction(event1 -> {
                dialog.close();
            });

            content.setActions(saveBtn, cancelBtn);

            dialog.setOnDialogClosed(event1 -> {
                mainPane.setEffect(null);
            });
            dialog.show();
        });

        // editing for edit btn
        menuEditBtn.setOnAction(event -> {
            if (tableView.getSelectionModel().getFocusedIndex() == -1) {
                Alerts.jfxAlert("Error", "No item selected");
                return;
            }

            List<RecursiveProduct> selectedProduct = new ArrayList<>();
            selectedProduct.add(tableView.getSelectionModel().getSelectedItem().getValue());

            BoxBlur blur = new BoxBlur(3.0, 3.0, 3);
            mainPane.setEffect(blur);

            JFXDialogLayout content = new JFXDialogLayout();
            JFXDialog dialog = new JFXDialog(primaryPane, content, JFXDialog.DialogTransition.TOP);
            content.setAlignment(Pos.CENTER);
            content.setHeading(new Text("Edit Product"));
            dialog.setOverlayClose(false);
            VBox box = new VBox();
            box.setSpacing(15);
            box.setAlignment(Pos.CENTER);

            JFXTextField productIdField = new JFXTextField();
            productIdField.setPromptText("Product Id");
            productIdField.setLabelFloat(true);
            productIdField.setDisable(true);

            JFXTextField productNameField = new JFXTextField();
            productNameField.setPromptText("Product Name");
            productNameField.setLabelFloat(true);

            JFXTextField productDescriptionField = new JFXTextField();
            productDescriptionField.setPromptText("Product Description");
            productDescriptionField.setLabelFloat(true);

            JFXTextField productPriceField = new JFXTextField();
            productPriceField.setPromptText("Product Price");
            productPriceField.setLabelFloat(true);

            JFXTextField numberInStockField = new JFXTextField();
            numberInStockField.setPromptText("Number In Stock");
            numberInStockField.setLabelFloat(true);

            JFXComboBox<String> categoryComboList = new JFXComboBox<>();
            categoryComboList.setPromptText("Category");
            categoryComboList.setPrefWidth(400.0);
            categoryComboList.setLabelFloat(true);

            selectedProduct.forEach(recursiveProduct -> {
                productIdField.setText(recursiveProduct.getId());
                productNameField.setText(recursiveProduct.getProductName());
                productDescriptionField.setText(recursiveProduct.getProductDescription());
                productPriceField.setText(recursiveProduct.getProductPrice());
                numberInStockField.setText(recursiveProduct.getNoInStock());
                categoryComboList.getItems().add(recursiveProduct.getProductCategory());
                categoryComboList.getSelectionModel().select(0);
            });

            ObservableList<Category> departmentsList = FXCollections.observableArrayList(categoriesDB.getAll());
            departmentsList.forEach(category -> {
                categoryComboList.getItems().add(category.getCategoryName());
            });

            box.getChildren().addAll(productIdField, productNameField, productDescriptionField, productPriceField, numberInStockField, categoryComboList);
            box.setSpacing(30.0);
            content.setBody(box);

            JFXButton saveBtn = new JFXButton("Save");
            JFXButton cancelBtn = new JFXButton("Cancel");

            saveBtn.getStyleClass().add("dial-btn");
            cancelBtn.getStyleClass().add("dial-btn");


            saveBtn.setOnAction(event1 -> {
                if (productNameField.getText().isEmpty() || productNameField.getText().trim().isEmpty()) {
                    Alerts.jfxAlert("Error", "Product Name Field cannot be empty");
                    return;
                }

                if (productDescriptionField.getText().isEmpty() || productDescriptionField.getText().trim().isEmpty()) {
                    Alerts.jfxAlert("Error", "Description Field cannot be empty");
                    return;
                }

                if (productPriceField.getText().isEmpty() || productPriceField.getText().trim().isEmpty()) {
                    Alerts.jfxAlert("Error", "Product Price Field cannot be empty");
                    return;
                }

                if (numberInStockField.getText().isEmpty() || numberInStockField.getText().trim().isEmpty()) {
                    Alerts.jfxAlert("Error", "Number In Stock Field cannot be empty");
                    return;
                }

                if (!validator.isNumber(numberInStockField.getText())) {
                    Alerts.jfxAlert("Error", "Number In Field Must Be a Number");
                    return;
                }

                if (!validator.isDouble(productPriceField.getText())) {
                    Alerts.jfxAlert("Error", "Product Price Field Must Be a Number");
                    return;
                }

                if (categoryComboList.getSelectionModel().isEmpty()) {
                    Alerts.jfxAlert("Error", "Category must be selected");
                    return;
                }

                History history = new History(
                        0,
                        Double.parseDouble(tableView.getSelectionModel().getSelectedItem().getValue().getProductPrice()),
                        tableView.getSelectionModel().getSelectedItem().getValue().getProductName(),
                        tableView.getSelectionModel().getSelectedItem().getValue().getProductCategory(),
                        tableView.getSelectionModel().getSelectedItem().getValue().getProductDescription(),
                        "edited", LocalDateTime.now().toString()
                );

                if (historyDB.create(history) != 1) {
                    Alerts.jfxAlert("Error", "An error occurred");
                    return;
                }

                Product product = new Product(0, Integer.parseInt(numberInStockField.getText()),
                        Double.parseDouble(productPriceField.getText()),
                        productNameField.getText(), productDescriptionField.getText(),
                        categoryComboList.getSelectionModel().getSelectedItem());

                if (productsDB.edit(Integer.parseInt(productIdField.getText()), productNameField.getText(),
                        productDescriptionField.getText(), Double.parseDouble(productPriceField.getText()),
                        Integer.parseInt(numberInStockField.getText()),
                        categoryComboList.getSelectionModel().getSelectedItem()) != 1) {
                    Alerts.jfxAlert("Error", "An error occurred");
                    return;
                }
                refreshAction();
                dialog.close();
            });

            cancelBtn.setOnAction(event1 -> {
                dialog.close();
            });

            content.setActions(saveBtn, cancelBtn);

            dialog.setOnDialogClosed(event1 -> {
                mainPane.setEffect(null);
            });
            dialog.show();
        });

        // deleting event
        menuDeleteBtn.setOnAction(event -> {
            if (tableView.getSelectionModel().getFocusedIndex() == -1) {
                Alerts.jfxAlert("Error", "No item selected");
                return;
            }

            int productId = Integer.parseInt(tableView.getSelectionModel().getSelectedItem().getValue().getId());
            BoxBlur blur = new BoxBlur(3.0, 3.0, 3);
            mainPane.setEffect(blur);

            JFXDialogLayout content = new JFXDialogLayout();
            JFXDialog dialog = new JFXDialog(primaryPane, content, JFXDialog.DialogTransition.TOP);
            content.setAlignment(Pos.CENTER);
            content.setHeading(new Text("Delete Product"));
            dialog.setOverlayClose(false);
            VBox box = new VBox();
            box.setSpacing(15);
            box.setAlignment(Pos.CENTER);

            Label text = new Label("Are you sure you want to delete this item ?");

            box.getChildren().addAll(text);
            box.setSpacing(30.0);
            content.setBody(box);

            JFXButton saveBtn = new JFXButton("Ok");
            JFXButton cancelBtn = new JFXButton("Cancel");

            saveBtn.getStyleClass().add("dial-btn");
            cancelBtn.getStyleClass().add("dial-btn");
            saveBtn.setOnAction(event1 -> {

                History history = new History(
                        0,
                        Double.parseDouble(tableView.getSelectionModel().getSelectedItem().getValue().getProductPrice()),
                        tableView.getSelectionModel().getSelectedItem().getValue().getProductName(),
                        tableView.getSelectionModel().getSelectedItem().getValue().getProductCategory(),
                        tableView.getSelectionModel().getSelectedItem().getValue().getProductDescription(),
                        "deleted", LocalDateTime.now().toString()
                );

                if (historyDB.create(history) != 1) {
                    Alerts.jfxAlert("Error", "An error occurred");
                    return;
                }

                if (productsDB.delete(productId) != 1) {
                    Alerts.jfxAlert("Error", "An error occurred");
                    return;
                }
                refreshAction();
                dialog.close();
            });

            cancelBtn.setOnAction(event1 -> {
                dialog.close();
            });

            content.setActions(saveBtn, cancelBtn);

            dialog.setOnDialogClosed(event1 -> {
                mainPane.setEffect(null);
            });
            dialog.show();
        });

    }

    // setting table values
    private void setTable() {
        col_id.setCellValueFactory(param -> param.getValue().getValue().id);
        col_name.setCellValueFactory(param -> param.getValue().getValue().productName);
        col_description.setCellValueFactory(param -> param.getValue().getValue().productDescription);
        col_price.setCellValueFactory(param -> param.getValue().getValue().productPrice);
        col_quantity.setCellValueFactory(param -> param.getValue().getValue().noInStock);
        col_category.setCellValueFactory(param -> param.getValue().getValue().productCategory);

        getProductsList.forEach(product -> {
            productList.add(new RecursiveProduct(String.valueOf(product.getId()), product.getProductName(),
                    String.valueOf(product.getPrice()), product.getProductDescription(),
                    String.valueOf(product.getNoInStock()), product.getProductCategory()));
        });

        TreeItem<RecursiveProduct> root = new RecursiveTreeItem<>(productList, RecursiveTreeObject::getChildren);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setRoot(root);
        tableView.setShowRoot(false);

        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                tableView.setPredicate(modelTreeItem ->
                        modelTreeItem.getValue().id.getValue().toLowerCase().contains(newValue)
                                | modelTreeItem.getValue().productName.getValue().toLowerCase().contains(newValue)));
    }


    static class RecursiveProduct extends RecursiveTreeObject<RecursiveProduct> {
        private StringProperty id, productName, productPrice, productDescription, noInStock, productCategory;

        public RecursiveProduct(String id, String productName, String productPrice, String productDescription, String noInStock, String productCategory) {
            this.id = new SimpleStringProperty(id);
            this.productName = new SimpleStringProperty(productName);
            this.productPrice = new SimpleStringProperty(productPrice);
            this.productDescription = new SimpleStringProperty(productDescription);
            this.noInStock = new SimpleStringProperty(noInStock);
            this.productCategory = new SimpleStringProperty(productCategory);
        }

        public String getId() {
            return id.get();
        }

        public void setId(String id) {
            this.id.set(id);
        }

        public StringProperty idProperty() {
            return id;
        }

        public String getProductName() {
            return productName.get();
        }

        public void setProductName(String productName) {
            this.productName.set(productName);
        }

        public StringProperty productNameProperty() {
            return productName;
        }

        public String getProductPrice() {
            return productPrice.get();
        }

        public void setProductPrice(String productPrice) {
            this.productPrice.set(productPrice);
        }

        public StringProperty productPriceProperty() {
            return productPrice;
        }

        public String getProductDescription() {
            return productDescription.get();
        }

        public void setProductDescription(String productDescription) {
            this.productDescription.set(productDescription);
        }

        public StringProperty productDescriptionProperty() {
            return productDescription;
        }

        public String getNoInStock() {
            return noInStock.get();
        }

        public void setNoInStock(String noInStock) {
            this.noInStock.set(noInStock);
        }

        public StringProperty noInStockProperty() {
            return noInStock;
        }

        public String getProductCategory() {
            return productCategory.get();
        }

        public void setProductCategory(String productCategory) {
            this.productCategory.set(productCategory);
        }

        public StringProperty productCategoryProperty() {
            return productCategory;
        }
    }
}