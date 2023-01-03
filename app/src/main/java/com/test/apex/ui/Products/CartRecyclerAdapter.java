package com.test.apex.ui.Products;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.apex.R;
import com.test.apex.ReceiveCartPosition;
import com.test.apex.SharedPrefManager;
import com.test.apex.Cart;
import com.test.apex.User;
import com.test.apex.database.CartFirebase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.RecyclerViewHolder> {

    private static final Locale locale = new Locale("id", "ID");
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Cart");
    private DatabaseReference pRef;
    private final ArrayList<Cart> recyclerDataArrayList;
    private final Context mcontext;
    private final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
    private String productName, productImage;
    private Long productPrice;
    ReceiveCartPosition mPosition;
    Product newProduct = new Product();
    CartFirebase cartFirebase = new CartFirebase();
    boolean isCancel = false;
    long subTotal = 0;
    long valueQuantity = 0;

    public CartRecyclerAdapter(ArrayList<Cart> recyclerDataArrayList, Context mcontext, ReceiveCartPosition listener) {
        this.recyclerDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.mPosition = listener;

    }

    @NonNull
    @Override
    public CartRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_recycle, parent, false);
        return new CartRecyclerAdapter.RecyclerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull CartRecyclerAdapter.RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = SharedPrefManager.getInstance(mcontext).getUser();
        String userID = String.valueOf(user.getId());
        DatabaseReference mRef = mDatabase.child(userID);

        Cart cart = recyclerDataArrayList.get(position);
        format.setMaximumFractionDigits(0);
        ArrayList<Product> productList = SharedPrefManager.getInstance(mcontext).loadProductList();

        if (!containProduct(productList, cart.getproductId())) {
            cartFirebase.deleteCart(mcontext, cart.getproductId());
        } else {
            holder.itemName.setText(newProduct.getProductName());
            holder.itemPrice.setText(format.format(newProduct.getproductPrice() * cart.getproductQuantity()));
            Glide.with(mcontext).load(newProduct.getproductImage()).into(holder.itemImage);
            holder.itemQty.setText(String.valueOf(cart.getproductQuantity()));

            holder.minus.setOnClickListener(view -> {
                mPosition.receivePosition(position, new Cart(cart.getproductId(), cart.getproductQuantity()-1));
                setQuantity(mRef, cart.getproductId(), -1);
            });

            holder.plus.setOnClickListener(view -> {
                mPosition.receivePosition(position, new Cart(cart.getproductId(), cart.getproductQuantity()+1));
                setQuantity(mRef, cart.getproductId(), 1);
            });

            holder.delete.setOnClickListener(view -> {
                mPosition.receivePosition(position, cart);
                cartFirebase.deleteCart(mcontext, cart.getproductId());
                isCancel = true;
                holder.remove.setVisibility(View.GONE);
                holder.layoutBtn.setVisibility(View.VISIBLE);
            });

            holder.remove.setOnClickListener(view -> {
                mPosition.receivePosition(position, cart);
                cartFirebase.deleteCart(mcontext, cart.getproductId());
                isCancel = true;
                holder.remove.setVisibility(View.GONE);
                holder.layoutBtn.setVisibility(View.VISIBLE);
            });

            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dsp : snapshot.getChildren()) {
                        if (dsp.hasChild("productQuantity")) {
                            valueQuantity = (long) dsp.child("productQuantity").getValue();

                            for (Product product : productList)
                                if (product.getproductId().equals(dsp.getKey())) {
                                    subTotal += (valueQuantity * product.getproductPrice());
                                }
                        }
                    }

                    mPosition.receiveSubTotal(subTotal);
                    subTotal = 0;


                    if (cart.getproductQuantity() == 0) {

                        holder.remove.setVisibility(View.VISIBLE);
                        holder.layoutBtn.setVisibility(View.GONE);

                        new CountDownTimer(6000, 1000) {
                            @SuppressLint("SetTextI18n")
                            public void onTick(long millisUntilFinished) {
                                holder.remove.setText("Remove " + millisUntilFinished / 1000);
                                if (isCancel) {
                                    cancel();
                                    isCancel = false;
                                }
                            }
                            public void onFinish() {
                                mPosition.receivePosition(position, cart);
                                cartFirebase.deleteCart(mcontext, cart.getproductId());
                                holder.remove.setVisibility(View.GONE);
                                holder.layoutBtn.setVisibility(View.VISIBLE);
                            }
                        }.start();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("FireBase Err", error.getDetails());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return recyclerDataArrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView itemName, itemPrice, itemQty;
        private final MaterialButton minus, plus, delete, remove;
        private final ImageView itemImage;
        private final LinearLayout layoutBtn;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.img);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemQty = itemView.findViewById(R.id.item_qty);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.plus);
            delete = itemView.findViewById(R.id.del);
            remove = itemView.findViewById(R.id.remove);
            layoutBtn = itemView.findViewById(R.id.layoutBtn);

        }
    }

    public boolean containProduct(ArrayList<Product> productList, String productId) {
        for(Product product : productList) {
            if(product.getproductId().equals(productId)) {
                newProduct = product;
                return true;
            }
        }
        return false;
    }

    public void setQuantity(DatabaseReference mRef, String productId, int change) {
        mRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("productQuantity")) {
                    valueQuantity = (long) snapshot.child("productQuantity").getValue();
                    cartFirebase.updateCart(mcontext, productId, valueQuantity + change);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FireBase Err", error.getDetails());
            }
        });
    }
}
