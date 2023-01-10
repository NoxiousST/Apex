package com.test.apex;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Belal on 9/5/2017.
 */

//here for this class we are using a singleton pattern

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_USER = "usersharedpref";
    private static final String SHARED_PREF_PRODUCTS = "productssharedpref";

    private static final String KEY_PROFILE = "keyprofile";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_PASSWORD = "keypassword";
    private static final String KEY_PHONE = "keyphone";
    private static final String KEY_GENDER = "keygender";
    private static final String KEY_BIRTHDATE = "keybirthdate";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_ID = "keyid";
    private static final String KEY_PRODUCTS = "keyproducts";

    private static SharedPrefManager mInstance;
    private Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_PROFILE, user.getProfilePicture());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.putString(KEY_PHONE, user.getPhoneNumber());
        editor.putString(KEY_GENDER, user.getGender());
        editor.putString(KEY_BIRTHDATE, user.getBirthDate());
        editor.putString(KEY_LOGIN, user.getLoginOption());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_PROFILE, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PASSWORD, null),
                sharedPreferences.getString(KEY_PHONE, null),
                sharedPreferences.getString(KEY_GENDER, null),
                sharedPreferences.getString(KEY_BIRTHDATE, null),
                sharedPreferences.getString(KEY_LOGIN, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        //mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }


    public void saveProductList(ArrayList<Product> productArrayList) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_PRODUCTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(productArrayList);

        editor.putString(KEY_PRODUCTS, json);
        editor.apply();
    }

    public ArrayList<Product> loadProductList() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_PRODUCTS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_PRODUCTS, null);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        ArrayList<Product> productArrayList = gson.fromJson(json, type);

        if (productArrayList == null) {
            productArrayList = new ArrayList<>();
        }
        return productArrayList;
    }
}