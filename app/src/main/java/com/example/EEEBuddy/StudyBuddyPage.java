package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StudyBuddyPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<StudyEvent> studyEventsList;
    private StudyEventAdapter adapter;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView addIcon, backBtn;

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userID;
    private String userEmail, userNode;
    private SpaceNavigationView spaceNavigationView;

    private Dialog dialog;
    private TextView message;
    private Button cancleBtn, confirmBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_buddy_page);

        //customise bottom navigation
        spaceNavigationView = findViewById(R.id.navigation);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Senior Buddy", R.drawable.ic_buddy_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Study Buddy", R.drawable.ic_msg_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Chat", R.drawable.ic_chat_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Account", R.drawable.ic_person_grey));

        spaceNavigationView.changeCurrentItem(1);
        spaceNavigationView.showIconOnly();


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(StudyBuddyPage.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                spaceNavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(StudyBuddyPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();

                if(itemIndex == 0){//goto senior buddy page
                    //startActivity(new Intent(StudyBuddyPage.this, AddStudyEvent.class));
                }
                if(itemIndex == 1){//goto study buddy page
                    //void
                }
                if(itemIndex == 2){//goto chat
                    //startActivity(new Intent(StudyBuddyPage.this, AddStudyEvent.class));
                }
                if(itemIndex == 3){//goto account page
                    startActivity(new Intent(StudyBuddyPage.this, Account.class));
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(StudyBuddyPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        //customise bottom navigation above


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        addIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        backBtn = (ImageView) findViewById(R.id.toolbar_back);

        recyclerView = (RecyclerView) findViewById(R.id.studybuddy_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studyEventsList = new ArrayList<StudyEvent>();

        backBtn.setVisibility(View.GONE);
        toolbarTitle.setText("Study Buddy");
        addIcon.setImageResource(R.drawable.ic_add);


        studyEventsList.clear();
        databaseReference = firebaseDatabase.getInstance().getReference("Study Event");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                        StudyEvent studyEvent = dataSnapshot2.getValue(StudyEvent.class);
                        studyEventsList.add(studyEvent);
                    }

                }
                adapter = new StudyEventAdapter(StudyBuddyPage.this, studyEventsList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudyBuddyPage.this, "Error, Please Try Again", Toast.LENGTH_LONG).show();
            }
        });



        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //showDialogBox();

                startActivity(new Intent(StudyBuddyPage.this, AddStudyEvent.class));

            }
        });

    }

    public void showDialogBox(final Context context){

        //need to pass the context from the class calling this method.

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        //LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.join_studyevent_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        //dialog.setContentView(R.layout.join_studyevent_dialog);
        message = (TextView) dialog.findViewById(R.id.join_msg);
        cancleBtn = (Button) dialog.findViewById(R.id.join_cancleBtn);
        confirmBtn = (Button) dialog.findViewById(R.id.join_cfmBtn);

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"TODO.......", Toast.LENGTH_LONG).show();
            }
        });

        //dialog.show();

    }

}
