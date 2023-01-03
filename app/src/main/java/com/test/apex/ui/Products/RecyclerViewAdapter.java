package com.test.apex.ui.Products;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.apex.Cart;
import com.test.apex.R;
import com.test.apex.ReceivePosition;
import com.test.apex.SharedPrefManager;
import com.test.apex.User;
import com.test.apex.database.CartFirebase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private static final Locale locale = new Locale("id", "ID");
    private final ArrayList<Product> productArrayList;
    private final Context mcontext;
    int tx, ty, width, height;
    NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    PopupWindow mypopupWindow;
    int[] location;
    Product recyclerData = new Product();
    ReceivePosition mPosition;
    private AlertDialog alertDialog;
    private AppCompatImageView appCompatImageView;
    private TextView textAlert;
    private MaterialButton exec;
    private View customAlertDialogView;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Cart");
    CartFirebase cartFirebase = new CartFirebase();
    long subTotal = 0;
    long valueQuantity = 0;

    public RecyclerViewAdapter(ArrayList<Product> recyclerDataArrayList, Context mcontext, ReceivePosition listener) {
        this.productArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.mPosition = listener;

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recycle, parent, false);
        return new RecyclerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        User user = SharedPrefManager.getInstance(mcontext).getUser();
        String userID = String.valueOf(user.getId());
        DatabaseReference mRef = mDatabase.child(userID);

        format.setMaximumFractionDigits(0);
        recyclerData = productArrayList.get(position);
        holder.progressIndicator.show();
        Glide.with(mcontext).load(recyclerData.getproductImage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressIndicator.hide();
                return false;
            }
        }).into(holder.itemImage);


        holder.itemName.setText(recyclerData.getProductName());
        holder.itemDesc.setText(String.valueOf(recyclerData.getproductType()));
        holder.itemPrice.setText(format.format(recyclerData.getproductPrice()));
        if (recyclerData.getproductManufacter().equals("Nike"))
            holder.itemManufacter.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.nike_logo));
        else
            holder.itemManufacter.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.adidas));

        holder.cardCV.setOnClickListener(view -> {
            recyclerData = productArrayList.get(position);
            Intent i = new Intent(mcontext, DetailActivity.class);
            i.putExtra("chosenProduct", recyclerData);
            mcontext.startActivity(i);
        });

        holder.cardCV.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                Point point = getPointOfView(view);
                tx = point.x;
                ty = point.y;
            }
            return false;
        });

        holder.cardCV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                holder.cardCV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = holder.cardCV.getWidth();
                height = holder.cardCV.getHeight();
            }
        });


        holder.cardCV.setOnLongClickListener(view -> {
            setPopUpWindow(position);
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.popup, null);

            mypopupWindow.showAsDropDown(v, tx, ty);
            return true;
        });


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    if (dsp.hasChild("productQuantity")) {
                        valueQuantity = (long) dsp.child("productQuantity").getValue();
                        Log.d("FireBase Snapshot", dsp.getKey() + ", " + valueQuantity);

                        for (Product product : productArrayList)
                            if (product.getproductId().equals(dsp.getKey())) {
                                Log.d("productArrayList id", product.getproductId());
                                subTotal += (valueQuantity * product.getproductPrice());
                            }
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
            recyclerData = productArrayList.get(position);
            view.startAnimation(AnimationUtils.loadAnimation(mcontext, R.anim.image_click));

            mRef.child(recyclerData.getproductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("productQuantity")) {
                        valueQuantity = (long) snapshot.child("productQuantity").getValue();
                        Log.d("FireBase Snapshot", "" + valueQuantity);
                        cartFirebase.updateCart(mcontext, recyclerData.getproductId(), valueQuantity+1);
                    } else {
                        cartFirebase.addCart(mcontext, new Cart(recyclerData.getproductId(), 1));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("FireBase Err", error.getDetails());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName, itemPrice, itemDesc;
        private final ImageView itemImage, itemManufacter;
        private final CircularProgressIndicator progressIndicator;
        private final MaterialCardView cardCV;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemImage = itemView.findViewById(R.id.item_image);
            itemManufacter = itemView.findViewById(R.id.item_brand);
            itemDesc = itemView.findViewById(R.id.item_desc);
            progressIndicator = itemView.findViewById(R.id.progress);
            cardCV = itemView.findViewById(R.id.cardCV);
        }
    }

    private void setPopUpWindow(int position) {
        recyclerData = productArrayList.get(position);

        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup, null);
        mypopupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        view.findViewById(R.id.add).setOnClickListener(v -> {
            Log.d("Pos", String.valueOf(position));
            mypopupWindow.dismiss();
            mPosition.receivePosition(position, 2, new Intent(mcontext, TambahBarang.class), recyclerData);
        });

        view.findViewById(R.id.upd).setOnClickListener(v -> {
            Log.d("Pos", String.valueOf(position));
            mypopupWindow.dismiss();
            mPosition.receivePosition(position, 2, new Intent(mcontext, UpdateBarang.class), recyclerData);
        });

        view.findViewById(R.id.dlt).setOnClickListener(v -> {
            alertDialog = new MaterialAlertDialogBuilder(mcontext, R.style.MaterialAlertDialog_Rounded).create();
            customAlertDialogView = inflater.inflate(R.layout.menu_dialog, null, false);
            alertDialog.setView(customAlertDialogView.findViewById(R.id.root));
            alertDialog.show();
            appCompatImageView = customAlertDialogView.findViewById(R.id.appCompatImageView);
            textAlert = customAlertDialogView.findViewById(R.id.text_alert);
            exec = customAlertDialogView.findViewById(R.id.exec);

            appCompatImageView.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.circle_rounded_filled_red));
            textAlert.setText(mcontext.getResources().getString(R.string.delete_product));
            exec.setBackgroundColor(mcontext.getResources().getColor(android.R.color.holo_red_light));
            exec.setText(mcontext.getResources().getString(R.string.delete));
            customAlertDialogView.findViewById(R.id.exec).setOnClickListener(view2 -> {
                alertDialog.dismiss();
                mPosition.receivePosition(position, 3, new Intent(mcontext, HomeActivity.class), recyclerData);
            });
            mypopupWindow.dismiss();
        });
    }

    private Point getPointOfView(View view) {
        location = new int[2];
        view.getLocationInWindow(location);
        return new Point(location[0], location[1]);
    }

}
