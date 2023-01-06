package com.test.apex;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import com.google.gson.reflect.TypeToken;
import com.test.apex.databinding.ActivityLoginBinding;
import com.test.apex.ui.Products.HomeActivity;
import com.test.apex.network.ServerAPI;
import com.test.apex.ui.Products.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    TextInputEditText editTextUsername, editTextPassword;
    TextInputLayout textLayoutUsername, textLayoutPassword;
    private LinearProgressIndicator progressBar;
    JSONParser jsonParser = new JSONParser();
    private String username, password;
    boolean cancel = false;
    ActivityLoginBinding binding;
    SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Bitmap icon;

    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    ProfileTracker profileTracker;
    AccessToken accessToken;

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

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        progressBar = findViewById(R.id.progress);
        progressBar.hide();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        textLayoutUsername = findViewById(R.id.textLayoutUsername);
        textLayoutPassword = findViewById(R.id.textLayoutPassword);

        Log.d("ICON", icon.toString());
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

        binding.btnLogin.setOnClickListener(view -> {
            if (!checkInput()) {
                if (!isEmail(username)) userLoginUsername();
                else userLoginEmail();
            }
        });
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
                            User user = new User(
                                    task.getResult().getId(),
                                    task.getResult().getDisplayName(),
                                    task.getResult().getEmail(),
                                    task.getResult().getIdToken(),
                                    "Google"
                            );
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
                        Log.d("Success", "Login");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                (object, response) -> {

                                    try {
                                        String id = object.getString("id");
                                        String name = object.getString("name");
                                        String email = object.getString("email");

                                        User user = new User(
                                                id,
                                                name,
                                                email,
                                                "",
                                                "Facebook"
                                        );

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

    private boolean checkInput() {
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

                binding.btnLogin.startAnimation();
                binding.btnLogin.setOnClickListener(view -> {
                    binding.btnLogin.recoverInitialState();
                });
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("ICON", icon.toString());
                binding.btnLogin.setPaddingProgress(0);

                try {
                    JSONObject obj = new JSONObject(s);


                    if (!obj.getBoolean("error")) {


                        binding.btnLogin.doneLoadingAnimation(getResources().getColor(R.color.blue), icon);

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject userJson = obj.getJSONObject("user");

                        User user = new User(
                                userJson.getString("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("password"),
                                "MySQL"
                        );
                        Log.d("USER", user.getUsername());
                        SharedPrefManager.getInstance(LoginActivity.this).userLogin(user);

                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }, 800);
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
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject userJson = obj.getJSONObject("user");
                        User user = new User(
                                userJson.getString("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("password"),
                                "MySQL"
                        );
                        SharedPrefManager.getInstance(LoginActivity.this).userLogin(user);

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
                params.put("email", username);
                params.put("password", password);

                return requestHandler.sendPostRequest(ServerAPI.URL_LOGIN_EMAIL, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void readProduct() {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.logoapex);
        String N = "NULL";
        Product newProduct = new Product(N, N, N, N, 0L, N, N, N, N, 0L, N);

        new VolleyServerRequest(getApplicationContext(), ServerAPI.URL_READ_PRODUCT, newProduct, bitmap, new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                Log.d("VolleyReq", "onSuccess: " + res);


                try {
                    JSONObject responseResult = null;
                    responseResult = new JSONObject(res);

                    JSONArray products = responseResult.getJSONArray("products");

                    Log.d("JsonArray", products.toString());

                    Type type = new TypeToken<List<Product>>() {
                    }.getType();
                    List<Product> productList = new Gson().fromJson(products.toString(), type);
                    SharedPrefManager.getInstance(getBaseContext()).saveProductList(new ArrayList<>(productList));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(R.id.root), error, 4800).
                        setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                        setTextColor(getResources().getColor(R.color.whiteAccent)).show();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}