package com.cs407.campuscrib;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cs407.campuscrib.model.ListingModel;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditListing extends AppCompatActivity {

    private static final int IMAGE_PICK_REQUEST_CODE = 1;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private ActivityResultLauncher<Intent> imagePickLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        Button imagesButton = findViewById(R.id.images_button);
        Button submitButton = findViewById(R.id.submit_button);
        String listingId = getIntent().getStringExtra("listingId");
        imagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createListing();
            }
        });

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUris.add(data.getData());
                            Toast.makeText(EditListing.this, "UPLOADED!, Upload Next Image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickLauncher.launch(intent);
    }

    private void createListing() {
        EditText locationEditText = findViewById(R.id.EditTextLocation);
        EditText costEditText = findViewById(R.id.EditTextCost);
        EditText roomNumEditText = findViewById(R.id.EditTextRoomNum);
        EditText availabilityEditText = findViewById(R.id.EditTextAvailability);
        EditText amenitiesEditText = findViewById(R.id.EditTextAmenities);

        String location = locationEditText.getText().toString().trim();
        String cost = costEditText.getText().toString().trim();
        String roomNum = roomNumEditText.getText().toString().trim();
        String availability = availabilityEditText.getText().toString().trim();
        String amenities = amenitiesEditText.getText().toString().trim();
        if (location.isEmpty() || cost.isEmpty() || roomNum.isEmpty() || availability.isEmpty() || amenities.isEmpty()) {
            Toast.makeText(EditListing.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("users").document(uid);

            ListingModel listing = new ListingModel(location, cost, roomNum, availability, amenities, Timestamp.now());

            // Save the listing within the personalListing subcollection of the current user
            userDocRef.collection("personalListing").document(listing.getListingId())
                    .set(listing, SetOptions.merge())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditListing.this, "Listing created successfully", Toast.LENGTH_SHORT).show();

                            // Upload images if selected
                            if (!selectedImageUris.isEmpty()) {
                                List<String> imageIds = new ArrayList<>();

                                for (Uri imageUri : selectedImageUris) {
                                    String imageId = UUID.randomUUID().toString();
                                    imageIds.add(imageId);
                                    FirebaseUtil.getPersonalListingImageRef().child(listing.getListingId()).child(imageId).putFile(imageUri);
                                }

                                // Set image IDs in the listing, update the Firestore doc
                                listing.setImageIds(imageIds);
                                userDocRef.collection("personalListing").document(listing.getListingId())
                                        .set(listing, SetOptions.merge());
                            }

                            finish();
                        } else {
                            Toast.makeText(EditListing.this, "Error creating listing", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUris.add(data.getData());
            Toast.makeText(EditListing.this, "UPLOADED!, Upload Next Image", Toast.LENGTH_SHORT).show();
        }
    }
}