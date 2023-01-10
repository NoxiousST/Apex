package com.test.apex;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.test.apex.network.ServerAPI;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import id.zelory.compressor.Compressor;


public class UpdateBarang extends AppCompatActivity implements GetProductDetail {

    private static final Locale locale = new Locale("id", "ID");
    private static final NumberFormat format = NumberFormat.getCurrencyInstance(locale);

    private TextInputLayout nmLt, tpLt, hrgLt, descLt;
    private TextInputEditText nmTxt, tpTxt, hrgTxt, descTxt;
    private MaterialButton nmBtn, tpBtn, hrgBtn, dtlBtn, descBtn;
    private String id, nmStr = "", tpStr = "", imgUri = "", hrgStr = "", descStr = "", conStr = "", mnfStr = "", wghStr = "", clrStr = "", stkStr = "";

    private RelativeLayout menuBg;
    private ScrollView scrollView;
    private View nmBrgLt, tpBrgLt, hrgBrgLt, dtlBrgLt, descBrgLt, ltDtl, ltDesc, getView = null, customAlertDialogView;
    private ImageView imageView;
    private Bitmap selectedImage;
    private MaterialTextView isSelected;
    private TextView setDesc, con, mnf, wgh, clr, stk;
    private FloatingActionButton save, clear;

    private LinearProgressIndicator indicator;
    private InputMethodManager imm;

    private AlertDialog alertDialog;
    private AppCompatImageView appCompatImageView;
    private TextView textAlert;
    private MaterialButton exec;

    private boolean isKeyboardShowing = false, active = false;
    private int i = 0, imageHeight = 0, layout = 0, oriHeight;
    private static final int RESULT_LOAD_IMG = 200;
    private Long hrgVal, stkVal;
    private Uri imagePath;

