package com.cs407.campuscrib.model;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
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
    private com.google.firebase.Timestamp lastEditTime;

    public ListingModel() {
        this.listingId = generateUniqueId();
    }

    public ListingModel(String cost, String roomNum, String amenities, String availability, String location, com.google.firebase.Timestamp lastEditTime) {
        this.cost = cost;
        this.roomNum = roomNum;
        this.amenities = amenities;
        this.availability = availability;
        this.location = location;
        this.listingId = generateUniqueId();
        this.uid = generateUid(location, cost, roomNum);
        this.lastEditTime = lastEditTime;
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

    public String getListingId() {
        return listingId;
    }

    public com.google.firebase.Timestamp getLastEditTime() { return lastEditTime; }

    public void setLastEditTime (com.google.firebase.Timestamp lastEditTime) {
        this.lastEditTime = lastEditTime;
    }
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
