package com.example.EEEBuddy;

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
    private String selectedStartTime, selectedEndTime;

    //private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener onStartTimeSetListener, onEndTimeSetListener;;




    //declare firebase stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String userEmail, userNode;




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
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                try {
                                    Date parsedStart = sdf.parse(selectedStartTime);
                                    Date parsedEnd = sdf.parse(selectedEndTime);

                                    if(parsedEnd.compareTo(parsedStart)<0){
                                        editTextEndTime.setError("End time cannot be early than start time");
                                        editTextStartTime.setError("Start time cannot be later than end time");
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
                createStudyEvent();
            }
        });


    }

    private void createStudyEvent() {
        String subjectCode = editTextSubjectCode.getText().toString().trim();
        String subjectName = editTextSubjectName.getText().toString().trim();
        String task = editTextTask.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String startTime = editTextStartTime.getText().toString().trim();
        String endTime = editTextEndTime.getText().toString().trim();
        String groupSize = editTextGroupSize.getText().toString().trim();




        if(!(TextUtils.isEmpty(subjectCode) && TextUtils.isEmpty(subjectName) && TextUtils.isEmpty(task) && TextUtils.isEmpty(location)
                && TextUtils.isEmpty(date) && TextUtils.isEmpty(startTime) && TextUtils.isEmpty(endTime) && TextUtils.isEmpty(groupSize))){

            String time = startTime + " - " + endTime;


            //getting a unique id using push().getKey() method //this unique key is the primary key for the event
            String eventID = databaseReference.push().getKey();

            //creating an StudyEvent Object
            StudyEvent studyEvent = new StudyEvent(subjectCode, subjectName, task, location, date, time, groupSize,userNode);

            //saving data to firebase
            databaseReference = firebaseDatabase.getInstance().getReference("Study Event").child(userNode);
            databaseReference.child(eventID).setValue(studyEvent);

            //empty all text field
            editTextSubjectCode.setText("");
            editTextSubjectName.setText("");
            editTextTask.setText("");
            editTextLocation.setText("");
            editTextDate.setText("");
            editTextStartTime.setText("");
            editTextEndTime.setText("");
            editTextGroupSize.setText("");


            Toast.makeText(this, "Study Event Created", Toast.LENGTH_LONG).show();

        }else{

            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_LONG).show();

        }

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
        String selectedDate = day + "/" + month + "/" + year;
        editTextDate.setText(selectedDate);

        //check if date is earlier than today
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            day = day+1;
            String modifiedDate = day + "/" + month + "/" + year;//allow to select the current date.
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

}
