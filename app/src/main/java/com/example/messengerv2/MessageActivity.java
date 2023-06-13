package com.example.messengerv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.messengerv2.Adapters.MessageAdapter;
import com.example.messengerv2.Classes.FirebaseDictionary;
import com.example.messengerv2.Models.Chat;
import com.example.messengerv2.Models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView userName;

    FirebaseUser fUser;

    DatabaseReference reference;

    FloatingActionButton btn_send;

    TextInputEditText text_send;

    MessageAdapter messageAdapter;

    List<Chat> mchat;

    RecyclerView recyclerView;
    Intent intent;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //RecyclerView redactor
        recyclerView = findViewById(R.id.recycler_view_message);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Ini
        profile_image = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        intent = getIntent();

        //Get current user
        String userID = intent.getStringExtra(FirebaseDictionary.USER_ID);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        //Clicks
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.isEmpty()){
                    sendMessage(fUser.getUid(), userID, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "НЕТ", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });


        reference = FirebaseDatabase.getInstance().getReference(FirebaseDictionary.USER_REF).child(userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                userName.setText(user.getUsername());

                if(user.getImageURL().equals(FirebaseDictionary.USER_DEFAULT_IMG)){
                    profile_image.setImageResource(R.drawable.us1);
                } else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                }

                readMessage(fUser.getUid(), userID, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(FirebaseDictionary.MESSAGE_SENDER, sender);
        hashMap.put(FirebaseDictionary.MESSAGE_RECEIVER, receiver);
        hashMap.put(FirebaseDictionary.MESSAGE_MESSAGE, message);

        reference.child(FirebaseDictionary.CHATS_REF).push().setValue(hashMap);
    }

    private void readMessage(String myid, String userid, String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference(FirebaseDictionary.CHATS_REF);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender() != null){
                        if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                            mchat.add(chat);
                        }
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}