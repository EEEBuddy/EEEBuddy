package com.example.EEEBuddy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TrackStudyAdapter extends RecyclerView.Adapter<TrackStudyAdapter.MyViewHolder> {

    Context context;
    ArrayList<TrackStudyModel> studyRecordList;
    ArrayList<String> keyArray;
    String userNode, dateNode;

    DatabaseReference trackStudyRef;

    public TrackStudyAdapter(Context context, ArrayList<TrackStudyModel> studyRecordList, ArrayList<String> keyArray, String userNode, String dateNode){

        this.context = context;
        this.studyRecordList = studyRecordList;
        this.keyArray = keyArray;
        this.userNode = userNode;
        this.dateNode = dateNode;
    }


    @NonNull
    @Override
    public TrackStudyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TrackStudyAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.study_record_layout,parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull TrackStudyAdapter.MyViewHolder holder, final int position) {

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

                String eventID = keyArray.get(position);

                Intent intent = new Intent(context, TrackStudyPage.class);

                intent.putExtra("recordID", eventID);
                intent.putExtra("fromActivity", "UpdateStudyRecordActivity");
                intent.putExtra("subject",studyRecordList.get(position).getSubject());
                intent.putExtra("task", studyRecordList.get(position).getTask());
                intent.putExtra("duration", studyRecordList.get(position).getDuration());
                intent.putExtra("completion",studyRecordList.get(position).getCompletion() );
                intent.putExtra("satisfaction", studyRecordList.get(position).getSatisfaction() );
                intent.putExtra("groupSize", studyRecordList.get(position).getGroupSize());
                intent.putExtra("method", studyRecordList.get(position).getMethod());
                intent.putExtra("location", studyRecordList.get(position).getLocation());
                intent.putExtra("remarks", studyRecordList.get(position).getRemarks());
                intent.putExtra("date", studyRecordList.get(position).getDate());

                context.startActivity(intent);

            }
        });

        holder.swipeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempKey = keyArray.get(position);

                trackStudyRef = FirebaseDatabase.getInstance().getReference("Track Study");
                trackStudyRef.child(userNode).child(dateNode).child(tempKey).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "Event Deleted", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


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
