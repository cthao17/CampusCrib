package com.cs407.campuscrib;

import static com.cs407.campuscrib.DBHelper.sqLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void loginClick(View view) {
        EditText user = findViewById(R.id.usernameEditText);
        EditText pw = findViewById(R.id.passwordEditText);
        String username = user.getText().toString();
        String password = pw.getText().toString();
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        if (dbHelper.isUserValid(username, password)) {
            sharedPreferences.edit().putString("username", username).apply();
            goToActivity();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
    public void goToActivity() {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }
}