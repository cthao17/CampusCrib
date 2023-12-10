package com.cs407.campuscrib.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs407.campuscrib.ChattingActivity;
import com.cs407.campuscrib.UserProfileModel;
import com.cs407.campuscrib.model.Chatroom;
import com.cs407.campuscrib.R;
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.auth.User;


public class RecentMsgRecycler extends FirestoreRecyclerAdapter<Chatroom, com.cs407.campuscrib.adapter.RecentMsgRecycler.ChatroomViewHolder> {

    Context context;
    public RecentMsgRecycler(@NonNull FirestoreRecyclerOptions<Chatroom> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomViewHolder holder, int position, @NonNull Chatroom model) {
        // final Boolean[] insertion = {true};

        FirebaseUtil.getOtherUserFromChat(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean latestSentMsgCurrentUser = model.getLastMessageSenderId().equals(FirebaseUtil.currentUser());


                        UserModel otherUser = task.getResult().toObject(UserModel.class);
                        FirebaseUtil.getOtherProfilePicsRef(otherUser.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if(t.isSuccessful()){
                                        Uri uri  = t.getResult();
                                        UserProfileModel.setProfilePic(context, uri, holder.profilePic);
                                    }

                                    /*else {
                                        insertion[0] = false;
                                    }*/
                                });

                        holder.username.setText(otherUser.getUsername());
                        if(latestSentMsgCurrentUser)
                            holder.lastMsgTxt.setText("You : " + model.getLatestMessage());
                        else
                            holder.lastMsgTxt.setText(model.getLatestMessage());
                        holder.lastMsgTime.setText(FirebaseUtil.timeStamptoString(model.getLastMessageTime()));
                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChattingActivity.class);
                            AndroidFunctionsUtil.passUsername(intent,otherUser);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
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
