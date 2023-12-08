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
import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.R;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class UserSearchRecycler extends FirestoreRecyclerAdapter<UserModel,UserSearchRecycler.UserModelViewHolder> {

   Context context;
    public UserSearchRecycler(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.username.setText(model.getUsername());
        if(model.getUserId().equals(FirebaseUtil.currentUser())) {
            holder.username.setText(model.getUsername()+" (Me)");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChattingActivity.class);
            AndroidFunctionsUtil.passUsername(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_row_search_recycler,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name_txt);
            profilePic = itemView.findViewById(R.id.profile_pic_img);
        }
    }
}
