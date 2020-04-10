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

public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChatModel> chatList_group_array;
    ArrayList<String> selected_groupID_array;
    String groupChatID;

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference messagesRef, studentProfileRef, studyEventRef, registeredEventRef;
    private String userEmail, userNode, getName, getProfileImageUrl, profileImageUrl;

    private Date parsedCurrentDate, parsedSentDate;
    private long diffDays;
    private String button_state = "upcoming";


    public GroupChatListAdapter(Context context, ArrayList<ChatModel> chatList_group_array, ArrayList<String> selected_groupID_array) {

        this.context = context;
        this.chatList_group_array = chatList_group_array;
        this.selected_groupID_array = selected_groupID_array;
    }


    @NonNull
    @Override
    public GroupChatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new GroupChatListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.chatlist_card, parent, false));

    }


    @Override
    public void onBindViewHolder(@NonNull final GroupChatListAdapter.MyViewHolder holder, final int position) {


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        groupChatID = selected_groupID_array.get(position);

        MaintenanceOfButton(groupChatID, holder);

        messagesRef = firebaseDatabase.getReference("Messages").child("Group Chat").child(groupChatID).child("messages");

        messagesRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messagesRef.keepSynced(true);
                if (dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

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


                        if (diffDays > 0 && diffDays != 1) {

                            holder.lastMsgTime.setText(sentDate);

                        } else if (diffDays == 1) {

                            holder.lastMsgTime.setText("Yesterday");

                        } else if (diffDays == 0) {

                            holder.lastMsgTime.setText(sentTime);
                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //messagesRef.keepSynced(true);


        messagesRef = firebaseDatabase.getReference("Messages").child("Group Chat").child(groupChatID).child("groupInfo");
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                String groupName = chatModel.getGroupName();
                holder.receiverName.setText(groupName);
                holder.profileImage.setImageResource(R.drawable.group);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent groupChatIntent = new Intent(context, Chat.class);

                groupChatIntent.putExtra("from", "groupChat");
                groupChatIntent.putExtra("senderUserID", userNode);
                groupChatIntent.putExtra("groupChatID", selected_groupID_array.get(position));
                groupChatIntent.putExtra("groupName", holder.receiverName.getText().toString());
                groupChatIntent.putExtra("groupProfileImgUrl", "default");

                context.startActivity(groupChatIntent);
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

                if (button_state.equals("passed")) {

                    holder.swipeDelete.setEnabled(true);

                    messagesRef = firebaseDatabase.getReference("Messages").child("Group Chat").child(groupChatID).child("members");

                    messagesRef.child(userNode).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(context, "Chat Deleted", Toast.LENGTH_LONG).show();
                                messagesRef = messagesRef = firebaseDatabase.getReference("Messages");
                                messagesRef.keepSynced(true);
                            }
                        }
                    });
                } else if (button_state.equals("upcoming")) {

                    holder.swipeDelete.setEnabled(false);
                    holder.swipeDelete.setBackgroundResource(R.color.gray);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList_group_array.size();
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

    public void MaintenanceOfButton(final String groupChatID, final GroupChatListAdapter.MyViewHolder holder) {

        studyEventRef = FirebaseDatabase.getInstance().getReference("Study Event").child(userNode);
        registeredEventRef = FirebaseDatabase.getInstance().getReference("Registered Event").child(userNode);

        studyEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(groupChatID)) {


                    StudyEvent getEventDateTime = dataSnapshot.child(groupChatID).getValue(StudyEvent.class);
                    String eventDate = getEventDateTime.getDate();
                    String eventStartTime = getEventDateTime.getStartTime();

                    try {
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date currentTime = new Date();
                        String eventDateTime = eventDate + " " + eventStartTime.substring(0, eventStartTime.lastIndexOf(" ")).trim();
                        Date formattedEventTime = sdf2.parse(eventDateTime);

                        if (formattedEventTime.compareTo(currentTime) > 0) {

                            button_state = "upcoming";
                            holder.swipeDelete.setEnabled(false);
                            holder.swipeDelete.setBackgroundResource(R.color.gray);
                        } else {

                            button_state = "passed";
                            holder.swipeDelete.setEnabled(true);

                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        registeredEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(groupChatID)) {

                    StudyEvent studyEvent = dataSnapshot.child(groupChatID).getValue(StudyEvent.class);
                    String createdBy = studyEvent.getCreatedBy();

                    studyEventRef = FirebaseDatabase.getInstance().getReference("Study Event").child(createdBy).child(groupChatID);
                    studyEventRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            StudyEvent getEventDateTime = dataSnapshot.getValue(StudyEvent.class);
                            String eventDate = getEventDateTime.getDate();
                            String eventStartTime = getEventDateTime.getStartTime();

                            try {
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                Date currentTime = new Date();
                                String eventDateTime = eventDate + " " + eventStartTime.substring(0, eventStartTime.lastIndexOf(" ")).trim();
                                Date formattedEventTime = sdf2.parse(eventDateTime);

                                if (formattedEventTime.compareTo(currentTime) > 0) {

                                    button_state = "upcoming";
                                    holder.swipeDelete.setEnabled(false);
                                    holder.swipeDelete.setBackgroundResource(R.color.gray);
                                } else {

                                    button_state = "passed";
                                    holder.swipeDelete.setEnabled(true);

                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
