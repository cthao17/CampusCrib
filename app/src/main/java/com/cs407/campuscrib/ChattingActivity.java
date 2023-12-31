package com.cs407.campuscrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.cs407.campuscrib.adapter.ChatRecycler;
import com.cs407.campuscrib.model.Chatroom;
import com.cs407.campuscrib.model.MessagesModel;
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    ImageView imageView;


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
        imageView = findViewById(R.id.profile_pic_img);

        FirebaseUtil.getOtherProfilePicsRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        UserProfileModel.setProfilePic(this, uri, imageView);
                    }
                });

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

        findViewById(R.id.profile_pic_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfilePictureClick();
            }
        });
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
                        sendNotification(message);
                    }
                });
    }

    void sendNotification(String message){

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try{
                    JSONObject jsonObject  = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUsername());
                    notificationObj.put("body",message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);


                }catch (Exception e){

                }

            }
        });

    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAAzsdHmBk:APA91bGyqTJHWW2xCIAQLaGg8OMOyz8SkDyKpqKc1NvxxNOZwtGCnmU7HlBx-ss5JwjvVbbZLYDw5PwBs4kq33TKooabHdXvKiZSkYMvkU-hJgdl0MnUkUKTXYRJ--d8Y7LSyhyqBqiY")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

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

    public void onProfilePictureClick() {
        Intent intent = new Intent(this, Profile.class);
        AndroidFunctionsUtil.passUsername(intent, otherUser);
        startActivity(intent);
    }
}