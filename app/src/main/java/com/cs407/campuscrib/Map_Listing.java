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
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Map_Listing extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_listing);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView textView = findViewById(R.id.myTextView2);

        if (user != null) {
            String username = user.getEmail();
            textView.setText("Logged in as " + username);
        }
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

    public void showSortPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.sort_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            String sortingOption = getSortOption(item.getItemId());
            if (!sortingOption.isEmpty()) {
                ((MainListingFragment) getSupportFragmentManager().findFragmentById(R.id.currentListingFragment))
                        .setUpListingView(sortingOption);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private String getSortOption(int itemId) {
        if (itemId == R.id.priceLowtoHigh) {
            return "priceLowtoHigh";
        } else if (itemId == R.id.priceHightoLow) {
            return "priceHightoLow";
        } else if (itemId == R.id.numRoomLowtoHigh) {
            return "numRoomLowtoHigh";
        } else if (itemId == R.id.numRoomHightoLow) {
            return "numRoomHightoLow";
        } else {
            return "";
        }
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