package com.cs407.campuscrib;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.cs407.campuscrib.adapter.ListingRecycler;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cs407.campuscrib.adapter.YourListingAdapter;
import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class ListingFragment extends Fragment implements YourListingAdapter.OnEditClickListener,  YourListingAdapter.OnDeleteClickListener  {
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

                            YourListingAdapter adapter = new YourListingAdapter(listingModels, this::onEditClick, this::onDeleteClick);
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
    public void onEditClick(String listingId) {
        Intent intent = new Intent(getContext(), EditListing.class);
        intent.putExtra("listingId", listingId);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(String listingId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("personalListing")
                    .document(listingId)
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            setUpListingView();
                            deleteImageFolder(uid, listingId);
                            Toast.makeText(getContext(), "Successfully Deleted Listing", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error Deleting Listing", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Retrieve image URIs and delete corresponding files
    private void getImageURIsAndDelete(final StorageReference folderRef, final OnFolderDeleteListener listener) {
        folderRef.listAll().addOnSuccessListener(listResult -> {
            // Retrieve image URIs
            List<String> imageURIs = new ArrayList<>();
            for (StorageReference item : listResult.getItems()) {
                imageURIs.add(item.toString()); // You might need to adjust this based on how URIs are stored
            }

            // Delete each file and corresponding URI
            for (int i = 0; i < listResult.getItems().size(); i++) {
                StorageReference itemRef = listResult.getItems().get(i);
                String uriToDelete = imageURIs.get(i);

                // Delete the file
                itemRef.delete().addOnFailureListener(exception -> {
                    // Handle failure to delete individual file
                    listener.onFolderDeleteFailure(exception);
                });
            }
        });
    }

    private void deleteImageFolder(String uid, String listingId) {
        StorageReference folderRef = FirebaseUtil.getPersonalListingImageRef().child(listingId);
         getImageURIsAndDelete(folderRef, new OnFolderDeleteListener() {
             @Override
             public void onFolderDeleteFailure(Exception exception) {
                 Toast.makeText(getContext(), "Error Deleting Images", Toast.LENGTH_SHORT).show();
             }
         });
    }
}
