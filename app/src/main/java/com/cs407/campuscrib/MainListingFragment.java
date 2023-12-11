package com.cs407.campuscrib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs407.campuscrib.adapter.SavedListingAdapter;
import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainListingFragment extends Fragment implements SavedListingAdapter.OnFavoriteClickListener, SavedListingAdapter.OnSendMessageClickListener {

    RecyclerView recyclerView;
    SavedListingAdapter adapter;
    UserModel otherUser = new UserModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        setUpListingView();
        return rootView;
    }

    void setUpListingView() {
        FirebaseFirestore.getInstance()
                .collection("personalListing") // Assuming personal listings are stored in "personalListing" collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ListingModel> listingModels = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListingModel listingModel = document.toObject(ListingModel.class);
                            listingModels.add(listingModel);
                        }

                        adapter = new SavedListingAdapter(listingModels, this::onFavoriteClick, this::onSendMessageClick, otherUser);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Handle the error
                    }
                });
    }

    @Override
    public void onFavoriteClick(ListingModel listingModel) {

    }

    @Override
    public void onSendMessageClick(UserModel otherUser) {

    }
}

//This version works with the old saveListingAdapter --> (MainListingAdapter)

//package com.cs407.campuscrib;
//
//        import android.os.Bundle;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import androidx.fragment.app.Fragment;
//        import androidx.recyclerview.widget.LinearLayoutManager;
//        import androidx.recyclerview.widget.RecyclerView;
//        import com.cs407.campuscrib.adapter.MainListingAdapter;
//        import com.cs407.campuscrib.adapter.SavedListingRecycler;
//        import com.cs407.campuscrib.model.ListingModel;
//        import com.cs407.campuscrib.model.UserModel;
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.auth.FirebaseUser;
//        import com.google.firebase.firestore.FirebaseFirestore;
//        import com.google.firebase.firestore.QueryDocumentSnapshot;
//        import java.util.ArrayList;
//        import java.util.List;
//
//public class MainListingFragment extends Fragment implements MainListingAdapter.OnFavoriteClickListener, MainListingAdapter.OnSendMessageClickListener {
//    RecyclerView recyclerView;
//    SavedListingRecycler adapter;
//
//    public MainListingFragment() {}
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
//        recyclerView = rootView.findViewById(R.id.recycler_view);
//        setUpListingView();
//        return rootView;
//    }
//
//    void setUpListingView() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (user != null) {
//            String currentUserId = user.getUid();
//            List<ListingModel> allListings = new ArrayList<>();
//
//            // Fetch personal listings from all users except the current user
//            FirebaseFirestore.getInstance()
//                    .collectionGroup("personalListing")  // Use collectionGroup to query across all users
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot listingDocument : task.getResult()) {
//                                ListingModel listingModel = listingDocument.toObject(ListingModel.class);
//                                allListings.add(listingModel);
//                            }
//
//                            // Update UI after fetching listings for all users
//                            MainListingAdapter adapter = new MainListingAdapter(allListings, this::onFavoriteClick, this::onSendMessageClick);
//                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                            recyclerView.setAdapter(adapter);
//                        } else {
//                            // Handle the error
//                        }
//                    });
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (adapter != null)
//            adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if(adapter!=null)
//            adapter.stopListening();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if(adapter!=null)
//            adapter.startListening();
//    }
//
//    @Override
//    public void onFavoriteClick(String listingId) {
//
//    }
//
//    @Override
//    public void onSendMessageClick(String listingId) {
//
//    }
//}

