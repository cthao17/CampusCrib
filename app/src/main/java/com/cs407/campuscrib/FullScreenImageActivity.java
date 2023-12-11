package com.cs407.campuscrib;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.cs407.campuscrib.adapter.ImagePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        String listingId = getIntent().getStringExtra("listingId");
        String Uid = getIntent().getStringExtra("Uid");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            StorageReference folderRef = FirebaseStorage.getInstance().getReference()
                    .child("listing_images")
                    .child(Uid)
                    .child(listingId);

            // Retrieve image URLs
            folderRef.listAll().addOnSuccessListener(listResult -> {
                List<String> imageURIs = new ArrayList<>();
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageURIs.add(uri.toString());

                        ViewPager viewPager = findViewById(R.id.viewPager);
                        ImagePagerAdapter adapter = new ImagePagerAdapter(FullScreenImageActivity.this, imageURIs);
                        viewPager.setAdapter(adapter);
                    }).addOnFailureListener(e -> {
                        // Handle failure to get URL
                    });
                }
            });
        }
    }
}
