package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListPage extends AppCompatActivity {

    //toolbar elements
    private ImageView backBtn, rightIcon;
    private TextView toolbarTitle, chatLabel_1to1, chatLabel_group;
    private CircleImageView profileImage;
    private RecyclerView chatList_1to1_recycler, chatList_group_recycler;
    private ArrayList<ChatModel> chatList_1to1_array, chatList_group_array;
    private ArrayList<String> receiverID_array, all_groupID_array, selected_groupID_array;
    private ChatListAdapter adapter;
    private GroupChatListAdapter groupChatListAdapter;
    //private HashMap<ArrayList<String>, ArrayList<ChatModel>> chatListHashMap = new HashMap<ArrayList<String>, ArrayList<ChatModel>>();

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference studentProfileRef, messagesRef, studyEventRef, registeredEventRef, notificationRef;
    private String userEmail, userNode, receiverID;

    private SpaceNavigationView spaceNavigationView;

    private TextView receiverName;
    private String getName, getProfileImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Initialization();
        NavigationSetUp(savedInstanceState);
        PrivateChatList();
        GroupChatList();

        /*

        chatLabel_1to1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatList_1to1_recycler.getVisibility() == View.INVISIBLE){
                    chatList_1to1_recycler.setVisibility(View.VISIBLE);
                }else{
                    chatList_1to1_recycler.setVisibility(View.INVISIBLE);
                }
            }
        });



        chatLabel_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatList_group_recycler.getVisibility() == View.INVISIBLE){
                    chatList_group_recycler.setVisibility(View.VISIBLE);
                }else{
                    chatList_group_recycler.setVisibility(View.INVISIBLE);
                }
            }
        });

         */



    }


    private void GroupChatList() {

        chatList_group_array = new ArrayList<ChatModel>();
        chatList_group_recycler.setLayoutManager(new LinearLayoutManager(ChatListPage.this));

        all_groupID_array = new ArrayList<String>();


        messagesRef = firebaseDatabase.getReference("Messages").child("Group Chat");
        registeredEventRef = firebaseDatabase.getReference("Registered Event").child(userNode);
        studyEventRef = firebaseDatabase.getReference("Study Event").child(userNode);

        messagesRef.keepSynced(true);
        registeredEventRef.keepSynced(true);
        studyEventRef.keepSynced(true);

        studyEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    all_groupID_array.clear();

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        String groupID = ds.getKey();
                        all_groupID_array.add(groupID);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        registeredEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot ds2 : dataSnapshot.getChildren()){

                        String groupID = ds2.getKey();
                        all_groupID_array.add(groupID);

                    }

                    MyGroupChat();

                }else{

                    MyGroupChat();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void MyGroupChat(){

        selected_groupID_array = new ArrayList<String>();

        chatList_group_array.clear();
        selected_groupID_array.clear();

        for(final String item : all_groupID_array){

            messagesRef.keepSynced(true);
            messagesRef = firebaseDatabase.getReference("Messages").child("Group Chat");
            messagesRef.child(item).child("members").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(userNode)){

                        messagesRef.child(item).child("messages").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    selected_groupID_array.add(item);

                                    ChatModel chatModel_groupChat = dataSnapshot.getValue(ChatModel.class);
                                    chatList_group_array.add(chatModel_groupChat);

                                //adapter here
                                groupChatListAdapter = new GroupChatListAdapter(ChatListPage.this, chatList_group_array, selected_groupID_array);
                                chatList_group_recycler.setAdapter(groupChatListAdapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void PrivateChatList() {

        chatList_1to1_array = new ArrayList<ChatModel>();
        chatList_1to1_recycler.setLayoutManager(new LinearLayoutManager(ChatListPage.this));


        receiverID_array = new ArrayList<String>();


        messagesRef = firebaseDatabase.getReference("Messages").child("One to One Chat").child(userNode);
        messagesRef.keepSynced(true);

        //chatList_1to1_array.clear();
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList_1to1_array.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    receiverID = ds.getKey();
                    receiverID_array.add(receiverID);

                    ChatModel chatModel_1to1 = ds.getValue(ChatModel.class);
                    chatList_1to1_array.add(chatModel_1to1);

                }

                adapter = new ChatListAdapter(ChatListPage.this,chatList_1to1_array,receiverID_array);
                chatList_1to1_recycler.setAdapter(adapter);

                //adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void NavigationSetUp(Bundle savedInstanceState) {

        spaceNavigationView = findViewById(R.id.navigation);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Senior Buddy", R.drawable.ic_buddy_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Study Buddy", R.drawable.ic_msg_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Chat", R.drawable.ic_chat_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Account", R.drawable.ic_person_grey));

        spaceNavigationView.changeCurrentItem(2);
        spaceNavigationView.showIconOnly();


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(ChatListPage.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                spaceNavigationView.setCentreButtonSelectable(true);
                startActivity(new Intent(ChatListPage.this, HeyBuddy.class));
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                //Toast.makeText(Account.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                if(itemIndex == 0){//goto senior buddy page
                    startActivity(new Intent(ChatListPage.this, SeniorBuddyPage.class));
                }
                if(itemIndex == 1){//goto study buddy page
                    startActivity(new Intent(ChatListPage.this, StudyBuddyPage.class));
                }
                if(itemIndex == 2){//goto chat
                    //startActivity(new Intent(Account.this, ChatListPage.class));
                }
                if(itemIndex == 3){//goto profile page
                    startActivity(new Intent(ChatListPage.this, Account.class));

                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(ChatListPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void Initialization() {
        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        rightIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        chatLabel_1to1 = (TextView) findViewById(R.id.chatlist_1to1chat_label);
        chatLabel_group = (TextView) findViewById(R.id.chatlist_groupchat_label);


        chatList_1to1_recycler = (RecyclerView) findViewById(R.id.chatlist_1to1chat);
        chatList_group_recycler = (RecyclerView) findViewById(R.id.chatlist_groupchat);

        backBtn.setVisibility(View.GONE);
        rightIcon.setVisibility(View.GONE);
        toolbarTitle.setText("My Chat");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));


    }
}
