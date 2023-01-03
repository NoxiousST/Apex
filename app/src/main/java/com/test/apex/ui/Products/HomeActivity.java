package com.test.apex.ui.Products;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.robinhood.ticker.TickerView;
import com.test.apex.KeranjangActivity;
import com.test.apex.R;
import com.test.apex.ReceivePosition;
import com.test.apex.ScrollAdapter;
import com.test.apex.SharedPrefManager;
import com.test.apex.VolleyOnEventListener;
import com.test.apex.VolleyServerRequest;
import com.test.apex.User;
import com.test.apex.network.ServerAPI;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ReceivePosition {

    private DiscreteScrollView itemPicker;
    private ArrayList<Product> productArrayList;
    private ArrayList<Product> mainArrayList;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView recyclerView;
    private ScrollAdapter scrollAdapter;
    private RecyclerViewAdapter adapter;
    private DrawerLayout drawerLayout;
    private MaterialToolbar topAppBar;
    private User user;
    private NavigationView navigationView;
    private View headView;
    private int listPosition;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private AppCompatButton toCart;
    private boolean active = false;
    private Product product;
    private static final Locale locale = new Locale("id", "ID");
    NumberFormat format = NumberFormat.getNumberInstance(locale);
    TickerView tickerView;
    RelativeLayout layoutBtn;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        topAppBar = findViewById(R.id.topAppBar);
        drawerLayout = findViewById(R.id.root);
        navigationView = findViewById(R.id.navigationView);
        layoutBtn = findViewById(R.id.layoutBtn);
        tickerView = findViewById(R.id.tickerView);
        navigationView.setNavigationItemSelectedListener(this);

        toCart = findViewById(R.id.toCart);

        topAppBar.setOnClickListener(view -> drawerLayout.open());
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.cart:
                    startActivity(new Intent(HomeActivity.this, KeranjangActivity.class));
                    break;
                case R.id.logout:
                    finish();
                    SharedPrefManager.getInstance(getApplicationContext()).logout();
                    break;
                default:
                    break;
            }
            return true;
        });

        user = SharedPrefManager.getInstance(this).getUser();
        headView = navigationView.getHeaderView(0);
        ((TextView) headView.findViewById(R.id.displayUsernmae)).setText(user.getUsername());
        ((TextView) headView.findViewById(R.id.displayEmail)).setText(user.getEmail());

        mainArrayList = new ArrayList<>();
        mainArrayList.add(new Product("1", "Shoe 1", "Running Shoe", "", 2000000L, "New", "Nike", "1000", "Red", 100L, "This is a good shoe"));
        mainArrayList.add(new Product("1", "Shoe 1", "Running Shoe", "", 2000000L, "New", "Nike", "1000", "Red", 100L, "This is a good shoe"));
        mainArrayList.add(new Product("1", "Shoe 1", "Running Shoe", "", 2000000L, "New", "Nike", "1000", "Red", 100L, "This is a good shoe"));

        itemPicker = findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        scrollAdapter = new ScrollAdapter(mainArrayList, this);
        itemPicker.setAdapter(scrollAdapter);
        itemPicker.setItemTransitionTimeMillis(200);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER)
                .build());


        recyclerView = findViewById(R.id.itemRV);
        productArrayList = SharedPrefManager.getInstance(this).loadProductList();

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        adapter = new RecyclerViewAdapter(productArrayList, this, this);
        recyclerView.setAdapter(adapter);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (!Objects.requireNonNull(result.getData()).getStringExtra("message").isEmpty()) {
                            Snackbar.make(findViewById(R.id.root), Objects.requireNonNull(result.getData()).getStringExtra("message"), 4800).
                                    setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                                    setTextColor(getResources().getColor(R.color.whiteAccent)).show();
                            readProduct();
                            //new Handler().postDelayed(() -> loadProduct(SharedPrefManager.getInstance(this).loadProductList()),8600);
                        }
                    }
                });

        findViewById(R.id.viewCart).setOnClickListener(view -> {
            ValueAnimator anim;
            if (!active) {
                anim = ValueAnimator.ofInt(0, dpToPx(360));
                view.animate().rotation(360f).setDuration(300).start();
                active = true;
            } else {
                anim = ValueAnimator.ofInt(dpToPx(360), 0);
                view.animate().rotation(0f).setDuration(300).start();
                active = false;
            }

            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layoutBtn.getLayoutParams();
                layoutParams.width = val;
                layoutBtn.setLayoutParams(layoutParams);
            });
            anim.setDuration(450);
            anim.start();

        });


        toCart.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, KeranjangActivity.class)));
    }

    private void loadProduct(ArrayList<Product> newProduct) {
        productArrayList = newProduct;

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        adapter = new RecyclerViewAdapter(newProduct, this, this);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.cart:
                startActivity(new Intent(HomeActivity.this, KeranjangActivity.class));
                break;
            case R.id.logout:
                finish();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                break;
            default:
                break;
        }
        //close navigation drawer
        drawerLayout.close();
        return true;
    }

    @Override
    public void receivePosition(int listPosition, int menu, Intent intent, Product product) {
        this.listPosition = listPosition;
        this.product = product;

        if (menu == 1) {
            activityResultLauncher.launch(intent);
        }
        else if (menu == 2) {
            intent.putExtra("chosenProduct", product);
            activityResultLauncher.launch(intent);
        }
        else if (menu == 3) {
            deleteProduct();
            readProduct();
        }
    }

    @Override
    public void receiveSubTotal(long subTotal) {
        format.setMaximumFractionDigits(0);
        tickerView.setText(format.format(subTotal));
    }


    public int dpToPx(int dip) {
        Resources r = this.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
    }

    private void deleteProduct() {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.logoapex);

        new VolleyServerRequest(getApplicationContext(), ServerAPI.URL_DELETE_PRODUCT, product, bitmap, new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                readProduct();
                Log.d("VolleyReq", "onSuccess: " + res);
                JsonObject responseResult = new Gson().fromJson(res, JsonObject.class);
                JsonPrimitive message = responseResult.get("message").getAsJsonPrimitive();
                Snackbar.make(findViewById(R.id.root), message.getAsString(), 3200).
                        setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                        setTextColor(getResources().getColor(R.color.whiteAccent)).show();
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(R.id.root), error, 4800).
                        setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                        setTextColor(getResources().getColor(R.color.whiteAccent)).show();
            }
        });
    }

    private void readProduct() {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.logoapex);
        String N = "NULL";
        Product newProduct = new Product(N, N, N, N, 0L, N, N, N, N, 0L, N);

        new VolleyServerRequest(getApplicationContext(), ServerAPI.URL_READ_PRODUCT, newProduct, bitmap, new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                Log.d("VolleyReq", "onSuccess: " + res);

                JsonObject responseResult = new Gson().fromJson(res, JsonObject.class);
                JsonArray products = responseResult.get("products").getAsJsonArray();

                Log.d("JsonArray", products.toString());

                Type type = new TypeToken<List<Product>>() {}.getType();
                List<Product> productList = new Gson().fromJson(products.toString(), type);
                SharedPrefManager.getInstance(getBaseContext()).saveProductList(new ArrayList<>(productList));
                loadProduct(new ArrayList<>(productList));
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(R.id.root), error, 4800).
                        setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                        setTextColor(getResources().getColor(R.color.whiteAccent)).show();
            }
        });


    }
}