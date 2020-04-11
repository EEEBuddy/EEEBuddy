package com.example.EEEBuddy;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyUpcomingEventAdapter extends RecyclerView.Adapter<MyUpcomingEventAdapter.MyViewHolder> {

    Context context;
    ArrayList<StudyEvent> myUpcomingEventList; //StudyEvent Model
    ArrayList<String> keyArray;
    DatabaseReference studyEventRef, messageRef, registerdEventRef;
    String userNode, button_state, status;


    public MyUpcomingEventAdapter(Context context, ArrayList<StudyEvent> myUpcomingEventList, ArrayList<String> keyArray, String userNode) {
        this.context = context;
        this.myUpcomingEventList = myUpcomingEventList;
        this.keyArray = keyArray;
        this.userNode = userNode;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_my_upcoming_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        String tempKey = keyArray.get(position);
        MaintenanceOfButton(tempKey, holder);


        holder.subjectCode.setText(myUpcomingEventList.get(position).getSubjectCode());
        holder.subjectName.setText(myUpcomingEventList.get(position).getSubjectName());
        holder.task.setText("Task: " + myUpcomingEventList.get(position).getTask());
        holder.location.setText("Location: " + myUpcomingEventList.get(position).getLocation());
        holder.date.setText("Date: " + myUpcomingEventList.get(position).getDate());
        holder.time.setText("Time: " + myUpcomingEventList.get(position).getStartTime() + " - " + myUpcomingEventList.get(position).getEndTime());
        holder.groupSize.setText("Vacancy: " + myUpcomingEventList.get(position).getGroupSize());

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

                String eventID = keyArray.get(position);

                Intent intent = new Intent(context, AddStudyEvent.class);
                intent.putExtra("eventID", eventID);
                intent.putExtra("from", "UpdateStudyEventActivity");
                intent.putExtra("subjectCode", myUpcomingEventList.get(position).getSubjectCode());
                intent.putExtra("subjectName", myUpcomingEventList.get(position).getSubjectName());
                intent.putExtra("task", myUpcomingEventList.get(position).getTask());
                intent.putExtra("location", myUpcomingEventList.get(position).getLocation());
                intent.putExtra("date", myUpcomingEventList.get(position).getDate());
                intent.putExtra("startTime", myUpcomingEventList.get(position).getStartTime());
                intent.putExtra("endTime", myUpcomingEventList.get(position).getEndTime());
                intent.putExtra("groupSize", myUpcomingEventList.get(position).getGroupSize());

                context.startActivity(intent);

            }
        });

        holder.swipeWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tempKey = keyArray.get(position);


                messageRef = FirebaseDatabase.getInstance().getReference("Messages").child("Group Chat").child(tempKey).child("members");

                status = holder.swipeWithdraw.getText().toString().toLowerCase();

                if (status.equals("withdraw")) {
                    //event creators can withdraw themselves
                    holder.contentLayout.setBackgroundResource(R.color.gray);
                    holder.hint.setVisibility(View.VISIBLE);
                    holder.swipeWithdraw.setText("Join");

                    messageRef.child(userNode).removeValue();


                } else if (status.equals("join")) {

                    //event creators can add themselves back after withdrawal

                    holder.contentLayout.setBackgroundResource(R.color.white_with_alpha);
                    holder.hint.setVisibility(View.INVISIBLE);
                    holder.swipeWithdraw.setText("Withdraw");

                    messageRef.child(userNode).child("identity").setValue("admin");


                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return myUpcomingEventList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView subjectCode, subjectName, task, location, date, time, groupSize, hint;
        SwipeLayout swipeLayout;
        TextView swipeEdit, swipeWithdraw;
        RelativeLayout contentLayout;


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
            swipeWithdraw = (TextView) itemView.findViewById(R.id.my_event_swipe_delete);

            contentLayout = (RelativeLayout) itemView.findViewById(R.id.my_event_contentLayout);
            hint = (TextView) itemView.findViewById(R.id.my_event_hint);

            swipeWithdraw.setText("Withdraw");

        }
    }


    public void MaintenanceOfButton(final String tempKey, final MyUpcomingEventAdapter.MyViewHolder holder) {

        DatabaseReference messageRef;
        messageRef = FirebaseDatabase.getInstance().getReference("Messages").child("Group Chat").child(tempKey).child("members");

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild(userNode)) {
                    button_state = "joined";
                    holder.swipeWithdraw.setText("Withdraw");
                    holder.swipeWithdraw.setTextSize(16f);


                } else {
                    button_state = "withdrawn";
                    holder.swipeWithdraw.setText("Join");
                    holder.contentLayout.setBackgroundResource(R.color.gray);
                    holder.hint.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
