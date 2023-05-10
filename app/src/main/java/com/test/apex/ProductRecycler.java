package com.test.apex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.apex.database.CartFirebase;
import com.test.apex.network.ServerAPI;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductRecycler extends RecyclerView.Adapter<ProductRecycler.ProductViewHolder> {

    private static final Locale locale = new Locale("id", "ID");
    private final ArrayList<Product> productArrayList;
    private final Context mcontext;
    private final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    private Product product = new Product();
    private ReceiveSubtotal mPosition;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Cart");
    private CartFirebase cartFirebase = new CartFirebase();
    long subTotal = 0;
    long valueQuantity = 0;

    public ProductRecycler(ArrayList<Product> recyclerDataArrayList, Context mcontext, ReceiveSubtotal listener) {
        this.productArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.mPosition = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_recycle, parent, false);
        return new ProductViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        User user = SharedPrefManager.getInstance(mcontext).getUser();
        String userID = String.valueOf(user.getId());
        DatabaseReference mRef = mDatabase.child(userID);

        format.setMaximumFractionDigits(0);
        product = productArrayList.get(position);

        holder.itemName.setText(product.getProductName());
        holder.itemDesc.setText(String.valueOf(product.getproductType()));
        holder.itemPrice.setText(format.format(product.getproductPrice()));
        holder.itemManufacter.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.steelseries_logo));
        String url = ServerAPI.URL_PRODUCT_IMAGE + productArrayList.get(position).getproductImage();
        Glide.with(mcontext).load(url).into(holder.itemImage);

        holder.cardCV.setOnClickListener(view -> {
            mcontext.startActivity(new Intent(mcontext, DetailActivity.class).putExtra("chosenProduct", product));
        });


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    if (dsp.hasChild("productQuantity")) {
                        valueQuantity = (long) dsp.child("productQuantity").getValue();

                        for (Product product : productArrayList)
                            if (product.getproductId().equals(dsp.getKey())) subTotal += (valueQuantity * product.getproductPrice());
                    }
                }
                mPosition.receiveSubTotal(subTotal);
                subTotal = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FireBase Err", error.getDetails());
            }
        });

        holder.itemImage.setOnClickListener(view -> {
            product = productArrayList.get(position);
            view.startAnimation(AnimationUtils.loadAnimation(mcontext, R.anim.image_click));

            mRef.child(product.getproductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("productQuantity")) {
                        valueQuantity = (long) snapshot.child("productQuantity").getValue();
                        cartFirebase.updateCart(mcontext, product.getproductId(), valueQuantity + 1);
                    } else {
                        cartFirebase.addCart(mcontext, new Cart(product.getproductId(), 1));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FireBase Err", error.getDetails());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName, itemPrice, itemDesc;
        private final ImageView itemManufacter;
        private final ImageView itemImage;
        private final MaterialCardView cardCV;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemImage = itemView.findViewById(R.id.item_image);
            itemManufacter = itemView.findViewById(R.id.item_brand);
            itemDesc = itemView.findViewById(R.id.item_desc);
            cardCV = itemView.findViewById(R.id.cardCV);
        }
    }
}
