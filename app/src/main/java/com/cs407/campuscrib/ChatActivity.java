package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class ChatActivity extends AppCompatActivity {
    ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        searchButton = findViewById(R.id.main_search_btn);
        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(ChatActivity.this, SearchActivity.class));
        });
    }
}
