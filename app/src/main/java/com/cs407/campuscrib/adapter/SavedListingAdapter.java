package com.cs407.campuscrib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cs407.campuscrib.R;
import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class SavedListingAdapter extends RecyclerView.Adapter<SavedListingAdapter.ListingViewHolder>{
    private List<ListingModel> listingModels;
    private SavedListingAdapter.OnFavoriteClickListener onFavoriteClickListener;
    private SavedListingAdapter.OnSendMessageClickListener onSendMessageClickListener;
    private UserModel otherUser;

    public SavedListingAdapter(List<ListingModel> listingModels, OnFavoriteClickListener onFavoriteClickListener, OnSendMessageClickListener onSendMessageClickListener, UserModel otherUser) {
        this.listingModels = listingModels;
        this.onFavoriteClickListener = onFavoriteClickListener;
        this.onSendMessageClickListener = onSendMessageClickListener;
        this.otherUser = otherUser;
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(String listingId);
    }

    public interface OnSendMessageClickListener {
        void onSendMessageClick(UserModel otherUser);
    }

    @NonNull
    @Override
    public SavedListingAdapter.ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_listing_layout,parent,false);
        return new SavedListingAdapter.ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedListingAdapter.ListingViewHolder holder, int position) {
        ListingModel listingModel = listingModels.get(position);
        holder.cost.setText("Cost: " + listingModel.getCost());
        holder.roomNum.setText("# of Rooms: " + listingModel.getRoomNum());
        holder.amenities.setText("Amenities: " + listingModel.getAmenities());
        holder.availability.setText("Availability: " + listingModel.getAvailability());
        holder.location.setText("Location: " + listingModel.getLocation());
        holder.poster.setText("Posted By: "+ listingModel.getEmail());
        otherUser.setUserId(listingModel.getUid());
        otherUser.setUsername(listingModel.getEmail());
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
                        // Handle failure to get download URL
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
        TextView poster;
        ImageButton favorite;
        ImageButton sendMessage;
        ImageView listingImages;


        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            cost = itemView.findViewById(R.id.textViewCost);
            roomNum = itemView.findViewById(R.id.textViewRoomNum);
            amenities = itemView.findViewById(R.id.textViewAmenities);
            availability = itemView.findViewById(R.id.textViewAvailability);
            location = itemView.findViewById(R.id.textViewLocation);
            favorite = itemView.findViewById(R.id.favoriteButton);
            poster = itemView.findViewById(R.id.textViewPoster);
            sendMessage = itemView.findViewById(R.id.chatToPoster);
            listingImages = itemView.findViewById(R.id.imageView);

            favorite.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String listingId = listingModels.get(position).getListingId(); // Ensure listingModels is accessible
                    onFavoriteClickListener.onFavoriteClick(listingId); // Ensure onEditClickListener is accessible
                }
            });

            sendMessage.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onSendMessageClickListener.onSendMessageClick(otherUser);
                }
            });
        }
    }
}
