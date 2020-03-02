package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseIndexArray;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.mikephil.charting.utils.ColorTemplate.JOYFUL_COLORS;

public class StudyReportPage extends AppCompatActivity {

    //toolbar items
    ImageView backBtn;
    ImageView rightIcon;
    TextView toolbarTitle;

    Spinner monthSpinner;
    Button loadButton;

    TextView totalStudyHourTextView;
    TextView totalTaskTextView;
    TextView avgTaskSatisfactionTextView;
    TextView avgTaskCompletionTextView;
    LinearLayout datasheet;


    LineChart studyHourLineChart;
    PieChart studyGroupSizePieChart;
    PieChart studyLocationPieChart;
    PieChart studyMethodPieChart;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userEmail, userNode;

    Map<String, Integer> groupSizeDataMap = new HashMap<String, Integer>();
    Map<String, Integer> studyMethodDataMap = new HashMap<String, Integer>();
    Map<String, Integer> studyLocationDataMap = new HashMap<String, Integer>();

    int selectedMonthDigit = 0;
    int totalStudyHours = 0;
    int totalTasks = 0;
    int totalTaskCompletion = 0;
    int totalTaskSatisfaction = 0;
    double avgTaskCompletion = 0.0;
    double avgTaskSatisfaction = 0.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_report_page);

        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        rightIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        rightIcon.setVisibility(View.GONE);
        toolbarTitle.setText("Study Report");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudyReportPage.this,TrackStudyPage.class));
            }
        });

        monthSpinner = (Spinner) findViewById(R.id.report_spinner);
        loadButton = (Button) findViewById(R.id.report_loadBtn);

        totalStudyHourTextView = (TextView) findViewById(R.id.report_total_duration);
        totalTaskTextView = (TextView) findViewById(R.id.report_total_tasks);
        avgTaskCompletionTextView = (TextView) findViewById(R.id.report_avg_completion);
        avgTaskSatisfactionTextView = (TextView) findViewById(R.id.report_avg_task_satisfaction);
        datasheet = (LinearLayout) findViewById(R.id.report_datasheet);

        firebaseAuth = firebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));
        databaseReference = FirebaseDatabase.getInstance().getReference("Track Study");


        studyHourLineChart = (LineChart) findViewById(R.id.report_study_hour_chart);
        studyGroupSizePieChart = (PieChart) findViewById(R.id.report_group_size_chart);
        studyLocationPieChart = (PieChart) findViewById(R.id.report_study_location_chart);
        studyMethodPieChart = (PieChart) findViewById(R.id.report_study_method_chart);

        //studyHours Line Chart Setting



        //studyGroupSize Chart Setting
        studyGroupSizePieChart.setUsePercentValues(true);
        studyGroupSizePieChart.getDescription().setEnabled(false);
        studyGroupSizePieChart.setExtraOffsets(5,5,5,5);

        studyGroupSizePieChart.setDragDecelerationFrictionCoef(0.99f);

        studyGroupSizePieChart.setDrawHoleEnabled(true);
        studyGroupSizePieChart.setHoleColor(Color.WHITE);
        studyGroupSizePieChart.setTransparentCircleRadius(70f);
        studyGroupSizePieChart.setCenterTextColor(Color.BLACK);
        studyGroupSizePieChart.setEntryLabelColor(Color.BLACK);

        //studyLocationPieChart Chart Setting
        studyLocationPieChart.setUsePercentValues(true);
        studyLocationPieChart.getDescription().setEnabled(false);
        studyLocationPieChart.setExtraOffsets(5,5,5,5);

        studyLocationPieChart.setDragDecelerationFrictionCoef(0.99f);

        studyLocationPieChart.setDrawHoleEnabled(true);
        studyLocationPieChart.setHoleColor(Color.WHITE);
        studyLocationPieChart.setTransparentCircleRadius(70f);


        //studyGroupSize Chart Setting
        studyMethodPieChart.setUsePercentValues(true);
        studyMethodPieChart.getDescription().setEnabled(false);
        studyMethodPieChart.setExtraOffsets(5,5,5,5);

        studyMethodPieChart.setDragDecelerationFrictionCoef(0.99f);

        studyMethodPieChart.setDrawHoleEnabled(true);
        studyMethodPieChart.setHoleColor(Color.WHITE);
        studyMethodPieChart.setTransparentCircleRadius(70f);




        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetReportData();
            }
        });

    }

    private void GetReportData() {

        //initialize data map
        groupSizeDataMap.put("1", 0);
        groupSizeDataMap.put("2-3", 0);
        groupSizeDataMap.put("4-6", 0);
        groupSizeDataMap.put("7-10", 0);
        groupSizeDataMap.put(">10", 0);

        studyMethodDataMap.put("Self Study", 0);
        studyMethodDataMap.put("Discussion", 0);
        studyMethodDataMap.put("Consultation", 0);

        studyLocationDataMap.put("Library", 0);
        studyLocationDataMap.put("Home", 0);
        studyLocationDataMap.put("Open Area", 0);
        studyLocationDataMap.put("Cafe", 0);
        studyLocationDataMap.put("Others", 0);

        totalStudyHours = 0;
        totalTasks = 0;
        totalTaskCompletion = 0;
        totalTaskSatisfaction = 0;
        avgTaskCompletion = 0;
        avgTaskSatisfaction = 0;


        //read month selected from the spinner and convert to digit
        String month = monthSpinner.getSelectedItem().toString().trim();

        if(month.equals("January")){
            selectedMonthDigit = 1;
        }else if(month.equals("February")){
            selectedMonthDigit = 2;
        }else if(month.equals("March")){
            selectedMonthDigit = 3;
        }else if(month.equals("April")){
            selectedMonthDigit = 4;
        }else if(month.equals("May")){
            selectedMonthDigit = 5;
        }else if(month.equals("June")){
            selectedMonthDigit = 6;
        }else if(month.equals("July")){
            selectedMonthDigit = 7;
        }else if(month.equals("August")){
            selectedMonthDigit = 8;
        }else if(month.equals("September")){
            selectedMonthDigit = 9;
        }else if(month.equals("October")){
            selectedMonthDigit = 10;
        }else if(month.equals("November")){
            selectedMonthDigit = 11;
        }else if(month.equals("December")){
            selectedMonthDigit = 12;
        }

        //Check selected month if is in the Past
        //Report can ONLY be generated for PAST Months
        final Calendar calendar1 = Calendar.getInstance();
        Date now = new Date(System.currentTimeMillis());
        calendar1.setTime(now);

        int currentMonth = calendar1.get(Calendar.MONTH)+1;

        final int MonthDiff = currentMonth - selectedMonthDigit;


        if(MonthDiff <= 0){
            datasheet.setVisibility(View.GONE);
            Toast.makeText(StudyReportPage.this, "Report can ONLY be generated for PAST Months", Toast.LENGTH_LONG).show();
        }else{
            datasheet.setVisibility(View.VISIBLE);
            //get data from Track Study node
            databaseReference.child(userNode).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                        //check the month node is the month selected by user
                        String date = dataSnapshot1.getKey().toString().trim();
                        Calendar calendar2 = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            Date parsedDate = sdf.parse(date);
                            calendar2.setTime(parsedDate);


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(selectedMonthDigit == (calendar2.get(Calendar.MONTH)+1)){
                            for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){

                                TrackStudyModel trackStudyModel = dataSnapshot2.getValue(TrackStudyModel.class);

                                //get total study duration
                                String tempStudyHour = trackStudyModel.getDuration().trim();
                                int tempStudyHourInt = Integer.parseInt(tempStudyHour);
                                totalStudyHours = totalStudyHours + tempStudyHourInt;

                                //get total number of tasks
                                totalTasks++;

                                //get total task completion
                                String tempCompletion = trackStudyModel.getCompletion();
                                int tempCompletionInt = Integer.parseInt(tempCompletion);
                                totalTaskCompletion = totalTaskCompletion + tempCompletionInt;

                                //get total task satisfaction
                                String tempSatisfaction = trackStudyModel.getSatisfaction();
                                int tempSatisfactionInt = Integer.parseInt(tempSatisfaction);
                                totalTaskSatisfaction = totalTaskSatisfaction + tempSatisfactionInt;

                                //get data for group size chart
                                String tempGroupSize = trackStudyModel.getGroupSize();
                                if(tempGroupSize.equals("1")){
                                    groupSizeDataMap.put("1", groupSizeDataMap.get("1") + 1);

                                }else if(tempGroupSize.equals("2-3")){
                                    groupSizeDataMap.put("2-3", groupSizeDataMap.get("2-3") + 1);

                                }else if(tempGroupSize.equals("4-6")){
                                    groupSizeDataMap.put("4-6", groupSizeDataMap.get("4-6") + 1);

                                }else if(tempGroupSize.equals("7-10")){
                                    groupSizeDataMap.put("7-10", groupSizeDataMap.get("7-10") + 1);

                                }else if(tempGroupSize.equals(">10")){
                                    groupSizeDataMap.put(">10", groupSizeDataMap.get(">10") + 1);
                                }

                                //get data for study method chart
                                String tempMethod = trackStudyModel.getMethod();
                                if(tempMethod.equals("Self Study")){
                                    studyMethodDataMap.put("Self Study", studyMethodDataMap.get("Self Study") +1);

                                }else if(tempMethod.equals("Discussion")){
                                    studyMethodDataMap.put("Discussion", studyMethodDataMap.get("Discussion") +1);

                                }else if(tempMethod.equals("Consultation")){
                                    studyMethodDataMap.put("Consultation", studyMethodDataMap.get("Consultation") +1);
                                }

                                //get data for study location
                                String tempLocation = trackStudyModel.getLocation();
                                if(tempLocation.equals("Library")){
                                    studyLocationDataMap.put("Library", studyLocationDataMap.get("Library") +1);

                                }else if(tempLocation.equals("Home")){
                                    studyLocationDataMap.put("Home", studyLocationDataMap.get("Home") +1);

                                }else if(tempLocation.equals("Open Area")){
                                    studyLocationDataMap.put("Open Area", studyLocationDataMap.get("Open Area") +1);

                                }else if(tempLocation.equals("Cafe")){
                                    studyLocationDataMap.put("Cafe", studyLocationDataMap.get("Cafe") +1);

                                }else if(tempLocation.equals("Others")){
                                    studyLocationDataMap.put("Others", studyLocationDataMap.get("Others") +1);

                                }

                                ShowDataAndDrawGraph();

                            }

                        }

                    }


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void ShowDataAndDrawGraph() {

        DecimalFormat df = new DecimalFormat("0.00");
        totalStudyHourTextView.setText(Integer.toString(totalStudyHours));
        totalTaskTextView.setText(Integer.toString(totalTasks));

        //cal avg task completion
        avgTaskCompletion = Double.parseDouble(df.format(totalTaskCompletion/totalTasks));
        avgTaskCompletionTextView.setText(Double.toString(avgTaskCompletion));

        //cal avg task satisfaction
        avgTaskSatisfaction = Double.parseDouble(df.format(totalTaskSatisfaction/totalTasks));
        avgTaskSatisfactionTextView.setText(Double.toString(avgTaskSatisfaction));


        //draw studyGroupSize PieChart
        studyGroupSizePieChart.animateY(1000, Easing.EaseInCubic);
        studyGroupSizePieChart.getViewPortHandler().setMaximumScaleX(2f);
        studyGroupSizePieChart.getViewPortHandler().setMaximumScaleY(2f);

        ArrayList<PieEntry> groupSize = new ArrayList<>();
        groupSize.add(new PieEntry(groupSizeDataMap.get("1"), "individual"));
        groupSize.add(new PieEntry(groupSizeDataMap.get("2-3"), "2-3"));
        groupSize.add(new PieEntry(groupSizeDataMap.get("4-6"), "4-6"));
        groupSize.add(new PieEntry(groupSizeDataMap.get("7-10"), "7-10"));
        groupSize.add(new PieEntry(groupSizeDataMap.get(">10"), ">10"));

        PieDataSet groupSizeDataSet = new PieDataSet(groupSize,"Study Group Size");
        groupSizeDataSet.setSliceSpace(3f);
        groupSizeDataSet.setSelectionShift(5f);
        groupSizeDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        PieData groupSizeData = new PieData(groupSizeDataSet);
        groupSizeData.setValueTextSize(12f);
        groupSizeData.setValueTextColor(Color.BLACK);

        studyGroupSizePieChart.setData(groupSizeData);
        studyGroupSizePieChart.invalidate();//refresh chart

        //draw studyLocation PieChart
        studyLocationPieChart.animateY(1000, Easing.EaseInCubic);
        studyLocationPieChart.getViewPortHandler().setMaximumScaleX(2f);
        studyLocationPieChart.getViewPortHandler().setMaximumScaleY(2f);

        ArrayList<PieEntry> studyLocation = new ArrayList<>();
        studyLocation.add(new PieEntry(studyLocationDataMap.get("Library"), "Library"));
        studyLocation.add(new PieEntry(studyLocationDataMap.get("Home"), "Home"));
        studyLocation.add(new PieEntry(studyLocationDataMap.get("Open Area"), "Open Area"));
        studyLocation.add(new PieEntry(studyLocationDataMap.get("Cafe"), "Cafe"));
        studyLocation.add(new PieEntry(studyLocationDataMap.get("Others"), "Others"));



        PieDataSet studyLocationDataSet = new PieDataSet(studyLocation,"Study Locations");
        studyLocationDataSet.setSliceSpace(3f);
        studyLocationDataSet.setSelectionShift(5f);
        studyLocationDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        PieData studyLocationData = new PieData(studyLocationDataSet);
        studyLocationData.setValueTextSize(12f);
        studyLocationData.setValueTextColor(Color.BLACK);

        studyLocationPieChart.setData(studyLocationData);
        studyLocationPieChart.invalidate(); //refresh chart

        //draw studyLocation PieChart
        studyMethodPieChart.animateY(1000, Easing.EaseInCubic);
        studyMethodPieChart.getViewPortHandler().setMaximumScaleX(2f);
        studyMethodPieChart.getViewPortHandler().setMaximumScaleY(2f);

        ArrayList<PieEntry> studyMethod = new ArrayList<>();
        studyMethod.add(new PieEntry(studyMethodDataMap.get("Self Study"), "Self Study"));
        studyMethod.add(new PieEntry(studyMethodDataMap.get("Discussion"), "Discussion"));
        studyMethod.add(new PieEntry(studyMethodDataMap.get("Consultation"), "Consultation"));


        PieDataSet studyMethodDataSet = new PieDataSet(studyMethod,"Study Methods");
        studyMethodDataSet.setSliceSpace(3f);
        studyMethodDataSet.setSelectionShift(5f);
        studyMethodDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        PieData studyMethodData = new PieData(studyMethodDataSet);
        studyMethodData.setValueTextSize(12f);
        studyMethodData.setValueTextColor(Color.BLACK);

        studyMethodPieChart.setData(studyMethodData);
        studyMethodPieChart.invalidate();//refresh chart

    }
}
