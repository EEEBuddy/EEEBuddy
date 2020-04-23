package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    private ImageView backBtn, rightIcon;
    private TextView toolbarTitle, notificationIcon;
    private CircleImageView profilePic;
    private ImageButton sendMsgBtn, sendImageBtn;
    private EditText msgEditText;
    private RecyclerView msgRecyclerView;
    private ArrayList<ChatModel> messageList = new ArrayList<>();
    private ArrayList<String> dateKeeper = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private GroupChatAdapter groupChatAdapter;
    private LinearLayoutManager linearLayoutManager;

    private String receiverUserID, receiverName, receiverProfileImgUrl, senderUserID;
    private String groupChatID, groupName, groupProfileImgUrl;
    private String source;

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference rootRef, studentProfileRef, messagesRef, notificationRef;
    private String userEmail, userNode, messageSentDate, messageSentTime, profileImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        InitialisedFields();

        GetIncomingIntents();

        DisplayReceiverInfo(source);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(Chat.this, BuddyManagementPage.class));
            }
        });

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendTextMessage(source);
            }
        });


        FetchMessages(source);


    }

    private void FetchMessages(String source) {

        if (source.equals("1to1Chat")) {

            rootRef.child("Messages").child("One to One Chat").child(senderUserID).child(receiverUserID)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            if (dataSnapshot.exists()) {
                                ChatModel messages = dataSnapshot.getValue(ChatModel.class);
                                messageList.add(messages);
                                String date = messages.getDate();
                                dateKeeper.add(date);

                                chatAdapter = new ChatAdapter(Chat.this, messageList, dateKeeper);
                                msgRecyclerView.setAdapter(chatAdapter);
                                chatAdapter.notifyDataSetChanged();
                                rootRef.child("Messages").keepSynced(true);

                                msgRecyclerView.smoothScrollToPosition(msgRecyclerView.getAdapter().getItemCount() - 1);

                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        } else if (source.equals("groupChat")) {

            rootRef.child("Messages").child("Group Chat").child(groupChatID).child("messages")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            if (dataSnapshot.exists()) {
                                ChatModel messages = dataSnapshot.getValue(ChatModel.class);
                                messageList.add(messages);
                                String date = messages.getDate();
                                dateKeeper.add(date);

                                groupChatAdapter = new GroupChatAdapter(Chat.this, messageList, dateKeeper);
                                msgRecyclerView.setAdapter(groupChatAdapter);
                                groupChatAdapter.notifyDataSetChanged();
                                rootRef.child("Messages").keepSynced(true);

                                msgRecyclerView.smoothScrollToPosition(msgRecyclerView.getAdapter().getItemCount() - 1);

                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }


    }

    private void SendTextMessage(String source) {

        if (source.equals("1to1Chat")) {

            String messageText = msgEditText.getText().toString().trim();

            if (TextUtils.isEmpty(messageText)) {
                Toast.makeText(Chat.this, "You can't send an empty message", Toast.LENGTH_LONG).show();
            } else {

                //path to store the message
                String message_sender_ref = "Messages/" + "One to One Chat/" + senderUserID + "/" + receiverUserID;
                String message_receiver_ref = "Messages/" + "One to One Chat/" + receiverUserID + "/" + senderUserID;

                //message unique key for each message

                DatabaseReference user_message_key = rootRef.child("Messages").child("One to One Chat").child(senderUserID).child(receiverUserID).push();
                String message_push_id = user_message_key.getKey();

                //time when message is sent
                Calendar calendarForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("yyyy/MM/dd");
                messageSentDate = currentDate.format(calendarForDate.getTime());

                Calendar calendarForTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa"); //aa for AM/PM
                messageSentTime = currentTime.format(calendarForTime.getTime());

                Map messageTextBody = new HashMap();
                messageTextBody.put("message", messageText);
                messageTextBody.put("time", messageSentTime);
                messageTextBody.put("date", messageSentDate);
                messageTextBody.put("type", "text");
                messageTextBody.put("from", senderUserID);

                Map messageBodyDetails = new HashMap();
                messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody);
                messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

                rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {

                            HashMap<String, String> chatNotificationMap = new HashMap<>();
                            chatNotificationMap.put("from", senderUserID);
                            chatNotificationMap.put("type", "message");

                            notificationRef.child(receiverUserID).push().setValue(chatNotificationMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Chat.this, "Sent", Toast.LENGTH_LONG).show();
                                                msgEditText.setText("");
                                                msgRecyclerView.smoothScrollToPosition(msgRecyclerView.getAdapter().getItemCount() - 1);
                                            }

                                        }
                                    });

                        } else {
                            Toast.makeText(Chat.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            msgRecyclerView.smoothScrollToPosition(msgRecyclerView.getAdapter().getItemCount() - 1);

                        }
                    }
                });


            }

        } else if (source.equals("groupChat")) {

            String messageText = msgEditText.getText().toString().trim();

            if (TextUtils.isEmpty(messageText)) {
                Toast.makeText(Chat.this, "You can't send an empty message", Toast.LENGTH_LONG).show();
            } else {

                //path to store the message
                String message_ref = "Messages/" + "Group Chat/" + groupChatID + "/" + "messages";

                //message unique key for each message

                DatabaseReference user_message_key = rootRef.child("Messages").child("Group Chat").child(groupChatID).child("messages").push();
                String message_push_id = user_message_key.getKey();

                //time when message is sent
                Calendar calendarForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("yyyy/MM/dd");
                messageSentDate = currentDate.format(calendarForDate.getTime());

                Calendar calendarForTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa"); //aa for AM/PM
                messageSentTime = currentTime.format(calendarForTime.getTime());

                Map messageTextBody = new HashMap();
                messageTextBody.put("message", messageText);
                messageTextBody.put("time", messageSentTime);
                messageTextBody.put("date", messageSentDate);
                messageTextBody.put("type", "text");
                messageTextBody.put("from", senderUserID);

                Map messageBodyDetails = new HashMap();
                messageBodyDetails.put(message_ref + "/" + message_push_id, messageTextBody);

                rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {

                            messagesRef = FirebaseDatabase.getInstance().getReference("Messages");
                            messagesRef.child("Group Chat").child(groupChatID).child("members")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from", senderUserID);
                                                chatNotificationMap.put("type", "group_message");
                                                chatNotificationMap.put("groupChatID", groupChatID);

                                                String receiverUserID = ds.getKey();
                                                notificationRef.child(receiverUserID).push().setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    Toast.makeText(Chat.this, "Sent", Toast.LENGTH_LONG).show();
                                                                    msgEditText.setText("");
                                                                }

                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                            // msgRecyclerView.smoothScrollToPosition(msgRecyclerView.getAdapter().getItemCount()-1);

                        } else {
                            Toast.makeText(Chat.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            //msgRecyclerView.smoothScrollToPosition(msgRecyclerView.getAdapter().getItemCount()-1);

                        }
                    }
                });


            }
        }


    }

    private void DisplayReceiverInfo(String source) {

        if (source.equals("1to1Chat")) {

            if (receiverProfileImgUrl.equals("none")) {
                studentProfileRef = firebaseDatabase.getReference("Student Profile");
                studentProfileRef.child(receiverUserID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                                profileImageUrl = userInfo.getProfileImageUrl();
                                receiverProfileImgUrl = profileImageUrl;
                                toolbarTitle.setText(receiverName);
                                Picasso.get().load(receiverProfileImgUrl).into(profilePic);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            } else {
                toolbarTitle.setText(receiverName);
                Picasso.get().load(receiverProfileImgUrl).into(profilePic);
            }

        } else if (source.equals("groupChat")) {

            toolbarTitle.setText(groupName);

            if (groupProfileImgUrl.equals("default")) {
                profilePic.setImageResource(R.drawable.group);
            }
        }


    }

    private void GetIncomingIntents() {

        source = getIntent().getStringExtra("from");

        if (source.equals("1to1Chat")) {

            receiverUserID = getIntent().getStringExtra("receiverUserID").trim();
            receiverName = getIntent().getStringExtra("receiverName").trim();
            receiverProfileImgUrl = getIntent().getStringExtra("receiverProfileImgUrl");
            senderUserID = getIntent().getStringExtra("senderUserID");

        } else if (source.equals("groupChat")) {

            groupChatID = getIntent().getStringExtra("groupChatID");
            groupName = getIntent().getStringExtra("groupName");
            groupProfileImgUrl = getIntent().getStringExtra("groupProfileImgUrl");
            senderUserID = getIntent().getStringExtra("senderUserID");

        }


    }


    private void InitialisedFields() {

        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        rightIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        profilePic = (CircleImageView) findViewById(R.id.toolbar_profilepic);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        sendMsgBtn = (ImageButton) findViewById(R.id.chat_sendMsgBtn);
        sendImageBtn = (ImageButton) findViewById(R.id.chat_sendImageBtn);
        msgEditText = (EditText) findViewById(R.id.chat_msg);


        rightIcon.setVisibility(View.GONE);
        backBtn.setVisibility(View.GONE);
        profilePic.setVisibility(View.VISIBLE);


        firebaseDatabase = FirebaseDatabase.getInstance();
        studentProfileRef = firebaseDatabase.getReference("Student Profile");
        messagesRef = firebaseDatabase.getReference("Messages");
        rootRef = firebaseDatabase.getReference();
        notificationRef = firebaseDatabase.getReference("Notification");
        firebaseAuth = FirebaseAuth.getInstance();

        messagesRef.keepSynced(true);


        msgRecyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setHasFixedSize(true);
        msgRecyclerView.setLayoutManager(linearLayoutManager);


    }

}
