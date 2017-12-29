package com.example.pulkit.chatapp1.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pulkit.chatapp1.ChatActivity;
import com.example.pulkit.chatapp1.Models.Friends;
import com.example.pulkit.chatapp1.Models.Users;
import com.example.pulkit.chatapp1.ProfileActivity;
import com.example.pulkit.chatapp1.R;
import com.example.pulkit.chatapp1.SettingsActivity;
import com.example.pulkit.chatapp1.UsersActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendlist;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;

    private String current_uid;

    private View mView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendlist = mView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        current_uid = mAuth.getCurrentUser().getUid();


        mFriendlist.setHasFixedSize(true);
        mFriendlist.setLayoutManager(new LinearLayoutManager(getContext()));

        return mView;

    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .child(current_uid);
        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new FriendsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {
                holder.setDate(model.getDate());

                final String uid = getRef(position).getKey();
                final String[] name = new String[1];
                final String[] image = new String[1];

                mUsersDatabase.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot documentSnapshot) {

                        name[0] = documentSnapshot.child("name").getValue().toString();
                        image[0] = documentSnapshot.child("thumb_image").getValue().toString();

                        if (documentSnapshot.hasChild("online")) {
                            String userOnline =  documentSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }
                        holder.bind(name[0], image[0], getContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent chatintent = new Intent(getContext(), ChatActivity.class);
                        chatintent.putExtra("user_id", uid);
                        chatintent.putExtra("name", name[0]);
                        chatintent.putExtra("image", image[0]);
                        startActivity(chatintent);


                    }
                });
            }
        };
        mFriendlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date) {

            TextView userstatusTextView = mView.findViewById(R.id.user_single_status);
            userstatusTextView.setText(date);

        }

        public void bind(String name, final String image, final Context context) {
            TextView userNameTextView = mView.findViewById(R.id.user_single_name);

            final CircleImageView userImageView = mView.findViewById(R.id.user_image);


            userNameTextView.setText(name);
            if (!image.equals("default"))
                Picasso.with(context)
                        .load(image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar)
                        .into(userImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                if (!image.equals("default"))
                                    Picasso.with(context)
                                            .load(image)
                                            .placeholder(R.drawable.default_avatar)
                                            .into(userImageView);

                            }
                        });

        }

        public void setUserOnline(String userOnline) {

            CircleImageView userOnlineView = mView.findViewById(R.id.user_single_online);
            if (userOnline.equals("true")) {
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
