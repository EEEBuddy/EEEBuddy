package com.example.EEEBuddy;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPastEventFragment extends Fragment{

    private RecyclerView recyclerView;
    private ArrayList<StudyEvent> myPastEventList;
    private ArrayList<String> keyArray;
    private MyPastEventAdapter adapter;

    private TextView hint,updateTime;
    private ImageView gif;


    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference  studyEventRef;
    private String userID;
    private String userEmail, userNode;


    public MyPastEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_my_past_event, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.my_past_event_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myPastEventList = new ArrayList<StudyEvent>();
        keyArray = new ArrayList<String>();

        updateTime = (TextView) view.findViewById(R.id.my_past_event_update_time);
        Date currentTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss aa");
        String formattedTime = simpleDateFormat.format(currentTime);

        updateTime.setText("Last Updated On: " + formattedTime);


        firebaseAuth = firebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        userNode = userEmail.substring(0,userEmail.indexOf("@"));

        studyEventRef = firebaseDatabase.getInstance().getReference("Study Event");



        studyEventRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myPastEventList.clear();
                studyEventRef.keepSynced(true);

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    String key = dataSnapshot1.getKey();
                     StudyEvent studyEvent = dataSnapshot1.getValue(StudyEvent.class);
                     String eventDate = studyEvent.getDate();
                    String eventTime = studyEvent.getStartTime();
                    String eventStartTime = eventTime.substring(0,eventTime.indexOf(' ')).trim();

                     Calendar calendar = Calendar.getInstance();
                     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String eventDateTime = eventDate + " " + eventStartTime;

                     try {
                         Date now = new Date(System.currentTimeMillis());
                         Date parsedDate = sdf.parse(eventDateTime);
                         //Date parsedCurrentTime= sdf2.parse(currentTime);
                        // Date parsedTime = sdf2.parse(eventTime);


                         if(parsedDate.compareTo(now) < 0){
                             myPastEventList.add(studyEvent);
                             keyArray.add(key);
                         }

                     } catch (ParseException e) {
                         e.printStackTrace();
                     }


                }

                //if no upcoming event show hints
                if(myPastEventList.size() == 0) {
                    hint = view.findViewById(R.id.my_past_event_hint);
                    hint.setVisibility(View.VISIBLE);

                    gif = view.findViewById(R.id.my_past_event_gif);
                    gif.setVisibility(View.VISIBLE);
                    Glide.with(MyPastEventFragment.this).asGif().load(R.drawable.happystudy).into(gif);
                }

                adapter = new MyPastEventAdapter(getActivity(), myPastEventList, keyArray, userNode);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "Error, Please Try Again", Toast.LENGTH_LONG).show();

            }
        });


        return view;
    }

}
