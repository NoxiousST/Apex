package com.test.apex.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.test.apex.R;
import com.test.apex.ReceiveDeliveryAmount;
import com.test.apex.data.cost.DataType;
import com.test.apex.network.Helper;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Azhar Rivaldi on 25-12-2020
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private static final Locale locale = new Locale("id", "ID");
    private static final NumberFormat format = NumberFormat.getNumberInstance(locale);
    Context context;
    List<DataType> data;
    List<String> courier;
    int imgLogo;
    View oldView;
    ReceiveDeliveryAmount mCallback;

    public MainAdapter(Context context, List<DataType> data, List<String> courier, ReceiveDeliveryAmount listener) {
        this.context = context;
        this.data = data;
        this.courier = courier;
        mCallback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        format.setMaximumFractionDigits(0);
        String strLogo = courier.get(position);

        if (strLogo.equals("JNE"))
            imgLogo = R.drawable.logo_jne;
        else if (strLogo.equals("POS"))
            imgLogo = R.drawable.logo_pos;
        else if (strLogo.equals("TIKI"))
            imgLogo = R.drawable.logo_tiki;

        holder.imgLogoKurir.setImageResource(imgLogo);
        holder.tvType.setText("(" + data.get(position).getService()+")");
        holder.tvPrice.setText("Rp. " + format.format(data.get(position).getCost().get(0).getValue()));
        String day = data.get(position).getCost().get(0).getEtd().replaceAll("[a-zA-Z]", "");
        holder.tvEst.setText(day + " Hari");

        holder.viewLayout.setOnClickListener(view -> {
            if (oldView != null) oldView.setBackground(ContextCompat.getDrawable(context, R.drawable.layout_rounded_radius_none));
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.layout_rounded_radius_blue));
            oldView = view;
            mCallback.getPengiriman(data.get(position).getCost().get(0).getValue());
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEst;
        TextView tvPrice;
        TextView tvType;
        ImageView imgLogoKurir;
        View viewLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEst = itemView.findViewById(R.id.tvEst);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvType = itemView.findViewById(R.id.tvType);
            imgLogoKurir = itemView.findViewById(R.id.imgLogo);
            viewLayout = itemView.findViewById(R.id.view);
        }
    }
}
