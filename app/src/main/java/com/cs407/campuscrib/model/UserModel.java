package com.cs407.campuscrib.model;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String username;
    private String userId;
    private String fcmToken;

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

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
