package com.example.EEEBuddy;

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
import android.widget.TextView;
import android.widget.Toast;

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

public class SeniorBuddyInfo extends AppCompatActivity implements View.OnClickListener {

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference seniorBuddyRef, studentProfileRef;
    private String userID;
    private String userEmail, userNode;


    private ImageView backBtn;
    private ImageView expandBtn, collapsBtn;
    private CircleImageView profilePic;
    private TextView infoName, infoEmail;
    private Button requestBtn, messageBtn;
    private TextView infoCourse, infoHall, infoGender, infoExperience, infoIntro;
    private String profileImageUrl, selfIntro, dateJoined;

    private String reEmail, reName, reCourse, reHall, reGender, reImgUrl, reYear;

    private View layout;

    private String exp = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seniorbuddy_info);

        //initialise firebase attributes
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        studentProfileRef = firebaseDatabase.getReference("Student Profile");
        seniorBuddyRef = firebaseDatabase.getReference("Senior Buddy");

        profilePic = (CircleImageView) findViewById(R.id.info_profilepic);
        infoName = (TextView) findViewById(R.id.info_name);
        infoEmail = (TextView) findViewById(R.id.info_email);
        infoCourse = (TextView) findViewById(R.id.info_course);
        infoHall = (TextView) findViewById(R.id.info_Hall);
        infoGender = (TextView) findViewById(R.id.info_gender);
        infoExperience = (TextView) findViewById(R.id.info_exp);
        infoIntro = (TextView) findViewById(R.id.card_intro);

        backBtn = (ImageView) findViewById(R.id.info_back);
        requestBtn = (Button) findViewById(R.id.info_requestBtn);
        messageBtn = (Button) findViewById(R.id.info_msgBtn);

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


        userNode = reEmail.substring(0, reEmail.indexOf("@"));

        seniorBuddyRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SeniorBuddyModel seniorBuddyModel = dataSnapshot.getValue(SeniorBuddyModel.class);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String joinDate = seniorBuddyModel.getJoinedDate();
                    Date today = new Date();
                    today = sdf.parse(sdf.format(new Date()));
                    Date parsedJoindedDate = sdf.parse(joinDate);
                    long diff = Math.abs(today.getTime() - parsedJoindedDate.getTime());
                    long diffDays = TimeUnit.MILLISECONDS.toDays(diff);
                    long diffMonth = diffDays/30;

                    if (diffMonth <= 12) {
                        exp = "Freshy";

                    } else if (diffMonth > 12 && diffMonth <= 24) {
                        exp = "Senior";
                    } else{
                        exp = "Expert";
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }



                infoExperience.setText("Joined Date: " + seniorBuddyModel.getJoinedDate() + " | Level: " + exp);
                selfIntro = seniorBuddyModel.getSelfIntro();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        backBtn.setOnClickListener(this);
        requestBtn.setOnClickListener(this);
        messageBtn.setOnClickListener(this);
        expandBtn.setOnClickListener(this);
        collapsBtn.setOnClickListener(this);

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



        if(v == requestBtn){

            //TODO...
            Toast.makeText(this,"TODO...", Toast.LENGTH_LONG).show();
        }

        if(v == messageBtn){

            //TODO...
            Toast.makeText(this,"TODO...", Toast.LENGTH_LONG).show();
        }
    }
}
