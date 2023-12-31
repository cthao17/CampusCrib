package com.cs407.campuscrib.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs407.campuscrib.EditListing;
import com.cs407.campuscrib.FullScreenImageActivity;
import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ListingRecycler extends FirestoreRecyclerAdapter<ListingModel, ListingRecycler.ListingModelViewHolder> {
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
                    .document(model.getListingId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ListingModel updatedModel = task.getResult().toObject(ListingModel.class);

                            if (updatedModel != null) {
                                holder.cost.setText(updatedModel.getCost());
                                holder.roomNum.setText(updatedModel.getRoomNum());
                                holder.amenities.setText(updatedModel.getAmenities());
                                holder.availability.setText(updatedModel.getAvailability());
                                holder.location.setText(updatedModel.getLocation());
                            }
                        } else {
                            // Handle the error
                        }
                    });
        }

        holder.buttonEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(context, EditListing.class);
            editIntent.putExtra("listingId", model.getListingId());
            context.startActivity(editIntent);
        });

        holder.listingImages.setOnClickListener(view -> {
            showAllImages(model.getListingId(), model.getUid());
        });
    }

    @NonNull
    @Override
    public ListingRecycler.ListingModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listing_layout, parent, false);
        return new ListingModelViewHolder(view);
    }

    class ListingModelViewHolder extends RecyclerView.ViewHolder {
        TextView cost;
        TextView roomNum;
        TextView amenities;
        TextView availability;
        TextView location;
        ImageButton buttonEdit;
        ImageView listingImages;

        public ListingModelViewHolder(@NonNull View itemView) {
            super(itemView);
            cost = itemView.findViewById(R.id.textViewCost);
            roomNum = itemView.findViewById(R.id.textViewRoomNum);
            amenities = itemView.findViewById(R.id.textViewAmenities);
            availability = itemView.findViewById(R.id.textViewAvailability);
            location = itemView.findViewById(R.id.textViewLocation);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            listingImages = itemView.findViewById(R.id.imageView);

            listingImages.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ListingModel listingModel = getItem(position);
                    showAllImages(listingModel.getListingId(), listingModel.getUid());
                }
            });
        }
    }

    private void showAllImages(String listingId, String Uid) {
        Intent intent = new Intent(context, FullScreenImageActivity.class);
        intent.putExtra("listingId", listingId);
        intent.putExtra("Uid", Uid);
        context.startActivity(intent);
    }
}
