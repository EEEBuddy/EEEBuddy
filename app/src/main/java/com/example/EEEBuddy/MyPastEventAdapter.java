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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyPastEventAdapter extends RecyclerView.Adapter<MyPastEventAdapter.MyViewHolder> {

    Context context;
    ArrayList<StudyEvent> pastEventList; //StudyEvent Model
    ArrayList<String> keyArray;
    DatabaseReference studyEventRef;
    String userNode;


    public MyPastEventAdapter (Context context, ArrayList<StudyEvent> pastEventList, ArrayList<String> keyArray, String userNode) {
        this.context = context;
        this.pastEventList = pastEventList;
        this.keyArray = keyArray;
        this.userNode = userNode;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_my_upcoming_event,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {



        holder.subjectCode.setText(pastEventList.get(position).getSubjectCode());
        holder.subjectName.setText(pastEventList.get(position).getSubjectName());
        holder.task.setText("Task: " + pastEventList.get(position).getTask());
        holder.location.setText("Location: " + pastEventList.get(position).getLocation());
        holder.date.setText("Date: " + pastEventList.get(position).getDate());
        holder.time.setText("Time: " + pastEventList.get(position).getStartTime() + " - " + pastEventList.get(position).getEndTime());
        holder.groupSize.setText("Vacancy: " + pastEventList.get(position).getGroupSize());

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.my_event_swipe_content));

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
                Toast.makeText(v.getContext(), "TODO..ADD TO TRACK", Toast.LENGTH_LONG).show();
            }
        });

        holder.swipeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempKey = keyArray.get(position);

                studyEventRef = FirebaseDatabase.getInstance().getReference("Study Event");
                studyEventRef.keepSynced(true);

                studyEventRef.child(userNode).child(tempKey).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(context, "Event Deleted", Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });


    }

    @Override
    public int getItemCount() {
        return pastEventList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView subjectCode, subjectName, task, location, date, time, groupSize;
        SwipeLayout swipeLayout;
        TextView swipeEdit, swipeDelete;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectCode = (TextView) itemView.findViewById(R.id.my_event_subjectCode);
            subjectName = (TextView) itemView.findViewById(R.id.my_event_subjectName);
            task = (TextView) itemView.findViewById(R.id.my_event_task);
            location = (TextView) itemView.findViewById(R.id.my_event_Location);
            date = (TextView) itemView.findViewById(R.id.my_event_date);
            time = (TextView) itemView.findViewById(R.id.my_event_time);
            groupSize = (TextView) itemView.findViewById(R.id.my_event_vacancy);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.my_event_swipeLayout);
            swipeEdit = (TextView) itemView.findViewById(R.id.my_event_swipe_edit);
            swipeDelete = (TextView) itemView.findViewById(R.id.my_event_swipe_delete);

            swipeEdit.setText("Add to Study Track");

        }
    }


}
