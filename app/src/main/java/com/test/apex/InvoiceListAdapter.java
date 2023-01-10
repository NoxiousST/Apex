package com.test.apex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.RecyclerViewHolder> {

    private static final Locale locale = new Locale("id", "ID");
    private final ArrayList<Cart> cartArrayList;
    private final Context mcontext;
    NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    private Cart invItem;

    public InvoiceListAdapter(ArrayList<Cart> cartArrayList, Context mcontext) {
        this.cartArrayList = cartArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_item_list, parent, false);
        return new RecyclerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        format.setMaximumFractionDigits(0);
        invItem = cartArrayList.get(position);
        ArrayList<Product> productList = SharedPrefManager.getInstance(mcontext).loadProductList();

        for(Product product : productList) {
            if (product.getproductId().equals(invItem.getproductId())) {
                holder.itemName.setText(product.getProductName());
                holder.itemQuantity.setText(invItem.getproductQuantity() + " x");
                holder.itemPrice.setText(format.format(product.getproductPrice()));
                holder.totalOfItem.setText(format.format(invItem.getproductQuantity() * product.getproductPrice()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName, itemQuantity, itemPrice, totalOfItem;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            totalOfItem = itemView.findViewById(R.id.totalOfItem);
        }
    }



}
