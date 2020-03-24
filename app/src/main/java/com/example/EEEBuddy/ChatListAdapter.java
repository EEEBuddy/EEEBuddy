package com.example.EEEBuddy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChatModel> chatList_1to1_array;
    ArrayList<String> receiverID_array;
    String receiverID;

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference messagesRef, studentProfileRef;
    private String userEmail, userNode,getName,getProfileImageUrl, profileImageUrl;

    private Date today, parsedsentDate;


    public ChatListAdapter(Context context,ArrayList<ChatModel> chatList_1to1_array, ArrayList<String> receiverID_array ){

        this.context = context;
        this.chatList_1to1_array = chatList_1to1_array;
        this.receiverID_array = receiverID_array;
    }


    @NonNull
    @Override
    public ChatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ChatListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.chatlist_card,parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.MyViewHolder holder, final int position) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        receiverID = receiverID_array.get(position);

        messagesRef = firebaseDatabase.getReference("Messages").child("One to One Chat").child(userNode).child(receiverID);

        messagesRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        ChatModel chatModel = ds.getValue(ChatModel.class);
                        String last_msg = chatModel.getMessage();
                        String sentTime = chatModel.getTime();
                        String sentDate = chatModel.getDate();
                        holder.lastMsgTextView.setText(last_msg);


                        //check if the message is from today
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        String currentDate = sdf.format(calendar.getTime());

                        try {
                            today = new Date();
                            today = sdf.parse(sdf.format(new Date()));
                            parsedsentDate = sdf.parse(sentDate);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(today.compareTo(parsedsentDate) > 1){

                            holder.lastMsgTime.setText(sentDate);

                        }else if(today.compareTo(parsedsentDate) == 1){

                            holder.lastMsgTime.setText("Yesterday");

                        }else if(today.compareTo(parsedsentDate) == 0){

                            holder.lastMsgTime.setText(sentTime);
                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        messagesRef.keepSynced(true);

        studentProfileRef = firebaseDatabase.getReference("Student Profile");
        studentProfileRef.child(receiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                        getName = userInfo.getName();
                        getProfileImageUrl = userInfo.getProfileImageUrl();
                        holder.receiverName.setText(getName);
                        Picasso.get().load(getProfileImageUrl).into(holder.profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent chatIntent = new Intent(context, Chat.class);
                chatIntent.putExtra("senderUserID", userNode);
                chatIntent.putExtra("receiverUserID", receiverID_array.get(position));
                chatIntent.putExtra("receiverName", holder.receiverName.getText().toString());
                chatIntent.putExtra("receiverProfileImgUrl", "none");

                context.startActivity(chatIntent);
            }
        });



        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.chatlist_swipe_content));

        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        holder.swipeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "TODO..DELETE", Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList_1to1_array.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView receiverName, lastMsgTextView, lastMsgTime;
        TextView swipeDelete;
        SwipeLayout swipeLayout;
        CircleImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverName = (TextView) itemView.findViewById(R.id.chatlist_receiver_name);
            lastMsgTextView = (TextView) itemView.findViewById(R.id.chatlist_last_msg);
            profileImage = (CircleImageView) itemView.findViewById(R.id.chatlist_profilepic);
            lastMsgTime = (TextView) itemView.findViewById(R.id.chatlist_msg_time);


            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.chatlist_swipeLayout);
            swipeDelete = (TextView) itemView.findViewById(R.id.chatlist_swipe_delete);

        }

    }
}
