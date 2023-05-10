package com.test.apex;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.robinhood.ticker.TickerView;
import com.test.apex.databinding.ActivityHomeBinding;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements ReceiveSubtotal {

    private ArrayList<Product> productArrayList;
    private ArrayList<Product> contextArrayList;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ScrollAdapter scrollAdapter;
    private ProductRecycler adapter;
    private User user = new User();
    private View headView;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private boolean active = false;
    private static final Locale locale = new Locale("id", "ID");
    NumberFormat format = NumberFormat.getNumberInstance(locale);
    ObjectMapper mapper = new ObjectMapper();

    private ActivityHomeBinding binding;
    private int listPosition;
    private Product product;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.topAppBar.setOnClickListener(view -> binding.root.open());
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.cart:
                    startActivity(new Intent(HomeActivity.this, KeranjangActivity.class));
                    return false;
                case R.id.history:
                    startActivity(new Intent(HomeActivity.this, TransactionActivity.class));
                    return false;
                case R.id.logout:
                    if (MainActivity.toLogout(this, user.getLoginOption())) {
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                        return false;
                    }
                    break;
                default:
                    break;
            }
            return true;
        });

        user = SharedPrefManager.getInstance(HomeActivity.this).getUser();
        headView = binding.navigationView.getHeaderView(0);
        ((TextView) headView.findViewById(R.id.displayUsernmae)).setText(user.getUsername());
        ((TextView) headView.findViewById(R.id.displayEmail)).setText(user.getEmail());

        binding.topAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.call:
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:0123456789"));
                    startActivity(callIntent);
                    return true;
                case R.id.msg:
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.setData(Uri.parse("smsto:0123456789"));
                    smsIntent.putExtra("sms_body", "Hello There");
                    startActivity(smsIntent);
                    return true;
                case R.id.map:
                    String getMap = String.format(Locale.ENGLISH, "geo:%f,%f", -7.4007514414175715, 110.68323302612266);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getMap));
                    startActivity(mapIntent);
                    return true;
                case R.id.upd:
                    Intent updIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(updIntent);
                    return true;
            }
            return false;
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.shimmerTop.startShimmer();
            binding.shimmerTop.setVisibility(View.VISIBLE);
            binding.shimmerProduct.startShimmer();
            binding.shimmerProduct.setVisibility(View.VISIBLE);

            productArrayList.clear();
            binding.productRecycler.setVisibility(View.GONE);

            contextArrayList.clear();
            binding.contextRecycler.setVisibility(View.GONE);

            refresh();
        });
        refresh();

        binding.viewCart.setOnClickListener(view -> {
            ValueAnimator anim;
            if (!active) {
                anim = ValueAnimator.ofInt(0, dpToPx(360));
                view.animate().rotation(360f).setDuration(300).start();
                active = true;
            } else {
                anim = ValueAnimator.ofInt(dpToPx(360), 0);
                view.animate().rotation(0f).setDuration(300).start();
                active = false;
            }

            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = binding.layoutBtn.getLayoutParams();
                layoutParams.width = val;
                binding.layoutBtn.setLayoutParams(layoutParams);
            });
            anim.setDuration(450);
            anim.start();

        });

        binding.toCart.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, KeranjangActivity.class)));
    }

    private void showTopList() {
        contextArrayList = new ArrayList<>();
        contextArrayList.add(productArrayList.get(0));
        contextArrayList.add(productArrayList.get(2));
        contextArrayList.add(productArrayList.get(4));

        scrollAdapter = new ScrollAdapter(this, contextArrayList);
        binding.contextRecycler.setAdapter(scrollAdapter);

        binding.contextRecycler.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(binding.contextRecycler);
    }

    private void refresh() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<Product>> call = apiInterface.retrieveProducts();

        call.enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Product>> call, @NonNull Response<ArrayList<Product>> response) {
                if (response.isSuccessful()) {
                    SharedPrefManager.getInstance(getBaseContext()).saveProductList(new ArrayList<>(Objects.requireNonNull(response.body())));
                    binding.shimmerProduct.stopShimmer();
                    binding.shimmerProduct.setVisibility(View.GONE);
                    binding.shimmerTop.stopShimmer();
                    binding.shimmerTop.setVisibility(View.GONE);

                    binding.productRecycler.setVisibility(View.VISIBLE);
                    binding.contextRecycler.setVisibility(View.VISIBLE);

                    productArrayList = SharedPrefManager.getInstance(getBaseContext()).loadProductList();

                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                    binding.productRecycler.setLayoutManager(staggeredGridLayoutManager);

                    adapter = new ProductRecycler(productArrayList, getBaseContext(), HomeActivity.this);
                    binding.productRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    showTopList();
                    binding.swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Product>> call, @NonNull Throwable t) {
                Log.d("Response Fail", "onFail: " + t.getMessage());
                t.printStackTrace();
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void receiveSubTotal(long subTotal) {
        format.setMaximumFractionDigits(0);
        binding.tickerView.setText(format.format(subTotal));
    }


    public int dpToPx(int dip) {
        Resources r = this.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
    }

}