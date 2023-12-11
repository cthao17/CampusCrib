package com.cs407.campuscrib;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cs407.campuscrib.EditListing;
import com.cs407.campuscrib.R;
import com.cs407.campuscrib.adapter.SavedListingAdapter;
import com.cs407.campuscrib.adapter.SavedListingRecycler;
import com.cs407.campuscrib.adapter.YourListingAdapter;
import com.cs407.campuscrib.model.ListingModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SavedListingFragment extends Fragment implements SavedListingAdapter.OnFavoriteClickListener, SavedListingAdapter.OnSendMessageClickListener {
    RecyclerView recyclerView;
    SavedListingRecycler adapter;
    public SavedListingFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        setUpListingView();
        return rootView;
    }

    interface OnFolderDeleteListener {
        void onFolderDeleteFailure(Exception exception);
    }

    void setUpListingView() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("personalListing")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<ListingModel> listingModels = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ListingModel listingModel = document.toObject(ListingModel.class);
                                listingModels.add(listingModel);
                            }

                            SavedListingAdapter adapter = new SavedListingAdapter(listingModels, this::onFavoriteClick, this::onSendMessageClick);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        } else {
                            // Handle the error
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
    public void onFavoriteClick(String listingId) {
        Intent intent = new Intent(getContext(), EditListing.class);
        intent.putExtra("listingId", listingId);
        startActivity(intent);
    }

    @Override
    public void onSendMessageClick(String listingId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
    }
}
