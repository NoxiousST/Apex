package com.test.apex.ui.search;

import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.test.apex.R;
import com.test.apex.data.city.DataCity;
import com.test.apex.data.city.ResponseCity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchCityActivity extends AppCompatActivity implements SearchCityContract.View {

    List<DataCity> dataSumber = new ArrayList<>();
    List<DataCity> dataList = new ArrayList<>();
    List<DataCity> dataSearch = new ArrayList<>();

    SearchCityAdapter adapter;
    SearchCityPresenter presenter;

    EditText etSearch;
    SwipeRefreshLayout refreshCity;
    RecyclerView rvCity;
    ImageView ivBarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        etSearch = findViewById(R.id.etSearch);
        refreshCity = findViewById(R.id.refreshCity);
        rvCity = findViewById(R.id.rvCity);
        ivBarBack = findViewById(R.id.ivBarBack);

        adapter = new SearchCityAdapter(this, dataList);
        rvCity.setLayoutManager(new LinearLayoutManager(this));
        rvCity.setAdapter(adapter);

        presenter = new SearchCityPresenter(this);

        refreshCity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getCity();
            }
        });

        ivBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getCity();
    }

    @Override
    public void onLoadingSearch(boolean loading) {
        if (loading) {
            refreshCity.setRefreshing(true);
        } else {
            refreshCity.setRefreshing(false);
        }
    }

    @Override
    public void onResultSearch(ResponseCity response) {
        dataSumber.clear();
        dataList.clear();
        dataSumber.clear();
        if (response.getRajaongkir().getResults().size() != 0) {
            dataSumber.addAll(response.getRajaongkir().getResults());
            dataList.addAll(response.getRajaongkir().getResults());

            adapter.notifyDataSetChanged();
            presenter.instantSearch(etSearch, dataSumber);
            Log.d("dataSumber", dataSumber.get(1).getCityName());
        }
    }

    @Override
    public void onErrorSearch() {
        refreshCity.setRefreshing(false);
    }

    @Override
    public void onResultInstantSearch(List<DataCity> data) {
        if (data.size() != 0) {
            dataSearch.clear();
            dataList.clear();
            dataSearch.addAll(data);
            dataList.addAll(data);

            adapter.notifyDataSetChanged();
        } else {
            dataList.clear();
            dataList.addAll(dataSumber);

            adapter.notifyDataSetChanged();
        }
        Log.d("dataSumber", dataSumber.get(1).getCityName());
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
