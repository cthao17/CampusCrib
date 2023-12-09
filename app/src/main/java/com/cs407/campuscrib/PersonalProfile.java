package com.cs407.campuscrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import android.graphics.Color;
public class PersonalProfile extends AppCompatActivity {
    Boolean editMode = true;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        db = FirebaseFirestore.getInstance();
        getUserData();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView emailView = findViewById(R.id.emailText);
        EditText nameEdit = findViewById(R.id.updateNameText);

        if (user != null) {
            String username = user.getEmail();
            emailView.setText("Email: " + username);
        }

        if (nameEdit != null && nameEdit.getText().toString().equals("")) {
            String name = user.getEmail();
            int index = name.indexOf('@');
            if (index > -1) {
                name = name.substring(0, index);
            }
            nameEdit.setText(name);
        }

    }
    public void getUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    UserProfileModel userProfile = document.toObject(UserProfileModel.class);

                                    if (userProfile.getStatus() != null) {
                                        EditText statusEdit = findViewById(R.id.statusText);
                                        statusEdit.setText(userProfile.getStatus());
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
                                Toast.makeText(PersonalProfile.this, "Error Retrieving Info", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void onEditProfileClick(View view) {
        ImageButton editButton = findViewById(R.id.editButton);
        EditText statusEdit = findViewById(R.id.statusText);
        EditText aboutMeEdit = findViewById(R.id.aboutMeText);
        EditText housingEdit = findViewById(R.id.housingText);
        EditText otherEdit = findViewById(R.id.otherText);
        EditText nameEdit = findViewById(R.id.updateNameText);

        if(editMode) {
            editMode = false;
            editButton.setImageResource(R.drawable.baseline_browser_not_supported_24);
            setTextToEdit(nameEdit);
            setTextToEdit(statusEdit);
            setTextToEdit(aboutMeEdit);
            setTextToEdit(housingEdit);
            setTextToEdit(otherEdit);
            setEditBackground(nameEdit);
            setEditBackground(statusEdit);
        } else {
            editMode = true;
            editButton.setImageResource(R.drawable.baseline_app_registration_24);
            disableTextToEdit(nameEdit);
            disableTextToEdit(statusEdit);
            disableTextToEdit(aboutMeEdit);
            disableTextToEdit(housingEdit);
            disableTextToEdit(otherEdit);
            disableEditBackground(nameEdit);
            disableEditBackground(statusEdit);
            setUserData();
        }
    }
    private void setTextToEdit(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setCursorVisible(true);
    }
    private void disableTextToEdit(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setCursorVisible(false);
    }
    private void setEditBackground(EditText editText) {
        editText.setBackgroundResource(R.drawable.rounded_border);
        editText.setTextColor(Color.BLACK);
        editText.setPadding(5,0,0,0);
    }
    private void disableEditBackground(EditText editText) {
        editText.setBackground(null);
        editText.setTextColor(Color.WHITE);
        editText.setPadding(0,0,0,0);
    }
    public void setUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            EditText statusEdit = findViewById(R.id.statusText);
            String status = statusEdit.getText().toString();

            EditText aboutMeEdit = findViewById(R.id.aboutMeText);
            String aboutMe = aboutMeEdit.getText().toString();

            EditText housingEdit = findViewById(R.id.housingText);
            String housingPreferences = housingEdit.getText().toString();

            EditText otherEdit = findViewById(R.id.otherText);
            String otherInfo = otherEdit.getText().toString();

            EditText nameEdit = findViewById(R.id.updateNameText);
            String name = nameEdit.getText().toString();

            DocumentReference userDocRef = db.collection("users").document(uid);

            userDocRef
                    .set(
                            new UserProfileModel(status, aboutMe, housingPreferences, otherInfo, name),
                            SetOptions.merge()
                    )
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PersonalProfile.this, "Updated Profile", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PersonalProfile.this, "Error Saving", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void onListingClick(View view) {
        if (editMode){
            Intent intent = new Intent(this, Map_Listing.class);
            startActivity(intent);
        }
    }
    public void onSavedClick(View view){
        if (editMode){
            Intent intent = new Intent(this, SavedListing.class);
            startActivity(intent);
        }
    }
}