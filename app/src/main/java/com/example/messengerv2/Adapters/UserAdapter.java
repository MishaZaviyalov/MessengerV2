package com.example.messengerv2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messengerv2.Classes.FirebaseDictionary;
import com.example.messengerv2.MessageActivity;
import com.example.messengerv2.Models.User;
import com.example.messengerv2.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;

    public UserAdapter(Context mContext, List<User> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = mUser.get(position);
        holder.username.setText(user.getUsername());
        if(!user.getImageURL().isEmpty()){
            if(user.getImageURL().equals(FirebaseDictionary.USER_DEFAULT_IMG)){
                holder.profile_image.setImageResource(R.drawable.us1);
            } else{
                Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra(FirebaseDictionary.USER_ID, user.getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_card);
            profile_image = itemView.findViewById(R.id.profile_image_card);

        }
    }
}
