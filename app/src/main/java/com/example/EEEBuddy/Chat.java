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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    private ImageView backBtn, rightIcon;
    private TextView toolbarTitle;
    private CircleImageView profilePic;
    private ImageButton sendMsgBtn, sendImageBtn;
    private EditText msgEditText;
    private RecyclerView msgRecyclerView;
    private ArrayList<ChatModel> messageList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private LinearLayoutManager linearLayoutManager;

    private String receiverUserID, receiverName, receiverProfileImgUrl,senderUserID;

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference rootRef, studentProfileRef, messagesRef;
    private String userEmail, userNode, messageSentDate, messageSentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        InitialisedFields();

        GetIncomingIntents();

        DisplayReceiverInfo();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Chat.this, BuddyManagementPage.class));
            }
        });

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendTextMessage();
            }
        });


        FetchMessages();


    }

    private void FetchMessages() {

        rootRef.child("Messages").child("One to One Chat").child(senderUserID).child(receiverUserID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if(dataSnapshot.exists()){
                            ChatModel messages = dataSnapshot.getValue(ChatModel.class);
                            messageList.add(messages);
                            chatAdapter.notifyDataSetChanged();
                            rootRef.child("Messages").keepSynced(true);
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

    private void SendTextMessage() {

        String messageText = msgEditText.getText().toString().trim();

        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(Chat.this,"You can't send an empty message", Toast.LENGTH_LONG).show();
        }
        else{

            //path to store the message
            String message_sender_ref = "Messages/" + "One to One Chat/" + senderUserID + "/" + receiverUserID;
            String message_receiver_ref = "Messages/" + "One to One Chat/" + receiverUserID +"/" + senderUserID;

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

                    if(task.isSuccessful()){
                        Toast.makeText(Chat.this,"Sent", Toast.LENGTH_LONG).show();
                        msgEditText.setText("");

                    }
                    else{
                        Toast.makeText(Chat.this,"Error:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            });



        }

    }

    private void DisplayReceiverInfo() {
        toolbarTitle.setText(receiverName);
        Picasso.get().load(receiverProfileImgUrl).into(profilePic);
    }

    private void GetIncomingIntents() {

        receiverUserID = getIntent().getStringExtra("receiverUserID").trim();
        receiverName = getIntent().getStringExtra("receiverName").trim();
        receiverProfileImgUrl = getIntent().getStringExtra("receiverProfileImgUrl");
        senderUserID = getIntent().getStringExtra("senderUserID");
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
        profilePic.setVisibility(View.VISIBLE);


        firebaseDatabase = FirebaseDatabase.getInstance();
        studentProfileRef = firebaseDatabase.getReference("Student Profile");
        messagesRef = firebaseDatabase.getReference("Messages");
        rootRef = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        messagesRef.keepSynced(true);

        chatAdapter = new ChatAdapter(Chat.this, messageList);
        msgRecyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setHasFixedSize(true);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        msgRecyclerView.setAdapter(chatAdapter);


    }
}