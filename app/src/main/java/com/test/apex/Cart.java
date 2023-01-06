package com.test.apex;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Parcelable {
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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", productId);
        result.put("quantity", productQuantity);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeLong(this.productQuantity);
    }

    public void readFromParcel(Parcel source) {
        this.productId = source.readString();
        this.productQuantity = source.readLong();
    }

    protected Cart(Parcel in) {
        this.productId = in.readString();
        this.productQuantity = in.readLong();
    }

    public static final Parcelable.Creator<Cart> CREATOR = new Parcelable.Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel source) {
            return new Cart(source);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };
}
