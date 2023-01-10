package com.test.apex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.apex.database.CartFirebase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private static final Locale locale = new Locale("id", "ID");
    private static final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    TextView nmTxt, tpTxt, hrgTxt, descTxt, con, mnf, wgh, clr, stk;
    private String id = "", nmStr = "", tpStr = "", imgUri = "", hrgStr = "", descStr = "", conStr = "", mnfStr = "", wghStr = "", clrStr = "", stkStr = "";
    Long hrgVal, stkVal;
    private ImageView imageView;
    BottomSheetBehavior sheetBehavior;
    BottomSheetDialog sheetDialog;
    View bottom_sheet;
    Bitmap imgBitmap;
    View layout;
    private Product recyclerData = new Product();
    private final CartFirebase cartFirebase = new CartFirebase();
    private Cart cart = new Cart();
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Cart");
    long qty;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));
        layout = findViewById(R.id.root);
        nmTxt = findViewById(R.id.nmTxt);
        tpTxt = findViewById(R.id.tpTxt);
        hrgTxt = findViewById(R.id.hrgTxt);
        descTxt = findViewById(R.id.descTxt);
        con = findViewById(R.id.con);
        mnf = findViewById(R.id.mnf);
        wgh = findViewById(R.id.wgh);
        clr = findViewById(R.id.clr);
        stk = findViewById(R.id.stk);
        imageView = findViewById(R.id.img);

        format.setMaximumFractionDigits(0);
        Bundle extras = getIntent().getExtras();
        recyclerData = (Product) extras.getSerializable("chosenProduct");
        id = recyclerData.getproductId();
        nmStr = recyclerData.getProductName();
        tpStr = recyclerData.getproductType();
        imgUri = recyclerData.getproductImage();
        hrgVal = recyclerData.getproductPrice();
        conStr = recyclerData.getproductCondition();
        mnfStr = recyclerData.getproductManufacter();
        wghStr = recyclerData.getproductWeight();
        clrStr = recyclerData.getproductColor();
        stkVal = recyclerData.getproductStock();
        descStr = recyclerData.getproductDescription();

        hrgStr = format.format(hrgVal);
        stkStr = String.valueOf(stkVal);
        nmTxt.setText(nmStr);
        tpTxt.setText(tpStr);

        Glide.with(this).load(imgUri).listener(new RequestListener<Drawable>() {
            @Override public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {return false;}
            @Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                new Handler().postDelayed(DetailActivity.this::onImageSet, 0);
                return false;}
        }).into(imageView);

        hrgTxt.setText(hrgStr);
        con.setText(conStr);
        mnf.setText(mnfStr);
        wgh.setText(wghStr + " gram");
        clr.setText(clrStr);
        stk.setText(stkStr);
        descTxt.setText(descStr);

        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        findViewById(R.id.addCart).setOnClickListener(view -> addCart());

    }
    public int getDominantColor(Bitmap bitmap) {
        List<Palette.Swatch> swatchesTemp = Palette.from(bitmap).generate().getSwatches();
        List<Palette.Swatch> swatches = new ArrayList<>(swatchesTemp);
        Collections.sort(swatches, (swatch1, swatch2) -> swatch2.getPopulation() - swatch1.getPopulation());
        return !swatches.isEmpty() ? swatches.get(0).getRgb() : getResources().getColor(R.color.blue);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addCart() {
        User user = SharedPrefManager.getInstance(this).getUser();
        String userID = String.valueOf(user.getId());

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userID).exists())
                    if (snapshot.child(userID).child(id).exists()) qty = (long) snapshot.child(userID).child(id).child("productQuantity").getValue();
                    else qty = 0L;
                else qty = 0L;

                cart = new Cart(id, qty + 1);
                cartFirebase.addCart(getApplicationContext(), cart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        View view = getLayoutInflater().inflate(R.layout.cart_sheet, null);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        ImageView img_sheet = view.findViewById(R.id.img_sheet);
        MaterialTextView name_sheet = view.findViewById(R.id.item_name_sheet);
        MaterialTextView price_sheet = view.findViewById(R.id.item_price_sheet);

        Glide.with(this).load(imgBitmap).into(img_sheet);
        name_sheet.setText(nmStr);
        price_sheet.setText(hrgStr);

        (view.findViewById(R.id.toCart)).setOnClickListener(v -> {
            startActivity(new Intent(DetailActivity.this, KeranjangActivity.class));
            sheetDialog.dismiss();
            finish();
        });

        sheetDialog = new BottomSheetDialog(this);
        sheetDialog.setContentView(view);
        sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        sheetDialog.show();
        sheetDialog.setOnDismissListener(dialog -> sheetDialog = null);
    }

    private void onImageSet() {
        imgBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        getWindow().setStatusBarColor(getDominantColor(imgBitmap));
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] {
                getResources().getColor(R.color.detail_background), getResources().getColor(R.color.darkBackground)
        });
        gd.setCornerRadius(0f);
        layout.setBackgroundDrawable(gd);
        findViewById(R.id.onLoad).setVisibility(View.GONE);
    }

}