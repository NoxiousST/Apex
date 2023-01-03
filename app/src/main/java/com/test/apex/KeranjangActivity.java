package com.test.apex;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.test.apex.ui.Products.CartRecyclerAdapter;
import com.test.apex.database.TransactionDatabase;
import com.test.apex.databinding.ActivityKeranjangBinding;
import com.test.apex.network.ServerAPI;
import com.test.apex.ui.home.MapActivity;

import java.text.SimpleDateFormat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class KeranjangActivity extends AppCompatActivity implements ReceiveCartPosition {

    private static final Locale locale = new Locale("id", "ID");
    ActivityKeranjangBinding binding;
    ArrayList<Cart> cartArrayList;
    RecyclerView recyclerView;
    CartRecyclerAdapter adapter;
    GridLayoutManager gridLayoutManager;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Cart");
    private int listPosition, menu;
    String productId = "0";
    TextView viewItems, viewSubTotal;
    private final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    User user;
    String userID;
    Cart cart = new Cart();
    MaterialButton cartButton;
    Transaction transaction;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    int transactionCounter;
    String address;
    long subTotal = 0, deliveryAmount = 0, tax = 0, totalAmount = 0;
    ArrayList<ProductTransaction> productTransactionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));
        format.setMaximumFractionDigits(0);
        user = SharedPrefManager.getInstance(this).getUser();
        userID = String.valueOf(user.getId());

        binding = ActivityKeranjangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewItems = findViewById(R.id.items);
        viewSubTotal = findViewById(R.id.subTotal);
        recyclerView = findViewById(R.id.itemRV);

        cartArrayList = new ArrayList<>();


        gridLayoutManager = new GridLayoutManager(this, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), gridLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new CartRecyclerAdapter(cartArrayList, this, this);
        recyclerView.setAdapter(adapter);
        changeList();
        //initializeListView();
        View detail = findViewById(R.id.detail);
        View content = findViewById(R.id.content);
        detail.post(() -> {
            int height = detail.getHeight();
            Log.d("getHeight", String.valueOf(height));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) content.getLayoutParams();
            params.bottomMargin = height;
            content.setLayoutParams(params);
        });



        binding.toCheckout.setOnClickListener(view -> {
            if (deliveryAmount > 0)
                createTransaction();
            else
                activityResultLauncher.launch(new Intent(KeranjangActivity.this, MapActivity.class));
        });

        binding.toMap.setOnClickListener(view -> {
            activityResultLauncher.launch(new Intent(KeranjangActivity.this, MapActivity.class));

        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent getResult = result.getData();
                        address = Objects.requireNonNull(getResult).getStringExtra("alamat");
                        deliveryAmount = getResult.getLongExtra("pengiriman", 0);
                        tax = (subTotal + deliveryAmount) * 5/100;
                        totalAmount = subTotal + deliveryAmount + tax;

                        binding.deliverAmount.setText(format.format(deliveryAmount));
                        binding.tax.setText("(5%) " + tax);
                        binding.totalAmount.setText(format.format(totalAmount));

                        binding.toMap.setVisibility(View.VISIBLE);
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deliveryAmount > 0) binding.toCheckout.setText("Checkout");
        else binding.toCheckout.setText("Pilih Pengiriman");
    }

    public void myAdapter(ArrayList<Cart> newCartArrayList) {
        adapter = new CartRecyclerAdapter(newCartArrayList, this, this);
        recyclerView.setAdapter(adapter);
    }

    public void changeList() {
        mDatabase.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DataSnapshot", ":" + dataSnapshot.getKey() + ", " + dataSnapshot.getValue());
                cartArrayList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.child("productQuantity").exists()) {
                        Log.d("PostSnapshot", ":" + postSnapshot.getKey() + ", " + postSnapshot.getValue());
                        Log.d("PostSnapshot Child", ":" + postSnapshot.getKey() + ", " + postSnapshot.child("productQuantity").getValue());
                        String id = (String) postSnapshot.child("productId").getValue();
                        long quantity = (long) postSnapshot.child("productQuantity").getValue();
                        cartArrayList.add(new Cart(id, quantity));
                    }
                }
                myAdapter(cartArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FirebaseErr", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }

    @Override
    public void receivePosition(int listPosition, Cart cart) {
        this.listPosition = listPosition;
        this.cart = cart;
    }

    @Override
    public void receiveSubTotal(long subTotal) {
        this.subTotal = subTotal;
        tax = (subTotal + deliveryAmount) * 5/100;
        totalAmount = subTotal + deliveryAmount + tax;

        viewSubTotal.setText(format.format(subTotal));
        binding.tax.setText("(5%) " + format.format(tax));
        binding.totalAmount.setText(format.format(totalAmount));
    }

    private void createTransaction() {

        new TransactionDatabase(getApplicationContext(), new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                JsonObject responseResult = new Gson().fromJson(res, JsonObject.class);
                JsonPrimitive message = responseResult.get("count").getAsJsonPrimitive();
                transactionCounter = Integer.parseInt(message.toString()) + 1;


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd", locale);
                String date = simpleDateFormat.format(new Date());
                String userid = String.format(Locale.getDefault(), "%03d", Integer.parseInt(userID));
                String transactionNumber = String.format(Locale.getDefault(), "%03d", transactionCounter);
                String invoiceNumber = "INV/" + date + "-" + userid + "-" + transactionNumber;

                SimpleDateFormat invDate = new SimpleDateFormat("dd MMMM yy", locale);
                String invoiceDate = invDate.format(new Date());

                GsonBuilder gsonBuilder = new GsonBuilder();

                Gson gson = gsonBuilder.create();
                String productList = gson.toJson(cartArrayList);
                Log.d("VolleyReq", "cartArrayList: " + productList);

                transaction = new Transaction("id", invoiceNumber, "Pending", totalAmount, address, invoiceDate, "-", userID, productList);
                new TransactionDatabase(getApplicationContext(), ServerAPI.URL_CREATE_TRANSACTION, transaction, new VolleyOnEventListener() {
                    @Override
                    public void onSuccess(String res) {
                        Log.d("VolleyReq", "onSuccess: " + res);
                        JsonObject responseResult = new Gson().fromJson(res, JsonObject.class);
                        JsonPrimitive message = responseResult.get("message").getAsJsonPrimitive();
                        if (!responseResult.get("error").getAsBoolean()) {
                            Intent intent = new Intent(KeranjangActivity.this, InvoiceActivity.class);
                            Log.d(TAG, transaction.getInvoiceNumber());
                            intent.putExtra("transactionInvoice", new Gson().toJson(transaction));
                            startActivity(intent);
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

            @Override
            public void onFailure(String error) {
                Log.d(TAG, error);
            }
        });
    }

}