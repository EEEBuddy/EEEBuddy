package com.example.EEEBuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toolbar;

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
    private EditText courseCode, courseName, task, location, date, startTime, endTime, groupSize;
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
        courseCode = (EditText) findViewById(R.id.add_courseCode);
        courseName = (EditText) findViewById(R.id.add_course);
        task = (EditText) findViewById(R.id.add_task);
        location = (EditText) findViewById(R.id.add_location);
        date = (EditText) findViewById(R.id.add_date);
        startTime = (EditText) findViewById(R.id.add_startTime);
        endTime = (EditText) findViewById(R.id.add_endTime);
        groupSize = (EditText) findViewById(R.id.add_groupSize);
        createBtn = (Button) findViewById(R.id.add_addEvent);

        //Initialise firebase stuff
        firebaseAuth = firebaseAuth.getInstance();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Study Event");
        user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        //change toolbar content
        toolbarTitle.setText("Create Study Event");
        toolbarRightIcon.setVisibility(View.GONE);

        //select a date
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //select start time event listener and action
        startTime.setOnClickListener(new View.OnClickListener() {
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

                                startTime.setText(selectedStartTime);
                            }
                        }, hour, minute, true);

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();

            }
        });

        //select end time event listener and action
        endTime.setOnClickListener(new View.OnClickListener() {
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

                                endTime.setText(selectedEndTime);

                                //time validation
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                try {
                                    Date parsedStart = sdf.parse(selectedStartTime);
                                    Date parsedEnd = sdf.parse(selectedEndTime);

                                    if(parsedEnd.compareTo(parsedStart)<0){
                                        endTime.setError("End time cannot be early than start time");
                                        startTime.setError("Start time cannot be later than end time");
                                    }else{
                                        endTime.setError(null);
                                        startTime.setError(null);
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                //startActivity(new Intent(this,StudyEvent.class));
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


    //show Date Pick Dialog
    private void showDatePickerDialog() {
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
        date.setText(selectedDate);

        //check if date is earlier than today
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            day = day+1;
            String modifiedDate = day + "/" + month + "/" + year;//allow to select the current date.
            Date now = new Date(System.currentTimeMillis());
            Date parsedDate = sdf.parse(modifiedDate);
            if (parsedDate.compareTo(now) < 0 ){ //parsedDate.compareTo(now) returns -1 if selected date is earlier than today, otherwise, returns 1.
                date.setError("please select today or future date");
                System.out.println(parsedDate.compareTo(now));
            }else{
                date.setError(null);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
