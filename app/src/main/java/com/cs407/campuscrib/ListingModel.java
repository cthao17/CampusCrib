package com.cs407.campuscrib;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ListingModel {
    private String cost;
    private String roomNum;
    private String distance;
    private String amenities;
    private String availability;
    private String location;

    public ListingModel() {
    }
    public ListingModel(String cost, String roomNum, String distance, String amenities, String availability, String location) {
        this.cost = cost;
        this.roomNum = roomNum;
        this.distance = distance;
        this.amenities = amenities;
        this.availability = availability;
        this.location = location;
    }

    public String getCost() {
        return cost;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public String getDistance() {
        return distance;
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

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    public static void setListingImages(Context context, List<Uri> imageUris, List<ImageView> imageViews) {
        for (int i = 0; i < Math.min(imageUris.size(), imageViews.size()); i++) {
            Glide.with(context).load(imageUris.get(i)).into(imageViews.get(i));
        }
    }
}
