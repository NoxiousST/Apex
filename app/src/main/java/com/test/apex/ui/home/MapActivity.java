package com.test.apex.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.schibstedspain.leku.LocationPickerActivity;
import com.test.apex.R;
import com.test.apex.ReceiveDeliveryAmount;
import com.test.apex.data.city.DataCity;
import com.test.apex.data.city.ResponseCity;
import com.test.apex.data.cost.DataType;
import com.test.apex.databinding.ActivityMapBinding;
import com.test.apex.network.Api;
import com.test.apex.network.ApiEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MapActivity extends AppCompatActivity implements MainContract.View, ReceiveDeliveryAmount {

    private static final String API_KEY = "";
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private static final int REQUEST_DESTINATION = 2;

    private String destination_id = "";

    private MainPresenter presenter;
    private MainAdapter adapter;

    private List<DataType> data = new ArrayList<>();
    private List<String> courier = new ArrayList<>();
    List<DataCity> dataCityList = new ArrayList<>();
    List<String> setMyCity = new ArrayList<>();
    ActivityMapBinding binding;
    LinearLayout llMain;
    RecyclerView rvMain;
    LinearProgressIndicator progressBar;
    ApiEndpoint endpoint;
    AutoCompleteTextView inputKotaTujuan;
    TextView mapAddress;
    MaterialButton setMap;

    String mapJalan;
    String mapNoJalan;
    String mapKecamatan;
    String mapKota;
    String mapProvinsi;
    String mapPostal;
    String mapNegara;
    String fullAlamat;
    long pengiriman;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inputKotaTujuan = findViewById(R.id.inputKotaTujuan);
        rvMain = findViewById(R.id.rvMain);
        progressBar = findViewById(R.id.indicator);
        mapAddress = findViewById(R.id.mapAddress);
        setMap = findViewById(R.id.setMap);

        endpoint = Api.getUrl().create(ApiEndpoint.class);

        Intent locationPickerActivity = new LocationPickerActivity.Builder()
                .withGeolocApiKey(API_KEY)
                .withGooglePlacesApiKey(API_KEY)
                .withSatelliteViewHidden()
                .withVoiceSearchHidden()
                .withLegacyLayout()
                .withSearchZone("id_ID")
                .withDefaultLocaleSearchZone()
                .withMapStyle(R.raw.raw_map)
                .build(this);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle bundle = result.getData().getExtras();
                        if (bundle != null) {
                            for (String key : bundle.keySet()) {
                                Log.d("KEY LIST", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                            }
                        }
                        Address fullAddress = result.getData().getParcelableExtra("address");
                        mapJalan = fullAddress.getThoroughfare();
                        mapNoJalan = fullAddress.getFeatureName();
                        mapKecamatan = fullAddress.getLocality();
                        mapKota = fullAddress.getSubAdminArea();
                        mapProvinsi = fullAddress.getAdminArea();
                        mapNegara = fullAddress.getCountryName();
                        mapPostal = fullAddress.getPostalCode();
                        fullAlamat = mapJalan + " No. " + mapNoJalan + ", " + mapKecamatan + ", " + mapKota + ", " + mapProvinsi + ", " + mapNegara + ", " + mapPostal;
                        mapAddress.setText(fullAlamat);

                        for (DataCity dataCity : dataCityList) {
                            String kota = dataCity.getType() + " " + dataCity.getCityName();
                            if (kota.equals(mapKota) || kota.contains(mapKota)) {
                                inputKotaTujuan.setText(dataCity.getCityName() + ", " + dataCity.getProvince());
                                destination_id = dataCity.getCityId();
                                getCourier();
                            }
                        }

                    }
                });

        getAllCity();
        activityResultLauncher.launch(locationPickerActivity);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, setMyCity);
        inputKotaTujuan.setAdapter(myAdapter);
        inputKotaTujuan.setThreshold(1);

        presenter = new MainPresenter(this);

        adapter = new MainAdapter(this, data, courier, this);
        rvMain.setAdapter(adapter);
        rvMain.setLayoutManager(new LinearLayoutManager(this));

        inputKotaTujuan.setOnItemClickListener((parent, view, position, rowId) -> {
            String selection = (String)parent.getItemAtPosition(position);
            Log.d("AutoComplete", String.valueOf(position)+", "+selection);
            Log.d("ListCity", dataCityList.get(position).getCityName());

            data.clear();
            courier.clear();
            presenter.setupENV(getOrigin(), "501", 1000);
        });

        setMap.setOnClickListener(view -> {
            activityResultLauncher.launch(locationPickerActivity);
        });

        binding.confirmButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("alamat", fullAlamat);
            intent.putExtra("pengiriman", pengiriman);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void getAllCity() {
        endpoint.getCity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseCity>() {
                    @Override public void onSubscribe(Disposable d) {}

                    @Override
                    public void onSuccess(ResponseCity responseCity) {
                        dataCityList.addAll(responseCity.getRajaongkir().getResults());
                        for (DataCity dataCity : dataCityList)
                            setMyCity.add(dataCity.getCityName() + ", " + dataCity.getProvince());

                    }

                    @Override public void onError(Throwable e) {}
                });
    }

    private void getCourier() {
        data.clear();
        courier.clear();
        presenter.setupENV(getOrigin(), getDestination(), 1000);
    }

    @Override
    public void onLoadingCost(boolean loadng, int progress) {
        if (loadng) {
            progressBar.show();
        } else {
            progressBar.hide();
        }
    }

    @Override
    public void onResultCost(List<DataType> data, List<String> courier) {
        this.data.addAll(data);
        this.courier.addAll(courier);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorCost() {
        rvMain.setVisibility(View.GONE);
        llMain.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getOrigin() {
        return "76";
    }

    @Override
    public String getDestination() {
        return destination_id;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DESTINATION && resultCode == RESULT_OK) {
            inputKotaTujuan.setText(data.getStringExtra("city"));
            destination_id = data.getStringExtra("city_id");
        }
    }

    @Override
    public void getPengiriman(long pengiriman) {
        this.pengiriman = pengiriman;
    }
}
