package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private ArrayList<String> keyArray;
    private TrackStudyAdapter trackStudyAdapter;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView backBtn;
    private ImageView reportBtn;

    private String dateNode, recordID;
    private String in_subject, in_task, in_duration, in_completion, in_satisfaction, in_groupSize, in_method, in_location, in_remarks, in_recordID, in_date, fromActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_study_page);

        firebaseAuth = firebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference("Track Study");
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
                String source = "AddStudyRecordActivity";
                AddStudyRecord(source);
            }
        });


        RetrieveStudyRecords();


        //for Update
        GetIncomingIntent(savedInstanceState);



    }

    private void RetrieveStudyRecords() {

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String yr = Integer.toString(year);
                String mon = Integer.toString(month+1);
                String day = Integer.toString(dayOfMonth);
                dateNode = yr+"-"+mon+"-"+day;

                trackDate.setText(year + " / " + mon + " / " + dayOfMonth + " - Study Records");


                studyRecordList = new ArrayList<TrackStudyModel>();
                keyArray = new ArrayList<String>();
                recyclerView.setLayoutManager(new LinearLayoutManager(TrackStudyPage.this));

                databaseReference.keepSynced(true);
                databaseReference.child(userNode).child(dateNode).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        studyRecordList.clear();

                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            String key = ds.getKey();
                            TrackStudyModel studyRecord = ds.getValue(TrackStudyModel.class);
                            studyRecordList.add(studyRecord);
                            keyArray.add(key);
                        }

                        trackStudyAdapter = new TrackStudyAdapter(TrackStudyPage.this, studyRecordList,keyArray,userNode,dateNode);
                        recyclerView.setAdapter(trackStudyAdapter);

                        if(studyRecordList.size() != 0 ){
                            //calendarView.setSelectedDateVerticalBar(R.color.blue);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(TrackStudyPage.this, "Error, Please Try Again", Toast.LENGTH_LONG).show();

                    }
                });


            }
        });

    }

    private void GetIncomingIntent(Bundle savedInstanceState) {

        fromActivity = getIntent().getStringExtra("fromActivity");
        if(fromActivity.equals("UpdateStudyRecordActivity")){

            in_recordID = getIntent().getStringExtra("recordID");
            in_subject = getIntent().getStringExtra("subject");
            in_task = getIntent().getStringExtra("task");
            in_duration = getIntent().getStringExtra("duration");
            in_completion = getIntent().getStringExtra("completion");
            in_satisfaction = getIntent().getStringExtra("satisfaction");
            in_groupSize = getIntent().getStringExtra("groupSize");
            in_method = getIntent().getStringExtra("method");
            in_location = getIntent().getStringExtra("location");
            in_remarks = getIntent().getStringExtra("remarks");
            in_date = getIntent().getStringExtra("date");

        }else{
            return;
        }

        AddStudyRecord(fromActivity);

    }

    private void GenerateReport() {

        startActivity(new Intent(TrackStudyPage.this, StudyReportPage.class));
    }

    private void AddStudyRecord(final String source){

        if(source.equals("UpdateStudyRecordActivity")){
            dateNode = in_date;
        }

        if (dateNode == null){
            Toast.makeText(this, "Choose a Date on the Calender", Toast.LENGTH_LONG).show();
            return;

        }else {


            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.card_add_study_record, null);
            dialogBuilder.setView(dialogView);

            final AlertDialog dialog = dialogBuilder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            TextView selectedDate = dialog.findViewById(R.id.add_record_date);

            final EditText editTextSubject = dialog.findViewById(R.id.add_record_subject);
            final EditText editTextTask = dialog.findViewById(R.id.add_record_task);
            final EditText editTextRemarks = dialog.findViewById(R.id.add_record_remarks);

            final Spinner durationSpinner = dialog.findViewById(R.id.add_record_duration);
            final Spinner completionSpinner = dialog.findViewById(R.id.add_record_completion);
            final Spinner satisfactionSpinner = dialog.findViewById(R.id.add_record_satisfaction);
            final Spinner groupSizeSpinner = dialog.findViewById(R.id.add_record_groupSize);
            final Spinner methodSpinner = dialog.findViewById(R.id.add_record_method);
            final Spinner locationSpinner = dialog.findViewById(R.id.add_record_location);

            final ImageView dialogCloseBtn = dialog.findViewById(R.id.add_record_closeBtn);

            LinearLayout buttonLayout = dialog.findViewById(R.id.add_record_button_layout);
            Button dialogAddBtn = dialog.findViewById(R.id.add_record_addBtn);
            Button dialogUpdateBtn = dialog.findViewById(R.id.add_record_updateBtn);

            buttonLayout.removeView(dialogUpdateBtn);

            selectedDate.setText(dateNode);


            if(source.equals("UpdateStudyRecordActivity")){
                editTextSubject.setText(in_subject);
                editTextTask.setText(in_task);
                editTextRemarks.setText(in_remarks);
                dialogAddBtn.setText("Update");

                ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this, R.array.duration, android.R.layout.simple_spinner_item);
                durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<CharSequence> completionAdapter = ArrayAdapter.createFromResource(this, R.array.completion, android.R.layout.simple_spinner_item);
                durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<CharSequence> satisfactionAdapter = ArrayAdapter.createFromResource(this, R.array.satisfactory, android.R.layout.simple_spinner_item);
                durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<CharSequence> groupSizeAdapter = ArrayAdapter.createFromResource(this, R.array.groupSize, android.R.layout.simple_spinner_item);
                durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<CharSequence> methodAdapter = ArrayAdapter.createFromResource(this, R.array.learningMethod, android.R.layout.simple_spinner_item);
                durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this, R.array.studyLocation, android.R.layout.simple_spinner_item);
                durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                durationSpinner.setSelection(durationAdapter.getPosition(in_duration));
                completionSpinner.setSelection(completionAdapter.getPosition(in_completion));
                satisfactionSpinner.setSelection(satisfactionAdapter.getPosition(in_satisfaction));
                groupSizeSpinner.setSelection(groupSizeAdapter.getPosition(in_groupSize));
                methodSpinner.setSelection(methodAdapter.getPosition(in_method));
                locationSpinner.setSelection(locationAdapter.getPosition(in_location));

            }


            dialogAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String subject = editTextSubject.getText().toString().trim();
                    String task = editTextTask.getText().toString().trim();
                    String remarks = editTextRemarks.getText().toString().trim();

                    String duration = durationSpinner.getSelectedItem().toString().trim();
                    String completion = completionSpinner.getSelectedItem().toString().trim();
                    String satisfaction = satisfactionSpinner.getSelectedItem().toString().trim();
                    String groupSize = groupSizeSpinner.getSelectedItem().toString().trim();
                    String method = methodSpinner.getSelectedItem().toString().trim();
                    String location = locationSpinner.getSelectedItem().toString().trim();

                    if (duration.equals("Select") ||
                            completion.equals("Select") ||
                            satisfaction.equals("Select") ||
                            groupSize.equals("Select") ||
                            method.equals("Select") ||
                            location.equals("Select") ||
                            TextUtils.isEmpty(subject) ||
                            TextUtils.isEmpty(task) ){

                        Toast.makeText(TrackStudyPage.this, "Fill in all fields", Toast.LENGTH_LONG).show();

                    }else{

                        if(source.equals("UpdateStudyRecordActivity")){
                            recordID = in_recordID;

                        }else if(source.equals("AddStudyRecordActivity")){
                            recordID = databaseReference.push().getKey();
                        }

                        TrackStudyModel trackStudyModel = new TrackStudyModel(subject, task, duration, completion, satisfaction, groupSize, method, location, remarks, dateNode);

                        databaseReference = FirebaseDatabase.getInstance().getReference("Track Study").child(userNode).child(dateNode);
                        databaseReference.child(recordID).setValue(trackStudyModel);


                        if(source.equals("UpdateStudyRecordActivity")){

                            try {

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date parsedDateNode = sdf.parse(dateNode);
                                long millis = parsedDateNode.getTime();
                                calendarView.setDate(millis);

                                RetrieveStudyRecords();

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(TrackStudyPage.this, "Records Updated Successfully", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(TrackStudyPage.this, "Records Added Successfully", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                }
            });


            dialogCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }
    }

}
