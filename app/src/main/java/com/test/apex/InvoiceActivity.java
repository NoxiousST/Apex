package com.test.apex;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.test.apex.database.TransactionDatabase;
import com.test.apex.databinding.ActivityInvoiceBinding;
import com.test.apex.network.ServerAPI;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        transaction = new Gson().fromJson(extras.getString("transactionInvoice"), Transaction.class);

        if (transaction.getTransactionStatus().equals("Success")) {
            ifSuccess();
        } else {
            ifPending();
        }

        priceAmount = transaction.getTransactionAmount();
        invoice = transaction.getInvoiceNumber();
        username = user.getUsername();
        email = user.getEmail();
        address = transaction.gettransactionAddress();
        factureDate = transaction.getinvoiceDate();

        binding.isSuccess.setText(transaction.getTransactionStatus());
        binding.price.setText("Rp. " + format.format(priceAmount));
        binding.invoiceNumber.setText(invoice);
        binding.username.setText(username);
        binding.email.setText(email);
        binding.address.setText(address);
        binding.factureDate.setText(factureDate);


        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 200);
    }

    private void ifSuccess() {
        Glide.with(this).load(R.drawable.logo_success).into(binding.isLogoSuccess);
        String payDate = transaction.getpaymentDate();
        binding.isDescSuccess.setText("Pembayaran telah berhasil dilakukan");
        binding.payDate.setText(payDate);
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


        binding.downloadInvoice.setOnClickListener(view -> {
            binding.scroll.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(binding.scroll.getDrawingCache());

            ContentResolver cr = getContentResolver();
            //Save with user editable title
            String title = transaction.getInvoiceNumber().replaceAll("[^\\dA-Za-z ]", "_").toLowerCase() + ".jpg";
            String description = "Apex Invoice";
            String savedURL = MediaStore.Images.Media.insertImage(cr, bitmap, title, description);

            Toast.makeText(this, "Saved to gallery " + savedURL, Toast.LENGTH_LONG).show();
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
            ifSuccess();
        }
    }

    private void createTransaction() {
        SimpleDateFormat invDate = new SimpleDateFormat("dd MMMM yy", Locale.getDefault());
        String paymentDate = invDate.format(new Date());
        Transaction newTransaction;
        newTransaction = new Transaction(transaction.getTransactionId(), invoice, "Success", priceAmount, address, invoice, paymentDate, String.valueOf(user.getId()), transaction.getproducts());
        new TransactionDatabase(getApplicationContext(), ServerAPI.URL_UPDATE_TRANSACTION, newTransaction, new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                Log.d("VolleyReq", "onSuccess: " + res);
                JsonObject responseResult = new Gson().fromJson(res, JsonObject.class);
                JsonPrimitive message = responseResult.get("message").getAsJsonPrimitive();


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