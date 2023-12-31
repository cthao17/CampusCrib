package com.cs407.campuscrib;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cs407.campuscrib.adapter.SavedListingAdapter;
import com.cs407.campuscrib.adapter.SavedListingRecycler;
import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SavedListingFragment extends Fragment implements SavedListingAdapter.OnFavoriteClickListener, SavedListingAdapter.OnSendMessageClickListener, SavedListingAdapter.OnMapClickListener {
    RecyclerView recyclerView;
    SavedListingRecycler adapter;
    public SavedListingFragment() {}
    UserModel otherUser = new UserModel();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        setUpListingView();
        return rootView;
    }

    void setUpListingView() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("savedListing")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<ListingModel> listingModels = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ListingModel listingModel = document.toObject(ListingModel.class);
                                listingModels.add(listingModel);
                            }

                            SavedListingAdapter adapter = new SavedListingAdapter(listingModels, this::onFavoriteClick, this::onSendMessageClick, this::onMapClick, otherUser);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        } else {
                            // Handle error
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
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
                                Intent intent = new Intent(getContext(), SavedListing.class);
                                startActivity(intent);
                                Toast.makeText(getContext(), "Listing removed from savedListing", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle error when deleting
                                Toast.makeText(getContext(), "Error removing listing from savedListing", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Listing doesn't exist in savedListing, save it!
                        savedListingRef.set(listingModel).addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Listing saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle error when saving
                                Toast.makeText(getContext(), "Error saving listing", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Handle error when checking existence
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

    @Override
    public void onMapClick(String location) {
        String address = location;
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Check if the Maps app is available
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getContext(), "Can't open map right now", Toast.LENGTH_SHORT).show();
        }
    }
}
