package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuddyManagementPage extends AppCompatActivity {

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference seniorBuddyRef, studentProfileRef;
    private String userEmail, userNode;


    private LinearLayout juniorBuddyLayout, seniorBuddyLayout;
    private Toolbar toolbar;
    private TextView title;
    private ImageView rightIcon, toolbar_backBtn;
    private LinearLayout btnLayout;

    private ImageView info_backBtn, messageBtn;
    private LinearLayout pageLayout, intro_section;
    private ScrollView scrollView;
    private CircleImageView profilePic;
    private TextView infoName, infoEmail;
    private Button requestBtn, declineBtn;
    private TextView infoCourse, infoHall, infoGender, infoExperience, infoIntro;

    private RecyclerView juniorBuddyList;
    private String seniorBuddy, exp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_management_page);


        firebaseDatabase = FirebaseDatabase.getInstance();
        studentProfileRef = firebaseDatabase.getReference("Student Profile");
        seniorBuddyRef = firebaseDatabase.getReference("Senior Buddy");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        juniorBuddyLayout = (LinearLayout) findViewById(R.id.juniorSection_layout);
        seniorBuddyLayout = (LinearLayout) findViewById(R.id.seniorSection_layout);
        juniorBuddyList = (RecyclerView) findViewById(R.id.buddy_juniorBuddyList);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.toolbar_title);
        rightIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        toolbar_backBtn = (ImageView) findViewById(R.id.toolbar_back);

        info_backBtn = (ImageView) findViewById(R.id.info_back);
        messageBtn = (ImageView) findViewById(R.id.info_msgBtn);
        profilePic = (CircleImageView) findViewById(R.id.info_profilepic);
        infoName = (TextView) findViewById(R.id.info_name);
        infoEmail = (TextView) findViewById(R.id.info_email);
        infoCourse = (TextView) findViewById(R.id.info_course);
        infoHall = (TextView) findViewById(R.id.info_Hall);
        infoGender = (TextView) findViewById(R.id.info_gender);
        infoExperience = (TextView) findViewById(R.id.info_exp);

        pageLayout = (LinearLayout) findViewById(R.id.seniorbuddy_linearLayout);
        intro_section = (LinearLayout) findViewById(R.id.seniorbuddy_intro_section);
        scrollView = (ScrollView) findViewById(R.id.info_scrollview);


        btnLayout = (LinearLayout) findViewById(R.id.info_buttons_layout);
        requestBtn = (Button) findViewById(R.id.info_requestBtn);
        declineBtn = (Button) findViewById(R.id.info_declineBtn);


        //switch between senior and junior layout of buddy management page
        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userNode)){
                    //senior buddy layout
                    seniorBuddyLayout.setVisibility(View.VISIBLE);
                    title.setText("Junior Buddy");
                    rightIcon.setVisibility(View.GONE);

                    toolbar_backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(BuddyManagementPage.this,Account.class));
                        }
                    });

                    ShowJuniorBuddyList();


                }else{ //junior's senior buddy layout
                    juniorBuddyLayout.setVisibility(View.VISIBLE);
                    btnLayout.removeView(declineBtn);
                    requestBtn.setText("UN-BUDDY");
                    pageLayout.removeView(scrollView);

                    info_backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(BuddyManagementPage.this,Account.class));
                        }
                    });

                    requestBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO...
                            Toast.makeText(getApplicationContext(), "TODO REMOVE BUDDY", Toast.LENGTH_LONG).show();

                        }
                    });

                    messageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO...
                            Toast.makeText(getApplicationContext(), "TODO MESSAGE FUNCTION", Toast.LENGTH_LONG).show();
                        }
                    });

                    ShowSeniorBuddyInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }



    private void ShowSeniorBuddyInfo() {

        studentProfileRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("seniorBuddy")) {
                    UserInfo juniorInfo = dataSnapshot.getValue(UserInfo.class);
                    seniorBuddy = juniorInfo.getSeniorBuddy().trim();

                studentProfileRef.child(seniorBuddy).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        UserInfo juniorInfo = dataSnapshot.getValue(UserInfo.class);
                        infoName.setText(juniorInfo.getName().trim());
                        infoEmail.setText(juniorInfo.getEmail().trim());
                        infoCourse.setText("Course: " + juniorInfo.getCourse().trim());
                        infoGender.setText("Gender: " + juniorInfo.getGender().trim());
                        infoHall.setText("Hall: " + juniorInfo.getHall().trim());
                        Picasso.get().load(juniorInfo.getProfileImageUrl()).into(profilePic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                seniorBuddyRef.child(seniorBuddy).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        SeniorBuddyModel seniorBuddyModel = dataSnapshot.getValue(SeniorBuddyModel.class);

                        long diffMonth = 0;
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String joinDate = seniorBuddyModel.getJoinedDate();
                            Date today = new Date();
                            today = sdf.parse(sdf.format(new Date()));
                            Date parsedJoindedDate = sdf.parse(joinDate);
                            long diff = Math.abs(today.getTime() - parsedJoindedDate.getTime());
                            long diffDays = TimeUnit.MILLISECONDS.toDays(diff);
                            diffMonth = diffDays / 30;

                            if (diffMonth <= 12) {
                                exp = "Freshy";

                            } else if (diffMonth > 12 && diffMonth <= 24) {
                                exp = "Senior";
                            } else {
                                exp = "Expert";
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        infoExperience.setText("Joined Date: " + seniorBuddyModel.getJoinedDate() + "\n" + "Experience: " + diffMonth + " Months\n" + "Level: " + exp);
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


    private void ShowJuniorBuddyList() {
        //TODO...
    }
}