    Product recyclerData = new Product();
    private final StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Images");
    private StorageReference sRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_barang);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkBackground));

        nmLt = findViewById(R.id.eNmLt);
        tpLt = findViewById(R.id.eTpLt);
        hrgLt = findViewById(R.id.eHrgLt);
        descLt = findViewById(R.id.eDescLt);
        imageView = findViewById(R.id.img);

        nmTxt = findViewById(R.id.nmTxt);
        tpTxt = findViewById(R.id.tpTxt);
        hrgTxt = findViewById(R.id.hrgTxt);
        descTxt = findViewById(R.id.descTxt);

        nmBtn = findViewById(R.id.nmBtn);
        tpBtn = findViewById(R.id.tpBtn);
        hrgBtn = findViewById(R.id.hrgBtn);
        dtlBtn = findViewById(R.id.dtlBtn);
        descBtn = findViewById(R.id.descBtn);
        setDesc = findViewById(R.id.setDesc);

        nmBrgLt = findViewById(R.id.nmBrgLt);
        tpBrgLt = findViewById(R.id.tpBrgLt);
        hrgBrgLt = findViewById(R.id.hrgBrgLt);
        dtlBrgLt = findViewById(R.id.dtlBrgLt);
        descBrgLt = findViewById(R.id.descBrgLt);

        ltDtl = findViewById(R.id.ltDtl);
        ltDesc = findViewById(R.id.ltDesc);

        con = findViewById(R.id.con);
        mnf = findViewById(R.id.mnf);
        wgh = findViewById(R.id.wgh);
        clr = findViewById(R.id.clr);
        stk = findViewById(R.id.stk);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        scrollView = findViewById(R.id.content);
        isSelected = findViewById(R.id.isSelected);
        indicator = findViewById(R.id.progress);
        indicator.hide();
        menuBg = findViewById(R.id.menuBg);
        save = findViewById(R.id.save);
        clear = findViewById(R.id.clear);

        toDef();

        nmBtn.setOnClickListener(view -> {
            layout = 1;
            onBtnClick(view);
        });
        tpBtn.setOnClickListener(view -> {
            layout = 2;
            onBtnClick(view);
        });
        hrgBtn.setOnClickListener(view -> {
            layout = 3;
            onBtnClick(view);
        });
        dtlBtn.setOnClickListener(view -> {
            layout = 4;
            onBtnClick(view);
        });
        descBtn.setOnClickListener(view -> {
            layout = 5;
            onBtnClick(view);
        });
    
        findViewById(R.id.selectImage).setOnClickListener(view -> {
            if (isKeyboardShowing) imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        });

        final Handler handler = new Handler();
        handler.postDelayed(() -> oriHeight = menuBg.getMeasuredHeight(), 500);
        findViewById(R.id.fabSet).setOnClickListener(view -> {
            ValueAnimator anim;
            if (!active) {
                anim = ValueAnimator.ofInt(oriHeight, 512);
                view.animate().rotation(90f).setDuration(300).start();
                active = true;
            } else {
                anim = ValueAnimator.ofInt(menuBg.getMeasuredHeight(), oriHeight);
                view.animate().rotation(0f).setDuration(300).start();
                active = false;
            }

            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = menuBg.getLayoutParams();
                layoutParams.height = val;
                menuBg.setLayoutParams(layoutParams);
            });
            anim.setDuration(300);
            anim.start();

        });

        save.setOnClickListener(view -> {
            boolean cancel = false;

            if (nmStr.isEmpty()) {
                cancel = true;
                layout = 1;
                onBtnClick(nmBtn);
            }
            else if (tpStr.isEmpty()) {
                cancel = true;
                layout = 2;
                onBtnClick(tpBtn);
            }
            else if (selectedImage == null) {
                cancel = true;
                isSelected.setText("Image required");
                if (isKeyboardShowing) imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
            else if (hrgStr.isEmpty() || hrgVal == 0) {
                cancel = true;
                layout = 3;
                onBtnClick(hrgBtn);
            }
            else if (conStr.isEmpty()) {
                cancel = true;
                layout = 4;
                onBtnClick(dtlBtn);
            }
            else if (mnfStr.isEmpty()) {
                cancel = true;
                layout = 4;
                onBtnClick(dtlBtn);
            }
            else if (wghStr.isEmpty()) {
                cancel = true;
                layout = 4;
                onBtnClick(dtlBtn);
            }
            else if (clrStr.isEmpty()) {
                cancel = true;
                layout = 4;
                onBtnClick(dtlBtn);
            }
            else if (descStr.isEmpty()) {
                cancel = true;
                layout = 5;
                onBtnClick(descBtn);
            }


            if (!cancel) {
                alertDialog = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded).create();
                customAlertDialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.menu_dialog, findViewById(R.id.root), false);
                alertDialog.setView(customAlertDialogView);
                alertDialog.show();

                appCompatImageView = customAlertDialogView.findViewById(R.id.appCompatImageView);
                textAlert = customAlertDialogView.findViewById(R.id.text_alert);
                exec = customAlertDialogView.findViewById(R.id.exec);

                appCompatImageView.setImageDrawable(getResources().getDrawable(R.drawable.circle_rounded_filled));
                textAlert.setText(getResources().getString(R.string.save_changes));
                exec.setBackgroundColor(getResources().getColor(R.color.green));
                exec.setText(getResources().getString(R.string.save));

                customAlertDialogView.findViewById(R.id.exec).setOnClickListener(view1 -> {
                    indicator.show();
                    recyclerData = new Product(id, nmStr, tpStr, imgUri, hrgVal, conStr, mnfStr, wghStr, clrStr, stkVal, descStr);
                    updateProduct();
                    alertDialog.dismiss();

                });
            } else {
                Toast.makeText(this, "Field must not be empty, " + layout, Toast.LENGTH_SHORT).show();
            }
        });

        clear.setOnClickListener(view1 -> {
            alertDialog = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded).create();
            customAlertDialogView = LayoutInflater.from(view1.getContext()).inflate(R.layout.menu_dialog, null, false);
            alertDialog.setView(customAlertDialogView);
            alertDialog.show();
            appCompatImageView = customAlertDialogView.findViewById(R.id.appCompatImageView);
            textAlert = customAlertDialogView.findViewById(R.id.text_alert);
            exec = customAlertDialogView.findViewById(R.id.exec);

            appCompatImageView.setImageDrawable(getResources().getDrawable(R.drawable.circle_rounded_filled_red));
            textAlert.setText(getResources().getString(R.string.clear_current_changes));
            exec.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            exec.setText(getResources().getString(R.string.clear));
            customAlertDialogView.findViewById(R.id.exec).setOnClickListener(view2 -> {
                alertDialog.dismiss();
                toDef();
            });
        });

        ArrayList<RecyclerText> textArrayList = new ArrayList<>();
        textArrayList.add(new RecyclerText(getResources().getString(R.string.eCon)));
        textArrayList.add(new RecyclerText(getResources().getString(R.string.eMnf)));
        textArrayList.add(new RecyclerText(getResources().getString(R.string.eWgh)));
        textArrayList.add(new RecyclerText(getResources().getString(R.string.eClr)));

        ArrayList<String> dtl = new ArrayList<>();
        dtl.add(conStr);
        dtl.add(mnfStr);
        dtl.add(wghStr);
        dtl.add(clrStr);
        dtl.add(stkStr);

        RecyclerView recyclerView = findViewById(R.id.dtlTxtRV);
        TextScrollAdapter scrollTextAdapter = new TextScrollAdapter(textArrayList, this, this, dtl);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(scrollTextAdapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null) return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) targetPosition = position - 1;
                    else targetPosition = position + 1;
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) targetPosition = position - 1;
                    else targetPosition = position + 1;
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed() {
        alertDialog = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded).create();
        customAlertDialogView = LayoutInflater.from(findViewById(R.id.root).getContext()).inflate(R.layout.menu_dialog, null, false);
        alertDialog.setView(customAlertDialogView);
        alertDialog.show();
        appCompatImageView = customAlertDialogView.findViewById(R.id.appCompatImageView);
        textAlert = customAlertDialogView.findViewById(R.id.text_alert);
        exec = customAlertDialogView.findViewById(R.id.exec);

        appCompatImageView.setImageDrawable(getResources().getDrawable(R.drawable.circle_rounded_filled_orange));
        textAlert.setText(getResources().getString(R.string.go_back));
        exec.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        exec.setText(getResources().getString(R.string.proceed));
        customAlertDialogView.findViewById(R.id.exec).setOnClickListener(view2 -> {
            alertDialog.dismiss();
            super.onBackPressed();
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        keyboardShown(findViewById(R.id.root));
    }

    private void keyboardShown(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            view.getWindowVisibleDisplayFrame(r);
            int screenHeight = view.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;
            if (keypadHeight > screenHeight * 0.15) {
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true;
                    final int newBottomMargin = 256;
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuBg.getLayoutParams();
                            params.bottomMargin = (int) (newBottomMargin * interpolatedTime);
                            menuBg.setLayoutParams(params);
                        }
                    };
                    a.setDuration(10); // in ms
                    menuBg.startAnimation(a);
                }
            } else {
                if (isKeyboardShowing) {
                    isKeyboardShowing = false;
                    onKeyboardHide();
                }
            }
        });
    }

    private void onBtnClick(View v) {
        if (!isKeyboardShowing) {
            getView = null;
            i = 0;
        }

        final Handler handlerFcs = new Handler();

        switch (layout) {
            case 1:
                nmBrgLt.setVisibility(View.VISIBLE);
                if (i == 1 && getView != nmBrgLt) {
                    getView.setVisibility(View.GONE);
                }
                getView = nmBrgLt;
                i = 1;
                nmLt.requestFocus();
                imm.showSoftInput(nmTxt, InputMethodManager.SHOW_FORCED);
                handlerFcs.postDelayed(() -> focusOnView(v), 0);
                nmTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        nmStr = String.valueOf(Objects.requireNonNull(nmTxt.getText()));
                        if (nmStr.isEmpty()) nmBtn.setText(R.string.eItemName);
                        else nmBtn.setText(nmStr);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                break;
            case 2:
                tpBrgLt.setVisibility(View.VISIBLE);
                if (i == 1 && getView != tpBrgLt) {
                    getView.setVisibility(View.GONE);
                }
                getView = tpBrgLt;
                i = 1;
                tpLt.requestFocus();
                imm.showSoftInput(tpTxt, InputMethodManager.SHOW_FORCED);
                handlerFcs.postDelayed(() -> focusOnView(v), 0);
                tpTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        tpStr = String.valueOf(Objects.requireNonNull(tpTxt.getText()));
                        if (tpStr.isEmpty()) tpBtn.setText(R.string.eItemName);
                        else tpBtn.setText(tpStr);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                break;
            case 3:
                hrgBrgLt.setVisibility(View.VISIBLE);
                if (i == 1 && getView != hrgBrgLt) {
                    getView.setVisibility(View.GONE);
                }
                getView = hrgBrgLt;
                i = 1;
                hrgLt.requestFocus();
                imm.showSoftInput(hrgTxt, InputMethodManager.SHOW_FORCED);
                handlerFcs.postDelayed(() -> focusOnView(v), 0);
                hrgTxt.addTextChangedListener(new MoneyTextWatcher(hrgTxt) {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        BigDecimal vHrg = MoneyTextWatcher.parseCurrencyValue(Objects.requireNonNull(hrgTxt.getText()).toString());
                        hrgVal = vHrg.longValue();
                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            hrgStr = String.valueOf(vHrg);
                            hrgBtn.setText(Objects.requireNonNull(hrgTxt.getText()).toString());
                        }, 20);
                    }
                });
                break;
            case 4:
                dtlBrgLt.setVisibility(View.VISIBLE);
                if (i == 1 && getView != dtlBrgLt) {
                    getView.setVisibility(View.GONE);
                }
                getView = dtlBrgLt;
                i = 1;
                if (!isKeyboardShowing) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                handlerFcs.postDelayed(() -> focusOnView(ltDtl), 0);
                break;
            case 5:
                descBrgLt.setVisibility(View.VISIBLE);
                if (i == 1 && getView != descBrgLt) {
                    getView.setVisibility(View.GONE);
                }
                getView = descBrgLt;
                i = 1;
                descLt.requestFocus();
                handlerFcs.postDelayed(() -> focusOnView(ltDesc), 0);
                imm.showSoftInput(descTxt, InputMethodManager.SHOW_FORCED);
                descTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        descStr = String.valueOf(Objects.requireNonNull(descTxt.getText()));
                        if (descStr.isEmpty()) setDesc.setText(R.string.product_description_content);
                        else setDesc.setText(descStr);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                break;
        }
    }

    private void onKeyboardHide() {
        revertAnimCloseKey();
        nmLt.clearFocus();
        nmBrgLt.setVisibility(View.GONE);
        tpLt.clearFocus();
        tpBrgLt.setVisibility(View.GONE);
        hrgLt.clearFocus();
        hrgBrgLt.setVisibility(View.GONE);
        dtlBrgLt.setVisibility(View.GONE);
        descLt.clearFocus();
        descBrgLt.setVisibility(View.GONE);
    }

    @SuppressLint({"NewApi", "Recycle"})
    private void focusOnView(View view) {
        ObjectAnimator yTranslate;
        if (view == nmBtn || view == tpBtn)
            yTranslate = ObjectAnimator.ofInt(scrollView, "scrollY", nmBtn.getTop());
        else {
            yTranslate = ObjectAnimator.ofInt(scrollView, "scrollY", view.getBottom() + imageHeight);
        }
        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(660L);
        animators.play(yTranslate);
        animators.setInterpolator(new AccelerateDecelerateInterpolator());
        animators.start();
    }

    @Override
    public void getDetail(String condition, String manufacter, String weight, String color, String stock) {
        conStr = condition;
        mnfStr = manufacter;
        wghStr = weight;
        clrStr = color;
        stkStr = stock;
        if (conStr.isEmpty()) con.setText(R.string.eItemCondition);
        else con.setText(conStr);
        if (mnfStr.isEmpty()) mnf.setText(R.string.eItemManufacter);
        else mnf.setText(mnfStr);
        if (wghStr.isEmpty()) wgh.setText(R.string.eItemWeight);
        else wgh.setText(wghStr);
        if (clrStr.isEmpty()) clr.setText(R.string.eItemColor);
        else clr.setText(clrStr);
        if (stkStr.isEmpty()) stk.setText(R.string.eItemStock);
        else {stk.setText(stkStr); stkVal = Long.parseLong(stkStr);}
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                imagePath = data.getData();
                imgUri = imagePath.toString();
                selectedImage = new Compressor(this).compressToBitmap(FileUtil.from(this, imagePath));
                Glide.with(this).load(imagePath).into(imageView);

                imageHeight = selectedImage.getHeight();
                imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.dashed));
                findViewById(R.id.placeholderImage).setVisibility(View.GONE);
            } catch (Exception e) {
                Log.d("Err", e.getMessage());
            }
        }
    }

    private void revertAnimCloseKey() {
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuBg.getLayoutParams();
                params.bottomMargin = (int) (dpToPx(24) * interpolatedTime);
                menuBg.setLayoutParams(params);
            }
        };
        a.setDuration(30); // in ms
        menuBg.startAnimation(a);
    }

    private int dpToPx(int dip) {
        Resources r = this.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
    }

    private void toDef() {
        format.setMaximumFractionDigits(0);
        Bundle extras = getIntent().getExtras();
        recyclerData = (Product) extras.getSerializable("chosenProduct");
        id = recyclerData.getproductId();
        nmStr = recyclerData.getProductName();
        tpStr = recyclerData.getproductType();
        imgUri = recyclerData.getproductImage();
        hrgVal = recyclerData.getproductPrice();
        conStr = recyclerData.getproductCondition();
        mnfStr = recyclerData.getproductManufacter();
        wghStr = recyclerData.getproductWeight();
        clrStr = recyclerData.getproductColor();
        stkVal = recyclerData.getproductStock();
        descStr = recyclerData.getproductDescription();

        hrgStr = String.valueOf(hrgVal);
        stkStr = String.valueOf(stkVal);

        nmBtn.setText(nmStr);
        nmTxt.setText(nmStr);
        tpBtn.setText(tpStr);
        tpTxt.setText(tpStr);
        Glide.with(this).load(imgUri).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                new Handler().postDelayed(() -> {
                    selectedImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    imageHeight = selectedImage.getHeight();
                    findViewById(R.id.onLoad).setVisibility(View.GONE);
                }, 0);
                return false;
            }
        }).into(imageView);
        hrgBtn.setText(hrgStr);
        hrgTxt.setText(hrgStr);
        con.setText(conStr);
        mnf.setText(mnfStr);
        wgh.setText(wghStr);
        clr.setText(clrStr);
        stk.setText(stkStr);
        setDesc.setText(descStr);
        descTxt.setText(descStr);

    }

    private void updateProduct() {
        new VolleyServerRequest(getApplicationContext(), ServerAPI.URL_UPDATE_PRODUCT, recyclerData, selectedImage, new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                Log.d("VolleyReq", "onSuccess: " + res);
                JsonObject responseResult = new Gson().fromJson(res, JsonObject.class);
                JsonPrimitive message = responseResult.get("message").getAsJsonPrimitive();
                if (responseResult.get("error").getAsBoolean())
                    Snackbar.make(findViewById(R.id.root), message.getAsString(), 4800).
                            setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                            setTextColor(getResources().getColor(R.color.whiteAccent)).show();
                else {
                    Intent intent = new Intent();
                    intent.putExtra("message", message.getAsString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                indicator.hide();
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(R.id.root), error, 4800).
                        setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                        setTextColor(getResources().getColor(R.color.whiteAccent)).show();
                indicator.hide();
            }
        });
    }

}
