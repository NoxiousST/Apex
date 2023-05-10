package com.test.apex;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.apex.database.TransactionDatabase;
import com.test.apex.databinding.ActivityKeranjangBinding;
import com.test.apex.network.ServerAPI;
import com.test.apex.ui.home.MapActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    ObjectMapper mapper = new ObjectMapper();
    String userid;
    APIInterface apiInterface;

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

        apiInterface = APIClient.getClient().create(APIInterface.class);

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
                readCounter();
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
                        tax = (subTotal + deliveryAmount) * 5 / 100;
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
                int totalItems = 0;
                Log.d("DataSnapshot", ":" + dataSnapshot.getKey() + ", " + dataSnapshot.getValue());
                cartArrayList.clear();
                StringBuilder sb = new StringBuilder("");
                ArrayNode arrayNode = mapper.createArrayNode();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.child("productQuantity").exists()) {
                        String id = (String) postSnapshot.child("productId").getValue();
                        long quantity = (long) postSnapshot.child("productQuantity").getValue();
                        cartArrayList.add(new Cart(id, quantity));
                        totalItems += quantity;

                    }
                }
                myAdapter(cartArrayList);
                binding.items.setText("Total " + totalItems + " items");
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
        tax = (subTotal + deliveryAmount) * 5 / 100;
        totalAmount = subTotal + deliveryAmount + tax;

        viewSubTotal.setText(format.format(subTotal));
        binding.tax.setText("(5%) " + format.format(tax));
        binding.totalAmount.setText(format.format(totalAmount));
    }

    private void readCounter() {

        Call<Object> call = apiInterface.retrieveCount(userID);

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    try {
                        String respData = mapper.writeValueAsString(response.body());
                        JsonNode actualObj = mapper.readTree(respData);

                        createTransaction(actualObj.get("count").asInt() + 1);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.d("Response Fail", "onFail: " + t.getMessage());
                t.printStackTrace();

            }
        });
    }

    private void createTransaction(int count) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd", locale);
        String date = simpleDateFormat.format(new Date());
        if (user.getLoginOption().equals("MySQL")) userid = String.format(Locale.getDefault(), "%04d", Long.parseLong(userID));
        else userid = String.format(Locale.getDefault(), "%04d", Long.parseLong(userID.substring(userID.length() - 4)));

        String transactionNumber = String.format(Locale.getDefault(), "%03d", count);
        String invoiceNumber = "INV/" + date + "-" + userid + "-" + transactionNumber;
        SimpleDateFormat invDate = new SimpleDateFormat("dd MMMM yyyy", locale);
        String invoiceDate = invDate.format(new Date());

        String productList = null;
        try {
            productList = mapper.writeValueAsString(cartArrayList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Call<Object> call = apiInterface.setTransaction(invoiceNumber, "Pending", totalAmount, address, invoiceDate, "-", userID, productList);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    try {
                        String respData = mapper.writeValueAsString(response.body());
                        JsonNode node = mapper.readTree(respData);
                        if (!node.get("error").asBoolean()) {
                            Intent intent = new Intent(KeranjangActivity.this, InvoiceActivity.class);
                            transaction.setTransactionId(node.get("lastId").asText());
                            intent.putExtra("transactionDetail", transaction);
                            startActivity(intent);
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.d("Response Fail", "onFail: " + t.getMessage());
                t.printStackTrace();

            }
        });
    }
}