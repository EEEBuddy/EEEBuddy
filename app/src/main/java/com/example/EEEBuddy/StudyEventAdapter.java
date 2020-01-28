package com.example.EEEBuddy;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudyEventAdapter extends RecyclerView.Adapter<StudyEventAdapter.MyViewHolder> {

    Context context;
    ArrayList<StudyEvent> studyEvents; //StudyEvent Model


    public StudyEventAdapter(Context context, ArrayList<StudyEvent> studyEvents) {
        this.context = context;
        this.studyEvents = studyEvents;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.subjectCode.setText(studyEvents.get(position).getSubjectCode());
        holder.subjectName.setText(studyEvents.get(position).getSubjectName());
        holder.task.setText("Task: " + studyEvents.get(position).getTask());
        holder.location.setText("Location: " + studyEvents.get(position).getLocation());
        holder.date.setText("Date: " + studyEvents.get(position).getDate());
        holder.time.setText("Time: " + studyEvents.get(position).getTime());
        holder.groupSize.setText("Vacancy: " + studyEvents.get(position).getGroupSize());

        holder.joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final StudyBuddyPage studyBuddyPage = new StudyBuddyPage();
               studyBuddyPage.showDialogBox(
                       context,
                       studyEvents.get(position).getSubjectCode(),
                       studyEvents.get(position).getSubjectName(),
                       studyEvents.get(position).getTask(),
                       studyEvents.get(position).getLocation(),
                       studyEvents.get(position).getDate(),
                       studyEvents.get(position).getTime()

                       );
            }
        });

    }

    @Override
    public int getItemCount() {
        return studyEvents.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView subjectCode, subjectName, task, location, date, time, groupSize;
        final Button joinBtn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectCode = (TextView) itemView.findViewById(R.id.card_subjectCode);
            subjectName = (TextView) itemView.findViewById(R.id.card_subjectName);
            task = (TextView) itemView.findViewById(R.id.card_task);
            location = (TextView) itemView.findViewById(R.id.card_Location);
            date = (TextView) itemView.findViewById(R.id.card_date);
            time = (TextView) itemView.findViewById(R.id.card_time);
            groupSize = (TextView) itemView.findViewById(R.id.card_vacancy);
            joinBtn = (Button) itemView.findViewById(R.id.card_join);


        }
    }


}
