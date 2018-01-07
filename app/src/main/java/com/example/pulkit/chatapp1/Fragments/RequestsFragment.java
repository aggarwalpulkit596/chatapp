package com.example.pulkit.chatapp1.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pulkit.chatapp1.MainActivity;
import com.example.pulkit.chatapp1.R;
import com.example.pulkit.chatapp1.StartActivity;
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
public class RequestsFragment extends Fragment {

    private View mView;

    private RecyclerView mRequestList;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mRequestDatabase, mFriendsDatabase, mUsersDatabase;

    private String current_uid;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestList = mView.findViewById(R.id.request_list);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            current_uid = mAuth.getCurrentUser().getUid();
        }


        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mRequestDatabase.keepSynced(true);

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendsDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
//            setData();
            mRequestList.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        }




    }

    private void setData() {

        Log.i("TAG", "setData: ");

        Query query = mRequestDatabase.child(current_uid);

        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(query, String.class)
                .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, RequestViewHolder>(options) {

            @Override
            public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RequestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_request, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull String model) {

                Log.i("TAG", "onBindViewHolder: " + model);
                mUsersDatabase.child("mnWKNjkhDzRjdsvff4eYakaJbdE3").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("thumb_image").getValue().toString();

                        holder.bind(name, image, getContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void bind(String name, final String image, final Context context) {
            TextView userNameTextView = mView.findViewById(R.id.request_user_name);

            final CircleImageView userImageView = mView.findViewById(R.id.request_user_image);

            userNameTextView.setText(name);
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
    }
}
