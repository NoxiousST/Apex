package com.test.apex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.test.apex.network.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextUsername, editTextEmail, editTextPassword, editTextRePassword;
    TextInputLayout textLayoutUsername, textLayoutEmail, textLayoutPassword, textLayoutRePassword;
    JSONObject obj;
    ObjectMapper mapper = new ObjectMapper();
    private LinearProgressIndicator progressBar;
    private String username, email, password, repassword;
    private boolean cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();
        new StatusBar().statusBar(this);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        progressBar = findViewById(R.id.progress);
        progressBar.hide();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);

        textLayoutUsername = findViewById(R.id.textLayoutUsername);
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);
        textLayoutRePassword = findViewById(R.id.textLayoutRePassword);

        findViewById(R.id.btnRegister).setOnClickListener(view -> {
            checkInput();
            if (!cancel) registerUser();
        });
    }

    private void checkInput() {
        username = Objects.requireNonNull(editTextUsername.getText()).toString();
        email = Objects.requireNonNull(editTextEmail.getText()).toString();
        password = Objects.requireNonNull(editTextPassword.getText()).toString();
        repassword = Objects.requireNonNull(editTextRePassword.getText()).toString();

        if (TextUtils.isEmpty(username)) {
            textLayoutUsername.setErrorEnabled(true);
            textLayoutUsername.setError("*Username cannot be null or empty");
            textLayoutUsername.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            textLayoutUsername.setErrorEnabled(true);
            textLayoutEmail.setError("*Email cannot be null or empty");
            textLayoutEmail.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            textLayoutUsername.setErrorEnabled(true);
            textLayoutPassword.setError("*Password cannot be null or empty");
            textLayoutPassword.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(repassword)) {
            textLayoutUsername.setErrorEnabled(true);
            textLayoutRePassword.setError("*Password does not match");
            textLayoutRePassword.requestFocus();
            cancel = true;
        }
    }

    private void registerUser() {


        class RegisterUser extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);

                return requestHandler.sendPostRequest(ServerAPI.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.hide();
                Log.d("WHAT", "1");
                try {
                    obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        JSONObject userJson = obj.getJSONObject("user");
                        //User user = new User(
                                //userJson.getString("id"),
                                //userJson.getString("username"),
                                //userJson.getString("email"),
                                //userJson.getString("password"),
                                //"MySQL"
                        //);
                        //SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        JsonNode node = mapper.readTree(s);
                        User user = mapper.treeToValue(node.get("user"), User.class);
                        user.setLoginOption("MySQL");
                        SharedPrefManager.getInstance(RegisterActivity.this).userLogin(user);
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finish();

                    } else {
                        if (obj.getInt("errorCode") == 1) {
                            textLayoutUsername.setError(obj.getString("message"));
                            textLayoutUsername.requestFocus();
                        }
                        if (obj.getInt("errorCode") == 2) {
                            textLayoutEmail.setError(obj.getString("message"));
                            textLayoutEmail.requestFocus();
                        }
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(RegisterActivity.this, (obj.getString("message")), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Log.d("WHATERR", e.getMessage());
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private void addDataToDatabase() {

        final String username = Objects.requireNonNull(editTextUsername.getText()).toString();
        final String email = Objects.requireNonNull(editTextEmail.getText()).toString();
        final String password = Objects.requireNonNull(editTextPassword.getText()).toString();
        final String repassword = Objects.requireNonNull(editTextRePassword.getText()).toString();

        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            textLayoutUsername.setErrorEnabled(true);
            textLayoutUsername.setError("*Username cannot be null or empty");
            textLayoutUsername.requestFocus();
            return;
        } else if (TextUtils.isEmpty(email)) {
            textLayoutUsername.setErrorEnabled(true);
            textLayoutEmail.setError("*Email cannot be null or empty");
            textLayoutEmail.requestFocus();
            return;
        } else if (TextUtils.isEmpty(password)) {
            textLayoutUsername.setErrorEnabled(true);
            textLayoutPassword.setError("*Password cannot be null or empty");
            textLayoutPassword.requestFocus();
            return;
        } else if (TextUtils.isEmpty(repassword)) {
            textLayoutUsername.setErrorEnabled(true);
            textLayoutRePassword.setError("*Password does not match");
            textLayoutRePassword.requestFocus();
            return;
        }

        // url to post our data
        String url = "https://faunal-sources.000webhostapp.com/createUser.php";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", "RESPONSE IS " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // on below line we are displaying a success toast message.
                    Toast.makeText(RegisterActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // and setting data to edit text as empty
                editTextUsername.setText("");
                editTextEmail.setText("");
                editTextPassword.setText("");
                editTextRePassword.setText("");
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Log.d("WhatErr", error.toString());
                Toast.makeText(RegisterActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                // as we are passing data in the form of url encoded
                // so we are passing the content type below
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                // below line we are creating a map for storing
                // our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our
                // key and value pair to our parameters.
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);

                // at last we are returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }
}