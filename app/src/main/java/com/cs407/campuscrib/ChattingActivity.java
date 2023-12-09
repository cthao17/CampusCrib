package com.cs407.campuscrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cs407.campuscrib.adapter.ChatRecycler;
import com.cs407.campuscrib.adapter.UserSearchRecycler;
import com.cs407.campuscrib.model.Chatroom;
import com.cs407.campuscrib.model.MessagesModel;
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.Arrays;

public class ChattingActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    Chatroom chatroomModel;
    EditText messageInput;
    ImageButton sendMsgBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ChatRecycler adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        otherUser = AndroidFunctionsUtil.getUserModelIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUser(),otherUser.getUserId());
        messageInput = findViewById(R.id.message_input);
        sendMsgBtn = findViewById(R.id.send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_user);
        recyclerView = findViewById(R.id.message_recycler_view);

        backBtn.setOnClickListener((v -> {
            goToChatActivity();
        }));

        otherUsername.setText(otherUser.getUsername());

        sendMsgBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessage(message);
        }));

        getCreateChatroom();
        setUpMessageRecyclerView();
    }

    void setUpMessageRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessagesRef(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MessagesModel> options = new FirestoreRecyclerOptions.Builder<MessagesModel>()
                .setQuery(query, MessagesModel.class).build();

        adapter = new ChatRecycler(options, getApplicationContext());
        LinearLayoutManager reverseLayout = new LinearLayoutManager(this);
        reverseLayout.setReverseLayout(true);
        recyclerView.setLayoutManager(reverseLayout);
        recyclerView.setAdapter(adapter);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessage(String message) {
        if (chatroomModel == null) {
            chatroomModel = new Chatroom(
                    chatroomId,
                    Arrays.asList(FirebaseUtil.currentUser(), otherUser.getUserId()),
                    Timestamp.now(),
                    ""
            );
            FirebaseUtil.getChatroomRef(chatroomId).set(chatroomModel);
        }

        chatroomModel.setLastMessageTime(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUser());
        chatroomModel.setLatestMessage(message);

        FirebaseUtil.getChatroomRef(chatroomId).set(chatroomModel);

        MessagesModel messagesModel = new MessagesModel(message, FirebaseUtil.currentUser(), Timestamp.now());
        FirebaseUtil.getChatroomMessagesRef(chatroomId).add(messagesModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageInput.setText("");
                    }
                });
    }

    void getCreateChatroom() {
        FirebaseUtil.getChatroomRef(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(Chatroom.class);
            }
        });
    }

    public void goToChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}