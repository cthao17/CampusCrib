package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs407.campuscrib.model.ListingModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Personal_Listing extends AppCompatActivity {
    private ImageView listingImage;
    private FirebaseFirestore db;
    private Uri selectedImageUri;
    private List<ListingModel> personalListing = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_listing);
        db = FirebaseFirestore.getInstance();
    }
//    public void onClickEdit(View view) {
//        Intent intent = new Intent(this, EditListing.class);
//        intent.putExtra("listing_Uid", ListingModel.getUid());
//        startActivity(intent);
//    }
    public void onClickDelete(View view) {

    }
    public void createListingClick(View view) {
        Intent intent = new Intent(this, EditListing.class);
        startActivity(intent);
    }
}