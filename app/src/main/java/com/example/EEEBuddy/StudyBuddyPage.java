package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StudyBuddyPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ArrayList<StudyEvent> studyEventsList, searchResultList;
    private ArrayList<String> keyArray;
    private StudyEventAdapter adapter;

    private TextView toolbarTitle;
    private ImageView addIcon, backBtn;

    //declare database stuff
    private FirebaseAuth firebaseAuth;
    private DatabaseReference studyEventRef, registeredEventRef;
    private String userEmail, userNode;

    private SpaceNavigationView spaceNavigationView;


    private TextView message1, message2, note;
    private Button cancleBtn, confirmBtn, joinBtn;
    private String button_state = "not_joined";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_buddy_page);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));


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

        spaceNavigationView.changeCurrentItem(1);
        spaceNavigationView.showIconOnly();


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(StudyBuddyPage.this, "onCentreButtonClick", Toast.LENGTH_SHORT).show();
                spaceNavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(StudyBuddyPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();

                if (itemIndex == 0) {//goto senior buddy page
                    startActivity(new Intent(StudyBuddyPage.this, SeniorBuddyPage.class));
                }
                if (itemIndex == 1) {//goto study buddy page
                    //void
                }
                if (itemIndex == 2) {//goto chat
                    startActivity(new Intent(StudyBuddyPage.this, ChatListPage.class));
                }
                if (itemIndex == 3) {//goto account page
                    startActivity(new Intent(StudyBuddyPage.this, Account.class));
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(StudyBuddyPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        //customise bottom navigation above


        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        addIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        backBtn = (ImageView) findViewById(R.id.toolbar_back);

        recyclerView = (RecyclerView) findViewById(R.id.studybuddy_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studyEventsList = new ArrayList<StudyEvent>();
        searchResultList = new ArrayList<StudyEvent>();
        keyArray = new ArrayList<String>();
        searchView = (SearchView) findViewById(R.id.studybuddy_searchview);

        backBtn.setVisibility(View.GONE);
        toolbarTitle.setText("Study Buddy");
        addIcon.setImageResource(R.drawable.ic_add);


        //retrieve list of study event
        studyEventRef = FirebaseDatabase.getInstance().getReference("Study Event");
        studyEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                studyEventsList.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {

                        StudyEvent getEventDateTime = dataSnapshot2.getValue(StudyEvent.class);
                        String eventDate = getEventDateTime.getDate();
                        String eventStartTime = getEventDateTime.getStartTime();

                        //time validation
                        try {
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date currentTime = new Date();
                            String eventDateTime = eventDate + " " + eventStartTime.substring(0, eventStartTime.lastIndexOf(" ")).trim();
                            Date formattedEventTime = sdf2.parse(eventDateTime);

                            if (formattedEventTime.compareTo(currentTime) > 0) {

                                String tempKey = dataSnapshot2.getKey();
                                StudyEvent studyEvent = dataSnapshot2.getValue(StudyEvent.class);
                                studyEventsList.add(studyEvent);
                                keyArray.add(tempKey);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                }
                adapter = new StudyEventAdapter(StudyBuddyPage.this, studyEventsList, keyArray);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudyBuddyPage.this, "Error, Please Try Again", Toast.LENGTH_LONG).show();
            }
        });


        //search is initiated when text changes
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String searchString) {
                    Search(searchString);
                    return true;
                }
            });
        }


        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //showDialogBox();
                Intent intent = new Intent(StudyBuddyPage.this, AddStudyEvent.class);
                intent.putExtra("from", "AddStudyEventActivity");

                startActivity(intent);

            }
        });


    }


    private void Search(String searchString) {

        searchResultList.clear();

        for (StudyEvent object : studyEventsList) {
            if (object.getSubjectCode().toLowerCase().contains(searchString.toLowerCase()) ||
                    object.getSubjectName().toLowerCase().contains(searchString.toLowerCase()) ||
                    object.getCreatedBy().toLowerCase().contains(searchString.toLowerCase())) {

                searchResultList.add(object);
            }
        }

        adapter = new StudyEventAdapter(this, searchResultList, keyArray);
        recyclerView.setAdapter(adapter);
    }

    public void showDialogBox(final Context context, final String eventID, final StudyEventAdapter.MyViewHolder holder, final String createdBy, String code, String subject, String task, String location, String date, String groupSize, String time) {

        //need to pass the context from the class calling this method.

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.join_studyevent_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        //dialog.setContentView(R.layout.join_studyevent_dialog);
        message1 = (TextView) dialog.findViewById(R.id.join_msg1);
        message2 = (TextView) dialog.findViewById(R.id.join_msg2);
        note = (TextView) dialog.findViewById(R.id.join_note);
        cancleBtn = (Button) dialog.findViewById(R.id.join_cancleBtn);
        confirmBtn = (Button) dialog.findViewById(R.id.join_cfmBtn);


        //message content
        message1.setText(code + " " + subject);
        message2.setText(
                "Task: " + task + "\n"
                        + "Location: " + location + "\n"
                        + "Date: " + date + "\n"
                        + "Time: " + time);
        note.setText("Note: Goto 'Account > Study Events' to manage your registered events.");


        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RegisterStudyEvent(context, createdBy, eventID, holder);
                AddToStudyGroupChat();
                dialog.dismiss();
            }
        });
    }


    private void RegisterStudyEvent(final Context context, final String createdBy, final String eventID, final StudyEventAdapter.MyViewHolder holder) {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        registeredEventRef = FirebaseDatabase.getInstance().getReference("Registered Event");
        studyEventRef = FirebaseDatabase.getInstance().getReference("Study Event");


        final StudyEvent studyEvent = new StudyEvent(createdBy, eventID);
        registeredEventRef.child(userNode).child(eventID).setValue(studyEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    holder.joinBtn.setBackgroundResource(R.drawable.btn_disabled);
                    holder.joinBtn.setText("Joined");
                    holder.joinBtn.setEnabled(false);

                    //update event vacancy
                    studyEventRef.child(createdBy).child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                String getGroupSize = dataSnapshot.getValue(StudyEvent.class).getGroupSize();
                                int groupSize = Integer.parseInt(getGroupSize);
                                if (groupSize > 0) {

                                    String newVacancy = String.valueOf(groupSize - 1);
                                    studyEventRef.child(createdBy).child(eventID).child("groupSize").setValue(newVacancy);

                                    if (groupSize <= 3) {
                                        holder.groupSize.setTextColor(Color.parseColor("#cc0000"));
                                    }
                                    holder.groupSize.setText(newVacancy);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(context, "Event Registered", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void AddToStudyGroupChat() {
    }


}
