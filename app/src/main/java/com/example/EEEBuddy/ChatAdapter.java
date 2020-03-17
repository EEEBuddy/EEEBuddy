package com.example.EEEBuddy;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    Context context;
    ArrayList<ChatModel> messageList;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userEmail, senderID;
    DatabaseReference studentProfileRef;
    FirebaseDatabase firebaseDatabase;


    public ChatAdapter(Context context, ArrayList<ChatModel> messageList){
        this.context = context;
        this.messageList = messageList;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(context).inflate(R.layout.message_layout,parent, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

        user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();

        senderID = userEmail.substring(0, userEmail.indexOf("@"));
        ChatModel chatModel = messageList.get(position);

        final String messageFrom = chatModel.getFrom();
        String messageType = chatModel.getType();

        holder.receiverMsgView.setVisibility(View.INVISIBLE);
        holder.receiverProfileImg.setVisibility(View.INVISIBLE);
        holder.senderMsgView.setVisibility(View.INVISIBLE);
        holder.senderProfileImg.setVisibility(View.INVISIBLE);


        studentProfileRef = firebaseDatabase.getReference().child("Student Profile");
        studentProfileRef.child(senderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String profileImgUrl = dataSnapshot.child("profileImageUrl").getValue().toString().trim();
                    Picasso.get().load(profileImgUrl).into(holder.senderProfileImg);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        studentProfileRef.child(messageFrom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String profileImgUrl = dataSnapshot.child("profileImageUrl").getValue().toString().trim();
                    Picasso.get().load(profileImgUrl).into(holder.receiverProfileImg);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        if(messageType.equals("text")){

            if(messageFrom.equals(senderID)){




                holder.receiverMsgView.setVisibility(View.INVISIBLE);
                holder.receiverProfileImg.setVisibility(View.INVISIBLE);
                holder.senderMsgView.setVisibility(View.VISIBLE);
                holder.senderProfileImg.setVisibility(View.VISIBLE);


                holder.senderMsgView.setBackgroundResource(R.drawable.sender_message_bubble);
                holder.senderMsgView.setGravity(Gravity.LEFT);
                holder.senderMsgView.setText(chatModel.getMessage());

            }else{




                holder.receiverMsgView.setVisibility(View.VISIBLE);
                holder.receiverProfileImg.setVisibility(View.VISIBLE);
                holder.senderMsgView.setVisibility(View.INVISIBLE);
                holder.senderProfileImg.setVisibility(View.INVISIBLE);

                holder.receiverMsgView.setBackgroundResource(R.drawable.receiver_message_bubble);
                holder.receiverMsgView.setGravity(Gravity.LEFT);
                holder.receiverMsgView.setText(chatModel.getMessage());
            }
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMsgView, receiverMsgView;
        public CircleImageView senderProfileImg, receiverProfileImg;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsgView = (TextView) itemView.findViewById(R.id.message_senderMsg);
            receiverMsgView = (TextView) itemView.findViewById(R.id.message_receiverMsg);

            senderProfileImg = (CircleImageView) itemView.findViewById(R.id.message_sender_profileImg);
            receiverProfileImg = (CircleImageView) itemView.findViewById(R.id.message_receiver_profileImg);



        }

    }

}
