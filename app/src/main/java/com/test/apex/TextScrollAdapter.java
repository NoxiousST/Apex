package com.test.apex;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class TextScrollAdapter extends RecyclerView.Adapter<TextScrollAdapter.ScrollViewHolder> {

    private ArrayList<RecyclerText> mainArrayList;
    private Context mcontext;
    private String conTxt = "", mnfTxt = "", wghTxt = "", clrTxt = "", stkTxt = "", option;
    private final GetProductDetail mCallback;
    private ArrayAdapter<String> adapter;
    private InputMethodManager imm;
    private ArrayList<String> detail;
    private boolean change = false;

    public TextScrollAdapter(ArrayList<RecyclerText> recyclerDataArrayList, Context mcontext, GetProductDetail listener, ArrayList<String> detail) {
        this.mainArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.mCallback = listener;
        this.detail = detail;
    }

    @NonNull
    @Override
    public ScrollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.textinput_recycle, parent, false);
        return new ScrollViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ScrollViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setIsRecyclable(false);
        imm = (InputMethodManager) mcontext.getSystemService(Context.INPUT_METHOD_SERVICE);
        ArrayList<String> condition = new ArrayList<>();
        condition.add("New");
        condition.add("Used");

        ArrayList<String> manufacter = new ArrayList<>();
        manufacter.add("SteelSeries");
        manufacter.add("Razer");
        Toast.makeText(mcontext, ""+position, Toast.LENGTH_SHORT).show();
        if (!change) {
            conTxt = detail.get(0);
            mnfTxt = detail.get(1);
            wghTxt = detail.get(2);
            clrTxt = detail.get(3);
            stkTxt = detail.get(4);
            change = true;
        }

        holder.inputTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (position == 2) wghTxt = String.valueOf(Objects.requireNonNull(holder.inputTxt.getText()));
                else if (position == 3) clrTxt = String.valueOf(Objects.requireNonNull(holder.inputTxt.getText()));
                else if (position == 4) stkTxt = String.valueOf(Objects.requireNonNull(holder.inputTxt.getText()));
                mCallback.getDetail(conTxt, mnfTxt, wghTxt, clrTxt, stkTxt);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        switch (position) {
            case 0:
                holder.menu.setVisibility(View.VISIBLE);
                holder.inputLayout.setVisibility(View.GONE);
                holder.menu.setHint(R.string.eCon);
                holder.actv.setInputType(InputType.TYPE_NULL);

                imm.showSoftInput(holder.actv, InputMethodManager.SHOW_FORCED);
                adapter = new ArrayAdapter<>(mcontext, R.layout.dropdown_item, condition);
                holder.actv.setAdapter(adapter);
                holder.actv.setText(conTxt, false);
                break;
            case 1:
                holder.menu.setVisibility(View.VISIBLE);
                holder.inputLayout.setVisibility(View.GONE);
                holder.menu.setHint(R.string.eMnf);

                imm.showSoftInput(holder.actv, InputMethodManager.SHOW_FORCED);
                adapter = new ArrayAdapter<>(mcontext, R.layout.dropdown_item, manufacter);
                holder.actv.setAdapter(adapter);
                holder.actv.setText(mnfTxt, false);
                break;
            case 2:
                holder.menu.setVisibility(View.GONE);
                holder.inputLayout.setVisibility(View.VISIBLE);
                holder.inputLayout.setHint(R.string.eWgh);
                holder.inputLayout.setSuffixText("gram");
                imm.showSoftInput(holder.inputTxt, InputMethodManager.SHOW_FORCED);
                holder.inputTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                holder.inputTxt.setText(detail.get(2));
                break;
            case 3:
                holder.menu.setVisibility(View.GONE);
                holder.inputLayout.setVisibility(View.VISIBLE);
                holder.inputLayout.setHint(R.string.eClr);
                holder.inputLayout.setSuffixText(null);
                imm.showSoftInput(holder.inputTxt, InputMethodManager.SHOW_FORCED);
                holder.inputTxt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                holder.inputTxt.setText(detail.get(3));
                break;
            case 4:
                holder.menu.setVisibility(View.GONE);
                holder.inputLayout.setVisibility(View.VISIBLE);
                holder.inputLayout.setHint(R.string.eStk);
                holder.inputLayout.setSuffixText(null);
                imm.showSoftInput(holder.inputTxt, InputMethodManager.SHOW_FORCED);
                holder.inputTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                holder.inputTxt.setText(detail.get(4));
                break;
        }


        holder.actv.setOnItemClickListener((parent, view, position1, id) -> {
            if (position == 0) conTxt = holder.actv.getText().toString();
            else if (position == 1) mnfTxt = holder.actv.getText().toString();
            mCallback.getDetail(conTxt, mnfTxt, wghTxt, clrTxt, stkTxt);
        });
    }

    @Override
    public int getItemCount() {
        return mainArrayList.size();
    }

    public static class ScrollViewHolder extends RecyclerView.ViewHolder {

        private final TextInputLayout menu, inputLayout;
        private final TextInputEditText inputTxt;
        private final AutoCompleteTextView actv;

        public ScrollViewHolder(@NonNull View itemView) {
            super(itemView);
            menu = itemView.findViewById(R.id.menu);
            inputLayout = itemView.findViewById(R.id.inputLayout);
            inputTxt = itemView.findViewById(R.id.inputTxt);
            actv = itemView.findViewById(R.id.actv);
        }
    }

}
