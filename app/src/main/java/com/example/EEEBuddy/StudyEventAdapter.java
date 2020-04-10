package com.example.EEEBuddy;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import okhttp3.internal.Internal;

public class StudyEventAdapter extends RecyclerView.Adapter<StudyEventAdapter.MyViewHolder> {

    Context context;
    ArrayList<StudyEvent> studyEventsList; //StudyEvent Model
    ArrayList<String> keyArray;

    //declare database stuff
    private FirebaseAuth firebaseAuth;
    private DatabaseReference registeredEventRef, studyEventRef;
    private String userEmail, userNode;


    public StudyEventAdapter(Context context, ArrayList<StudyEvent> studyEventsList, ArrayList<String> keyArray) {
        this.context = context;
        this.studyEventsList = studyEventsList;
        this.keyArray = keyArray;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));


        final String tempKey = keyArray.get(position);
        MaintenanceOfButtons(tempKey, holder);

        //check if the event is created by the current user
        CheckEventCreatedBy(tempKey, holder);

        //check if the current user has already joined the event
        JoinedEvent(tempKey, holder);

        //check if the event vacancy is full
        String createdBy = studyEventsList.get(position).getCreatedBy();
        CheckEventVacancy(tempKey, holder, createdBy);

        holder.subjectCode.setText(studyEventsList.get(position).getSubjectCode());
        holder.subjectName.setText(studyEventsList.get(position).getSubjectName());
        holder.task.setText("Task: " + studyEventsList.get(position).getTask());
        holder.location.setText("Location: " + studyEventsList.get(position).getLocation());
        holder.date.setText("Date: " + studyEventsList.get(position).getDate());
        holder.time.setText("Time: " + studyEventsList.get(position).getStartTime() + " - " + studyEventsList.get(position).getEndTime());


        if(Integer.parseInt(studyEventsList.get(position).getGroupSize()) <= 3){
            holder.groupSize.setTextColor(Color.parseColor("#cc0000"));
        }

        holder.groupSize.setText("Vacancy: " + studyEventsList.get(position).getGroupSize());


        holder.joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventID = keyArray.get(position);

                final StudyBuddyPage studyBuddyPage = new StudyBuddyPage();
                studyBuddyPage.showDialogBox(
                        context,
                        eventID,
                        holder,
                        studyEventsList.get(position).getCreatedBy(),
                        studyEventsList.get(position).getSubjectCode(),
                        studyEventsList.get(position).getSubjectName(),
                        studyEventsList.get(position).getTask(),
                        studyEventsList.get(position).getLocation(),
                        studyEventsList.get(position).getDate(),
                        studyEventsList.get(position).getGroupSize(),
                        studyEventsList.get(position).getStartTime() + " - " + studyEventsList.get(position).getEndTime()

                );

            }
        });

    }


    @Override
    public int getItemCount() {
        return studyEventsList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView subjectCode, subjectName, task, location, date, time, groupSize;
        final Button joinBtn, attendBtn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectCode = (TextView) itemView.findViewById(R.id.card_subjectCode);
            subjectName = (TextView) itemView.findViewById(R.id.card_subjectName);
            task = (TextView) itemView.findViewById(R.id.card_task);
            location = (TextView) itemView.findViewById(R.id.card_Location);
            date = (TextView) itemView.findViewById(R.id.card_date);
            time = (TextView) itemView.findViewById(R.id.card_time);
            groupSize = (TextView) itemView.findViewById(R.id.card_vacancy);
            joinBtn = (Button) itemView.findViewById(R.id.card_button);
            attendBtn = (Button) itemView.findViewById(R.id.card_button_attended);

            attendBtn.setVisibility(View.GONE);

        }
    }


    private void MaintenanceOfButtons(final String tempKey, final StudyEventAdapter.MyViewHolder holder) {


    }


    private void CheckEventCreatedBy(final String tempKey, final MyViewHolder holder) {

        studyEventRef = FirebaseDatabase.getInstance().getReference("Study Event");

        studyEventRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild(tempKey)) {

                    holder.joinBtn.setText("My Event");
                    holder.joinBtn.setEnabled(false);
                    holder.joinBtn.setBackgroundResource(R.drawable.btn_disabled);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void JoinedEvent(final String tempKey, final MyViewHolder holder) {

        registeredEventRef = FirebaseDatabase.getInstance().getReference("Registered Event").child(userNode);

        registeredEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild(tempKey)) {
                    holder.joinBtn.setBackgroundResource(R.drawable.btn_disabled);
                    holder.joinBtn.setText("Joined");
                    holder.joinBtn.setEnabled(false);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void CheckEventVacancy(String tempKey, final MyViewHolder holder, String createdBy) {

        studyEventRef = FirebaseDatabase.getInstance().getReference("Study Event");



        studyEventRef.child(createdBy).child(tempKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {


                    StudyEvent studyEvent = dataSnapshot.getValue(StudyEvent.class);
                    int groupSize = Integer.parseInt(studyEvent.getGroupSize());

                    if (groupSize == 0) {

                        holder.joinBtn.setText("FULL");
                        holder.joinBtn.setEnabled(false);
                        holder.joinBtn.setBackgroundResource(R.drawable.btn_disabled);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

}
