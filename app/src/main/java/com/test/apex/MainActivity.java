package com.test.apex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.apex.network.ServerAPI;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
        new StatusBar().statusBar(this);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        new Handler().postDelayed(() -> {
            Intent login = new Intent(MainActivity.this, NewCalendar.class);
            startActivity(login);
            finish();
        }, 3000);
    }

    public static boolean toLogout(Context context, String option) {
        boolean logout = false;
        switch (option) {
            case "MySQL":
                logout = true;
                break;
            case "Google":
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                GoogleSignInClient gsc = GoogleSignIn.getClient(context, gso);
                gsc.signOut().addOnCompleteListener(task -> {});
                logout = true;
                break;
            case "Facebook":
                LoginManager.getInstance().logOut();
                logout = true;
                break;
        }
        SharedPrefManager.getInstance(context).logout();
        return logout;
    }


}