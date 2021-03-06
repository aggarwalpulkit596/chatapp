package com.example.pulkit.chatapp1.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pulkit.chatapp1.ChatActivity;
import com.example.pulkit.chatapp1.Models.Conv;
import com.example.pulkit.chatapp1.R;
import com.example.pulkit.chatapp1.StartActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    private FirebaseRecyclerAdapter firebaseConvAdapter;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mConvList = mMainView.findViewById(R.id.conv_list);
//        mAuth = FirebaseAuth.getInstance();
//
//        if (mAuth == null) {
//
//            startActivity(new Intent(getActivity(), StartActivity.class));
//        }
//
//        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
//
//        mConvDatabase.keepSynced(true);
//        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
//        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
//        mUsersDatabase.keepSynced(true);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//
//        mConvList.setHasFixedSize(true);
//        mConvList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();
//        Log.i("TAG", "onStart: here");
//
//        Query conversationQuery = mConvDatabase.orderByChild("timestamp");
//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("Users");
//        FirebaseRecyclerOptions<Conv> options =
//                new FirebaseRecyclerOptions.Builder<Conv>()
//                        .setQuery(query, Conv.class)
//                        .build();
//
//        firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(options) {
//            @Override
//            public ConvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                return new ConvViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false));
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull final ConvViewHolder convViewHolder, int position, @NonNull final Conv conv) {
//
//                final String list_user_id = getRef(position).getKey();
//                Log.i("TAG", "onChildAdded: here" + list_user_id);
//
//
//                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);
//
//                lastMessageQuery.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                        String data = dataSnapshot.child("message").getValue().toString();
//                        convViewHolder.setMessage(data, conv.isSeen());
//                        Log.i("TAG", "onChildAdded: here");
//
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//
//                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        final String userName = dataSnapshot.child("name").getValue().toString();
//                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
//
//                        if (dataSnapshot.hasChild("online")) {
//
//                            String userOnline = dataSnapshot.child("online").getValue().toString();
//                            convViewHolder.setUserOnline(userOnline);
//
//                        }
//
//                        convViewHolder.setName(userName);
//                        convViewHolder.setUserImage(userThumb, getContext());
//
//                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//
//                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
//                                chatIntent.putExtra("user_id", list_user_id);
//                                chatIntent.putExtra("user_name", userName);
//                                startActivity(chatIntent);
//
//                            }
//                        });
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//
//        };
//
//        mConvList.setAdapter(firebaseConvAdapter);
//        firebaseConvAdapter.startListening();
//
//    }
//
//    public static class ConvViewHolder extends RecyclerView.ViewHolder {
//
//        View mView;
//
//        public ConvViewHolder(View itemView) {
//            super(itemView);
//            Log.i("TAG", "onChildAdded: here");
//
//            mView = itemView;
//
//        }
//
//        public void setMessage(String message, boolean isSeen) {
//
//
//            Log.i("TAG", "onChildAdded: here" + isSeen + message);
//
//
//            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
//            userStatusView.setText(message);
//
//            if (!isSeen) {
//                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
//            } else {
//                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
//            }
//
//        }
//
//        public void setName(String name) {
//
//            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
//            userNameView.setText(name);
//
//        }
//
//        public void setUserImage(String thumb_image, Context ctx) {
//
//            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_image);
//            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
//
//        }
//
//        public void setUserOnline(String online_status) {
//
//
//            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online);
//
//            if (online_status.equals("true")) {
//
//                userOnlineView.setVisibility(View.VISIBLE);
//
//            } else {
//
//                userOnlineView.setVisibility(View.INVISIBLE);
//
//            }
//
//        }


    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
