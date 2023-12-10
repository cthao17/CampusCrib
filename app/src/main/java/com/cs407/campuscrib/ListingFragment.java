package com.cs407.campuscrib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cs407.campuscrib.R;
import com.cs407.campuscrib.adapter.ListingRecycler;
import com.cs407.campuscrib.adapter.RecentMsgRecycler;
import com.cs407.campuscrib.adapter.YourListingAdapter;
import com.cs407.campuscrib.model.Chatroom;
import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListingFragment extends Fragment {
    RecyclerView recyclerView;
    ListingRecycler adapter;
    public ListingFragment() {}
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
                    .collection("personalListing")
                    .orderBy("lastEditTime", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<ListingModel> listingModels = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ListingModel listingModel = document.toObject(ListingModel.class);
                                listingModels.add(listingModel);
                            }

                            YourListingAdapter adapter = new YourListingAdapter(listingModels);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        } else {
                            // Handle the error
                        }
                    });
        }
    }

    private List<ListingModel> createDummyData() {
        List<ListingModel> dummyData = new ArrayList<>();

        // Add multiple dummy ListingModel objects to the list
        dummyData.add(new ListingModel("Location1", "Cost1", "RoomNum1", "Availability1", "Amenities1", Timestamp.now()));
        dummyData.add(new ListingModel("Location2", "Cost2", "RoomNum2", "Availability2", "Amenities2", Timestamp.now()));
        dummyData.add(new ListingModel("Location3", "Cost3", "RoomNum3", "Availability3", "Amenities3", Timestamp.now()));

        return dummyData;
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
}
