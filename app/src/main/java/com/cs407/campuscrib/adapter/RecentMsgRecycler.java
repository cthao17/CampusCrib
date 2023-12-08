package com.cs407.campuscrib.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs407.campuscrib.ChattingActivity;
import com.cs407.campuscrib.model.Chatroom;
import com.cs407.campuscrib.R;
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.auth.User;


public class RecentMsgRecycler extends FirestoreRecyclerAdapter<Chatroom,RecentMsgRecycler.ChatroomViewHolder> {

    Context context;
    public RecentMsgRecycler(@NonNull FirestoreRecyclerOptions<Chatroom> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomViewHolder holder, int position, @NonNull Chatroom model) {
        FirebaseUtil.getOtherUserFromChat(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserModel otherUser = task.getResult().toObject(UserModel.class);
                        holder.username.setText(otherUser.getUsername());
                        holder.lastMsgTxt.setText(model.getLatestMessage());
                        holder.lastMsgTime.setText(FirebaseUtil.timeStamptoString(model.getLastMessageTime()));
                    }
                });

    }

    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recentmsg_row_recycler,parent,false);
        return new ChatroomViewHolder(view);
    }

    class ChatroomViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        TextView lastMsgTxt;
        TextView lastMsgTime;
        ImageView profilePic;

        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name_txt);
            lastMsgTxt = itemView.findViewById(R.id.latest_txt);
            lastMsgTime = itemView.findViewById(R.id.msg_time);
            profilePic = itemView.findViewById(R.id.profile_pic_img);
        }
    }
}
