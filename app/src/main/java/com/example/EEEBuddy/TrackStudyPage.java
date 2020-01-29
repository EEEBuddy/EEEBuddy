package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TrackStudyPage extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userEmail, userNode;

    private CalendarView calendarView;
    private TextView trackDate;
    private ImageView addBtn;

    private RecyclerView recyclerView;
    private ArrayList<TrackStudyModel> studyRecordList;
    private TrackStudyAdapter trackStudyAdapter;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView backBtn;
    private ImageView reportBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_study_page);

        databaseReference = FirebaseDatabase.getInstance().getReference("Track Study");
        firebaseAuth = firebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        calendarView = (CalendarView) findViewById(R.id.calendarview);
        trackDate = (TextView) findViewById(R.id.track_selected_date);
        recyclerView = (RecyclerView) findViewById(R.id.track_recyclerview);
        addBtn = (ImageView) findViewById(R.id.track_addbtn);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        reportBtn = (ImageView) findViewById(R.id.toolbar_right_icon);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        toolbarTitle.setText("Track My Study");
        reportBtn.setImageResource(R.drawable.report);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(TrackStudyPage.this, Account.class));
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateReport();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddStudyRecord();
            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String yr = Integer.toString(year);
                String mon = Integer.toString(month+1);
                String day = Integer.toString(dayOfMonth);
                String dateNode = yr+"-"+mon+"-"+day;

                trackDate.setText(year + " / " + mon + " / " + dayOfMonth + " - Study Records");


                studyRecordList = new ArrayList<TrackStudyModel>();
                recyclerView.setLayoutManager(new LinearLayoutManager(TrackStudyPage.this));
                studyRecordList.clear();
                databaseReference.child(userNode).child(dateNode).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()){

                            TrackStudyModel studyRecord = ds.getValue(TrackStudyModel.class);
                            studyRecordList.add(studyRecord);
                        }

                        trackStudyAdapter = new TrackStudyAdapter(TrackStudyPage.this, studyRecordList);
                        recyclerView.setAdapter(trackStudyAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(TrackStudyPage.this, "Error, Please Try Again", Toast.LENGTH_LONG).show();

                    }
                });


            }
        });


    }

    private void GenerateReport() {

        //TODO...
        Toast.makeText(this, "TODO...", Toast.LENGTH_SHORT).show();
    }

    private void AddStudyRecord(){

        //TODO...
        Toast.makeText(this, "TODO...", Toast.LENGTH_SHORT).show();
    }

}
