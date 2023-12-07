package com.cs407.campuscrib.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String username;
    private String userId;

    public UserModel() {

    }

    public UserModel(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
