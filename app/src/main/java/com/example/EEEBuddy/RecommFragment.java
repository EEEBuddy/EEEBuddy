package com.example.EEEBuddy;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecommFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<UserInfo> juniorBuddyList;
    private BuddyRequestAdapter adapter;
    private String request_type;
    private TextView last_update;


    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference studentProfileRef, buddyRequestRef, seniorBuddyRef;
    private String userEmail, userNode;
    private FirebaseUser user;


    public RecommFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.seniorbuddy_fragment_recomm, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.seniorbuddy_buddyRequest_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        juniorBuddyList = new ArrayList<UserInfo>();



        firebaseDatabase = FirebaseDatabase.getInstance();
        studentProfileRef = firebaseDatabase.getReference("Student Profile");
        buddyRequestRef = firebaseDatabase.getReference("Buddy Requests");
        seniorBuddyRef = firebaseDatabase.getReference("Senior Buddy");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));


        last_update = (TextView) view.findViewById(R.id.seniorbuddy_buddyRequest_update_time);
        Date currentTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss aa");
        String formattedTime = simpleDateFormat.format(currentTime);

        last_update.setText("Last Updated On: " + formattedTime);



        buddyRequestRef.keepSynced(true);
        buddyRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userNode)) {

                    buddyRequestRef.child(userNode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                                juniorBuddyList.clear();
                                String juniorID = dataSnapshot1.getKey();

                                final BuddyRequestModel buddyRequestModel = dataSnapshot1.getValue(BuddyRequestModel.class);
                                request_type = buddyRequestModel.getRequest_type();

                                if(request_type.equals("buddy_request_received") || request_type.equals("remove_request_received")){

                                    studentProfileRef.child(juniorID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            UserInfo juniorInfo = dataSnapshot.getValue(UserInfo.class);
                                            juniorBuddyList.add(juniorInfo);

                                            //int arraySize = juniorBuddyList.size();



                                            //create adapter
                                            adapter = new BuddyRequestAdapter(getActivity(), juniorBuddyList);
                                            recyclerView.setAdapter(adapter);

                                            adapter.notifyDataSetChanged();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }


                            }

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
