package com.test.apex;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.apex.network.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyServerRequest {
    Context context;
    Product newProduct;
    Bitmap bitmap;
    private VolleyOnEventListener<String> mCallBack;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String keyImage, urlProduct;
    private int reqMethod;

    public VolleyServerRequest(Context context, String urlProduct, Product newProduct, Bitmap bitmap, VolleyOnEventListener callback) {
        this.context = context;
        this.urlProduct = urlProduct;
        this.newProduct = newProduct;
        this.bitmap = bitmap;
        mCallBack = callback;
        keyImage = database.getReference().push().getKey();
        setRequest();
    }

    public void setRequest() {
        switch (urlProduct) {
            case ServerAPI.URL_CREATE_PRODUCT:
            case ServerAPI.URL_UPDATE_PRODUCT:
            case ServerAPI.URL_DELETE_PRODUCT:
                reqMethod = Request.Method.POST;
                break;
            case ServerAPI.URL_READ_PRODUCT:
                reqMethod = Request.Method.GET;
                break;
        }

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(reqMethod, urlProduct, response -> {
            String resultResponse = new String(response.data);
            Log.d("Response", resultResponse);
            try {
                mCallBack.onSuccess(resultResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }, error -> {
            NetworkResponse networkResponse = error.networkResponse;
            String errorMessage = "Unknown error";
            if (networkResponse == null) {
                if (error.getClass().equals(TimeoutError.class)) {
                    errorMessage = "Request timeout";
                } else if (error.getClass().equals(NoConnectionError.class)) {
                    errorMessage = "Failed to connect server";
                }
            } else {
                String result = new String(networkResponse.data);
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getString("status");
                    String message = response.getString("message");

                    Log.e("Error Status", status);
                    Log.e("Error Message", message);

                    if (networkResponse.statusCode == 404) {
                        errorMessage = "Resource not found";
                    } else if (networkResponse.statusCode == 401) {
                        errorMessage = message + " Please login again";
                    } else if (networkResponse.statusCode == 400) {
                        errorMessage = message + " Check your inputs";
                    } else if (networkResponse.statusCode == 500) {
                        errorMessage = message + " Something is getting wrong";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("Error", errorMessage);
            try {
                mCallBack.onSuccess(errorMessage);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                return newProduct.toMap();
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("productImage", new DataPart(keyImage + replaceImageName(newProduct.getProductName()) + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5600,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);


        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, "url", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type type = new TypeToken<List<Product>>() {}.getType();
                List<Product> productList = new Gson().fromJson(response, type);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG,"Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    String replaceImageName(String name) {
        return "_" + name.replaceAll("\\s", "_").toLowerCase();
    }

}
