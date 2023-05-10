package com.test.apex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.test.apex.databinding.ActivityLoginBinding;
import com.test.apex.databinding.ActivityRegisterBinding;
import com.test.apex.network.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ObjectMapper mapper = new ObjectMapper();
    private String username, email, password, repassword;
    private Bitmap icon;
    private APIInterface apiInterface;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();
        new StatusBar().statusBar(this);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        icon = drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.check));
        apiInterface = APIClient.getClient().create(APIInterface.class);

        binding.btnRegister.setEnabled(binding.checkBox.isChecked());
        binding.btnRegister.setOnClickListener(view -> checkInput());
    }

    private void checkInput() {
        binding.btnRegister.startAnimation();
        username = Objects.requireNonNull(binding.editTextUsername.getText()).toString();
        email = Objects.requireNonNull(binding.editTextEmail.getText()).toString();
        password = Objects.requireNonNull(binding.editTextPassword.getText()).toString();
        repassword = Objects.requireNonNull(binding.editTextRePassword.getText()).toString();

        if (TextUtils.isEmpty(username)) {
            binding.textLayoutUsername.setErrorEnabled(true);
            binding.textLayoutUsername.setError("*Username cannot be null or empty");
            binding.textLayoutUsername.requestFocus();
            return;
        } else if (TextUtils.isEmpty(email)) {
            binding.textLayoutUsername.setErrorEnabled(true);
            binding.textLayoutEmail.setError("*Email cannot be null or empty");
            binding.textLayoutEmail.requestFocus();
            return;
        }  else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textLayoutUsername.setErrorEnabled(true);
            binding.textLayoutEmail.setError("*Email is invalid");
            binding.textLayoutEmail.requestFocus();
            return;
        }else if (TextUtils.isEmpty(password)) {
            binding.textLayoutUsername.setErrorEnabled(true);
            binding.textLayoutPassword.setError("*Password cannot be null or empty");
            binding.textLayoutPassword.requestFocus();
            return;
        } else if (TextUtils.isEmpty(repassword)) {
            binding.textLayoutUsername.setErrorEnabled(true);
            binding.textLayoutRePassword.setError("*Password does not match");
            binding.textLayoutRePassword.requestFocus();
            return;
        }
        registerUser();
    }

    private void registerUser() {
        Call<Object> call = apiInterface.setSignup(username, email, password);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

                try {
                    String respData = mapper.writeValueAsString(response.body());
                    JsonNode actualObj = mapper.readTree(respData);

                    if (actualObj.get("error").asBoolean()) {
                        binding.btnRegister.revertAnimation();
                        binding.btnRegister.recoverInitialState();
                        Snackbar.make(binding.getRoot(), actualObj.get("message").asText(), Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.accentBackground))
                                .setTextColor(getResources().getColor(R.color.whiteAccent)).show();
                        return;
                    }
                    binding.btnRegister.doneLoadingAnimation(getResources().getColor(R.color.blue), icon);
                    User user = mapper.treeToValue(actualObj.get("user"), User.class);
                    user.setLoginOption("MySQL");
                    SharedPrefManager.getInstance(RegisterActivity.this).userLogin(user);
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    finish();

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.d("Response Fail", "onFail: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}