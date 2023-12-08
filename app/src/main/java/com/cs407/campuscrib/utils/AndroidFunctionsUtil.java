package com.cs407.campuscrib.utils;

import android.content.Intent;

import com.cs407.campuscrib.model.UserModel;

public class AndroidFunctionsUtil {

    public static void passUsername(Intent intent, UserModel model) {
        intent.putExtra("username",model.getUsername());
        intent.putExtra("userId",model.getUserId());
    }

    public static UserModel getUserModelIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }
}
