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

public class RegisteredEventAdapter extends RecyclerView.Adapter<RegisteredEventAdapter.MyViewHolder> {

    Context context;
    ArrayList<StudyEvent> upcomingEventList; //StudyEvent Model


    public RegisteredEventAdapter (Context context, ArrayList<StudyEvent> upcomingEventList) {
        this.context = context;
        this.upcomingEventList = upcomingEventList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.subjectCode.setText(upcomingEventList.get(position).getSubjectCode());
        holder.subjectName.setText(upcomingEventList.get(position).getSubjectName());
        holder.task.setText("Task: " + upcomingEventList.get(position).getTask());
        holder.location.setText("Location: " + upcomingEventList.get(position).getLocation());
        holder.date.setText("Date: " + upcomingEventList.get(position).getDate());
        holder.time.setText("Time: " + upcomingEventList.get(position).getTime());
        holder.groupSize.setText("Vacancy: " + upcomingEventList.get(position).getGroupSize());

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO...
                Toast.makeText(context, "TODO...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return upcomingEventList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView subjectCode, subjectName, task, location, date, time, groupSize;
        final Button cancelButton;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectCode = (TextView) itemView.findViewById(R.id.card_subjectCode);
            subjectName = (TextView) itemView.findViewById(R.id.card_subjectName);
            task = (TextView) itemView.findViewById(R.id.card_task);
            location = (TextView) itemView.findViewById(R.id.card_Location);
            date = (TextView) itemView.findViewById(R.id.card_date);
            time = (TextView) itemView.findViewById(R.id.card_time);
            groupSize = (TextView) itemView.findViewById(R.id.card_vacancy);
            cancelButton= (Button) itemView.findViewById(R.id.card_button);

            cancelButton.setText("Cancle");


        }
    }


}
