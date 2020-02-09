package com.example.EEEBuddy;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ArrayList<SeniorBuddyModel> seniorBuddyList, searchResultList, filterResultList;
    private SeniorBuddyAdapter adapter1, adapter2;

    //toolbar
    private ImageView backBtn, filterIcon;
    private TextView title;
    private TextView clearFilter;


    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference seniorBuddyRef, studentProfileRef;
    private String userID;
    private String userEmail, userNode;

    //filter dialog
    private ImageView closeBtn;
    private ImageView loadingView;
    private ImageView filterBtn;

    private EditText editTextCourse;
    private Spinner spinnerHall;
    private Spinner spinnerExp;
    private Spinner spinnerGender;
    private String joinDate;

    private String calExp = "";




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
        searchView = (SearchView) view.findViewById(R.id.seniorbuddy_searchview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        seniorBuddyList = new ArrayList<SeniorBuddyModel>();
        searchResultList = new ArrayList<SeniorBuddyModel>();
        filterResultList = new ArrayList<SeniorBuddyModel>();


        title = view.findViewById(R.id.toolbar_title);
        title.setText("Senior Buddy");

        backBtn = (ImageView) view.findViewById(R.id.toolbar_back);
        backBtn.setVisibility(View.GONE);

        clearFilter = view.findViewById(R.id.seniorbuddy_clearFilter);


        filterIcon = (ImageView) view.findViewById(R.id.toolbar_right_icon);
        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterContent();
            }
        });


        seniorBuddyRef = firebaseDatabase.getInstance().getReference("Senior Buddy");
        studentProfileRef = firebaseDatabase.getInstance().getReference("Student Profile");

        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    seniorBuddyList.clear();
                    userNode = dataSnapshot1.getKey();

                    studentProfileRef.child(userNode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            SeniorBuddyModel studentInfo = dataSnapshot.getValue(SeniorBuddyModel.class);
                            seniorBuddyList.add(studentInfo);

                            //creating adapter
                            adapter1 = new SeniorBuddyAdapter(getActivity(), seniorBuddyList);
                            //attaching adapter to recycler-view
                            recyclerView.setAdapter(adapter1);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if (searchView != null) {
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String searchString) {
                                Search(searchString);
                                return true;
                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error, Please Try Again", Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }


    private void Search(String searchString) {

        searchResultList.clear();

        for (SeniorBuddyModel object : seniorBuddyList) {
            if (object.getName().toLowerCase().contains(searchString.toLowerCase()) ||
                    object.getCourse().toLowerCase().contains(searchString.toLowerCase())) {

                searchResultList.add(object);
            }
        }

        adapter2 = new SeniorBuddyAdapter(getActivity(), searchResultList);
        recyclerView.setAdapter(adapter2);
    }


    private void FilterContent() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.card_filter, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        editTextCourse = dialogView.findViewById(R.id.filter_course);
        final Spinner spinnerHall = dialogView.findViewById(R.id.filter_hall);
        final Spinner spinnerExp = dialogView.findViewById(R.id.filter_exp);
        final Spinner spinnerGender = dialogView.findViewById(R.id.add_filter_gender);

        closeBtn = dialogView.findViewById(R.id.filter_closeBtn);
        loadingView = dialogView.findViewById(R.id.filter_loading);
        filterBtn = dialogView.findViewById(R.id.filter);

        filterIcon.setImageResource(R.drawable.ic_filter_colour2);
        Glide.with(getActivity()).asGif().load(R.drawable.circle_loading).into(loadingView);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterIcon.setImageResource(R.drawable.ic_filter_unselect);
                dialog.dismiss();
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String course = editTextCourse.getText().toString().trim().toLowerCase();
                String hall = spinnerHall.getSelectedItem().toString().trim().toLowerCase();
                String exp = spinnerExp.getSelectedItem().toString().trim().toLowerCase();
                String gender = spinnerGender.getSelectedItem().toString().trim().toLowerCase();


                FilterResult(course, hall, gender, exp);
                filterIcon.setImageResource(R.drawable.ic_filter_unselect);
                dialog.dismiss();
                clearFilter.setVisibility(View.VISIBLE);
                clearFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter1 = new SeniorBuddyAdapter(getActivity(), seniorBuddyList);
                        recyclerView.setAdapter(adapter1);
                        clearFilter.setVisibility(View.GONE);
                    }
                });
            }
        });


    }


    public void FilterResult(final String course, final String hall, final String gender, final String exp) {

        filterResultList.clear();

        for (final SeniorBuddyModel object : seniorBuddyList) {

                //get date joined as senior buddy, calculate experience
                userEmail = object.getEmail();
                String ID = userEmail.substring(0, userEmail.indexOf("@"));

                seniorBuddyRef.child(ID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SeniorBuddyModel seniorBuddyModel = dataSnapshot.getValue(SeniorBuddyModel.class);
                        joinDate = seniorBuddyModel.getJoinedDate();

                        try {
                            if(exp != "no preference"){

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date today = sdf.parse(sdf.format(new Date()));
                                Date parsedJoindedDate = sdf.parse(joinDate);
                                long diff = Math.abs(today.getTime() - parsedJoindedDate.getTime());
                                long diffDays = TimeUnit.MILLISECONDS.toDays(diff);
                                long diffMonth = diffDays/30;

                                if (diffMonth <= 12) {
                                    calExp = "freshy";

                                } else if (diffMonth > 12 && diffMonth <= 24) {
                                    calExp = "senior";
                                } else{
                                    calExp = "expert";
                                }
                            }else {
                                calExp = exp;
                            }


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(course.equals("") &&
                                object.getHall().toLowerCase().equals(hall) &&
                                object.getGender().toLowerCase().equals(gender) &&
                                calExp.equals(exp)){
                            filterResultList.add(object);

                        }else if(hall.equals("no preference") &&
                                object.getCourse().toLowerCase().equals(course) &&
                                object.getGender().toLowerCase().equals(gender) &&
                                calExp.equals(exp)){
                            filterResultList.add(object);

                        }else if(gender.equals("no preference") &&
                                object.getCourse().toLowerCase().equals(course) &&
                                object.getHall().toLowerCase().equals(hall) &&
                                calExp.equals(exp)){
                            filterResultList.add(object);

                        }else if(course.equals("") && hall.equals("no preference") &&
                                object.getGender().toLowerCase().equals(gender) &&
                                calExp.equals(exp)){
                            filterResultList.add(object);

                        }else if(course.equals("") && gender.equals("no preference") &&
                                object.getHall().toLowerCase().equals(hall) &&
                                calExp.equals(exp)){
                            filterResultList.add(object);

                        }else if(course.equals("") && exp.equals("no preference") &&
                                object.getHall().toLowerCase().equals(hall) &&
                                object.getGender().toLowerCase().equals(gender)){
                            filterResultList.add(object);

                        }else if(hall.equals("no preference") && gender.equals("no preference") &&
                                object.getCourse().toLowerCase().equals(course) &&
                                calExp.equals(exp)){
                            filterResultList.add(object);

                        }else if(hall.equals("no preference") && exp.equals("no preference")&&
                                object.getCourse().toLowerCase().equals(course) &&
                                object.getGender().toLowerCase().equals(gender)){
                            filterResultList.add(object);

                        }else if(gender.equals("no preference") && exp.equals("no preference") &&
                                object.getCourse().toLowerCase().equals(course) &&
                                object.getHall().toLowerCase().equals(hall)){

                            filterResultList.add(object);
                        }else if(course.equals("") && hall.equals("no preference") && gender.equals("no preference") &&
                                calExp.equals(exp)){
                            filterResultList.add(object);

                        }else if(course.equals("") && hall.equals("no preference") && exp.equals("no preference") &&
                                object.getGender().toLowerCase().equals(gender)){
                            filterResultList.add(object);

                        }else if(course.equals("") && gender.equals("no preference") && exp.equals("no preference") &&
                                object.getHall().toLowerCase().equals(hall)){
                            filterResultList.add(object);

                        }else if(hall.equals("no preference") && gender.equals("no preference")  && exp.equals("no preference")  &&
                                object.getCourse().toLowerCase().equals(course)){
                            filterResultList.add(object);

                        }else if(course.equals("") && hall.equals("no preference") && gender.equals("no preference") && exp.equals("no preference")){
                            Toast.makeText(getActivity(), "indicate at least one field", Toast.LENGTH_LONG).show();

                        }else if(object.getCourse().toLowerCase().equals(course) &&
                                object.getHall().toLowerCase().equals(hall) &&
                                object.getGender().toLowerCase().equals(gender) &&
                                calExp.equals(exp)){
                            filterResultList.add(object);
                        }

                        adapter2 = new SeniorBuddyAdapter(getActivity(), filterResultList);
                        recyclerView.setAdapter(adapter2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        }
    }

    private void Hint() {

    }
}