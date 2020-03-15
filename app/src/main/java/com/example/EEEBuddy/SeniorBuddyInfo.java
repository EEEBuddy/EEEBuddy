package com.example.EEEBuddy;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    private Button requestBtn, declineBtn, commentBtn, appointmentBtn;
    private TextView infoCourse, infoHall, infoGender, infoExperience, infoIntro;
    private String profileImageUrl, selfIntro, dateJoined;

    private LinearLayout pageLayout;
    private ScrollView scrollView;

    private String reEmail, reName, reCourse, reHall, reGender, reImgUrl, reYear, juniorBuddyPageRequestPage, myJuniorBuddyPage;

    //private View layout;
    private LinearLayout btnLinearLayout;
    private String exp = "";

    //for request for a senior-junior relationship
    private String senderUserID, receiverUserID, CURRENT_STATE;
    private String currentDate;
    private boolean seniorBuddyIdentity, hasSeniorBuddy, removeRequestReceived, buddyRequestReceived, buddyRequestSent;



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
        commentBtn = (Button) findViewById(R.id.info_commentBtn);
        appointmentBtn = (Button) findViewById(R.id.info_appointmentBtn);

        expandBtn = (ImageView) findViewById(R.id.card_expand);
        collapsBtn = (ImageView) findViewById(R.id.card_collaps);

        pageLayout = (LinearLayout) findViewById(R.id.seniorbuddy_linearLayout);
        scrollView = (ScrollView) findViewById(R.id.info_scrollview);

        myJuniorBuddyPage = "";
        juniorBuddyPageRequestPage ="";

        getIncomingIntent();
        infoEmail.setText(reEmail);
        infoName.setText(reName);
        infoCourse.setText(reCourse + ", Year " + reYear);
        infoGender.setText("Gender: " + reGender);
        Picasso.get().load(reImgUrl).into(profilePic);

        if(reHall.equals("no")){
            infoHall.setText("Hall: NIL");
        }else{
            infoHall.setText("Hall: " + reHall);
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

                }else if(myJuniorBuddyPage.equals("yes")){

                    pageLayout.removeView(scrollView);
                    infoExperience.setVisibility(View.INVISIBLE);

                    commentBtn.setVisibility(View.VISIBLE);
                    commentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PostCommentForJunior();
                        }
                    });

                    MaintenanceOfButtons();


                }else if(juniorBuddyPageRequestPage.equals("yes")){

                    pageLayout.removeView(scrollView);
                    infoExperience.setVisibility(View.INVISIBLE);
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


        senderUserID = userNode;
        receiverUserID = seniorNode;
        CURRENT_STATE = "not_buddy";
        seniorBuddyIdentity = false;
        hasSeniorBuddy = false;
        removeRequestReceived = false;
        buddyRequestReceived = false;
        buddyRequestSent = false;



        if(senderUserID.equals(receiverUserID)){
            btnLinearLayout.removeView(requestBtn);
        }



        //junior buddy functions, not a senior buddy and does not have a senior buddy
        if(!senderUserID.equals(receiverUserID) && !CheckSeniorBuddyIdentity() && !HasSeniorBuddy()){


            requestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(CURRENT_STATE.equals("not_buddy")){

                        requestBtn.setEnabled(false);
                        SendBuddyRequest();
                    }

                    if(CURRENT_STATE.equals("buddy_request_sent")){
                        CancelBuddyRequest();
                    }


                    if(CURRENT_STATE.equals("buddy_request_received")) {
                        AcceptBuddyRequest();
                    }

                    if(CURRENT_STATE.equals("buddy")){
                        SendRemoveBuddyRequest();
                    }

                    if(CURRENT_STATE.equals("remove_request_sent")){
                        CancelRemoveBuddyRequest();
                    }

                    if(CURRENT_STATE.equals("remove_request_received")) {
                        AcceptRemoveBuddyRequest();
                    }

                }

            });

            MaintenanceOfButtons();
            //senior buddy functions
        }

        if(!senderUserID.equals(receiverUserID) &&  CheckSeniorBuddyIdentity()){

            requestBtn.setEnabled(true);
            requestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(CURRENT_STATE.equals("buddy_request_received")){
                        AcceptBuddyRequest();
                    }
                }
            });


        }


        //if a junior already had a senior buddy, remove the send request btn
        if(HasSeniorBuddy()){
            requestBtn.setBackgroundResource(R.drawable.btn_disabled);
            requestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "You already have a Senior Buddy, Please check out at the Account Page", Toast.LENGTH_LONG).show();
                }
            });
            //btnLinearLayout.removeView(requestBtn);
        }

    }



    private boolean HasSeniorBuddy() {

        studentProfileRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("seniorBuddy")){
                    hasSeniorBuddy = true;
                }else{
                    hasSeniorBuddy = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return  hasSeniorBuddy;
    }


    //senior buddy cannot apply for a senior buddy
    private boolean CheckSeniorBuddyIdentity() {
        seniorBuddyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(senderUserID)){
                    seniorBuddyIdentity = true;
                    btnLinearLayout.removeView(requestBtn);

                    //senior buddy can accept or decline the junior buddy request
                    MaintenanceOfButtons();
                }else{
                    seniorBuddyIdentity = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return seniorBuddyIdentity;
    }


    private void SendRemoveBuddyRequest() {

        buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("remove_request_sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            buddyRequestRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("remove_request_received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                requestBtn.setEnabled(true);
                                                CURRENT_STATE = "remove_request_sent";
                                                requestBtn.setText("Cancel Request");

                                                btnLinearLayout.removeView(declineBtn);
                                                declineBtn.setEnabled(false);
                                                Toast.makeText(getApplicationContext(), "Remove Buddy Request Sent", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void CancelRemoveBuddyRequest() {

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
                                                btnLinearLayout.removeView(declineBtn);
                                                declineBtn.setEnabled(false);
                                                Toast.makeText(getApplicationContext(), "Remove Buddy Request Cancelled", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void AcceptRemoveBuddyRequest() {
        //senior buddy agree to remove  buddy relationship
        seniorBuddyRef.child(senderUserID).child("juniorBuddy").child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            studentProfileRef.child(receiverUserID).child("seniorBuddy").removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

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

                                                                                        CURRENT_STATE = "not_buddy";
                                                                                        requestBtn.setText("Request");

                                                                                        btnLinearLayout.removeView(declineBtn);
                                                                                        declineBtn.setEnabled(false);
                                                                                        finish();
                                                                                        startActivity(new Intent (getApplicationContext(), SeniorBuddyPage.class));
                                                                                        Toast.makeText(getApplicationContext(), "Buddy Relationship Removed successfully", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });

                        }else{

                            Toast.makeText(getApplicationContext(), "Failed to remove senior Buddy. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }





    private void AcceptBuddyRequest() {

        //which date they become buddy
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentDate = currentDateFormat.format(calendar.getTime());



        //record the senior buddy in junior's profile
        studentProfileRef.child(receiverUserID).child("seniorBuddy").setValue(senderUserID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            //record the junior in senior buddy's profile
                            seniorBuddyRef.child(senderUserID).child("juniorBuddy").child(receiverUserID).child("relationDate").setValue(currentDate)
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

                                                                                        btnLinearLayout.removeView(declineBtn);
                                                                                        declineBtn.setEnabled(false);
                                                                                        Toast.makeText(getApplicationContext(), "Buddy Relationship Established", Toast.LENGTH_LONG).show();
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
                                                btnLinearLayout.removeView(declineBtn);
                                                declineBtn.setEnabled(false);
                                                Toast.makeText(getApplicationContext(), "Buddy Request Cancelled", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }



    private void SendBuddyRequest() {

        buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("buddy_request_sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            buddyRequestRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("buddy_request_received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                requestBtn.setEnabled(true);
                                                CURRENT_STATE = "buddy_request_sent";
                                                requestBtn.setText("Cancel Request");

                                                btnLinearLayout.removeView(declineBtn);
                                                declineBtn.setEnabled(false);
                                                Toast.makeText(getApplicationContext(), "Buddy Request Sent", Toast.LENGTH_LONG).show();
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
                            if(request_type.equals("buddy_request_sent")){

                                buddyRequestSent = true;
                                CURRENT_STATE = "buddy_request_sent";
                                requestBtn.setText("Cancel Request");

                                declineBtn.setVisibility(View.INVISIBLE);
                                declineBtn.setEnabled(false);
                            }
                            else if(request_type.equals("buddy_request_received")){

                                buddyRequestReceived = true;
                                CURRENT_STATE = "buddy_request_received";
                                requestBtn.setText("Accept Request");

                                btnLinearLayout.removeView(requestBtn);
                                btnLinearLayout.addView(requestBtn);
                                btnLinearLayout.removeView(declineBtn);
                                btnLinearLayout.addView(declineBtn);

                                declineBtn.setEnabled(true);
                                requestBtn.setEnabled(true);

                                declineBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CancelBuddyRequest();

                                    }
                                });
                            } else if(request_type.equals("remove_request_sent")){

                                CURRENT_STATE = "remove_request_sent";
                                requestBtn.setText("Cancel Request");

                                declineBtn.setVisibility(View.INVISIBLE);
                                declineBtn.setEnabled(false);


                            }
                            else if(request_type.equals("remove_request_received")){

                                removeRequestReceived = true;
                                CURRENT_STATE = "remove_request_received";
                                requestBtn.setText("Accept remove Request");
                                requestBtn.setTextSize(14f);

                                btnLinearLayout.removeView(requestBtn);
                                btnLinearLayout.addView(requestBtn);
                                //btnLinearLayout.removeView(declineBtn);
                                //btnLinearLayout.addView(declineBtn);

                                //declineBtn.setEnabled(true);
                                requestBtn.setEnabled(true);


                                /*
                                declineBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CancelBuddyRequest();

                                    }
                                });

                                 */
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        seniorBuddyRef.child(senderUserID).child("juniorBuddy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(receiverUserID) && removeRequestReceived == false){


                    CURRENT_STATE = "buddy";
                    requestBtn.setText("Remove Buddy");

                    btnLinearLayout.removeView(requestBtn);
                    btnLinearLayout.addView(requestBtn);
                    btnLinearLayout.removeView(declineBtn);

                    declineBtn.setEnabled(false);
                    requestBtn.setEnabled(true);

                }else if(dataSnapshot.hasChild(receiverUserID) && removeRequestReceived == true){

                    CURRENT_STATE = "remove_request_received";
                    requestBtn.setText("Accept Remove Request");
                    requestBtn.setTextSize(14f);

                    btnLinearLayout.removeView(requestBtn);
                    btnLinearLayout.addView(requestBtn);
                    btnLinearLayout.removeView(declineBtn);

                    declineBtn.setEnabled(false);
                    requestBtn.setEnabled(true);

                }else if(!dataSnapshot.hasChild(receiverUserID) && buddyRequestReceived == false && buddyRequestSent == false && removeRequestReceived == false){

                    CURRENT_STATE = "not_buddy";

                    if(!senderUserID.equals(receiverUserID)){
                        requestBtn.setText("Request");
                        btnLinearLayout.removeView(requestBtn);
                        btnLinearLayout.addView(requestBtn);
                        btnLinearLayout.removeView(declineBtn);
                    }

                    declineBtn.setEnabled(false);
                    requestBtn.setEnabled(true);

                }else if(!dataSnapshot.hasChild(receiverUserID) && buddyRequestReceived == true && buddyRequestSent == true){

                    CURRENT_STATE = "buddy_request_received";
                    requestBtn.setText("Accept");

                    btnLinearLayout.removeView(requestBtn);
                    btnLinearLayout.addView(requestBtn);
                    btnLinearLayout.removeView(declineBtn);
                    btnLinearLayout.addView(declineBtn);

                    declineBtn.setEnabled(true);
                    requestBtn.setEnabled(true);

                    declineBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            CancelBuddyRequest();

                        }
                    });

                }else if(!dataSnapshot.hasChild(receiverUserID) && buddyRequestSent == true){

                    CURRENT_STATE = "buddy_request_received";

                    if(!senderUserID.equals(receiverUserID)){
                        requestBtn.setText("Cancel Request");
                        btnLinearLayout.removeView(requestBtn);
                        btnLinearLayout.addView(requestBtn);
                        btnLinearLayout.removeView(declineBtn);
                    }

                    declineBtn.setEnabled(false);
                    requestBtn.setEnabled(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void PostCommentForJunior() {
        //TODO
        Toast.makeText(getApplicationContext(), "TODO...", Toast.LENGTH_LONG).show();
    }






    private void getIncomingIntent(){

        if(getIntent().hasExtra("email")
                && getIntent().hasExtra("name")
                && getIntent().hasExtra("course")
                && getIntent().hasExtra("hall")
                && getIntent().hasExtra("gender")
                && getIntent().hasExtra("profileImg")
                && getIntent().hasExtra("year")
                && getIntent().hasExtra("juniorBuddyRequestPage")

        ){

            reEmail = getIntent().getStringExtra("email");
            reName = getIntent().getStringExtra("name");
            reCourse = getIntent().getStringExtra("course");
            reHall = getIntent().getStringExtra("hall");
            reGender = getIntent().getStringExtra("gender");
            reImgUrl = getIntent().getStringExtra("profileImg");
            reYear = getIntent().getStringExtra("year");
            juniorBuddyPageRequestPage = getIntent().getStringExtra("juniorBuddyRequestPage");

        }else if(getIntent().hasExtra("email")
                && getIntent().hasExtra("name")
                && getIntent().hasExtra("course")
                && getIntent().hasExtra("hall")
                && getIntent().hasExtra("gender")
                && getIntent().hasExtra("profileImg")
                && getIntent().hasExtra("year")
                && getIntent().hasExtra("myJuniorBuddyPage")

        ){

            reEmail = getIntent().getStringExtra("email");
            reName = getIntent().getStringExtra("name");
            reCourse = getIntent().getStringExtra("course");
            reHall = getIntent().getStringExtra("hall");
            reGender = getIntent().getStringExtra("gender");
            reImgUrl = getIntent().getStringExtra("profileImg");
            reYear = getIntent().getStringExtra("year");
            myJuniorBuddyPage = getIntent().getStringExtra("myJuniorBuddyPage");

        }else{

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