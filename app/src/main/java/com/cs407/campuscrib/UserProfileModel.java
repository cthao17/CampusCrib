package com.cs407.campuscrib;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class UserProfileModel {
    private String status;
    private String aboutMe;
    private String housingPreferences;
    private String otherInfo;
    private String name;

    public UserProfileModel() {
    }

    public UserProfileModel(String status, String aboutMe, String housingPreferences, String otherInfo, String name) {
        this.status = status;
        this.aboutMe = aboutMe;
        this.housingPreferences = housingPreferences;
        this.otherInfo = otherInfo;
        this.name = name;
    }

    // Getters and setters (required for Firestore)

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getHousingPreferences() {
        return housingPreferences;
    }

    public void setHousingPreferences(String housingPreferences) {
        this.housingPreferences = housingPreferences;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}