package com.test.apex.database;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.test.apex.SharedPrefManager;
import com.test.apex.ui.Products.Product;
import com.test.apex.network.ServerAPI;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDatabase {

    public final StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Images");
    public StorageReference sRef;

    public ProductDatabase() {
        //This Constructor Required
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createProduct(Context mcontext, Product newRecyclerData) {
        sRef = mStorage.child(replaceImageName(newRecyclerData.getProductName()));

        UploadTask uploadTask = sRef.putFile(Uri.parse(newRecyclerData.getproductImage()));
        uploadTask.addOnFailureListener(exception -> {
        }).addOnSuccessListener(taskSnapshot -> sRef.getDownloadUrl().addOnSuccessListener(uri -> {
            newRecyclerData.setproductImage(uri.toString());

            RequestQueue queue = Volley.newRequestQueue(mcontext);
            StringRequest postRequest = new StringRequest(Request.Method.POST, ServerAPI.URL_CREATE_PRODUCT,
                    response -> Log.d("Response", response),
                    error -> Log.d("Error.Response", error.getMessage())
            ) {
                @Override
                protected Map<String, String> getParams() {
                    return newRecyclerData.toMap();
                }
            };
            queue.add(postRequest);

        }).addOnFailureListener(e -> Toast.makeText(mcontext, e.getMessage(), Toast.LENGTH_SHORT).show()));

    }

    public void readProduct(Context mcontext) {
        RequestQueue queue = Volley.newRequestQueue(mcontext);
        StringRequest getRequest = new StringRequest(Request.Method.GET, ServerAPI.URL_READ_PRODUCT,
                response -> {

                    Type type = new TypeToken<List<Product>>(){}.getType();
                    List<Product> productList = new Gson().fromJson(response, type);
                    SharedPrefManager.getInstance(mcontext).saveProductList(new ArrayList<>(productList));

                }, error -> Log.d("Error.Response", error.getMessage())
        );
        queue.add(getRequest);
    }

    String replaceImageName(String name) {
        return name.replaceAll("\\s", "_").toLowerCase();
    }
}
