package com.example.pulkit.chatapp1;


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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;

    private static final CollectionReference sChatCollection =
            FirebaseFirestore.getInstance().collection("users");
    /**
     * Get the last 50 chat messages ordered by timestamp .
     */
    private static final Query sChatQuery = sChatCollection.limit(50);


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


    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.i("Checking Status", "onStart: ");
        attachRecyclerViewAdapter();

    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();
        Log.i("Checking Status", "onStart: ");

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.getItemCount();
        Log.i("Checking Status", "attachRecyclerViewAdapter: " + adapter.getItemCount());


    }

    private RecyclerView.Adapter newAdapter() {

//        Query query = FirebaseFirestore.getInstance().collection("users");
        Log.i("Checking Status", "onStart: ");
        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(sChatQuery, Users.class)
                .build();
        return new FirestoreRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup group, int i) {
                Log.i("Checking Status", "onStart: ");
                return new UsersViewHolder(LayoutInflater.from(group.getContext())
                        .inflate(R.layout.user_layout, group, false));

            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                Log.i("Checking Status", "onStart: ");

                holder.setName(model.getName());

            }
        };




    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name) {

            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
            Log.i("Get Data", "onBindViewHolder: " + name);


        }
    }

}
