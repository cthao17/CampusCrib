package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class createAccount extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
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

        if (!username.contains("@wisc.edu")) {
            Toast.makeText(this, "Username must be your wisc email", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(repassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            auth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user.getText().clear();
                                pw.getText().clear();
                                goToActivity();
                            } else {
                                Toast.makeText(createAccount.this, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show();
                                user.getText().clear();
                                pw.getText().clear();
                            }
                        }
                    });
        }
    }
    public void goToActivity() {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }
}