package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.cs407.campuscrib.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.cs407.campuscrib.model.UserModel;

public class createAccount extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void createAccountClick(View view) {
        EditText user = findViewById(R.id.createUsernameEditText);
        EditText pw = findViewById(R.id.createPasswordEditText);
        EditText rePw = findViewById(R.id.createRepasswordEditText);
        String username = user.getText().toString();
        String password = pw.getText().toString();
        String repassword = rePw.getText().toString();

        if (username.equals("") || password.equals("") || repassword.equals("")) {
            Toast.makeText(this, "Please fill in all information", Toast.LENGTH_SHORT).show();
        } else if (!username.contains("@wisc.edu")) {
            Toast.makeText(this, "Username must be your wisc email", Toast.LENGTH_SHORT).show();
            user.getText().clear();
            pw.getText().clear();
            rePw.getText().clear();
        } else if (!password.equals(repassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            pw.getText().clear();
            rePw.getText().clear();
        } else {
            auth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // User creation successful
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                UserModel userModel = new UserModel(username, FirebaseUtil.currentUser());

                                db.collection("users")
                                        .document(userId)
                                        .set(userModel)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Firestore update successful
                                                    user.getText().clear();
                                                    pw.getText().clear();
                                                    rePw.getText().clear();
                                                    goToActivity();
                                                } else {
                                                    // Handle the error
                                                    Toast.makeText(createAccount.this, "Error storing user information", Toast.LENGTH_SHORT).show();
                                                    pw.getText().clear();
                                                    rePw.getText().clear();
                                                }
                                            }
                                        });
                            } else {
                                // User creation failed
                                Toast.makeText(createAccount.this, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show();
                                user.getText().clear();
                                pw.getText().clear();
                                rePw.getText().clear();
                            }
                        }
                    });
        }
    }

    public void onAlreadyGotLoginClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToActivity() {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }
}
