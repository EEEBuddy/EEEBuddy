package com.example.EEEBuddy;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class MyUpcomingEventFragment extends Fragment{

    private RecyclerView recyclerView;
    private ArrayList<StudyEvent> myUpcomingEventList;
    private ArrayList<String> keyArray;
    private MyUpcomingEventAdapter adapter;
    private TextView hint,updateTime;
    private ImageView gif;


    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference  studyEventRef;
    private String userID;
    private String userEmail, userNode;


    public MyUpcomingEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_my_upcoming_event, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.my_upcoming_event_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myUpcomingEventList = new ArrayList<StudyEvent>();
        keyArray = new ArrayList<String>();

        updateTime = (TextView) view.findViewById(R.id.my_upcoming_event_update_time);
        Date currentTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss aa");
        String formattedTime = simpleDateFormat.format(currentTime);

        updateTime.setText("Last Updated On: " + formattedTime);


        firebaseAuth = firebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        userNode = userEmail.substring(0,userEmail.indexOf("@"));

        studyEventRef = firebaseDatabase.getInstance().getReference("Study Event");
        studyEventRef.child(userNode).keepSynced(true);



        studyEventRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myUpcomingEventList.clear();
                studyEventRef.keepSynced(true);

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    String key = dataSnapshot1.getKey();
                    StudyEvent studyEvent = dataSnapshot1.getValue(StudyEvent.class);
                    String eventDate = studyEvent.getDate();
                    String eventTime = studyEvent.getStartTime();
                    String eventStartTime = eventTime.substring(0,eventTime.indexOf(' ')).trim();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String eventDateTime = eventDate + " " + eventStartTime;


                    try {
                        Date now = new Date(System.currentTimeMillis());
                        Date parsedDate = sdf.parse(eventDateTime);


                        if(parsedDate.compareTo(now) > 0){
                            myUpcomingEventList.add(studyEvent);
                            keyArray.add(key);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                //if no upcoming event show hints
                if(myUpcomingEventList.size() == 0) {
                    hint = view.findViewById(R.id.my_upcoming_event_hint);
                    hint.setVisibility(View.VISIBLE);

                    gif = view.findViewById(R.id.my_upcoming_event_gif);
                    gif.setVisibility(View.VISIBLE);
                    Glide.with(MyUpcomingEventFragment.this).asGif().load(R.drawable.happystudy).into(gif);

                }

                adapter = new MyUpcomingEventAdapter(getActivity(),myUpcomingEventList, keyArray,userNode);
                recyclerView.setAdapter(adapter);

                // Reload current fragment
                /*
                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.my_upcoming_event_fragment);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(currentFragment);
                ft.attach(currentFragment);
                ft.commit();

                 */

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "Error, Please Try Again", Toast.LENGTH_LONG).show();

            }
        });


        return view;
    }

}
