package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Comment extends AppCompatActivity {


    //toolbar
    private ImageView backBtn, rightIcon;
    private TextView toolbarTitle;

    //Body
    private TextView lastUpdateTime;
    private RecyclerView commentRecycler;
    private ArrayList<CommentModel> commentList;
    private CommentAdapter commentAdapter;


    //declare database stuff
    private FirebaseUser user;
    private DatabaseReference seniorBuddyRef, studentProfileRef;
    private String userEmail, userNode, commentOfWho, identity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Initialisation();
        GetIncomingIntent();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        if (identity.equals("junior")) {
            //comments of the selected junior
            DisplayJuniorCommentList();

        } else if (identity.equals("senior")) {
            //comments of the selected senior
            DisplaySeniorCommentList();

        }
        /*
        else if (identity.equals("junior/senior")){

            seniorBuddyRef = FirebaseDatabase.getInstance().getReference("Senior Buddy");
            seniorBuddyRef.keepSynced(true);

            //if he/she is a senior buddy, DisplaySeniorCommentList();
            seniorBuddyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(commentOfWho)){

                        DisplaySeniorCommentList();

                    }else{

                        DisplayJuniorCommentList();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

         */


    }



    private void DisplaySeniorCommentList() {

        seniorBuddyRef = FirebaseDatabase.getInstance().getReference("Senior Buddy");
        seniorBuddyRef.keepSynced(true);

        seniorBuddyRef.child(commentOfWho).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("juniorBuddyComment").exists()) {
                        for (DataSnapshot ds : dataSnapshot.child("juniorBuddyComment").getChildren()) {

                            CommentModel commentModel = ds.getValue(CommentModel.class);
                            commentList.add(commentModel);
                        }

                        commentAdapter = new CommentAdapter(Comment.this, commentList);
                        commentRecycler.setAdapter(commentAdapter);
                    } else {

                        Toast.makeText(Comment.this, "No Comment Found", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(Comment.this, "wrong identity!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void DisplayJuniorCommentList() {

        studentProfileRef = FirebaseDatabase.getInstance().getReference("Student Profile").child(commentOfWho);
        studentProfileRef.keepSynced(true);

        studentProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("seniorBuddyComment").exists()) {
                        for (DataSnapshot ds : dataSnapshot.child("seniorBuddyComment").getChildren()) {

                            CommentModel commentModel = ds.getValue(CommentModel.class);
                            commentList.add(commentModel);
                        }

                        commentAdapter = new CommentAdapter(Comment.this, commentList);
                        commentRecycler.setAdapter(commentAdapter);
                    } else {

                        Toast.makeText(Comment.this, "No Comment Found", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(Comment.this, "wrong identity!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void GetIncomingIntent() {

        if (getIntent().hasExtra("commentOfWho") &&
                getIntent().hasExtra("identity")) {

            commentOfWho = getIntent().getStringExtra("commentOfWho");
            identity = getIntent().getStringExtra("identity");
        }
    }

    private void Initialisation() {

        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        rightIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        rightIcon.setVisibility(View.GONE);
        toolbarTitle.setText("Comments");

        lastUpdateTime = (TextView) findViewById(R.id.comment_update_time);
        commentRecycler = (RecyclerView) findViewById(R.id.comment_recyclerview);
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<CommentModel>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        //last update time
        Date currentTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss aa");
        String formattedTime = simpleDateFormat.format(currentTime);
        lastUpdateTime.setText("Last Updated On: " + formattedTime);
    }
}
