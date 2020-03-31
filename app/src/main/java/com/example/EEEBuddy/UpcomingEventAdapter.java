package com.example.EEEBuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.MyViewHolder> {

    Context context;
    ArrayList<StudyEvent> upcomingEventList; //StudyEvent Model
    ArrayList<String> keyArray;
    String userNode;
    DatabaseReference registeredEventRef;

    public UpcomingEventAdapter(Context context, ArrayList<StudyEvent> upcomingEventList, ArrayList<String> keyArray, String userNode) {
        this.context = context;
        this.upcomingEventList = upcomingEventList;
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

        holder.subjectCode.setText(upcomingEventList.get(position).getSubjectCode());
        holder.subjectName.setText(upcomingEventList.get(position).getSubjectName());
        holder.task.setText("Task: " + upcomingEventList.get(position).getTask());
        holder.location.setText("Location: " + upcomingEventList.get(position).getLocation());
        holder.date.setText("Date: " + upcomingEventList.get(position).getDate());
        holder.time.setText("Time: " + upcomingEventList.get(position).getStartTime() + " - " + upcomingEventList.get(position).getEndTime());
        holder.groupSize.setText("Vacancy: " + upcomingEventList.get(position).getGroupSize());

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


        holder.swipeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempKey = keyArray.get(position);

                registeredEventRef = FirebaseDatabase.getInstance().getReference("Registered Event");

                registeredEventRef.child(userNode).child(tempKey).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(context, "Event Deleted", Toast.LENGTH_LONG).show();
                            }
                        });

                registeredEventRef.keepSynced(true);

            }
        });
    }

    @Override
    public int getItemCount() {
        return upcomingEventList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView subjectCode, subjectName, task, location, date, time, groupSize;
        SwipeLayout swipeLayout;
        TextView swipeEdit, swipeDelete;
        LinearLayout eventSwipeContainer;
        ViewGroup.LayoutParams params;



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

            eventSwipeContainer = (LinearLayout) itemView.findViewById(R.id.my_event_swipe_content);
            eventSwipeContainer.removeView(swipeEdit);

            params = eventSwipeContainer.getLayoutParams();
            params.width =200;
            eventSwipeContainer.setLayoutParams(params);

        }
    }


}
