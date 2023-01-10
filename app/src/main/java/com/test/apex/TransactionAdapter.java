package com.test.apex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.divider.MaterialDivider;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.RecyclerViewHolder> {

    private static final Locale locale = new Locale("id", "ID");
    private final ArrayList<Transaction> transactionArrayList;
    private final Context mcontext;
    NumberFormat format = NumberFormat.getNumberInstance(locale);
    Transaction transaction;
    ObjectMapper mapper = new ObjectMapper();
    private ArrayList<Cart> cartList;
    String productImage;
    ReceiveTransactionPosition callback;


    public TransactionAdapter(ArrayList<Transaction> transactionArrayList, Context mcontext, ReceiveTransactionPosition listener) {
        this.transactionArrayList = transactionArrayList;
        this.mcontext = mcontext;
        callback = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_transaction, parent, false);
        return new RecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        format.setMaximumFractionDigits(0);
        transaction = transactionArrayList.get(position);
        ArrayList<Product> productList = SharedPrefManager.getInstance(mcontext).loadProductList();
        StringBuilder sb = new StringBuilder();
        int totalItems = 0;

        if (transaction.getTransactionStatus().equals("Pending")) {
            holder.transactionStatus.setBackgroundColor(mcontext.getResources().getColor(android.R.color.holo_orange_light));
            holder.leftBorder.setDividerColor(mcontext.getResources().getColor(android.R.color.holo_orange_light));
        }
        holder.transactionDate.setText(transaction.getInvoiceDate());
        holder.transactionStatus.setText(transaction.getTransactionStatus());

        JsonNode node = null;
        try {node = mapper.readTree(transaction.getProducts());
        } catch (JsonProcessingException e) {e.printStackTrace();}
        ObjectReader reader = mapper.readerFor(new TypeReference<List<Cart>>() {});

        try {cartList = reader.readValue(node);}
        catch (IOException e) {e.printStackTrace();}

        for (Cart cart : cartList) {
            for(Product product : productList) {
                if (cart.getproductId().equals(product.getproductId())) {
                    sb.append(product.getProductName()).append(", ");
                    productImage = product.getproductImage();
                    totalItems += cart.getproductQuantity();
                }
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        holder.allProductName.setText(sb);

        Glide.with(mcontext).load(productImage).into(holder.image);
        holder.totalPrice.setText("Rp. " + format.format(transaction.getTransactionAmount()) + "("+totalItems+" items)");

        holder.root.setOnClickListener(view -> {
            callback.receivePosition(position);
        });



    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView transactionDate, transactionStatus, allProductName, totalPrice;
        private final ImageView image;
        private final MaterialDivider leftBorder;
        private final MaterialCardView root;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionDate = itemView.findViewById(R.id.transactionDate);
            transactionStatus = itemView.findViewById(R.id.transactionStatus);
            allProductName = itemView.findViewById(R.id.allProductName);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            image = itemView.findViewById(R.id.image);
            leftBorder = itemView.findViewById(R.id.leftBorder);
            root = itemView.findViewById(R.id.root);
        }
    }



}
