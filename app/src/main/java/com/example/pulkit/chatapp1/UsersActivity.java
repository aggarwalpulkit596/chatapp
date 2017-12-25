package com.example.pulkit.chatapp1;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.lang.ref.Reference;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;

    private FirestoreRecyclerAdapter adapter;

    private TextView mEmptyListMessage;

    private static final String TAG = "debugging";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.user_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView = findViewById(R.id.users_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEmptyListMessage = findViewById(R.id.emptyTextView);


    }


    @Override
    protected void onStart() {
        super.onStart();
        attachRecyclerViewAdapter();

    }

    private void attachRecyclerViewAdapter() {

        Query query = FirebaseFirestore.getInstance()
                .collection("users");
        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new UsersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {

                holder.bind(model, getApplicationContext());
                final String user_id = getSnapshots().getSnapshot(position).getId();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent profileintent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileintent.putExtra("user_id", user_id);
                        startActivity(profileintent);


                    }
                });

            }

            @Override
            public int getItemCount() {
                Log.i(TAG, "onStart: " + super.getItemCount());

                return super.getItemCount();

            }

            @Override
            public void onDataChanged() {
                mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        void bind(Users model, Context applicationContext) {
            setName(model.getName());
            setStatus(model.getStatus());
            setThumbImage(model.getThumb_image(), applicationContext);
        }

        void setStatus(String status) {
            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);

        }

        void setThumbImage(String thumbImage, Context applicationContext) {
            CircleImageView userImageView = mView.findViewById(R.id.user_image);
            if (!thumbImage.equals("default"))
                Picasso.with(applicationContext)
                        .load(thumbImage)
                        .placeholder(R.drawable.default_avatar)
                        .into(userImageView);

        }
    }
}

