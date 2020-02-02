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
public class PastEventFragment extends Fragment{

    private RecyclerView recyclerView;
    private ArrayList<StudyEvent> pastEventList;
    private PastEventAdapter adapter;

    private TextView hint;
    private ImageView gif;


    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference registeredEventRef, studyEventRef;
    private String userID;
    private String userEmail, userNode;


    public PastEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_past_event, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.past_event_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pastEventList = new ArrayList<StudyEvent>();


        firebaseAuth = firebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        userNode = userEmail.substring(0,userEmail.indexOf("@"));

        registeredEventRef = firebaseDatabase.getInstance().getReference("Registered Events");
        studyEventRef = firebaseDatabase.getInstance().getReference("Study Event");


        pastEventList.clear();
        registeredEventRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    final StudyEvent studyEvent = dataSnapshot1.getValue(StudyEvent.class);
                    String eventID = studyEvent.getEventID();
                    String createdBy = studyEvent.getCreatedBy();

                    studyEventRef.child(createdBy).child(eventID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            StudyEvent studyEventDetail = dataSnapshot.getValue(StudyEvent.class);
                            String eventDate = studyEventDetail.getDate();

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                            try {
                                Date now = new Date(System.currentTimeMillis());
                                Date parsedDate = sdf.parse(eventDate);

                                if(parsedDate.compareTo(now) < 0 ){
                                    pastEventList.add(studyEventDetail);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            //if no upcoming event show hints
                            if(pastEventList.size() == 0) {
                                hint = view.findViewById(R.id.past_event_hint);
                                hint.setVisibility(View.VISIBLE);

                                gif = view.findViewById(R.id.past_event_gif);
                                gif.setVisibility(View.VISIBLE);
                                Glide.with(PastEventFragment.this).asGif().load(R.drawable.happystudy).into(gif);

                            }

                            adapter = new PastEventAdapter(getActivity(), pastEventList);
                            recyclerView.setAdapter(adapter);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "Error, Please Try Again", Toast.LENGTH_LONG).show();

            }
        });


        return view;
    }

}
