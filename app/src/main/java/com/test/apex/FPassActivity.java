package com.test.apex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.test.apex.R;
import com.test.apex.StatusBar;

import java.util.Objects;

public class FPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpass);
        Objects.requireNonNull(getSupportActionBar()).hide();
        new StatusBar().statusBar(this);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));
    }
}