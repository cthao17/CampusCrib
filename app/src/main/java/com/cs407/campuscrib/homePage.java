package com.cs407.campuscrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

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
        getFCMToken();
    }
    void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        });
    }
    public void createListingClick(View view) {
        goToCreateListingActivity();
    }
    public void listingClick(View view) {
        goToMapListingActivity();
    }
    public void updateProfileClick(View view) {
        goToProfileActivity();
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
    public void goToLoginActivity() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("loggedIn", false);
        editor.apply();
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseUtil.logout();
                    Intent intent = new Intent(homePage.this ,SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void goToCreateListingActivity() {
        Intent intent = new Intent(this, EditListing.class);
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