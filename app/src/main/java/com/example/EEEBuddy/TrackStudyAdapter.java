package com.example.EEEBuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;

public class TrackStudyAdapter extends RecyclerView.Adapter<TrackStudyAdapter.MyViewHolder> {

    Context context;
    ArrayList<TrackStudyModel> studyRecordList;

    public TrackStudyAdapter(Context context, ArrayList<TrackStudyModel> studyRecordList){

        this.context = context;
        this.studyRecordList = studyRecordList;
    }


    @NonNull
    @Override
    public TrackStudyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TrackStudyAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.study_record_layout,parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull TrackStudyAdapter.MyViewHolder holder, int position) {

        holder.subject.setText(studyRecordList.get(position).getSubject());
        holder.task.setText("Task: " + studyRecordList.get(position).getTask());
        holder.duration.setText("Duration: " + studyRecordList.get(position).getDuration() + " hrs");
        holder.completion.setText("Completion: " + studyRecordList.get(position).getCompletion() + "%");
        holder.satisfaction.setText("Satisfaction: " + studyRecordList.get(position).getSatisfaction() + "/5");
        holder.groupSize.setText("Group Size: " + studyRecordList.get(position).getGroupSize());
        holder.method.setText("Learning Method: " + studyRecordList.get(position).getMethod());
        holder.location.setText("Location: " + studyRecordList.get(position).getLocation());
        holder.remarks.setText("Remarks: " + studyRecordList.get(position).getRemarks());


        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.add_record_swipe_content));

        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        holder.swipeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "TODO..EDIT", Toast.LENGTH_LONG).show();
            }
        });

        holder.swipeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "TODO..DELETE", Toast.LENGTH_LONG).show();

            }
        });



    }

    @Override
    public int getItemCount() {
        return studyRecordList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView subject, task, duration, completion, satisfaction, groupSize, method, location, remarks;
        TextView swipeEdit, swipeDelete;
        SwipeLayout swipeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subject = (TextView) itemView.findViewById(R.id.study_record_subject);
            task = (TextView) itemView.findViewById(R.id.study_record_task);
            duration = (TextView) itemView.findViewById(R.id.study_record_duration);
            completion = (TextView) itemView.findViewById(R.id.study_record_completion);
            satisfaction = (TextView) itemView.findViewById(R.id.study_record_satisfaction);
            groupSize = (TextView) itemView.findViewById(R.id.study_record_groupSize);
            method = (TextView) itemView.findViewById(R.id.study_record_method);
            location = (TextView) itemView.findViewById(R.id.study_record_location);
            remarks = (TextView) itemView.findViewById(R.id.study_record_remarks);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.add_record_swipe);
            swipeEdit = (TextView) itemView.findViewById(R.id.add_record_swipe_edit);
            swipeDelete = (TextView) itemView.findViewById(R.id.add_record_swipe_delete);

        }

    }
}