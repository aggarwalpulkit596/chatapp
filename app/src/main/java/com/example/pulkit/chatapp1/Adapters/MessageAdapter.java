package com.example.pulkit.chatapp1.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pulkit.chatapp1.Models.messages;
import com.example.pulkit.chatapp1.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pulkit on 12/30/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<messages> mMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<messages> mMessageList) {
        this.mMessageList = mMessageList;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        String current_uid = mAuth.getCurrentUser().getUid();
        String from_user;

        messages message = mMessageList.get(position);

        from_user = message.getFrom();

        if(from_user.equals(current_uid)){

            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);

        }else{
            holder.messageText.setBackgroundResource(R.drawable.message_text_back);
            holder.messageText.setTextColor(Color.WHITE);

        }
        holder.messageText.setText(message.getMessage());



    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView messageImage;


        public MessageViewHolder(View itemView) {
            super(itemView);

            messageImage = itemView.findViewById(R.id.message_image);
            messageText = itemView.findViewById(R.id.message_text);
        }
    }
}
