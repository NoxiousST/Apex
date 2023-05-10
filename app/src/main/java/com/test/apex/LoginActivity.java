package com.test.apex;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.test.apex.database.UserDatabase;
import com.test.apex.databinding.ActivityLoginBinding;
import com.test.apex.network.ServerAPI;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    TextInputEditText editTextUsername, editTextPassword;
    TextInputLayout textLayoutUsername, textLayoutPassword;
    private LinearProgressIndicator progressBar;
    private String username, password;
    ActivityLoginBinding binding;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Bitmap icon;

    CallbackManager callbackManager;

    ObjectMapper mapper = new ObjectMapper();
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        new StatusBar().statusBar(this);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        icon = drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.check));
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);


        progressBar = findViewById(R.id.progress);
        progressBar.hide();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        textLayoutUsername = findViewById(R.id.textLayoutUsername);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
            return;
        }
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
            return;
        }

        binding.btnLogin.setOnClickListener(view -> checkInput());
        findViewById(R.id.btnRegister).setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        findViewById(R.id.fpass).setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, FPassActivity.class)));


        binding.btnGoogle.setOnClickListener(view -> {
            Intent signInIntent = gsc.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

                        try {
                            task.getResult(ApiException.class);

                            User user = new User();
                            user.setUsername(task.getResult().getDisplayName());
                            user.setId(task.getResult().getId());
                            user.setEmail(task.getResult().getEmail());
                            user.setLoginOption("Google");

                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();

                        } catch (ApiException e) {
                            Log.d("GoogleErr", e.getMessage());
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                (object, response) -> {
                                    try {
                                        User user = new User();
                                        user.setId(object.getString("id"));
                                        user.setUsername(object.getString("name"));
                                        user.setEmail(object.getString("email"));
                                        user.setLoginOption("Facebook");

                                        SharedPrefManager.getInstance(LoginActivity.this).userLogin(user);
                                        new Handler().postDelayed(() -> {
                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                            finish();
                                        }, 800);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,link");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        binding.btnFacebook.setOnClickListener(view ->
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "email")));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getManualLogin(int option) {
        Call<Object> call;
        if (option == 0) call = apiInterface.retrieveLoginUsername(username, password);
        else call = apiInterface.retrieveLoginEmail(username, password);

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

                try {
                    String respData = mapper.writeValueAsString(response.body());
                    JsonNode actualObj = mapper.readTree(respData);

                    if (actualObj.get("error").asBoolean()) {
                        binding.btnLogin.revertAnimation();
                        binding.btnLogin.recoverInitialState();
                        Snackbar.make(binding.getRoot(), actualObj.get("message").asText(), Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                                setTextColor(getResources().getColor(R.color.whiteAccent)).show();
                        return;
                    }
                    binding.btnLogin.doneLoadingAnimation(getResources().getColor(R.color.blue), icon);
                    User user = mapper.treeToValue(actualObj.get("user"), User.class);
                    user.setLoginOption("MySQL");
                    SharedPrefManager.getInstance(LoginActivity.this).userLogin(user);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
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

    private void checkInput() {
        binding.btnLogin.startAnimation();
        username = Objects.requireNonNull(editTextUsername.getText()).toString();
        password = Objects.requireNonNull(editTextPassword.getText()).toString();

        if (TextUtils.isEmpty(username)) {
            textLayoutUsername.setError("Please enter your username");
            textLayoutUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            textLayoutPassword.setError("Please enter your password");
            textLayoutPassword.requestFocus();
            return;
        }
        int x = 0;
        if (isEmail(username)) x = 1;
        getManualLogin(x);
    }

    private boolean isEmail(String str) {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}