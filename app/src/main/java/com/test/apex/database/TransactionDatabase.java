package com.test.apex.database;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.test.apex.SharedPrefManager;
import com.test.apex.Transaction;
import com.test.apex.VolleyMultipartRequest;
import com.test.apex.VolleyOnEventListener;
import com.test.apex.VolleySingleton;
import com.test.apex.User;
import com.test.apex.network.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TransactionDatabase {

    Context context;
    Transaction newTransaction;
    private VolleyOnEventListener<String> mCallBack;
    String keyImage, urlProduct;
    User user;
    String userID;

    public TransactionDatabase(Context context, String urlTransaction,  Transaction newTransaction, VolleyOnEventListener callback) {
        this.context = context;
        this.newTransaction = newTransaction;
        urlProduct = urlTransaction;
        mCallBack = callback;

        setTransaction();
    }

    public TransactionDatabase(Context context, VolleyOnEventListener callback) {
        this.context = context;
        urlProduct = ServerAPI.URL_READ_TRANSACTION;
        mCallBack = callback;

        setTransaction();
    }

    public void setTransaction() {

        user = SharedPrefManager.getInstance(context).getUser();
        userID = String.valueOf(user.getId());

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, urlProduct, response -> {
            String resultResponse = new String(response.data);
            Log.d("Response", resultResponse);
            mCallBack.onSuccess(resultResponse);
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
            mCallBack.onFailure(errorMessage);
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (newTransaction == null) {
                    HashMap<String, String> result = new HashMap<>();
                    result.put("userId", userID);
                    return result;
                }
                return newTransaction.toMap();
            }

        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5600,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
    }
}
