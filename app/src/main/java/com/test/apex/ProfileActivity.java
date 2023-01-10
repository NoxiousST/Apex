package com.test.apex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.test.apex.database.UserDatabase;
import com.test.apex.databinding.ActivityUpdateBinding;
import com.test.apex.network.ServerAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 200;
    private User user;
    BottomSheetBehavior sheetBehavior;
    BottomSheetDialog sheetDialog;
    private ArrayAdapter<String> adapter;
    private ImageLoader mImageLoader;
    ActivityUpdateBinding binding;
    String profile, username, email, phone, gender, birth;
    Bitmap selectedImage;
    ObjectMapper mapper = new ObjectMapper();
    String password = "", newPassword = "", confirmPasswrd = "";
    boolean first = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Objects.requireNonNull(getSupportActionBar()).hide();

        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progress.hide();

        user = SharedPrefManager.getInstance(this).getUser();
        profile = user.getProfilePicture();
        username = user.getUsername();
        email = user.getEmail();
        phone = user.getPhoneNumber();
        gender = user.getGender();
        birth = user.getBirthDate();

        if (!user.getLoginOption().equals("MySQL")) {
            binding.myName.setEnabled(false);
            binding.editTextUsername.setEnabled(false);
            binding.editTextEmail.setEnabled(false);
            binding.editTextPhone.setEnabled(false);
            binding.editTextGender.setEnabled(false);
            binding.setBirthday.setEnabled(false);
            binding.addImage.setEnabled(false);
            binding.btnPassword.setVisibility(View.GONE);
        }

        binding.myName.setText(username);
        binding.editTextUsername.setText(username);
        binding.editTextEmail.setText(email);

        binding.editTextPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, genderList);
        binding.editTextGender.setAdapter(adapter);


        if (profile == null) {

        } else if (!profile.isEmpty()) {
            mImageLoader = VolleySingleton.getInstance(this).getImageLoader();
            binding.profilePicture.setImageUrl(user.getProfilePicture(), mImageLoader);
            binding.progress.hide();
        } else {
            binding.profilePictureGallery.setVisibility(View.VISIBLE);
            binding.progress.hide();
        }

        if (phone == null) {

        } else if (!phone.isEmpty()) {
            binding.editTextPhone.setText(phone);
        }

        Log.d("Gender", "gender: " + gender);
        if (gender == null) {

        } else if (!gender.isEmpty()) {
            binding.editTextGender.postDelayed(() -> binding.editTextGender.setText(gender), 50);
        }
        if (birth == null) {

        } else if (!birth.isEmpty()) {
            binding.editTextBirthday.setText(birth);
        }


        binding.addImage.setOnClickListener(view -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        });

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        binding.setBirthday.setOnClickListener(v -> {
            materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        });

        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("WIB"));
            calendar.setTimeInMillis(selection);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            dateFormat.setTimeZone(calendar.getTimeZone());
            binding.editTextBirthday.setText(dateFormat.format(calendar.getTime()));

        });

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        binding.btnPassword.setOnClickListener(view -> {
            passwordSheet();
        });


        binding.topAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.update:
                    setUser();
                    return true;

            }
            return false;
        });

    }


    private void setUser() {
        profile = Objects.requireNonNull(binding.editTextUsername.getText()).toString();
        username = Objects.requireNonNull(binding.editTextUsername.getText()).toString();
        email = Objects.requireNonNull(binding.editTextEmail.getText()).toString();

        phone = Objects.requireNonNull(binding.editTextPhone.getText()).toString();
        gender = Objects.requireNonNull(binding.editTextGender.getText()).toString();
        birth = Objects.requireNonNull(binding.editTextBirthday.getText()).toString();

        if (!validate())
            return;

        password = newPassword;

        updateUser();
    }

    private boolean validate() {

        if (username.isEmpty()) {
            binding.textLayoutUsername.setError("*Username must not be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textLayoutEmail.setError("*Email is not valid");
            return false;
        }

        if (user.getPassword().equals(newPassword)) {
            Toast.makeText(this, "New password should not be same as old password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!newPassword.equals(confirmPasswrd)) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!user.getPassword().equals(password) && !password.isEmpty()) {
            Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateUser() {
        if (password == null || password.isEmpty()) password = user.getPassword();
        password = user.getPassword();
        User newUser = new User(user.getId(), profile, username, email, password, phone, gender, birth, "MySQL");
        new UserDatabase(getBaseContext(), ServerAPI.URL_UPDDATE_USER, newUser, selectedImage, new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                Log.d("VolleyReq", "onSuccess: " + res);

                try {
                    JsonNode node = mapper.readTree(res);
                    if (node.get("error").asBoolean()) {

                        return;
                    }
                    Snackbar.make(binding.getRoot(), node.get("message").asText(), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                            setTextColor(getResources().getColor(R.color.whiteAccent)).show();
                    readUser();

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(binding.getRoot(), error, 4800).
                        setBackgroundTint(getResources().getColor(R.color.accentBackground)).
                        setTextColor(getResources().getColor(R.color.whiteAccent)).show();
            }
        });
    }

    private void readUser() {
        new UserDatabase(getBaseContext(), ServerAPI.URL_READ_UPDATED_USER, new VolleyOnEventListener() {
            @Override
            public void onSuccess(String res) {
                Log.d("VolleyReq", "onSuccess: " + res);
                try {
                    JsonNode node = mapper.readTree(res);
                    if (!node.get("error").asBoolean()) {

                        User dataUser = mapper.treeToValue(node.get("user"), User.class);
                        dataUser.setId(user.getId());
                        dataUser.setLoginOption("MySQL");
                        SharedPrefManager.getInstance(ProfileActivity.this).userLogin(dataUser);


                    }

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }


    private void passwordSheet() {
        View view = getLayoutInflater().inflate(R.layout.password_sheet, null);
        TextInputEditText curr = view.findViewById(R.id.editTextCurPassword);
        TextInputEditText newp = view.findViewById(R.id.editTextNewPassword);
        TextInputEditText newc = view.findViewById(R.id.editTextRePassword);

        curr.setText(password);
        newp.setText(newPassword);
        newc.setText(confirmPasswrd);

        curr.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
            }
        });

        newp.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newPassword = s.toString();
            }
        });
        newc.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPasswrd = s.toString();
            }
        });


        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }


        sheetDialog = new BottomSheetDialog(this);
        sheetDialog.setContentView(view);
        sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        sheetDialog.show();
        sheetDialog.setOnDismissListener(dialog -> sheetDialog = null);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Uri imagePath = data.getData();
                binding.profilePictureGallery.setVisibility(View.VISIBLE);
                selectedImage = new Compressor(this).compressToBitmap(FileUtil.from(this, imagePath));
                Glide.with(this).load(imagePath).into(binding.profilePictureGallery);
                binding.profilePicture.setVisibility(View.GONE);

            } catch (Exception e) {
                Log.d("Err", e.getMessage());
            }
        }
    }


}