package com.test.apex;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cart extends ArrayList<Cart> {
    private String productId;
    private long productQuantity;

    public Cart() {}

    public String getproductId() {return productId;}
    public void setproductId(String productId) {this.productId = productId;}

    public long getproductQuantity() {return productQuantity;}
    public void setproductQuantity(long productQuantity) {this.productQuantity = productQuantity;}

    public Cart(String productId, long productQuantity) {
        this.productId = productId;
        this.productQuantity = productQuantity;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", productId);
        result.put("quantity", productQuantity);
        return result;
    }
}
