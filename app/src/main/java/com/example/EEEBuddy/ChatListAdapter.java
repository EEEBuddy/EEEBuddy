package com.example.EEEBuddy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChatModel> chatList_1to1_array;
    ArrayList<String> receiverID_array;
    String receiverID;

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference messagesRef, studentProfileRef, notificationRef;
    private String userEmail, userNode,getName,getProfileImageUrl, profileImageUrl;

    private Date parsedCurrentDate, parsedSentDate;
    private long diffDays;
    private int request_code = 0;


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

        UnreadMessageNotification(holder, receiverID);

        messagesRef = firebaseDatabase.getReference("Messages").child("One to One Chat").child(userNode).child(receiverID);

        messagesRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messagesRef.keepSynced(true);
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
                            parsedCurrentDate = sdf.parse(currentDate);
                            parsedSentDate = sdf.parse(sentDate);

                            long diff = Math.abs(parsedCurrentDate.getTime() - parsedSentDate.getTime());
                            diffDays = TimeUnit.MILLISECONDS.toDays(diff);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        if(diffDays > 0 && diffDays != 1){

                            holder.lastMsgTime.setText(sentDate);

                        }else if(diffDays == 1){

                            holder.lastMsgTime.setText("Yesterday");

                        }else if(diffDays == 0){

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

                chatIntent.putExtra("from", "1to1Chat");
                chatIntent.putExtra("senderUserID", userNode);
                chatIntent.putExtra("receiverUserID", receiverID_array.get(position));
                chatIntent.putExtra("receiverName", holder.receiverName.getText().toString());
                chatIntent.putExtra("receiverProfileImgUrl", "none");
                context.startActivity(chatIntent);

                DeleteNotification(holder, receiverID_array.get(position), 1);
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

                messagesRef = firebaseDatabase.getReference("Messages").child("One to One Chat").child(userNode).child(receiverID);

                messagesRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Chat Deleted", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList_1to1_array.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView receiverName, lastMsgTextView, lastMsgTime,notificationIcon;
        TextView swipeDelete;
        SwipeLayout swipeLayout;
        CircleImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverName = (TextView) itemView.findViewById(R.id.chatlist_receiver_name);
            lastMsgTextView = (TextView) itemView.findViewById(R.id.chatlist_last_msg);
            profileImage = (CircleImageView) itemView.findViewById(R.id.chatlist_profilepic);
            lastMsgTime = (TextView) itemView.findViewById(R.id.chatlist_msg_time);
            notificationIcon = (TextView) itemView.findViewById(R.id.chatlist_notification);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.chatlist_swipeLayout);
            swipeDelete = (TextView) itemView.findViewById(R.id.chatlist_swipe_delete);

        }

    }

    private void UnreadMessageNotification(final ChatListAdapter.MyViewHolder holder, final String receiverID) {

        notificationRef = FirebaseDatabase.getInstance().getReference("Notification");
        notificationRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    int count = 0;

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        String from = ds.getValue(NotificationModel.class).getFrom();
                        String type = ds.getValue(NotificationModel.class).getType();

                        if(type.equals("message") && from.equals(receiverID)){

                            count ++;
                        }

                    }

                    if(count>0){

                        holder.notificationIcon.setVisibility(View.VISIBLE);
                        holder.notificationIcon.setText(String.valueOf(count));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DeleteNotification(final ChatListAdapter.MyViewHolder holder, final String receiverID, final int request_code) {
        //delete the notification

        if(request_code == 1){

            final ArrayList<String> tempIDArray = new ArrayList<>();

            notificationRef = FirebaseDatabase.getInstance().getReference("Notification");
            notificationRef.child(userNode).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        for(DataSnapshot ds : dataSnapshot.getChildren()){

                            String notificationKey = ds.getKey();
                            String from = ds.getValue(NotificationModel.class).getFrom();
                            String type = ds.getValue(NotificationModel.class).getType();

                            if(type.equals("message") && from.equals(receiverID)){
                                tempIDArray.add(notificationKey);
                            }

                        }

                        for(String notificationKey : tempIDArray){

                            notificationRef.child(userNode).child(notificationKey).removeValue();
                        }

                        holder.notificationIcon.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            return;
        }

    }
}
