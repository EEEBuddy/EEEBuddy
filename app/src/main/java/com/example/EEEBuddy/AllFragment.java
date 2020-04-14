package com.example.EEEBuddy;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


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
    private TextView clearFilter, applyText;
    boolean yearOne, hasSeniorBuddy;


    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference seniorBuddyRef, studentProfileRef;
    private String userID;
    private String userEmail, userNode, seniorNode;

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


    //dialogbox
    private TextView message1, message2, note;
    private Button cancleBtn, confirmBtn;


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

        applyText = view.findViewById(R.id.toolbar_apply);
        applyText.setVisibility(View.VISIBLE);
        applyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = applyText.getText().toString().toLowerCase();
                if (text.equals("apply")) {
                    SeniorBuddyApplication();

                } else if (text.equals("withdraw")) {
                    SeniorBuddyWithdraw();
                }
            }
        });


        filterIcon = (ImageView) view.findViewById(R.id.toolbar_right_icon);
        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterContent();
            }
        });


        seniorBuddyRef = firebaseDatabase.getInstance().getReference("Senior Buddy");
        studentProfileRef = firebaseDatabase.getInstance().getReference("Student Profile");


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));


        MaintenanceOfButton();


        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    seniorBuddyList.clear();
                    seniorNode = dataSnapshot1.getKey();

                    studentProfileRef.child(seniorNode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            SeniorBuddyModel studentInfo = dataSnapshot.getValue(SeniorBuddyModel.class);
                            seniorBuddyList.add(studentInfo);

                            //creating adapter
                            adapter1 = new SeniorBuddyAdapter(getActivity(), seniorBuddyList);
                            adapter1.notifyDataSetChanged();
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
                        if (exp != "no preference") {

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date today = sdf.parse(sdf.format(new Date()));
                            Date parsedJoindedDate = sdf.parse(joinDate);
                            long diff = Math.abs(today.getTime() - parsedJoindedDate.getTime());
                            long diffDays = TimeUnit.MILLISECONDS.toDays(diff);
                            long diffMonth = diffDays / 30;

                            if (diffMonth <= 12) {
                                calExp = "freshy";

                            } else if (diffMonth > 12 && diffMonth <= 24) {
                                calExp = "senior";
                            } else {
                                calExp = "expert";
                            }
                        } else {
                            calExp = exp;
                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (course.equals("") &&
                            object.getHall().toLowerCase().equals(hall) &&
                            object.getGender().toLowerCase().equals(gender) &&
                            calExp.equals(exp)) {
                        filterResultList.add(object);

                    } else if (hall.equals("no preference") &&
                            object.getCourse().toLowerCase().equals(course) &&
                            object.getGender().toLowerCase().equals(gender) &&
                            calExp.equals(exp)) {
                        filterResultList.add(object);

                    } else if (gender.equals("no preference") &&
                            object.getCourse().toLowerCase().equals(course) &&
                            object.getHall().toLowerCase().equals(hall) &&
                            calExp.equals(exp)) {
                        filterResultList.add(object);

                    } else if (course.equals("") && hall.equals("no preference") &&
                            object.getGender().toLowerCase().equals(gender) &&
                            calExp.equals(exp)) {
                        filterResultList.add(object);

                    } else if (course.equals("") && gender.equals("no preference") &&
                            object.getHall().toLowerCase().equals(hall) &&
                            calExp.equals(exp)) {
                        filterResultList.add(object);

                    } else if (course.equals("") && exp.equals("no preference") &&
                            object.getHall().toLowerCase().equals(hall) &&
                            object.getGender().toLowerCase().equals(gender)) {
                        filterResultList.add(object);

                    } else if (hall.equals("no preference") && gender.equals("no preference") &&
                            object.getCourse().toLowerCase().equals(course) &&
                            calExp.equals(exp)) {
                        filterResultList.add(object);

                    } else if (hall.equals("no preference") && exp.equals("no preference") &&
                            object.getCourse().toLowerCase().equals(course) &&
                            object.getGender().toLowerCase().equals(gender)) {
                        filterResultList.add(object);

                    } else if (gender.equals("no preference") && exp.equals("no preference") &&
                            object.getCourse().toLowerCase().equals(course) &&
                            object.getHall().toLowerCase().equals(hall)) {

                        filterResultList.add(object);
                    } else if (course.equals("") && hall.equals("no preference") && gender.equals("no preference") &&
                            calExp.equals(exp)) {
                        filterResultList.add(object);

                    } else if (course.equals("") && hall.equals("no preference") && exp.equals("no preference") &&
                            object.getGender().toLowerCase().equals(gender)) {
                        filterResultList.add(object);

                    } else if (course.equals("") && gender.equals("no preference") && exp.equals("no preference") &&
                            object.getHall().toLowerCase().equals(hall)) {
                        filterResultList.add(object);

                    } else if (hall.equals("no preference") && gender.equals("no preference") && exp.equals("no preference") &&
                            object.getCourse().toLowerCase().equals(course)) {
                        filterResultList.add(object);

                    } else if (course.equals("") && hall.equals("no preference") && gender.equals("no preference") && exp.equals("no preference")) {
                        Toast.makeText(getActivity(), "indicate at least one field", Toast.LENGTH_LONG).show();

                    } else if (object.getCourse().toLowerCase().equals(course) &&
                            object.getHall().toLowerCase().equals(hall) &&
                            object.getGender().toLowerCase().equals(gender) &&
                            calExp.equals(exp)) {
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


    private void SeniorBuddyApplication() {

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userKey = user.getEmail().substring(0, user.getEmail().indexOf("@"));



        studentProfileRef = firebaseDatabase.getInstance().getReference("Student Profile");
        studentProfileRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SeniorBuddyModel seniorBuddyModel = dataSnapshot.getValue(SeniorBuddyModel.class);
                Integer year = Integer.parseInt(seniorBuddyModel.getYear());

                if (year.equals(1)) {
                    yearOne = true;
                }

                if(dataSnapshot.child("seniorBuddy").exists()){

                    hasSeniorBuddy = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        seniorBuddyRef = firebaseDatabase.getInstance().getReference("Senior Buddy");

        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(userKey)) {
                    Toast.makeText(getActivity(), "You have already registered as senior buddy, check out at Account page", Toast.LENGTH_LONG).show();

                }else if(hasSeniorBuddy == true){
                    Toast.makeText(getActivity(), "Only students WITHOUT a Senior buddy can apply as a Senior Buddy", Toast.LENGTH_LONG).show();

                }else if (yearOne == true) {
                    Toast.makeText(getActivity(), "Only Year 2 or above are allowed to apply as senior buddy", Toast.LENGTH_LONG).show();

                }else if(!(dataSnapshot.hasChild(userNode))){

                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    final String applicationDate = sdf.format(date);

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.card_applicaition_form, null);
                    dialogBuilder.setView(dialogView);

                    final AlertDialog applicationDialog = dialogBuilder.create();
                    applicationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    applicationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    applicationDialog.show();

                    final EditText editTextSelfIntro = dialogView.findViewById(R.id.apply_intro);
                    Button applyBtn = dialogView.findViewById(R.id.apply_applybtn);
                    ImageView closeBtn = dialogView.findViewById(R.id.apply_closeBtn);

                    //add record into database
                    applyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String selfIntro = editTextSelfIntro.getText().toString();

                            if (!TextUtils.isEmpty(selfIntro)) {
                                seniorBuddyRef = firebaseDatabase.getInstance().getReference("Senior Buddy");
                                SeniorBuddyModel registration = new SeniorBuddyModel(selfIntro, applicationDate);
                                seniorBuddyRef.child(userKey).setValue(registration);
                                Toast.makeText(getActivity(), "Successfully Registered", Toast.LENGTH_LONG).show();
                                applicationDialog.dismiss();
                                applyText.setText("Withdraw");

                            } else {
                                Toast.makeText(getActivity(), "Introduction cannot be empty", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applicationDialog.dismiss();
                        }
                    });
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void SeniorBuddyWithdraw() {

        seniorBuddyRef = firebaseDatabase.getInstance().getReference("Senior Buddy");

        seniorBuddyRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("juniorBuddy")) {

                    Toast.makeText(getActivity(), "Please remove all your junior buddy before withdrawal", Toast.LENGTH_LONG).show();
                } else {

                    //need to pass the context from the class calling this method.

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.join_studyevent_dialog, null);
                    dialogBuilder.setView(dialogView);

                    final AlertDialog dialog = dialogBuilder.create();
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.show();

                    //dialog.setContentView(R.layout.join_studyevent_dialog);
                    message1 = (TextView) dialog.findViewById(R.id.join_msg1);
                    message2 = (TextView) dialog.findViewById(R.id.join_msg2);
                    note = (TextView) dialog.findViewById(R.id.join_note);
                    cancleBtn = (Button) dialog.findViewById(R.id.join_cancleBtn);
                    confirmBtn = (Button) dialog.findViewById(R.id.join_cfmBtn);

                    note.setVisibility(View.GONE);
                    confirmBtn.setText("Withdraw");


                    message1.setText("Senior Buddy Withdrawal");
                    message2.setText("Are your sure you want to withdraw from Senior Buddy Team?");


                    cancleBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            seniorBuddyRef.child(userNode).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(getActivity(), "Withdraw Successfully, Hope to see you back again", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            applyText.setText("Apply");
                            dialog.dismiss();
                            startActivity(new Intent(getContext(), SeniorBuddyPage.class));
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void MaintenanceOfButton(){

        seniorBuddyRef = firebaseDatabase.getInstance().getReference("Senior Buddy");

        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userNode)){
                    applyText.setText("Withdraw");
                }else{
                    applyText.setText("Apply");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}