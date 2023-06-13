package com.example.messengerv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.messengerv2.Classes.FirebaseDictionary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText nickName, email, password;
    Button bt_register;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;

    private void Ini(){
        nickName = findViewById(R.id.ti_fullName);
        email = findViewById(R.id.ti_email);
        password = findViewById(R.id.ti_password);
        bt_register = findViewById(R.id.btn_register);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Ini();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = Objects.requireNonNull(nickName.getText()).toString().trim();
                String userEmail = Objects.requireNonNull(email.getText()).toString().trim();
                String userPassword = Objects.requireNonNull(password.getText()).toString().trim();

                if(TextUtils.isEmpty(userName)) {
                    nickName.setError("");
                } else if (TextUtils.isEmpty(userEmail)){
                    email.setError("");
                } else if (TextUtils.isEmpty(userPassword)) {
                    password.setError("");
                } else if (userPassword.length() < 6){
                    password.setError("");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    email.setError("");
                } else{
                    register(userName, userEmail, userPassword);
                }
            }
        });

    }

    private void register(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference(FirebaseDictionary.USER_REF).child(userID);

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put(FirebaseDictionary.USER_ID, userID);
                            hashMap.put(FirebaseDictionary.USER_NAME, username);
                            hashMap.put(FirebaseDictionary.USER_IMG, FirebaseDictionary.USER_DEFAULT_IMG);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else{
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}