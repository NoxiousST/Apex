package com.test.apex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.test.apex.ui.Products.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ScrollAdapter extends RecyclerView.Adapter<ScrollAdapter.ScrollViewHolder> {
    private static final Locale locale = new Locale("id", "ID");
    private final ArrayList<Product> mainArrayList;
    private final Context mcontext;
    NumberFormat format = NumberFormat.getCurrencyInstance(locale);

    public ScrollAdapter(ArrayList<Product> recyclerDataArrayList, Context mcontext) {
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

        Product recyclerData = mainArrayList.get(position);
        holder.itemName.setText(recyclerData.getProductName());
        holder.itemPrice.setText(format.format(recyclerData.getproductPrice()));
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
