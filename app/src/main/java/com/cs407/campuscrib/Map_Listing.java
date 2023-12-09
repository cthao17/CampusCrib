package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Map_Listing extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_listing);
    }

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.top_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.logout) {
                    goToLoginActivity();
                    return true;
                } else if (item.getItemId() == R.id.home) {
                    goToHomeActivity();
                    return true;
                } else if (item.getItemId() == R.id.personalProfile) {
                    goToProfileActivity();
                    return true;
                } else if (item.getItemId() == R.id.MapListing) {
                    goToMapListingActivity();
                    return true;
                } else if (item.getItemId() == R.id.saved) {
                    goToSavedListingActivity();
                    return true;
                } else if (item.getItemId() == R.id.personalListing) {
                    goToPersonalListingActivity();
                    return true;
                } else if (item.getItemId() == R.id.chat) {
                    goToChatActivity();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popup.show();
    }
    public void goToHomeActivity() {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }
    public void goToLoginActivity() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("loggedIn", false);
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    //    public void goToCreateListingActivity() {
//        Intent intent = new Intent(this, createActivity.class)
//        startActivity(intent);
//    }
    public void goToProfileActivity() {
        Intent intent = new Intent(this, PersonalProfile.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 300);
    }
    public void goToMapListingActivity() {
        Intent intent = new Intent(this, Map_Listing.class);
        startActivity(intent);
    }
        public void goToSavedListingActivity() {
        Intent intent = new Intent(this, SavedListing.class);
        startActivity(intent);
    }
    public void goToPersonalListingActivity() {
        Intent intent = new Intent(this, Personal_Listing.class);
        startActivity(intent);
    }
    public void goToChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}