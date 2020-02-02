package com.example.EEEBuddy;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllFragment extends Fragment{

    private RecyclerView recyclerView;
    private ArrayList<SeniorBuddyModel> seniorBuddyList;
    private SeniorBuddyAdapter adapter;


    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference seniorBuddyRef, studentProfileRef;
    private String userID;
    private String userEmail, userNode;


    public AllFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.seniorbuddy_fragment_alltab, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.seniorbuddy_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        seniorBuddyList = new ArrayList<SeniorBuddyModel>();


        seniorBuddyRef = firebaseDatabase.getInstance().getReference("Senior Buddy");
        studentProfileRef = firebaseDatabase.getInstance().getReference("Student Profile");

        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                seniorBuddyList.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        userNode  = dataSnapshot1.getKey();

                      studentProfileRef.child(userNode).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                SeniorBuddyModel studentInfo = dataSnapshot.getValue(SeniorBuddyModel.class);
                                seniorBuddyList.add(studentInfo);

                                /*for(DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){
                                        SeniorBuddyModel studentInfo = dataSnapshot2.getValue(SeniorBuddyModel.class);
                                        seniorBuddyList.add(studentInfo);
                                }

                                 */

                                //creating adapter
                                adapter = new SeniorBuddyAdapter(getActivity(), seniorBuddyList);
                                //attaching adapter to recycler-view
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

   /*
    //Toolbar Menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //do the button action here
        if (item.getItemId() == R.id.seniorbuddy_filterIcon){

            Toast.makeText(getActivity(), "CLICKED",Toast.LENGTH_LONG).show();
            item.setIcon(R.drawable.ic_filter_colour2);

        }
        return true;
    }

    */

}
