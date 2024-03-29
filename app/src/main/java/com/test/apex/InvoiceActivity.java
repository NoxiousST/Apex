package com.test.apex;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.test.apex.database.TransactionDatabase;
import com.test.apex.databinding.ActivityInvoiceBinding;
import com.test.apex.network.ServerAPI;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class InvoiceActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 200;
    NumberFormat format = NumberFormat.getNumberInstance();
    ActivityInvoiceBinding binding;
    ImageView isLogoSuccess;
    TextView isSuccess, isDescSuccess, isPrice, invoiceNumber;
    MaterialButton downloadInvoice, done;
    private User user;
    String status;
    Transaction transaction = new Transaction();
    Intent intent = getIntent();
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String currentPhotoPath;
    Bitmap bitmap;
    long priceAmount;
    String invoice;
    String username;
    String email;
    String address;
    String factureDate;
    String payDate;
    ObjectMapper mapper = new ObjectMapper();
    private ArrayList<Cart> invItem;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.blue));

        format.setMaximumFractionDigits(0);
        user = SharedPrefManager.getInstance(this).getUser();

        binding = ActivityInvoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        transaction = extras.getParcelable("transactionDetail");
        Log.d("Intent ID", "id transaction: " + transaction.getTransactionId());
        if (transaction.getTransactionStatus().equals("Success")) {
            ifSuccess();
        } else {
            ifPending();
        }

        priceAmount = transaction.getTransactionAmount();
        invoice = transaction.getInvoiceNumber();
        username = user.getUsername();
        email = user.getEmail();
        address = transaction.getTransactionAddress();
        factureDate = transaction.getInvoiceDate();
        payDate = transaction.getPaymentDate();


        binding.isSuccess.setText(transaction.getTransactionStatus());
        binding.price.setText("Rp. " + format.format(priceAmount));
        binding.invoiceNumber.setText(invoice);
        binding.username.setText(username);
        binding.email.setText(email);
        binding.address.setText(address);
        binding.factureDate.setText(factureDate);
        binding.payDate.setText(payDate);


        try {
            JsonNode node = mapper.readTree(transaction.getProducts());
            ObjectReader reader = mapper.readerFor(new TypeReference<List<Cart>>() {});

            invItem = reader.readValue(node);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            binding.itemRV.setLayoutManager(linearLayoutManager);

            InvoiceListAdapter adapter = new InvoiceListAdapter(invItem, this);
            binding.itemRV.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 200);

        binding.downloadInvoice.setOnClickListener(view -> {
            bitmap = getBitmapFromView(binding.scroll, binding.scroll.getChildAt(0).getHeight(), binding.scroll.getChildAt(0).getWidth());

            ContentResolver cr = getContentResolver();
            //Save with user editable title
            String title = transaction.getInvoiceNumber().replaceAll("[^\\dA-Za-z ]", "_").toLowerCase() + ".jpg";
            String description = "Apex Invoice";
            String savedURL = MediaStore.Images.Media.insertImage(cr, bitmap, title, description);

            Toast.makeText(this, "Saved to gallery " + savedURL, Toast.LENGTH_LONG).show();
        });
    }

    private void ifSuccess() {
        binding.isSuccess.setText(transaction.getTransactionStatus());
        Glide.with(this).load(R.drawable.logo_success).into(binding.isLogoSuccess);
        binding.isDescSuccess.setText("Pembayaran telah berhasil dilakukan");
        binding.done.setText("Selesai");
        binding.done.setOnClickListener(view -> {
            finish();
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void ifPending() {
        Glide.with(this).load(R.drawable.logo_pending).into(binding.isLogoSuccess);
        binding.isDescSuccess.setText("Menunggu pembayaran");
        binding.done.setText("Bayar");
        binding.done.setOnClickListener(view -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            binding.proofPayment.setVisibility(View.VISIBLE);
            Glide.with(this).load(photo).into(binding.proofPayment);
            updateTransaction();
            binding.isSuccess.setText("Success");
            ifSuccess();
        }
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmapScroll = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapScroll);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmapScroll;
    }

    private void updateTransaction() {
        transaction.setTransactionStatus("Success");
        SimpleDateFormat invDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String paymentDate = invDate.format(new Date());
        transaction.setPaymentDate(paymentDate);
        new TransactionDatabase(getApplicationContext(), ServerAPI.URL_UPDATE_NEW_TRANSACTION, transaction, new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                Log.d("VolleyReq", "onSuccess: " + res);
                try {
                    JsonNode node = mapper.readTree(res);
                        Snackbar.make(binding.getRoot(), node.get("message").asText(), 4800).
                                setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                                setTextColor(getResources().getColor(R.color.whiteAccent)).show();
                } catch (JsonProcessingException e) {
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
}