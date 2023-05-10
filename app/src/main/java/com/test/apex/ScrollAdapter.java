package com.test.apex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;
import com.test.apex.network.ServerAPI;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ScrollAdapter extends RecyclerView.Adapter<ScrollAdapter.ScrollViewHolder> {
    private static final Locale locale = new Locale("id", "ID");
    private final ArrayList<Product> mainArrayList;
    private final Context mcontext;
    NumberFormat format = NumberFormat.getCurrencyInstance(locale);

    public ScrollAdapter(Context mcontext, ArrayList<Product> recyclerDataArrayList) {
        this.mainArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public ScrollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scroll_recycle, parent, false);
        return new ScrollViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ScrollViewHolder holder, int position) {

        Product product = mainArrayList.get(position);

        String url = ServerAPI.URL_PRODUCT_IMAGE + product.getproductImage();
        Glide.with(mcontext).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.itemName.setText(product.getProductName());
                holder.itemPrice.setText(format.format(product.getproductPrice()));
                return false;
            }
        }).into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return mainArrayList.size();
    }

    public static class ScrollViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName, itemPrice;
        private final ImageView itemImage, itemBrand;
        private final MaterialCardView cardCV;

        public ScrollViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemImage = itemView.findViewById(R.id.item_image);
            itemBrand = itemView.findViewById(R.id.item_brand);
            cardCV = itemView.findViewById(R.id.cardCV);
        }
    }

    public int dpToPx(int dip) {
        Resources r = mcontext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
    }
}
