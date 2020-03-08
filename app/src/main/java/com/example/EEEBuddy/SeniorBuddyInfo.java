package com.example.EEEBuddy;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeniorBuddyInfo extends AppCompatActivity implements View.OnClickListener {

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference seniorBuddyRef, studentProfileRef, buddyRequestRef;
    private String userEmail, userNode, seniorNode;


    private ImageView backBtn, messageBtn;
    private ImageView expandBtn, collapsBtn;
    private CircleImageView profilePic;
    private TextView infoName, infoEmail;
    private Button requestBtn, declineBtn;
    private TextView infoCourse, infoHall, infoGender, infoExperience, infoIntro;
    private String profileImageUrl, selfIntro, dateJoined;

    private String reEmail, reName, reCourse, reHall, reGender, reImgUrl, reYear;

    //private View layout;
    private LinearLayout btnLinearLayout;
    private String exp = "";

    //for request for a senior-junior relationship
    private String senderUserID, receiverUserID, CURRENT_STATE;
    private String currentDate;
    private boolean seniorBuddyIdentity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seniorbuddy_info);

        //initialise firebase attributes
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));


        studentProfileRef = firebaseDatabase.getReference("Student Profile");
        seniorBuddyRef = firebaseDatabase.getReference("Senior Buddy");
        buddyRequestRef = firebaseDatabase.getReference().child("Buddy Requests");


        profilePic = (CircleImageView) findViewById(R.id.info_profilepic);
        infoName = (TextView) findViewById(R.id.info_name);
        infoEmail = (TextView) findViewById(R.id.info_email);
        infoCourse = (TextView) findViewById(R.id.info_course);
        infoHall = (TextView) findViewById(R.id.info_Hall);
        infoGender = (TextView) findViewById(R.id.info_gender);
        infoExperience = (TextView) findViewById(R.id.info_exp);
        infoIntro = (TextView) findViewById(R.id.card_intro);

        btnLinearLayout = (LinearLayout) findViewById(R.id.info_buttons_layout);
        backBtn = (ImageView) findViewById(R.id.info_back);
        requestBtn = (Button) findViewById(R.id.info_requestBtn);
        declineBtn = (Button) findViewById(R.id.info_declineBtn);
        messageBtn = (ImageView) findViewById(R.id.info_msgBtn);

        expandBtn = (ImageView) findViewById(R.id.card_expand);
        collapsBtn = (ImageView) findViewById(R.id.card_collaps);


        getIncomingIntent();
        infoEmail.setText(reEmail);
        infoName.setText(reName);
        infoCourse.setText(reCourse + ", Year " + reYear);
        infoGender.setText("Gender: " + reGender);
        Picasso.get().load(reImgUrl).into(profilePic);

        if(reHall.equals("no")){
            infoHall.setText("Hall: NIL");
        }else{
            infoHall.setText("Hall:" + reHall);
        }


        seniorNode = reEmail.substring(0, reEmail.indexOf("@"));

        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check if the person is a senior buddy in (all senior buddy tab)or a junior in (buddy request tab).

                if(dataSnapshot.hasChild(seniorNode)){

                    seniorBuddyRef.child(seniorNode).addValueEventListener(new ValueEventListener() {
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


                            //infoExperience.setSingleLine(false);
                            infoExperience.setText("Joined Date: " + seniorBuddyModel.getJoinedDate() + "\n" + "Experience: " + diffMonth + " Months\n" + "Level: " + exp);
                            selfIntro = seniorBuddyModel.getSelfIntro();

                            MaintenanceOfButtons();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    infoExperience.setVisibility(View.INVISIBLE);
                    infoIntro.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        backBtn.setOnClickListener(this);
        //requestBtn.setOnClickListener(this);
        messageBtn.setOnClickListener(this);
        expandBtn.setOnClickListener(this);
        collapsBtn.setOnClickListener(this);


        //request senior-junior relationship
        //declineBtn.setEnabled(false);
        btnLinearLayout.removeView(declineBtn);
        //btnLinearLayout.removeView(requestBtn);


        senderUserID = userNode;
        receiverUserID = seniorNode;
        CURRENT_STATE = "not_buddy";
        seniorBuddyIdentity = false;



        //senior buddy cannot apply for a senior buddy
        seniorBuddyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(senderUserID)){
                    seniorBuddyIdentity = true;
                    btnLinearLayout.removeView(requestBtn);

                    //senior buddy can accept or decline the junior buddy request
                    MaintenanceOfButtons();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        if(!senderUserID.equals(receiverUserID) && seniorBuddyIdentity == false){

            requestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(CURRENT_STATE.equals("not_buddy")){

                        requestBtn.setEnabled(false);
                        SendBuddyRequest();
                    }

                    if(CURRENT_STATE.equals("request_sent")){
                        CancelBuddyRequest();
                    }

                    /*
                    if(CURRENT_STATE.equals("request_received")){
                        AcceptBuddyRequest();
                    }

                     */
                }
            });

        }else if(!senderUserID.equals(receiverUserID) && seniorBuddyIdentity == true){

                requestBtn.setEnabled(true);
                requestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(CURRENT_STATE.equals("request_received")){
                            AcceptBuddyRequest();
                        }
                    }
                });
        }
        /*

        else{

            btnLinearLayout.removeView(declineBtn);
            btnLinearLayout.removeView(requestBtn);

        }
         */





    }

    private void AcceptBuddyRequest() {

        //which date they become buddy
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = currentDateFormat.format(calendar.getTime());



        //record the senior buddy in junior's profile
        studentProfileRef.child(senderUserID).child("seniorBuddy").child(receiverUserID).child("date").setValue(currentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            //record the junior in senior buddy's profile
                            seniorBuddyRef.child(receiverUserID).child("juniorBuddy").child(senderUserID).child("date").setValue(currentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    //remove the record from buddy request node of both sender and receiver, as the relationship is already established
                                                                    buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if(task.isSuccessful()){
                                                                                        buddyRequestRef.child(receiverUserID).child(senderUserID).child("request_type").removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        if(task.isSuccessful()){

                                                                                                            requestBtn.setEnabled(true);
                                                                                                            CURRENT_STATE = "buddy";
                                                                                                            requestBtn.setText("Remove Buddy");

                                                                                                            declineBtn.setVisibility(View.INVISIBLE);
                                                                                                            declineBtn.setEnabled(false);
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });


    }


    private void CancelBuddyRequest() {

        buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            buddyRequestRef.child(receiverUserID).child(senderUserID).child("request_type").removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                requestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_buddy";
                                                requestBtn.setText("Send Request");

                                                declineBtn.setVisibility(View.INVISIBLE);
                                                declineBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void MaintenanceOfButtons() {

        buddyRequestRef.child(senderUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserID)){

                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString().trim();
                            if(request_type.equals("sent")){

                                CURRENT_STATE = "request_sent";
                                requestBtn.setText("Cancel Request");

                                declineBtn.setVisibility(View.INVISIBLE);
                                declineBtn.setEnabled(false);
                            }
                            else if(request_type.equals("received")){

                                CURRENT_STATE = "request_received";
                                requestBtn.setText("Accept Request");

                                btnLinearLayout.addView(requestBtn);
                                btnLinearLayout.addView(declineBtn);
                                declineBtn.setEnabled(true);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void SendBuddyRequest() {

        buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            buddyRequestRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                requestBtn.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                requestBtn.setText("Cancel Request");

                                                declineBtn.setVisibility(View.INVISIBLE);
                                                declineBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    private void getIncomingIntent(){
        if(getIntent().hasExtra("email")
                && getIntent().hasExtra("name")
                && getIntent().hasExtra("course")
                && getIntent().hasExtra("hall")
                && getIntent().hasExtra("gender")
                && getIntent().hasExtra("profileImg")
                && getIntent().hasExtra("year")
        ){

             reEmail = getIntent().getStringExtra("email");
             reName = getIntent().getStringExtra("name");
             reCourse = getIntent().getStringExtra("course");
             reHall = getIntent().getStringExtra("hall");
             reGender = getIntent().getStringExtra("gender");
             reImgUrl = getIntent().getStringExtra("profileImg");
             reYear = getIntent().getStringExtra("year");

        }
    }

    @Override
    public void onClick(View v) {

        if(v == backBtn){

            //add fragment to stack
          /* AllFragment allFragment = new AllFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction().replace(R.id.seniorbuddy_info_layout, allFragment);
            transaction.addToBackStack("AllFragment");
            transaction.commit();

            manager.popBackStackImmediate();

           */

           startActivity(new Intent(this, SeniorBuddyPage.class));

        }


        if(v == expandBtn){
            expandBtn.setVisibility(View.GONE);
            collapsBtn.setVisibility(View.VISIBLE);
            infoIntro.setVisibility(View.VISIBLE);
            infoIntro.setText(selfIntro);
        }

        if(v == collapsBtn){
            expandBtn.setVisibility(View.VISIBLE);
            collapsBtn.setVisibility(View.GONE);
            infoIntro.setText("");
            infoIntro.setVisibility(View.GONE);
        }



        if(v == declineBtn){

            //TODO...
            //btnLinearLayout.removeView(messageBtn);
            Toast.makeText(this,"TODO...", Toast.LENGTH_LONG).show();
        }


        if(v == messageBtn){

            //TODO
            Toast.makeText(this,"TODO...", Toast.LENGTH_LONG).show();
        }
    }
}
