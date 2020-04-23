package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SeniorBuddyPage extends AppCompatActivity {

    Toolbar toolbar;
    TextView title, applyText;
    ImageView backBtn, filterIcon, reminder_icon, notificationIcon;

    TabLayout tabLayout;
    TabItem tab_recommendation, tab_all;
    PagerAdapter pageAdapter;
    ViewPager viewPager;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference buddyRequestRef, rootRef, seniorBuddyRef, studentProfileRef, notificationRef;
    private String userEmail, userNode;
    private FirebaseUser user;

    private String seniorBuddyID, juniorBuddyID;
    private int year;
    private Date parsedExpDate, today;


    SpaceNavigationView spaceNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_buddy_page);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            //if user has already logged out
            finish();
            startActivity(new Intent(this, Login.class));
        }

        //customise bottom navigation
        spaceNavigationView = findViewById(R.id.navigation);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Senior Buddy", R.drawable.ic_buddy_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Study Buddy", R.drawable.ic_msg_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Chat", R.drawable.ic_chat_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Account", R.drawable.ic_person_grey));

        spaceNavigationView.changeCurrentItem(0);
        spaceNavigationView.showIconOnly();


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(SeniorBuddyPage.this, "onCentreButtonClick", Toast.LENGTH_SHORT).show();
                spaceNavigationView.setCentreButtonSelectable(true);
                startActivity(new Intent(SeniorBuddyPage.this, HeyBuddy.class));

            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                //Toast.makeText(Account.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                if (itemIndex == 0) {//goto senior buddy page
                    //startActivity(new Intent(Account.this, SeniorBuddyPage.class));
                }
                if (itemIndex == 1) {//goto study buddy page
                    startActivity(new Intent(SeniorBuddyPage.this, StudyBuddyPage.class));
                }
                if (itemIndex == 2) {//goto chat
                    startActivity(new Intent(SeniorBuddyPage.this, ChatListPage.class));
                }
                if (itemIndex == 3) {//goto profile page
                    startActivity(new Intent(SeniorBuddyPage.this, Account.class));
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(SeniorBuddyPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });
        //customise bottom navigation above


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.toolbar_title);
        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        filterIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        reminder_icon = (ImageView) findViewById(R.id.seniorbuddy_reminder);
        notificationIcon = (ImageView) findViewById(R.id.navigation_notification);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.seniorbuddy_tablayout);
        tab_recommendation = (TabItem) findViewById(R.id.seniorbuddy_request);
        tab_all = (TabItem) findViewById(R.id.seniorbuddy_all);


        firebaseDatabase = FirebaseDatabase.getInstance();
        buddyRequestRef = firebaseDatabase.getReference("Buddy Requests");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));


        backBtn.setVisibility(View.GONE);
        title.setText("Senior Buddy");

        filterIcon.setVisibility(View.GONE);

        pageAdapter = new SeniorBuddyPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        UnreadMessageNotification();

        buddyRequestRef.keepSynced(true);
        buddyRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int tempCount = 0;
                if (dataSnapshot.hasChild(userNode)) {

                    for (DataSnapshot ds : dataSnapshot.child(userNode).getChildren()) {

                        String request_type = ds.getValue(BuddyRequestModel.class).getRequest_type();

                        if (request_type.equals("buddy_request_received") || request_type.equals("remove_request_received")) {
                            tempCount++;
                        }
                    }

                    if (tempCount > 0) {
                        reminder_icon.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    //All Senior Buddy Fragment
                    pageAdapter.notifyDataSetChanged();
                    filterIcon.setVisibility(View.VISIBLE);

                } else if (tab.getPosition() == 1) {
                    //Buddy Request Fragment
                    pageAdapter.notifyDataSetChanged();
                    filterIcon.setVisibility(View.GONE);

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        CheckRelationshipExpiryDate();

    }

    private void CheckRelationshipExpiryDate() {

        rootRef = FirebaseDatabase.getInstance().getReference();
        seniorBuddyRef = FirebaseDatabase.getInstance().getReference("Senior Buddy");
        studentProfileRef = FirebaseDatabase.getInstance().getReference("Student Profile");

        rootRef.child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    String expDate = dataSnapshot.child("BuddyRelationshipEXP").getValue().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    parsedExpDate = sdf.parse(expDate);
                    today = new Date();

                    if (today.compareTo(parsedExpDate) > 0) {

                        //set new expiry date for next batch of junior.
                        //all year 1 student will promote to year 2, no longer can apply for a senior buddy again.
                        //need to restart the application for new round of senior/junior application to take effect.

                        year = parsedExpDate.getYear() + 1900 + 1; // plus 1900 to get the current year
                        Calendar calendar = Calendar.getInstance();
                        Date newExp = new GregorianCalendar(year, calendar.JUNE, 31).getTime();
                        String BuddyRelationshipEXP = sdf.format(newExp);

                        rootRef.child("Admin").child("BuddyRelationshipEXP").setValue(BuddyRelationshipEXP);


                        //remove all buddy relationship
                        rootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild("Senior Buddy")) {

                                    DataSnapshot allSeniorBuddyDS = dataSnapshot.child("Senior Buddy");

                                    for (DataSnapshot seniorBuddy : allSeniorBuddyDS.getChildren()) {

                                        for (DataSnapshot juniorBuddy : seniorBuddy.child("juniorBuddy").getChildren()) {

                                            seniorBuddyID = seniorBuddy.getKey();
                                            juniorBuddyID = juniorBuddy.getKey();

                                            //remove buddy relationship
                                            seniorBuddyRef.child(seniorBuddyID).child("juniorBuddy").child(juniorBuddyID).removeValue();
                                            studentProfileRef.child(juniorBuddyID).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.hasChild("seniorBuddy")) {
                                                        studentProfileRef.child(juniorBuddyID).child("seniorBuddy").removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }


                                    }

                                }

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void UnreadMessageNotification() {

        notificationRef = FirebaseDatabase.getInstance().getReference("Notification");
        notificationRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    int count = 0;

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        NotificationModel notificationModel = ds.getValue(NotificationModel.class);
                        String type = notificationModel.getType();


                        if(type.equals("message") || type.equals("group_messages")){

                            count ++;
                        }

                    }

                    if(count>0){

                        notificationIcon.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
