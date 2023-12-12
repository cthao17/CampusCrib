package com.cs407.campuscrib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cs407.campuscrib.R;
import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class YourListingAdapter extends RecyclerView.Adapter<YourListingAdapter.ListingViewHolder> {
    private List<ListingModel> listingModels;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public YourListingAdapter(List<ListingModel> listingModels, OnEditClickListener onEditClickListener,  OnDeleteClickListener onDeleteClickListener) {
        this.listingModels = listingModels;
        this.onEditClickListener = onEditClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public interface OnEditClickListener {
        void onEditClick(String listingId);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String listingId);
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_layout,parent,false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        ListingModel listingModel = listingModels.get(position);
        holder.cost.setText("Cost: " + listingModel.getCost());
        holder.roomNum.setText("# of Rooms: " + listingModel.getRoomNum());
        holder.amenities.setText("Amenities: " + listingModel.getAmenities());
        holder.availability.setText("Availability: " + listingModel.getAvailability());
        holder.location.setText("Location: " + listingModel.getLocation());
        setImages(listingModel.getListingId(), holder.listingImages);
    }

    public void setImages(String listingId, ImageView listingImage) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        if (user != null) {
            StorageReference folderRef = FirebaseUtil.getPersonalListingImageRef().child(listingId);
            folderRef.listAll().addOnSuccessListener(listResult -> {
                // Retrieve image URIs
                List<String> imageURIs = new ArrayList<>();
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageURIs.add(uri.toString());

                        if (!imageURIs.isEmpty()) {
                            String firstImageUri = imageURIs.get(0);
                            Glide.with(listingImage.getContext()).load(firstImageUri).centerCrop().into(listingImage);
                        }
                    }).addOnFailureListener(e -> {
                        // Handle errorL
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listingModels.size();
    }

    public class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView cost;
        TextView roomNum;
        TextView amenities;
        TextView availability;
        TextView location;
        ImageButton buttonEdit;
        ImageButton buttonDelete;
        ImageView listingImages;


        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            cost = itemView.findViewById(R.id.textViewCost);
            roomNum = itemView.findViewById(R.id.textViewRoomNum);
            amenities = itemView.findViewById(R.id.textViewAmenities);
            availability = itemView.findViewById(R.id.textViewAvailability);
            location = itemView.findViewById(R.id.textViewLocation);
            buttonDelete = itemView.findViewById(R.id.buttonRemove);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            listingImages = itemView.findViewById(R.id.imageView);

            buttonEdit.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String listingId = listingModels.get(position).getListingId(); // Ensure listingModels is accessible
                    onEditClickListener.onEditClick(listingId); // Ensure onEditClickListener is accessible
                }
            });

            buttonDelete.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String listingId = listingModels.get(position).getListingId();
                    onDeleteClickListener.onDeleteClick(listingId);
                }
            });
        }
    }
}