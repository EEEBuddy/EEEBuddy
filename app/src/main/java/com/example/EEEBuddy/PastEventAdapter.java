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

import com.daimajia.swipe.SwipeLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PastEventAdapter extends RecyclerView.Adapter<PastEventAdapter.MyViewHolder> {

    Context context;
    ArrayList<StudyEvent> pastEventList; //StudyEvent Model


    public PastEventAdapter (Context context, ArrayList<StudyEvent> pastEventList) {
        this.context = context;
        this.pastEventList = pastEventList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.subjectCode.setText(pastEventList.get(position).getSubjectCode());
        holder.subjectName.setText(pastEventList.get(position).getSubjectName());
        holder.task.setText("Task: " + pastEventList.get(position).getTask());
        holder.location.setText("Location: " + pastEventList.get(position).getLocation());
        holder.date.setText("Date: " + pastEventList.get(position).getDate());
        holder.time.setText("Time: " + pastEventList.get(position).getTime());
        holder.groupSize.setText("Vacancy: " + pastEventList.get(position).getGroupSize());

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO...
                Toast.makeText(context, "TODO...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return pastEventList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView subjectCode, subjectName, task, location, date, time, groupSize;
        final Button deleteBtn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectCode = (TextView) itemView.findViewById(R.id.card_subjectCode);
            subjectName = (TextView) itemView.findViewById(R.id.card_subjectName);
            task = (TextView) itemView.findViewById(R.id.card_task);
            location = (TextView) itemView.findViewById(R.id.card_Location);
            date = (TextView) itemView.findViewById(R.id.card_date);
            time = (TextView) itemView.findViewById(R.id.card_time);
            groupSize = (TextView) itemView.findViewById(R.id.card_vacancy);
            deleteBtn= (Button) itemView.findViewById(R.id.card_button);

            deleteBtn.setText("Delete");

        }
    }


}
