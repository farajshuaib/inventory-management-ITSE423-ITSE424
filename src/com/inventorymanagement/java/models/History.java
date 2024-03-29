
package com.inventorymanagement.java.models;

public class History {
    public final static String RECORD_ID = "id";
    public final static String RECORD_PRODUCT_NAME = "product_name";
    public final static String RECORD_PRICE = "product_price";
    public final static String RECORD_CATEGORY = "product_category";
    public final static String RECORD_DESCRIPTION = "product_description";
    public final static String RECORD_ACTION = "action";
    public final static String RECORD_DATE = "date";
    private int id;
    private double productPrice;
    private String productName, productCategory, description, action, date;

    public History() {
    }

    public History(
            int id, double productPrice,
            String productName, String productCategory,
            String description, String action, String date
    ) {
        this.id = id;
        this.productPrice = productPrice;
        this.productName = productName;
        this.productCategory = productCategory;
        this.description = description;
        this.action = action;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
