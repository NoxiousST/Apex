package com.test.apex.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.test.apex.R;
import com.test.apex.SharedPrefManager;
import com.test.apex.Transaction;
import com.test.apex.User;
import com.test.apex.VolleyMultipartRequest;
import com.test.apex.VolleyOnEventListener;
import com.test.apex.VolleySingleton;
import com.test.apex.network.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {

    Context context;
    User newUser = new User();
    private VolleyOnEventListener<String> mCallBack;
    String urlUser;
    String userID;
    Bitmap bitmap;

    public UserDatabase(Context context, String urlUser, VolleyOnEventListener callback) {
        this.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logoapex);
        this.context = context;
        this.urlUser = urlUser;
        User user = SharedPrefManager.getInstance(context).getUser();
        userID = user.getId();
        mCallBack = callback;
        setUser();
    }

    public UserDatabase(Context context, String urlUser, User newUser, Bitmap bitmap, VolleyOnEventListener callback) {
        this.context = context;
        this.newUser = newUser;
        this.bitmap = bitmap;
        this.urlUser = urlUser;
        mCallBack = callback;
        setUser();
    }

    public UserDatabase(Context context, VolleyOnEventListener callback) {
        this.context = context;
        urlUser = ServerAPI.URL_READ_TRANSACTION;
        mCallBack = callback;

        setUser();
    }

    public void setUser() {

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, urlUser, response -> {
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
                if (newUser == null) {
                    HashMap<String, String> result = new HashMap<>();
                    result.put("userId", userID);
                    return result;
                }
                return newUser.toMap();
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                Date currentDate = new Date();
                long milliSeconds = currentDate.getTime();
                params.put("profilePicture", new DataPart(milliSeconds + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }

        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5600,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
