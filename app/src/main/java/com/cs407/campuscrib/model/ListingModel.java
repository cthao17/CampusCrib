package com.cs407.campuscrib.model;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListingModel {
    private String listingId; // Unique ID for the listing
    private String cost;
    private String roomNum;
    private String amenities;
    private String availability;
    private String location;
    private String uid;
    private List<String> imageIds = new ArrayList<>();
    private Timestamp lastEditTime;
    private String email;
    private boolean isFavorite;

    public ListingModel(String location, String cost, String roomNum, String availability, String amenities, Timestamp lastEditTime, String email) {
        this.cost = cost;
        this.roomNum = roomNum;
        this.amenities = amenities;
        this.availability = availability;
        this.location = location;
        this.listingId = generateUniqueId();
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.lastEditTime = lastEditTime;
        this.email = email;
    }

    public double getCostAsDouble() {
        try {
            // Try to parse the cost as a double
            return Double.parseDouble(cost);
        } catch (NumberFormatException e) {
            // Handle the case where parsing fails, return a default value or throw an exception
            return 0.0; // You can modify this based on your requirements
        }
    }

    public double getRoomNumAsDouble() {
        try {
            return Double.parseDouble(roomNum);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public ListingModel() {
        this.listingId = generateUniqueId();
    }

    public List<String> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<String> imageIds) {
        this.imageIds = imageIds;
    }

    public String getUid() {
        return uid;
    }

    public void addImageId(String imageId) {
        imageIds.add(imageId);
    }

    private String generateUid(String location, String cost, String roomNum) {
        return location + cost + roomNum;
    }
    public String getCost() {
        return cost;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getAvailability() {
        return availability;
    }

    public String getLocation() {
        return location;
    }

    public String getListingId() { return listingId; }

    public String getEmail() { return email; }

    public Timestamp getLastEditTime() { return lastEditTime; }

    public void setLastEditTime (Timestamp lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public void setEmail (String email) {this.email = email; }
    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static void setListingImages(Context context, List<Uri> imageUris, List<ImageView> imageViews) {
        for (int i = 0; i < Math.min(imageUris.size(), imageViews.size()); i++) {
            Glide.with(context).load(imageUris.get(i)).into(imageViews.get(i));
        }
    }
}
