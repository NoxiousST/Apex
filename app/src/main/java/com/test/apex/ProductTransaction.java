package com.test.apex;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProductTransaction implements Serializable {

    private String productId, productName;
    private Long productPrice, productQuantity;

    ProductTransaction() {

    }

    public String getproductId() {return productId;}
    public void setproductId(String productId) {this.productId = productId;}

    public String getProductName() {return productName;}
    public void setProductName(String productName) {this.productName = productName;}

    public Long getproductPrice() {return productPrice;}
    public void setproductPrice(Long productPrice) {this.productPrice = productPrice;}

    public Long getproductQuantity() {return productQuantity;}
    public void setproductQuantity(Long productQuantity) {this.productQuantity = productQuantity;}


    public ProductTransaction(String productId, String productName, Long productPrice, Long productQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    @Exclude
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("productId", productId);
        result.put("productName", productName);
        result.put("productPrice", String.valueOf(productPrice));
        result.put("productQuantity", String.valueOf(productQuantity));
        return result;
    }
}