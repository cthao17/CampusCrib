package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

public class Personal_Listing extends AppCompatActivity {
    private ImageView listingImage;
    private FirebaseFirestore db;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_listing);
    }

    public void onClickEdit(View view){
        Intent intent = new Intent(this, EditListing.class);
        intent.putExtra("listing_id", /*listing_id*/);
        startActivity(intent);
    }

    public void onClickDelete(View view){

    }


}