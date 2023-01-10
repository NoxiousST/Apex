package com.test.apex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.apex.database.TransactionDatabase;
import com.test.apex.databinding.ActivityTransactionBinding;


import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TransactionActivity extends AppCompatActivity implements ReceiveTransactionPosition {

    ObjectMapper mapper = new ObjectMapper();
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();

    ActivityTransactionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progress.show();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.itemRV.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        readTransactions();
    }

    private void refreshList(ArrayList<Transaction> list) {
        TransactionAdapter adapter = new TransactionAdapter(list, TransactionActivity.this, this);
        binding.itemRV.setAdapter(adapter);
    }

    private void readTransactions() {
        new TransactionDatabase(getApplicationContext(), new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                Log.d("Response", "onSuccess: " + res);
                binding.progress.hide();
                try {
                    JsonNode node = mapper.readTree(res).get("transactions");
                    Log.d("Response ID", "onSuccess: " + node.toString());

                    CollectionType typeReference = TypeFactory.defaultInstance().constructCollectionType(List.class, Transaction.class);
                    List<Transaction> transactionList = mapper.readValue(node.toString(), typeReference);

                    for (Transaction tr : transactionList) {
                        Log.d("Response ID", "onSuccess: " + tr.toMap());
                    }

                    transactionArrayList = new ArrayList<>(transactionList);
                    refreshList(transactionArrayList);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                binding.progress.hide();
                Snackbar.make(binding.getRoot(), error, 4800).
                        setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                        setTextColor(getResources().getColor(R.color.whiteAccent)).show();
            }
        });
    }

    @Override
    public void receivePosition(int listPosition) {
        Intent intent = new Intent(TransactionActivity.this, InvoiceActivity.class);
        intent.putExtra("transactionInvoice", transactionArrayList.get(listPosition));
        startActivity(intent);
    }
}
