package com.cs407.campuscrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("loggedIn", false);
        if (isLoggedIn) {
            goToActivity();
            finish();
        }
    }

    public void loginPageLoginClick(View view) {
        EditText user = findViewById(R.id.usernameEditText);
        EditText pw = findViewById(R.id.passwordEditText);
        String username = user.getText().toString();
        String password = pw.getText().toString();
        if (username.equals("") || password.equals("")) {
            Toast.makeText(this, "Please fill in all information", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                goToActivity();
                                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("loggedIn", true);
                                editor.apply();
                                user.getText().clear();
                                pw.getText().clear();
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                pw.getText().clear();
                            }
                        }
                    });
        }
    }
    public void onCreateAccountClick(View view) {
        Intent intent = new Intent(this, createAccount.class);
        startActivity(intent);
    }
    public void goToActivity() {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }
}