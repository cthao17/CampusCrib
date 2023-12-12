package com.cs407.campuscrib;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs407.campuscrib.adapter.SavedListingAdapter;
import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainListingFragment extends Fragment implements SavedListingAdapter.OnFavoriteClickListener, SavedListingAdapter.OnSendMessageClickListener {

    RecyclerView recyclerView;
    SavedListingAdapter adapter;
    UserModel otherUser = new UserModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        setUpListingView("timestamp DESC");
        return rootView;
    }

    void setUpListingView(String sortBy) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String currentUserId = user.getUid();
            List<ListingModel> allListings = new ArrayList<>();

            FirebaseFirestore.getInstance()
                    .collectionGroup("personalListing")  // Use collectionGroup to query across all users
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot listingDocument : task.getResult()) {
                                ListingModel listingModel = listingDocument.toObject(ListingModel.class);
                                allListings.add(listingModel);
                            }

                            if ("priceLowtoHigh".equals(sortBy)) {
                                Collections.sort(allListings, Comparator.comparing(ListingModel::getCostAsDouble));
                            } else if ("priceHightoLow".equals(sortBy)) {
                                Collections.sort(allListings, Comparator.comparing(ListingModel::getCostAsDouble).reversed());
                            } else if ("numRoomLowtoHigh".equals(sortBy)) {
                                Collections.sort(allListings, Comparator.comparing(ListingModel::getRoomNumAsDouble));
                            } else if ("numRoomHightoLow".equals(sortBy)) {
                                Collections.sort(allListings, Comparator.comparing(ListingModel::getRoomNumAsDouble).reversed());
                            }

                            SavedListingAdapter adapter = new SavedListingAdapter(allListings, this::onFavoriteClick, this::onSendMessageClick, otherUser);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "Error checking listing existence", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onFavoriteClick(ListingModel listingModel) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            String listingId = listingModel.getListingId();

            DocumentReference savedListingRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("savedListing")
                    .document(listingId);

            savedListingRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && task.getResult().exists()) {
                        // Listing exists in savedListing, delete it
                        savedListingRef.delete().addOnCompleteListener(deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Listing removed from savedListing", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error while deleting
                                Toast.makeText(getContext(), "Error removing listing from savedListing", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Listing doesn't exist in savedListing, save it!
                        savedListingRef.set(listingModel).addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Listing saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error while saving
                                Toast.makeText(getContext(), "Error saving listing", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Handle the error while checking existence
                    Toast.makeText(getContext(), "Error checking listing existence", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onSendMessageClick(UserModel otherUser) {
        Intent intent = new Intent(getContext(), ChattingActivity.class);
        AndroidFunctionsUtil.passUsername(intent, otherUser);
        startActivity(intent);
    }
}