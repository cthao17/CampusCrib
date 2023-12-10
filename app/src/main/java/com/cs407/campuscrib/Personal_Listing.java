package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Personal_Listing extends AppCompatActivity {
    private ImageView listingImage;
    private FirebaseFirestore db;
    private Uri selectedImageUri;
    private List<ListingModel> personalListing;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_listing);
    }
//    public void onClickEdit(View view) {
//        Intent intent = new Intent(this, EditListing.class);
//        intent.putExtra("listing_Uid", ListingModel.getUid());
//        startActivity(intent);
//    }
//    public void onClickDelete(View view) {
//
//    }
    public void createListingClick(View view) {
        Intent intent = new Intent(this, EditListing.class);
        startActivity(intent);
    }
    private void updateListingAdapter() {
        ArrayAdapter<ListingModel> adapter = new ArrayAdapter<>(this, R.layout.listing_layout, personalListing);
        ListView listingView = (ListView) findViewById(R.id.currentListing);
        listingView.setAdapter(adapter);
    }
    private void getListing() {
        if (user != null) {
         String uid = user.getUid();
         TextView emailView = findViewById(R.id.emailText);
         emailView.setText("Email: " + user.getEmail());

         db.collection("users").document(uid).collection("personalListing")
                 .get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                             ListingModel listingModel = documentSnapshot.toObject(ListingModel.class);
                             personalListing.add(listingModel);
                         }
                         updateListingAdapter();
                     } else {
                         Toast.makeText(this, "Error Retrieving Info", Toast.LENGTH_SHORT).show();
                     }
                 });
         }
    }
    private void setListing(ListingModel newListing) {
        if (user != null) {
            String uid = user.getUid();
            String listingId = newListing.getUid();

            // Reference to the "personalListing" subcollection for the user
            CollectionReference personalListingRef = db.collection("users").document(uid)
                    .collection("personalListing");

            // Check if the document already exists
            personalListingRef.document(listingId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                // Document exists, update the existing listing
                                personalListingRef.document(listingId).set(newListing)
                                        .addOnSuccessListener(aVoid -> {
                                            // Handle success
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                        });
                            } else {
                                // Document doesn't exist, add a new listing
                                personalListingRef.document(listingId).set(newListing)
                                        .addOnSuccessListener(aVoid -> {
                                            // Handle success
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                        });
                            }
                        } else {
                            // Handle failure
                        }
                    });
        }
    }
}