package com.test.apex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.apex.ui.Products.Product;
import com.test.apex.network.ServerAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
        new StatusBar().statusBar(this);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        readProduct();

        new Handler().postDelayed(() -> {
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }, 3000);
    }

    private void readProduct() {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.logoapex);
        String N = "NULL";
        Product newProduct = new Product(N, N, N, N, 0L, N, N, N, N, 0L, N);

        new VolleyServerRequest(getApplicationContext(), ServerAPI.URL_READ_PRODUCT, newProduct, bitmap, new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                Log.d("VolleyReq", "onSuccess: " + res);

                try {
                    JsonNode node = mapper.readTree(res);
                    JsonNode products = node.get("products");

                    Type type = new TypeToken<List<Product>>() {}.getType();
                    List<Product> productList = new Gson().fromJson(products.toString(), type);
                    SharedPrefManager.getInstance(getBaseContext()).saveProductList(new ArrayList<>(productList));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(R.id.root), error, 4800).
                        setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                        setTextColor(getResources().getColor(R.color.whiteAccent)).show();
            }
        });
    }

}