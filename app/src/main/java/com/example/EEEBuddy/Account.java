package com.example.EEEBuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends AppCompatActivity {

    private CircleImageView profileImage;
    private String profileImageUrl;
    private TextView profileName;
    private ImageView profilePage;
    private ImageView events;
    private ImageView trackStudy;
    private ImageView logout;

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userID;
    private String userEmail, userNode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);




        profileName = (TextView) findViewById(R.id. profile_name);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        profilePage = (ImageView) findViewById(R.id.acct_profile);
        events = (ImageView) findViewById(R.id.acct_event);
        trackStudy = (ImageView) findViewById(R.id.acct_study);
        logout = (ImageView) findViewById(R.id.acct_logout);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Student Profile");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        if(firebaseAuth.getCurrentUser() == null){
            //if user has already logged out
            finish();
            startActivity(new Intent(this,Login.class));
        }


        // Read from the database student profile pic and name
        databaseReference.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                profileName.setText(userInfo.getName());
                profileImageUrl = userInfo.getProfileImageUrl();
                Picasso.get().load(profileImageUrl).into(profileImage);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoProfile();
            }
        });


        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisteredEvents();
            }
        });


        trackStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TrackStudy();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });



    }


    private void GotoProfile(){
        finish();
        startActivity(new Intent(this, Profile.class));
    }



    private void RegisteredEvents() {

        //TODO
    }

    private void TrackStudy(){

        //TODO
    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, Login.class));
    }


}

