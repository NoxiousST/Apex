package com.test.apex.ui.Products;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Product implements Serializable {

    private String productId, productName, productType, productImage, productCondition, productManufacter, productWeight, productColor, productDescription;
    private Long productPrice, productStock;

    Product() {

    }

    public String getproductId() {return productId;}
    public void setproductId(String productId) {this.productId = productId;}

    public String getProductName() {return productName;}
    public void setProductName(String productName) {this.productName = productName;}

    public String getproductType() {return productType;}
    public void setproductType(String productType) {this.productType = productType;}

    public String getproductImage() {return productImage;}
    public void setproductImage(String productImage) {this.productImage = productImage;}

    public Long getproductPrice() {return productPrice;}
    public void setproductPrice(Long productPrice) {this.productPrice = productPrice;}

    public String getproductCondition() {return productCondition;}
    public void setproductCondition(String productCondition) {this.productCondition = productCondition;}

    public String getproductManufacter() {return productManufacter;}
    public void setproductManufacter(String productManufacter) {this.productManufacter = productManufacter;}

    public String getproductWeight() {return productWeight;}
    public void setproductWeight(String productWeight) {this.productWeight = productWeight;}

    public String getproductColor() {return productColor;}
    @Exclude
    public void setproductColor(String productColor) {this.productColor = productColor;}

    public Long getproductStock() {return productStock;}
    public void setproductStock(Long productStock) {this.productStock = productStock;}

    public String getproductDescription() {return productDescription;}
    public void setproductDescription(String productDescription) {this.productDescription = productDescription;}

    public Product(String productId, String productName, String productType, String productImage, Long productPrice, String productCondition, String productManufacter, String productWeight, String productColor, Long productStock, String productDescription) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.productImage= productImage;
        this.productPrice = productPrice;
        this.productCondition = productCondition;
        this.productManufacter = productManufacter;
        this.productWeight = productWeight;
        this.productColor = productColor;
        this.productStock = productStock;
        this.productDescription = productDescription;
    }

    @Exclude
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("productId", productId);
        result.put("productName", productName);
        result.put("productType", productType);
        result.put("productPrice", String.valueOf(productPrice));
        result.put("productCondition", productCondition);
        result.put("productManufacter", productManufacter);
        result.put("productWeight", productWeight);
        result.put("productColor", productColor);
        result.put("productStock", String.valueOf(productStock));
        result.put("productDescription", productDescription);
        return result;
    }
}