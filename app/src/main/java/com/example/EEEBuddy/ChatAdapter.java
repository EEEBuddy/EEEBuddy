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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    Context context;
    ArrayList<ChatModel> messageList;
    ArrayList<String> dateKeeper = new ArrayList<String>();
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userEmail, senderID;
    DatabaseReference studentProfileRef;
    FirebaseDatabase firebaseDatabase;
    String previous_message_date, current_message_date;
    Date previousDate, currentDate;


    public ChatAdapter(Context context, ArrayList<ChatModel> messageList, ArrayList<String> dateKeeper){
        this.context = context;
        this.messageList = messageList;
        this.dateKeeper = dateKeeper;
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


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        final String messageFrom = chatModel.getFrom();
        String messageType = chatModel.getType();
        current_message_date = chatModel.getDate();

        try {
            currentDate = sdf.parse(current_message_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(position != 0){
            ChatModel chatModelForDate = messageList.get(position-1);
            previous_message_date = chatModelForDate.getDate();

            try {
                previousDate = sdf.parse(previous_message_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }else{
            previous_message_date = "none";
        }

        holder.receiverMsgView.setVisibility(View.INVISIBLE);
        holder.receiverProfileImg.setVisibility(View.INVISIBLE);
        holder.senderMsgView.setVisibility(View.INVISIBLE);
        holder.senderProfileImg.setVisibility(View.INVISIBLE);
        holder.receiverMsgView_time.setVisibility(View.INVISIBLE);
        holder.senderMsgView_time.setVisibility(View.INVISIBLE);
        holder.date.setVisibility(View.INVISIBLE);


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

                holder.date.setVisibility(View.VISIBLE);
                holder.receiverMsgView.setVisibility(View.INVISIBLE);
                holder.receiverProfileImg.setVisibility(View.INVISIBLE);
                holder.receiverMsgView_time.setVisibility(View.INVISIBLE);
                holder.senderMsgView.setVisibility(View.VISIBLE);
                holder.senderProfileImg.setVisibility(View.VISIBLE);
                holder.senderMsgView_time.setVisibility(View.VISIBLE);


                holder.senderMsgView.setBackgroundResource(R.drawable.sender_message_bubble);
                holder.senderMsgView.setGravity(Gravity.LEFT);
                holder.senderMsgView.setText(chatModel.getMessage());
                holder.senderMsgView_time.setText(chatModel.getTime());

                //comparing if the current message is received on a different date from the previous message
                //if yes, show the current date.
                // if not, hide the current date.
                // previous_message_date.equals("none"), always show the date for very first message.

                if(previous_message_date.equals("none") || currentDate.compareTo(previousDate) > 0){
                    holder.date.setText(chatModel.getDate());

                }else if(currentDate.compareTo(previousDate) == 0 ){

                    holder.date.setVisibility(View.INVISIBLE);
                }

            }else{


                holder.date.setVisibility(View.VISIBLE);
                holder.receiverMsgView.setVisibility(View.VISIBLE);
                holder.receiverProfileImg.setVisibility(View.VISIBLE);
                holder.receiverMsgView_time.setVisibility(View.VISIBLE);
                holder.senderMsgView.setVisibility(View.INVISIBLE);
                holder.senderProfileImg.setVisibility(View.INVISIBLE);
                holder.senderMsgView_time.setVisibility(View.INVISIBLE);

                holder.receiverMsgView.setBackgroundResource(R.drawable.receiver_message_bubble);
                holder.receiverMsgView.setGravity(Gravity.LEFT);
                holder.receiverMsgView.setText(chatModel.getMessage());
                holder.receiverMsgView_time.setText(chatModel.getTime());

                if(previous_message_date.equals("none") || currentDate.compareTo(previousDate) > 0){
                    holder.date.setText(chatModel.getDate());

                }else if(currentDate.compareTo(previousDate) == 0 ){

                    holder.date.setVisibility(View.INVISIBLE);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMsgView, receiverMsgView, senderMsgView_time, receiverMsgView_time, date;
        public CircleImageView senderProfileImg, receiverProfileImg;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsgView = (TextView) itemView.findViewById(R.id.message_senderMsg);
            receiverMsgView = (TextView) itemView.findViewById(R.id.message_receiverMsg);

            senderMsgView_time = (TextView) itemView.findViewById(R.id.message_senderMsg_time);
            receiverMsgView_time = (TextView) itemView.findViewById(R.id.message_receiverMsg_time);
            date = (TextView) itemView.findViewById(R.id.message_date);


            senderProfileImg = (CircleImageView) itemView.findViewById(R.id.message_sender_profileImg);
            receiverProfileImg = (CircleImageView) itemView.findViewById(R.id.message_receiver_profileImg);



        }

    }

}
