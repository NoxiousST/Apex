package com.test.apex.database;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.apex.SharedPrefManager;
import com.test.apex.Cart;
import com.test.apex.User;

import java.util.Objects;

public class CartFirebase {

    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Cart");
    private long qty;

    public CartFirebase() {
        //this constructor is required
    }

    public void addCart(Context context, Cart newCart) {
        User user = SharedPrefManager.getInstance(context).getUser();
        String userID = String.valueOf(user.getId());
        DatabaseReference mRef = mDatabase.child(userID).child(newCart.getproductId());

        mRef.child("productId").setValue(newCart.getproductId());
        mRef.child("productQuantity").setValue(newCart.getproductQuantity());

    }

    public void updateCart(Context context, String productId, long productQuantity) {
        User user = SharedPrefManager.getInstance(context).getUser();
        String userID = String.valueOf(user.getId());

        mDatabase.child(userID).child(productId).child("productQuantity").setValue(productQuantity);
    }

    public void deleteCart(Context context, String productId) {
        User user = SharedPrefManager.getInstance(context).getUser();
        String userID = String.valueOf(user.getId());
        mDatabase.child(userID).child(productId).removeValue();
        Log.d("Delete", "Deleted");
    }

    public void deleteAllCart(String productId) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child(productId).exists())
                        mDatabase.child((String) Objects.requireNonNull(snapshot.getValue())).child(productId).removeValue();

                }
                mDatabase.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public long readItemQuantity(Context context, String productId) {
        User user = SharedPrefManager.getInstance(context).getUser();
        String userID = String.valueOf(user.getId());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userID).exists())
                    if (dataSnapshot.child(userID).child(productId).exists()) qty = (long) dataSnapshot.child(userID).child(productId).child("productQuantity").getValue();
                    else qty = 0L;
                else qty = 0L;
                mDatabase.removeEventListener(this);
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabase.addValueEventListener(postListener);
        return qty;
    }

}
