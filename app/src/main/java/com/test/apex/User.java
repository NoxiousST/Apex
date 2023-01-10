package com.test.apex;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {

    private String id;
    private String username;
    private String email;
    private String password;
    private String loginOption;
    private String profilePicture;
    private String phoneNumber;
    private String gender;
    private String birthDate;

    public User() {

    }

    public User(String id, String profilePicture, String username, String email, String password, String phoneNumber, String gender, String birthDate, String loginOption) {
        this.id = id;
        this.profilePicture = profilePicture;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.loginOption = loginOption;
    }

    public String getId() {
        return id;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginOption() {
        return loginOption;
    }
    public void setLoginOption(String loginOption) {
        this.loginOption = loginOption;
    }

    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("id", id);
        result.put("username", username);
        result.put("email", email);
        result.put("password", password);
        result.put("loginOption", loginOption);
        result.put("phoneNumber", phoneNumber);
        result.put("gender", gender);
        result.put("birthDate", birthDate);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.loginOption);
        dest.writeString(this.profilePicture);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.gender);
        dest.writeString(this.birthDate);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.username = source.readString();
        this.email = source.readString();
        this.password = source.readString();
        this.loginOption = source.readString();
        this.profilePicture = source.readString();
        this.phoneNumber = source.readString();
        this.gender = source.readString();
        this.birthDate = source.readString();
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.loginOption = in.readString();
        this.profilePicture = in.readString();
        this.phoneNumber = in.readString();
        this.gender = in.readString();
        this.birthDate = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}