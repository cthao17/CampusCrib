package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class homePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView textView = findViewById(R.id.myTextView2);

        if (user != null) {
            String username = user.getEmail();
            textView.setText("Logged in as " + username);
        }
    }
    public void createListingClick(View view) {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }
    public void listingClick(View view) {
        goToMapListingActivity();
    }
    public void updateProfileClick(View view) {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }
    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.top_menu, popup.getMenu());

        // remove home from menu when we are on home page
        MenuItem homeItem = popup.getMenu().findItem(R.id.home);
        homeItem.setVisible(false);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.logout) {
                    goToLoginActivity();
                    return true;
                    //} else if (item.getItemId() == R.id.personalProfile) {
                    //    goToProfileActivity();
                    //    return true;
                    } else if (item.getItemId() == R.id.MapListing) {
                        goToMapListingActivity();
                        return true;
                    //} else if (item.getItemId() == R.id.saved) {
                    //    goToSavedListingActivity();
                    //    return true;
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
    public void goToLoginActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
//    public void goToCreateListingActivity() {
//        Intent intent = new Intent(this, createActivity.class)
//        startActivity(intent);
//    }
//    public void goToProfileActivity() {
//        Intent intent = new Intent(this, ProfileActivity.class);
//        startActivity(intent);
//    }
    public void goToMapListingActivity() {
        Intent intent = new Intent(this, Map_Listing.class);
        startActivity(intent);
    }
//    public void goToSavedListingActivity() {
//        Intent intent = new Intent(this, SavedListingActivity.class);
//        startActivity(intent);
//    }
    public void goToPersonalListingActivity() {
        Intent intent = new Intent(this, Personal_Listing.class);
        startActivity(intent);
    }
    public void goToChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}