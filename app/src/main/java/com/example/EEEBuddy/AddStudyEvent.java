package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddStudyEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private Toolbar toolbar;
    private ImageView backBtn, toolbarRightIcon;
    private TextView toolbarTitle;
    private EditText editTextSubjectCode, editTextSubjectName, editTextTask, editTextLocation;
    private EditText editTextDate, editTextStartTime, editTextEndTime, editTextGroupSize;
    private Integer duration;
    private Button createBtn;
    private String selectedStartTime, selectedEndTime,selectedDate;

    //private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener onStartTimeSetListener, onEndTimeSetListener;;




    //declare firebase stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference studyEventRef, messagesRef;
    private FirebaseUser user;
    private String userEmail, userNode;

    //variable to store incoming intent values;
    String in_subjectCode, in_subjectName, in_task, in_location, in_date, in_time, in_groupSize, in_startTime, in_endTime, fromActivity, in_eventID;
    String eventID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_study_event);



        //initialise elements on add_study_event layout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        toolbarRightIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        editTextSubjectCode = (EditText) findViewById(R.id.add_subjectCode);
        editTextSubjectName = (EditText) findViewById(R.id.add_subjectName);
        editTextTask = (EditText) findViewById(R.id.add_task);
        editTextLocation = (EditText) findViewById(R.id.add_location);
        editTextDate = (EditText) findViewById(R.id.add_date);
        editTextStartTime = (EditText) findViewById(R.id.add_startTime);
        editTextEndTime = (EditText) findViewById(R.id.add_endTime);
        editTextGroupSize = (EditText) findViewById(R.id.add_groupSize);
        createBtn = (Button) findViewById(R.id.add_addEvent);

        //Initialise firebase stuff
        firebaseAuth = firebaseAuth.getInstance();
        //firebaseDatabase = firebaseDatabase.getInstance();

        //relating user and study event. userNode is the foreign key in Study Event and the primary key in Student Profile
        user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        //change toolbar content
        toolbarTitle.setText("Create Study Event");
        toolbarRightIcon.setVisibility(View.GONE);
        //this.setSupportActionBar(toolbar);
        //this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //use to update the created event
        GetIncomingIntent();


        //select a date
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //select start time event listener and action
        editTextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddStudyEvent.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                if (hourOfDay < 12) {
                                    selectedStartTime = hourOfDay + ":" + minute + " AM";
                                } else {
                                    selectedStartTime = hourOfDay + ":" + minute + " PM";
                                }

                                editTextStartTime.setText(selectedStartTime);


                                //time validation
                                try {
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Date currentTime = new Date();
                                    String eventTime = selectedDate + " " + selectedStartTime.substring(0,selectedStartTime.lastIndexOf(" ")).trim();
                                    Date formattedEventTime = sdf2.parse(eventTime);

                                    if(formattedEventTime.compareTo(currentTime) < 0){
                                        editTextStartTime.setError("Event time should be in the future");
                                        //editTextStartTime.setText("");
                                    }else{
                                        editTextStartTime.setText(selectedStartTime);
                                        editTextStartTime.setError(null);
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, hour, minute, true);

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();

            }
        });

        //select end time event listener and action
        editTextEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddStudyEvent.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                if (hourOfDay < 12) {
                                    selectedEndTime = hourOfDay + ":" + minute + " AM";
                                } else {
                                    selectedEndTime = hourOfDay + ":" + minute + " PM";
                                }

                                editTextEndTime.setText(selectedEndTime);

                                //time validation
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa");
                                try {
                                    Date parsedStart = sdf.parse(selectedStartTime);
                                    Date parsedEnd = sdf.parse(selectedEndTime);

                                    if(parsedEnd.compareTo(parsedStart)<0){
                                        editTextEndTime.setError("End time cannot be before start time");
                                        editTextStartTime.setError("Start time cannot be after end time");
                                    }else{
                                        editTextEndTime.setError(null);
                                        editTextStartTime.setError(null);
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }



                            }
                        }, hour, minute, true);

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        //back to previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AddStudyEvent.this, StudyBuddyPage.class));
            }
        });

        //trigger to save data to firebase
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String source = createBtn.getText().toString().toLowerCase();
                createStudyEvent(source);

            }
        });


    }

    private void GetIncomingIntent(){

        fromActivity = getIntent().getStringExtra("from");

        if(fromActivity.equals("UpdateStudyEventActivity")){

            in_eventID = getIntent().getStringExtra("eventID");
            in_subjectCode = getIntent().getStringExtra("subjectCode");
            in_subjectName = getIntent().getStringExtra("subjectName");
            in_task = getIntent().getStringExtra("task");
            in_location = getIntent().getStringExtra("location");
            in_groupSize = getIntent().getStringExtra("groupSize");
            in_date = getIntent().getStringExtra("date");
            in_startTime = getIntent().getStringExtra("startTime");
            in_endTime = getIntent().getStringExtra("endTime");

            UpdateStudyEvent();

        }else{

            return;
        }

    }

    private void UpdateStudyEvent() {
        editTextSubjectCode.setText(in_subjectCode);
        editTextSubjectName.setText(in_subjectName);
        editTextTask.setText(in_task);
        editTextLocation.setText(in_location);
        editTextDate.setText(in_date);
        editTextStartTime.setText(in_startTime);
        editTextEndTime.setText(in_endTime);
        editTextGroupSize.setText(in_groupSize);

        createBtn.setText("Update");

    }



    //show Date Pick Dialog
    public void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                this,
                year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    //set Date on text field and do date validation
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        month = month +1;
        selectedDate = year + "-" + month + "-" + day;
        editTextDate.setText(selectedDate);

        //check if date is earlier than today
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            day = day+1;
            String modifiedDate = year + "-" + month + "-" + day;//allow to select the current date.
            Date now = new Date(System.currentTimeMillis());
            Date parsedDate = sdf.parse(modifiedDate);
            if (parsedDate.compareTo(now) < 0 ){ //parsedDate.compareTo(now) returns -1 if selected date is earlier than today, otherwise, returns 1.
                editTextDate.setError("please select today or future date");
                System.out.println(parsedDate.compareTo(now));
            }else{
                editTextDate.setError(null);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private void createStudyEvent(String activity) {

        String subjectCode = editTextSubjectCode.getText().toString().trim();
        String subjectName = editTextSubjectName.getText().toString().trim();
        String task = editTextTask.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String startTime = editTextStartTime.getText().toString().trim();
        String endTime = editTextEndTime.getText().toString().trim();
        String groupSize = editTextGroupSize.getText().toString().trim();




        if(!TextUtils.isEmpty(subjectCode) && !TextUtils.isEmpty(subjectName) && !TextUtils.isEmpty(task) && !TextUtils.isEmpty(location)
                && !TextUtils.isEmpty(date) && !TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime) && !TextUtils.isEmpty(groupSize)){


            if(Integer.parseInt(groupSize) < 2){
                editTextGroupSize.setError("Group Size must be more than 2 people");
                return;
            }


            //getting a unique id using push().getKey() method //this unique key is the primary key for the event
            studyEventRef = firebaseDatabase.getInstance().getReference("Study Event");

            if(activity.equals("update")){
                eventID = in_eventID;

            }else if (activity.equals("create")){
                eventID = studyEventRef.push().getKey();
            }

            //creating an StudyEvent Object
            StudyEvent studyEvent = new StudyEvent(subjectCode, subjectName, task, location, date, startTime, endTime, groupSize,userNode);

            //saving data to firebase
            //studyEventRef = firebaseDatabase.getInstance().getReference("Study Event").child(userNode);
            studyEventRef.child(userNode).child(eventID).setValue(studyEvent);

            //empty all text field
            editTextSubjectCode.setText("");
            editTextSubjectName.setText("");
            editTextTask.setText("");
            editTextLocation.setText("");
            editTextDate.setText("");
            editTextStartTime.setText("");
            editTextEndTime.setText("");
            editTextGroupSize.setText("");


            if(activity.equals("update")){
                Toast.makeText(AddStudyEvent.this, "Study Event Updated Successfully", Toast.LENGTH_LONG).show();

            }else if(activity.equals("create")){
                Toast.makeText(AddStudyEvent.this, "Study Event Created Successfully", Toast.LENGTH_LONG).show();

                CreateStudyGroupChat(subjectCode, subjectName, date);

            }

            startActivity(new Intent(AddStudyEvent.this, StudyBuddyPage.class));

        }else{

            Toast.makeText(AddStudyEvent.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();

        }

    }

    private void CreateStudyGroupChat(String subjectCode, String subjectName, String date) {

        messagesRef = firebaseDatabase.getInstance().getReference("Messages").child("Group Chat").child(eventID);
        messagesRef.child("groupInfo").child("groupName").setValue(subjectCode + " "  + subjectName + " " + date);
        messagesRef.child("members").child(userNode).child("identity").setValue("admin")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(AddStudyEvent.this, "Study Group Chat is Created", Toast.LENGTH_LONG).show();
                    }
                });

        //event creator creates the first message

        //time when message is sent
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy/MM/dd");
        String messageSentDate = currentDate.format(calendarForDate.getTime());

        Calendar calendarForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa"); //aa for AM/PM
        String messageSentTime = currentTime.format(calendarForTime.getTime());

        String messageKey = messagesRef.child("messages").push().getKey();
        messagesRef.child("messages").child(messageKey).child("date").setValue(messageSentDate);
        messagesRef.child("messages").child(messageKey).child("time").setValue(messageSentTime);
        messagesRef.child("messages").child(messageKey).child("message").setValue("Welcome to '" + subjectCode + " " + subjectName + "'Group Study Event!");
        messagesRef.child("messages").child(messageKey).child("from").setValue(userNode);
        messagesRef.child("messages").child(messageKey).child("type").setValue("text");

    }

}
