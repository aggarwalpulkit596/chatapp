package com.example.pulkit.chatapp1;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String chatUser, userName, userImage, userOnline;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private DatabaseReference mRootRef;

    private TextView mUserName, mUserSeen;
    private CircleImageView mUserImage;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatUser = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("name");


        mToolbar = findViewById(R.id.chatAppBar);
        setSupportActionBar(mToolbar);


        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


//        getSupportActionBar().setTitle(userName);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar = layoutInflater.inflate(R.layout.chat_bar, null);

        actionBar.setCustomView(action_bar);


        mUserImage = findViewById(R.id.chatBarImageView);
        mUserName = findViewById(R.id.chatBarUserName);
        mUserSeen = findViewById(R.id.chatBarUserOnline);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");

        }
        settingview();


    }

    private void settingview() {

        mUserName.setText(userName);
        Log.i("TAG", "settingview: " + userName);

        mRootRef.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userImage = dataSnapshot.child("image").getValue().toString();
                userOnline = dataSnapshot.child("online").getValue().toString();

                if (userOnline.equals("true")) {
                    mUserSeen.setText("Online");
                } else {
                    mUserSeen.setText(userOnline);
                }
                if (!userImage.equals("default"))
                    Picasso.with(ChatActivity.this)
                            .load(userImage)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar)
                            .into(mUserImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    if (!userImage.equals("default"))
                                        Picasso.with(ChatActivity.this)
                                                .load(userImage)
                                                .placeholder(R.drawable.default_avatar)
                                                .into(mUserImage);

                                }
                            });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");

        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);

        }

    }
}
