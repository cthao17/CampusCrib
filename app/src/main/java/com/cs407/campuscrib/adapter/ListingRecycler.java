package com.cs407.campuscrib.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cs407.campuscrib.ChattingActivity;
import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.UserProfileModel;
import com.cs407.campuscrib.model.Chatroom;
import com.cs407.campuscrib.R;
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ListingRecycler extends FirestoreRecyclerAdapter<ListingModel, com.cs407.campuscrib.adapter.ListingRecycler.ListingModelViewHolder> {
    Context context;
    public ListingRecycler(@NonNull FirestoreRecyclerOptions<ListingModel> options, Context context) {
        super(options);
        this.context = context;
    }
    @Override
    protected void onBindViewHolder(@NonNull ListingModelViewHolder holder, int position, @NonNull ListingModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            db.collection("users").document(uid).collection("personalListing")
                    .document(model.getListingId())  // Use the unique ID of the listing
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ListingModel updatedModel = task.getResult().toObject(ListingModel.class);

                            if (updatedModel != null) {
                                // Now, updatedModel contains the latest data for the specific listing
                                // Use updatedModel to populate your ViewHolder
                                holder.cost.setText(updatedModel.getCost());
                                holder.roomNum.setText(updatedModel.getRoomNum());
                                holder.amenities.setText(updatedModel.getAmenities());
                                holder.availability.setText(updatedModel.getAvailability());
                                holder.location.setText(updatedModel.getLocation());

                                // Add any other relevant UI updates based on the updated data
                            }
                        } else {
                            // Handle the error
                        }
                    });
        }
    }
    @NonNull
    @Override
    public ListingModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listing_layout,parent,false);
        return new ListingRecycler.ListingModelViewHolder(view);
    }

    class ListingModelViewHolder extends RecyclerView.ViewHolder {
        TextView cost;
        TextView roomNum;
        TextView amenities;
        TextView availability;
        TextView location;
        public ListingModelViewHolder(@NonNull View itemView) {
            super(itemView);
            cost = itemView.findViewById(R.id.textViewCost);
            roomNum = itemView.findViewById(R.id.textViewRoomNum);
            amenities = itemView.findViewById(R.id.textViewAmenities);
            availability = itemView.findViewById(R.id.textViewAvailability);
            location = itemView.findViewById(R.id.textViewLocation);
        }
    }
}