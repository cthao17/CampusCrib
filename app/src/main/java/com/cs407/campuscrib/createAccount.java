package com.cs407.campuscrib;

import static com.cs407.campuscrib.DBHelper.sqLiteDatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class createAccount extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
    }

    public void createAccountClick() {
        EditText user = findViewById(R.id.createUsernameEditText);
        EditText pw = findViewById(R.id.createPasswordEditText);
        EditText rePw = findViewById(R.id.createRepasswordEditText);
        String username = user.getText().toString();
        String password = pw.getText().toString();
        String repassword = rePw.getText().toString();
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        if (!username.contains("@wisc.edu")) {
            Toast.makeText(this, "Username must be your wisc email", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(repassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            sharedPreferences.edit().putString("username", username).apply();
            dbHelper.addUser(username, password);
            goToActivity(username);
        }
    }

    public void goToActivity(String user) {
        Intent intent = new Intent(this, homePage.class);
        intent.putExtra("username", user);
        startActivity(intent);
    }
}
