package com.test.apex.accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.test.apex.ui.Products.HomeActivity;
import com.test.apex.JSONParser;
import com.test.apex.User;
import com.test.apex.R;
import com.test.apex.RequestHandler;
import com.test.apex.network.ServerAPI;
import com.test.apex.SharedPrefManager;
import com.test.apex.StatusBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextUsername, editTextPassword;
    TextInputLayout textLayoutUsername, textLayoutPassword;
    private LinearProgressIndicator progressBar;
    JSONParser jsonParser = new JSONParser();
    private String username, password;
    boolean cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        new StatusBar().statusBar(this);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        progressBar = findViewById(R.id.progress);
        progressBar.hide();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        textLayoutUsername = findViewById(R.id.textLayoutUsername);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);


        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
            return;
        }

        findViewById(R.id.btnLogin).setOnClickListener(view -> {
            if (!checkInput()) {
                if (!isEmail(username)) userLoginUsername();
                else userLoginEmail();
            }
        });
        findViewById(R.id.btnRegister).setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        findViewById(R.id.fpass).setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, FPassActivity.class)));

    }

    private boolean checkInput(){
        cancel = false;
        username = Objects.requireNonNull(editTextUsername.getText()).toString();
        password = Objects.requireNonNull(editTextPassword.getText()).toString();

        if (TextUtils.isEmpty(username)) {
            textLayoutUsername.setError("Please enter your username");
            textLayoutUsername.requestFocus();
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            textLayoutPassword.setError("Please enter your password");
            textLayoutPassword.requestFocus();
            cancel = true;
        }
        return cancel;
    }

    private boolean isEmail(String str) {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    private void userLoginUsername() {
        class UserLogin extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.hide();

                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject userJson = obj.getJSONObject("user");

                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("password")
                        );
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                return requestHandler.sendPostRequest(ServerAPI.URL_LOGIN_USERNAME, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void userLoginEmail() {
        class UserLogin extends AsyncTask<Void, Void, String> {

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
                    Log.d("WHAT", "2");
                    JSONObject obj = new JSONObject(s);
                    Log.d("WHAT", "3");
                    if (!obj.getBoolean("error")) {
                        Log.d("WHAT", "4");
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        JSONObject userJson = obj.getJSONObject("user");
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("password")
                        );
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.d("WHATERR", e.getMessage());
                    Log.d("WHATS", s);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("email", username);
                params.put("password", password);

                return requestHandler.sendPostRequest(ServerAPI.URL_LOGIN_EMAIL, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }


}