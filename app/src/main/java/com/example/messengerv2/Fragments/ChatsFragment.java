package com.example.messengerv2.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.messengerv2.Adapters.UserAdapter;
import com.example.messengerv2.Classes.FirebaseDictionary;
import com.example.messengerv2.Models.Chat;
import com.example.messengerv2.Models.User;
import com.example.messengerv2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView_chats;
    private UserAdapter adapter;
    private List<User> mUser;

    FirebaseUser fUser;
    DatabaseReference reference;

    private List<String> userList;

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        mContext = view.getContext();

        recyclerView_chats = view.findViewById(R.id.recycler_view_chats);
        recyclerView_chats.setHasFixedSize(true);
        recyclerView_chats.setLayoutManager(new LinearLayoutManager(view.getContext()));

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference(FirebaseDictionary.CHATS_REF);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat.getSender().equals(fUser.getUid())){
                        userList.add(chat.getReceiver());
                    }

                    if(chat.getReceiver().equals(fUser.getUid())){
                        userList.add(chat.getSender());
                    }

                    readChats();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void readChats(){
        mUser = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference(FirebaseDictionary.USER_REF);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    for (String id: userList){
                        if(user.getId().equals(id)){
                            if(mUser.size() != 0){
                                for (User user1 : mUser){
                                    if(!user.getId().equals(user1.getId())){
                                        mUser.add(user);
                                    }
                                }
                            } else{
                                mUser.add(user);
                            }
                        }
                    }
                }

                adapter = new UserAdapter(mContext, mUser);
                recyclerView_chats.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}