package com.example.pulkit.chatapp1;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    //Firebase
    private FirebaseFirestore mUserDatabase, mFriendReqDatabse, mFriendsDatabase;
    private FirebaseUser mCurrentUser;

    private ImageView mProfileImage;
    private TextView mProfileStatus, mProfileName, mProfileFriendsCount;
    private Button mProfileReqBtn,mDeclineReqBtn;

    private String mCurrent_State;

    private String current_uid;

    private static final String TAG = "DEBUGGING";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();


        mProfileImage = findViewById(R.id.profile_image);
        mProfileName = findViewById(R.id.profile_displayname);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalfriends);
        mProfileReqBtn = findViewById(R.id.profile_sendrequest);
        mDeclineReqBtn = findViewById(R.id.profile_decline);

        mCurrent_State = "not friends";

        mUserDatabase = FirebaseFirestore.getInstance();
        mFriendReqDatabse = FirebaseFirestore.getInstance();
        mFriendsDatabase = FirebaseFirestore.getInstance();


        mUserDatabase.collection("users").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String image = documentSnapshot.getString("image");
                    String thumb_image = documentSnapshot.getString("thumb_image");
                    String status = documentSnapshot.getString("status");

                    mProfileName.setText(name);
                    mProfileStatus.setText(status);

                    if (!image.equals("default"))
                        Picasso.with(ProfileActivity.this)
                                .load(image)
                                .placeholder(R.drawable.default_avatar)
                                .into(mProfileImage);


                    //----------FRIEND LIST/REQUEST --------------
                    mFriendReqDatabse.collection("friend_requests").document(current_uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists() && documentSnapshot.contains(user_id)) {
                                Map<String, Object> abc = documentSnapshot.getData();
                                Object data = abc.get(user_id);

                                String req_type = data.toString().substring(14, data.toString().length() - 1);
                                Log.i(TAG, "onSuccess: " + req_type);
                                if (req_type.equals("recieved")) {

                                    mCurrent_State = "req_recieved";
                                    mProfileReqBtn.setText("Accept Friend Request");

                                    mDeclineReqBtn.setVisibility(View.VISIBLE);
                                    mDeclineReqBtn.setEnabled(true);
                                } else if (req_type.equals("req_sent")) {

                                    mCurrent_State = "req_sent";
                                    mProfileReqBtn.setText("Cancel Friend Request");
                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mDeclineReqBtn.setEnabled(false);
                                }
                            }else{
                                mFriendsDatabase.collection("friends").document(current_uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists() && documentSnapshot.contains(user_id)){

                                            mCurrent_State = "friends";
                                            mProfileReqBtn.setText("Unfriend");

                                            mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                            mDeclineReqBtn.setEnabled(false);
                                        }
                                    }
                                });
                            }

                        }
                    });


                }

            }
        });

        mProfileReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileReqBtn.setEnabled(false);

                //-----------------NOT FRIENDS STATE---------

                if (mCurrent_State.equals("not friends")) {
                    Map<String, Object> nesteddata = new HashMap<>();
                    nesteddata.put("request_type", "sent");
                    Map<String, Object> Data = new HashMap<>();
                    Data.put(user_id, nesteddata);

                    mFriendReqDatabse.collection("friend_requests").document(current_uid)
                            .set(Data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Map<String, Object> nesteddata = new HashMap<>();
                                nesteddata.put("request_type", "recieved");
                                Map<String, Object> Data = new HashMap<>();
                                Data.put(current_uid, nesteddata);
                                mFriendReqDatabse.collection("friend_requests").document(user_id)
                                        .set(Data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mCurrent_State = "req_sent";
                                        mProfileReqBtn.setText(R.string.Cancelfriendreq);

                                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mDeclineReqBtn.setEnabled(false);

                                        Toast.makeText(ProfileActivity.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                            }
                            mProfileReqBtn.setEnabled(true);

                        }
                    });
                }

                //-----------------CANCEL REQUEST STATE---------
                if (mCurrent_State.equals("req_sent")) {
                    Map<String, Object> Data = new HashMap<>();
                    Data.put(user_id, FieldValue.delete());

                    mFriendReqDatabse.collection("friend_requests").document(current_uid)
                            .update(Data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map<String, Object> Data = new HashMap<>();
                            Data.put(current_uid, FieldValue.delete());
                            mFriendReqDatabse.collection("friend_requests").document(user_id)
                                    .update(Data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileReqBtn.setEnabled(true);
                                    mCurrent_State = "not_friends";
                                    mProfileReqBtn.setText("Send Friend Request");

                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mDeclineReqBtn.setEnabled(false);
                                }
                            });
                        }
                    });


                }
                //-----------------REQUEST RECIEVED STATE---------
                if (mCurrent_State.equals("req_recieved")) {

                    Map<String, Object> data = new HashMap<>();
                    data.put(user_id, DateFormat.getDateTimeInstance().format(new Date()));

                    mFriendsDatabase.collection("friends").document(current_uid)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Map<String, Object> data = new HashMap<>();
                                    data.put(current_uid, DateFormat.getDateTimeInstance().format(new Date()));

                                    mFriendsDatabase.collection("friends").document(user_id)
                                            .set(data, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Map<String, Object> Data = new HashMap<>();
                                                    Data.put(user_id, FieldValue.delete());

                                                    mFriendReqDatabse.collection("friend_requests").document(current_uid)
                                                            .update(Data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Map<String, Object> Data = new HashMap<>();
                                                            Data.put(current_uid, FieldValue.delete());
                                                            mFriendReqDatabse.collection("friend_requests").document(user_id)
                                                                    .update(Data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mProfileReqBtn.setEnabled(true);
                                                                    mCurrent_State = "friends";
                                                                    mProfileReqBtn.setText("Unfriend");

                                                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                                    mDeclineReqBtn.setEnabled(false);
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });

                                }
                            });


                }

            }


        });
    }
}
