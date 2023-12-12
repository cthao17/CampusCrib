package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class ChatActivity extends AppCompatActivity {
    ImageButton searchButton;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener((v -> {
            goToHomePgae();
        }));

        searchButton = findViewById(R.id.main_search_btn);
        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(ChatActivity.this, SearchActivity.class));
        });
    }

    public void goToHomePgae() {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }

}
