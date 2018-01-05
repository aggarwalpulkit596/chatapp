package com.example.pulkit.chatapp1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pulkit.chatapp1.Adapters.MessageAdapter;
import com.example.pulkit.chatapp1.Models.GetTime;
import com.example.pulkit.chatapp1.Models.messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String chatUser, userName, userImage, userOnline;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private DatabaseReference mRootRef;
    private FirebaseUser currentUser;

    private TextView mUserName, mUserSeen;
    private CircleImageView mUserImage;

    private String uid;

    private ImageButton mSendBtn, mAddbtn;
    private EditText mMsgView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final List<messages> MessageList = new ArrayList<>();
    private MessageAdapter mAdapter;
    private LinearLayoutManager mLinearLayout;

    private static final int TOTAL_ITEM_LOAD = 10;
    private int mCurrentPage = 1;


    //new solution
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    // Storage Firebase
    private StorageReference mImageStorage;


    private static final int GALLERY_PICK = 1;


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


        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar = layoutInflater.inflate(R.layout.chat_bar, null);

        actionBar.setCustomView(action_bar);

        bindingViews();

        loadMessages();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                mCurrentPage++;
                itemPos = 0;

                loadmoreMessages();
            }

        });


    }

    private void bindingViews() {
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        uid = currentUser.getUid();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();


        mUserImage = findViewById(R.id.chatBarImageView);
        mUserName = findViewById(R.id.chatBarUserName);
        mUserSeen = findViewById(R.id.chatBarUserOnline);
        mAddbtn = findViewById(R.id.chat_addbtn);
        mSendBtn = findViewById(R.id.chat_sendbtn);
        mMsgView = findViewById(R.id.chat_msgview);
        mMessagesList = findViewById(R.id.messageslist);
        mSwipeRefreshLayout = findViewById(R.id.swipe_message_layout);

        mAdapter = new MessageAdapter(MessageList, getApplicationContext());

        mMessagesList.setHasFixedSize(true);
        mLinearLayout = new LinearLayoutManager(this);
        mMessagesList.setLayoutManager(mLinearLayout);
        mMessagesList.setAdapter(mAdapter);



    }

    private void loadmoreMessages() {
        DatabaseReference messageRef = mRootRef.child("messages").child(uid).child(chatUser);

        Query messagequery = messageRef.orderByKey().endAt(mLastKey).limitToFirst(10);

        messagequery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                messages messages = dataSnapshot.getValue(com.example.pulkit.chatapp1.Models.messages.class);

                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    MessageList.add(itemPos++, messages);

                }else{

                    mPrevKey = mLastKey;

                }

                if (itemPos == 1) {
                    mLastKey = messageKey;
                }

                mAdapter.notifyDataSetChanged();

                mSwipeRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10,0);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMessages() {


        DatabaseReference messageRef = mRootRef.child("messages").child(uid).child(chatUser);

        Query messagequery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEM_LOAD);

        messagequery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                messages messages = dataSnapshot.getValue(com.example.pulkit.chatapp1.Models.messages.class);

                itemPos++;

                if (itemPos == 1) {
                    mLastKey = dataSnapshot.getKey();
                    mPrevKey = mLastKey;
                }

                MessageList.add(messages);
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(MessageList.size() - 1);

                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");

        }
        settingview();

        chatFuctions();


    }

    private void chatFuctions() {

        mRootRef.child("chat").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatUser)) {

                    Map<String, Object> chatAddMap = new HashMap<>();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map<String, Object> chatUserMap = new HashMap<>();
                    chatUserMap.put("chat/" + uid + "/" + chatUser, chatAddMap);
                    chatUserMap.put("chat/" + chatUser + "/" + uid, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                Log.i("TAG", "onComplete: " + databaseError.getMessage().toString());
                            }

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

                    GetTime getTime = new GetTime();
                    long time = Long.parseLong(userOnline);

                    String lastseen = getTime.getTimeAgo(time, getApplicationContext());


                    mUserSeen.setText("last seen " + lastseen);
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

    public void SendMessage(View view) {

        getMessage();
    }

    private void getMessage() {

        String message = mMsgView.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + uid + "/" + chatUser;
            String chat_user_ref = "messages/" + chatUser + "/" + uid;

            DatabaseReference userMessagePush = mRootRef.child("messages")
                    .child(uid).child(chatUser).push();

            String push_id = userMessagePush.getKey();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", uid);

            Map<String, Object> messageUserMap = new HashMap<>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mMsgView.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.i("TAG", "onComplete: " + databaseError.getMessage().toString());
                    }
                }
            });

        }


    }

    public void sendImage(View view) {
        Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            final String current_user_ref = "messages/" + uid + "/" + chatUser;
            final String chat_user_ref = "messages/" + chatUser + "/" + uid;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(uid).child(chatUser).push();

            final String push_id = user_message_push.getKey();


            StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        String download_url = task.getResult().getDownloadUrl().toString();


                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", uid);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        mMsgView.setText("");

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }

                            }
                        });


                    }

                }
            });

        }
    }
}
