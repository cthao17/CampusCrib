package com.cs407.campuscrib;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditListing extends AppCompatActivity {

    private EditText locationEditText;
    private EditText costEditText;
    private EditText roomNumEditText;
    private EditText availabilityEditText;
    private EditText amenitiesEditText;
    private static final int IMAGE_PICK_REQUEST_CODE = 1;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private ActivityResultLauncher<Intent> imagePickLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        Button imagesButton = findViewById(R.id.images_button);
        Button submitButton = findViewById(R.id.submit_button);
        TextView headingTextView = findViewById(R.id.textView5);

        locationEditText = findViewById(R.id.EditTextLocation);
        costEditText = findViewById(R.id.EditTextCost);
        roomNumEditText = findViewById(R.id.EditTextRoomNum);
        availabilityEditText = findViewById(R.id.EditTextAvailability);
        amenitiesEditText = findViewById(R.id.EditTextAmenities);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView textView = findViewById(R.id.myTextView2);

        if (user != null) {
            String username = user.getEmail();
            textView.setText("Logged in as " + username);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("listingId")) {
            String listingId = intent.getStringExtra("listingId");
            submitButton.setText("Submit Edits");
            headingTextView.setText("Edit your Listing");

            retrieveListingData(listingId);
        }
        imagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditingExistingListing()) {
                    updateListing();
                } else {
                    createListing();
                }
            }
        });


        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUris.add(data.getData());
                            Toast.makeText(EditListing.this, "UPLOADED!, Upload Next Image", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditListing.this, "Error getting selected files", Toast.LENGTH_SHORT).show();
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
        String location = locationEditText.getText().toString().trim();
        String cost = costEditText.getText().toString().trim();
        String roomNum = roomNumEditText.getText().toString().trim();
        String availability = availabilityEditText.getText().toString().trim();
        String amenities = amenitiesEditText.getText().toString().trim();

        if (!isValidNumericValue(cost) || !isValidNumericValue(roomNum)) {
            Toast.makeText(EditListing.this, "Please enter valid numeric values for Cost and Number of Rooms", Toast.LENGTH_SHORT).show();
            return;
        }

        if (location.isEmpty() || cost.isEmpty() || roomNum.isEmpty() || availability.isEmpty() || amenities.isEmpty()) {
            Toast.makeText(EditListing.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("users").document(uid);

            ListingModel listing = new ListingModel(location, cost, roomNum, availability, amenities, Timestamp.now(), user.getEmail());

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

    private void updateListing() {
        String location = locationEditText.getText().toString().trim();
        String cost = costEditText.getText().toString().trim();
        String roomNum = roomNumEditText.getText().toString().trim();
        String availability = availabilityEditText.getText().toString().trim();
        String amenities = amenitiesEditText.getText().toString().trim();

        if (!isValidNumericValue(cost) || !isValidNumericValue(roomNum)) {
            Toast.makeText(EditListing.this, "Please enter valid numeric values for Cost and Number of Rooms", Toast.LENGTH_SHORT).show();
            return;
        }

        if (location.isEmpty() || cost.isEmpty() || roomNum.isEmpty() || availability.isEmpty() || amenities.isEmpty()) {
            Toast.makeText(EditListing.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference listingDocRef;

            if (isEditingExistingListing()) {
                String listingId = getIntent().getStringExtra("listingId");
                listingDocRef = db.collection("users").document(uid)
                        .collection("personalListing").document(listingId);
            } else {
                ListingModel newListing = new ListingModel(location, cost, roomNum, availability, amenities, Timestamp.now(), user.getEmail());
                listingDocRef = db.collection("users").document(uid)
                        .collection("personalListing").document(newListing.getListingId());
            }

            // Delete existing images linked with the listing
            if (!selectedImageUris.isEmpty()) {
                deleteListingImages(listingDocRef);
            }

            listingDocRef.update(
                    "location", location,
                    "cost", cost,
                    "roomNum", roomNum,
                    "availability", availability,
                    "amenities", amenities
            ).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditListing.this, "Listing updated successfully", Toast.LENGTH_SHORT).show();

                    // Upload images if selected
                    if (!selectedImageUris.isEmpty()) {
                        List<String> imageIds = new ArrayList<>();

                        for (Uri imageUri : selectedImageUris) {
                            String imageId = UUID.randomUUID().toString();
                            imageIds.add(imageId);
                            FirebaseUtil.getPersonalListingImageRef().child(listingDocRef.getId()).child(imageId).putFile(imageUri);
                        }

                        // Set image IDs in the listing, update the Firestore doc
                        listingDocRef.update("imageIds", imageIds)
                                .addOnCompleteListener(imageUploadTask -> {
                                    if (imageUploadTask.isSuccessful()) {
                                        Toast.makeText(EditListing.this, "Images uploaded successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EditListing.this, "Error uploading images", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    finish();
                } else {
                    Toast.makeText(EditListing.this, "Error updating listing", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isValidNumericValue(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void deleteListingImages(DocumentReference listingDocRef) {
        // Delete existing images linked with the listing
        listingDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ListingModel existingListing = task.getResult().toObject(ListingModel.class);

                if (existingListing != null && existingListing.getImageIds() != null) {
                    for (String imageId : existingListing.getImageIds()) {
                        FirebaseUtil.getPersonalListingImageRef().child(listingDocRef.getId()).child(imageId).delete()
                                .addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Toast.makeText(EditListing.this, "Existing images deleted successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EditListing.this, "Error deleting existing images", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
    }

    private void retrieveListingData(String listingId) {
        // Use Firestore to retrieve the listing data based on the listingId
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            db.collection("users").document(uid).collection("personalListing")
                    .document(listingId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ListingModel listing = task.getResult().toObject(ListingModel.class);

                            if (listing != null) {
                                // Populate the EditText fields with the retrieved data
                                locationEditText.setText(listing.getLocation());
                                costEditText.setText(listing.getCost());
                                roomNumEditText.setText(listing.getRoomNum());
                                availabilityEditText.setText(listing.getAvailability());
                                amenitiesEditText.setText(listing.getAmenities());
                            }
                        } else {
                            // Handle the error
                        }
                    });
        }
    }

    private boolean isEditingExistingListing() {
        Intent intent = getIntent();
        return intent != null && intent.hasExtra("listingId");
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