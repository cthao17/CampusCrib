package com.cs407.campuscrib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs407.campuscrib.R;
import com.cs407.campuscrib.model.ListingModel;

import java.util.List;

public class YourListingAdapter extends RecyclerView.Adapter<YourListingAdapter.ListingViewHolder> {

    private List<ListingModel> listingModels;

    // Constructor to initialize the data
    public YourListingAdapter(List<ListingModel> listingModels) {
        this.listingModels = listingModels;
    }

    // Create a ViewHolder for each item in the RecyclerView
    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_layout,parent,false);
        return new ListingViewHolder(view);
    }

    // Bind the data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        ListingModel listingModel = listingModels.get(position);

        // Set the data to the views in your layout
        holder.cost.setText(listingModel.getCost());
        holder.roomNum.setText(listingModel.getRoomNum());
        holder.amenities.setText(listingModel.getAmenities());
        holder.availability.setText(listingModel.getAvailability());
        holder.location.setText(listingModel.getLocation());

        // You can add any other UI updates based on the data
    }

    // Return the total number of items in the data set
    @Override
    public int getItemCount() {
        return listingModels.size();
    }

    // ViewHolder class to hold the views
    static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView cost;
        TextView roomNum;
        TextView amenities;
        TextView availability;
        TextView location;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            cost = itemView.findViewById(R.id.textViewCost);
            roomNum = itemView.findViewById(R.id.textViewRoomNum);
            amenities = itemView.findViewById(R.id.textViewAmenities);
            availability = itemView.findViewById(R.id.textViewAvailability);
            location = itemView.findViewById(R.id.textViewLocation);
        }
    }
}