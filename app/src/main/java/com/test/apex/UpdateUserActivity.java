package com.test.apex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.menu.MenuBuilder;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class UpdateUserActivity extends AppCompatActivity {

    TextInputLayout updUser, updCurPass, updNewPass1, updNewPass2, dltPass;
    TextInputEditText updUserText;
    ExtendedFloatingActionButton updBtn, rstBtn, dltBtn;
    private User user;
    int position;
    View customAlertDialogView;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    Button btnHapus;
    ImageButton btnCancel;
    AlertDialog alertDialog;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Update Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = SharedPrefManager.getInstance(this).getUser();

        updUser = findViewById(R.id.updUser);
        updBtn = findViewById(R.id.updBtn);

        updCurPass = findViewById(R.id.updCurPass);
        updNewPass1 = findViewById(R.id.updNewPass1);
        updNewPass2 = findViewById(R.id.updNewPass2);
        rstBtn = findViewById(R.id.rstBtn);

        dltPass = findViewById(R.id.dltPass);
        dltBtn = findViewById(R.id.dltBtn);

        updUserText = findViewById(R.id.updUserText);


        updUserText.setText(user.getUsername());

        updBtn.setOnClickListener(view -> {
            updUser.setError(null);
            View fokus = null;
            boolean cancel = false;

            String user = String.valueOf(Objects.requireNonNull(updUser.getEditText()).getText());

            if (TextUtils.isEmpty(user)){
                updUser.setError("This field is required");
                fokus = updUser;
                cancel = true;
            }
            else if(cekUser(user)){
                updUser.setError("Username must be different");
                fokus = updUser;
                cancel = true;
            }
            if (cancel){
                fokus.requestFocus();
            }else{

                Snackbar.make(view, "Username berhasil diupdate", Snackbar.LENGTH_LONG)
                        .setAction("OK", v ->{})
                        .show();
            }
        });

        rstBtn.setOnClickListener(view -> {
            updCurPass.setError(null);
            updNewPass1.setError(null);
            updNewPass2.setError(null);
            View fokus = null;
            boolean cancel = false;

            String cur_password = String.valueOf(Objects.requireNonNull(updCurPass.getEditText()).getText());
            String new_password = String.valueOf(Objects.requireNonNull(updNewPass1.getEditText()).getText());
            String new_repassword = String.valueOf(Objects.requireNonNull(updNewPass2.getEditText()).getText());

            if (TextUtils.isEmpty(cur_password)){
                updCurPass.setError("This field is required");
                fokus = updCurPass;
                cancel = true;
            }else if (!cekPassword(new_password,new_repassword)){
                updNewPass1.setError("This password is incorrect");
                updNewPass1.setError("This password is incorrect");
                fokus = updNewPass1;
                cancel = true;
            }else if (cur_password.equals(new_password)) {
                updCurPass.setError("New Password should not be the same as old password");
                fokus = updCurPass;
                cancel = true;
            }else if (!cekCurrentPassword(cur_password)) {
                updCurPass.setError("Password is incorrect");
                fokus = updCurPass;
                cancel = true;
            }

            if (cancel){
                fokus.requestFocus();
            }else{

                Snackbar.make(view, "Password berhasil diupdate", Snackbar.LENGTH_LONG)
                        .setAction("OK", v ->{})
                        .show();
            }
        });

        dltBtn.setOnClickListener(view -> {
            dltPass.setError(null);
            View fokus = null;
            boolean cancel = false;

            String dlt_pass = String.valueOf(Objects.requireNonNull(dltPass.getEditText()).getText());

            if(!cekCurrentPassword(dlt_pass)){
                dltPass.setError("Password is incorrect");
                fokus = dltPass;
                cancel = true;
            }

            if (TextUtils.isEmpty(dlt_pass)){
                dltPass.setError("This field is required");
                fokus = dltPass;
                cancel = true;
            }

            if (cancel) fokus.requestFocus();
            else {



                btnCancel.setOnClickListener(v -> alertDialog.dismiss());
                btnHapus.setOnClickListener(v -> {
                });
            }
        });
    }

    private boolean cekPassword(String password, String repassword){
        return password.equals(repassword);
    }

    private boolean cekUser(String user){
        return this.user.getUsername().equals(user);
    }

    private boolean cekCurrentPassword(String password){
        return this.user.getPassword().equals(password);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }


}