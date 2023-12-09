package com.cs407.campuscrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;

public class Profile extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageView profilePic;
    private UserModel otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseFirestore.getInstance();
        profilePic = findViewById(R.id.profile_image_view);
        otherUser = AndroidFunctionsUtil.getUserModelIntent(getIntent());

        getUserData();
    }

    public void getUserData() {
        final Boolean[] insertion = {true};

        TextView emailView = findViewById(R.id.emailText);

        FirebaseUtil.getOtherProfilePicsRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        UserProfileModel.setProfilePic(this, uri, profilePic);
                    } else {
                        insertion[0] = false;
                    }
                });

        db.collection("users")
                .document(otherUser.getUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                UserProfileModel userProfile = document.toObject(UserProfileModel.class);

                                if (userProfile.getStatus() != null) {
                                    TextView statusText = findViewById(R.id.statusText);
                                    statusText.setText("Housing Status: " + userProfile.getStatus());
                                }

                                EditText aboutMeEdit = findViewById(R.id.aboutMeText);
                                aboutMeEdit.setText(userProfile.getAboutMe());

                                EditText housingEdit = findViewById(R.id.housingText);
                                housingEdit.setText(userProfile.getHousingPreferences());

                                EditText otherEdit = findViewById(R.id.otherText);
                                otherEdit.setText(userProfile.getOtherInfo());

                                if (userProfile.getName() != null) {
                                    EditText nameEdit = findViewById(R.id.updateNameText);
                                    nameEdit.setText(userProfile.getName());
                                }
                            }
                        } else {
                            insertion[0] = false;
                        }
                    }
                });

        if (!insertion[0]) {
            Toast.makeText(this, "Error Retrieving Info", Toast.LENGTH_SHORT).show();
        }
    }


    public void onMessageClick(View view) {
        otherUser = AndroidFunctionsUtil.getUserModelIntent(getIntent());

        Intent intent = new Intent(this, ChattingActivity.class);
        AndroidFunctionsUtil.passUsername(intent, otherUser);
        startActivity(intent);
    }
}