package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuddyManagementPage extends AppCompatActivity {

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference seniorBuddyRef, studentProfileRef, buddyRequestRef;
    private String userEmail, userNode;


    private LinearLayout juniorBuddyLayout, seniorBuddyLayout;
    private Toolbar toolbar;
    private TextView title;
    private ImageView rightIcon, toolbar_backBtn;
    private LinearLayout btnLinearLayout;

    private ImageView info_backBtn, messageBtn;
    private LinearLayout pageLayout, intro_section;
    private ScrollView scrollView;
    private CircleImageView profilePic;
    private TextView infoName, infoEmail;
    private Button requestBtn, declineBtn, commentBtn, appointBtn;
    private TextView infoCourse, infoHall, infoGender, infoExperience, infoBuddyRelationDate, removeBuddyRequestHint, commentFromSeniorBuddy;
    private View underline;

    private ArrayList<UserInfo> juniorBuddyList;
    private MyJuniorBuddyAdapter adapter;

    private RecyclerView recyclerView;
    private String seniorBuddy, exp, relationDate;

    private String CURRENT_STATE, remove_request_type, senderUserID, receiverUserID;
    private Boolean seniorRemovedFromJuniorProfile;
    private String receiverName, receiverProfileImgUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_management_page);


        firebaseDatabase = FirebaseDatabase.getInstance();
        studentProfileRef = firebaseDatabase.getReference("Student Profile");
        seniorBuddyRef = firebaseDatabase.getReference("Senior Buddy");
        buddyRequestRef = firebaseDatabase.getReference("Buddy Requests");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        juniorBuddyLayout = (LinearLayout) findViewById(R.id.juniorSection_layout);
        seniorBuddyLayout = (LinearLayout) findViewById(R.id.seniorSection_layout);
        recyclerView = (RecyclerView) findViewById(R.id.buddy_juniorBuddyList);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.toolbar_title);
        rightIcon = (ImageView) findViewById(R.id.toolbar_right_icon);
        toolbar_backBtn = (ImageView) findViewById(R.id.toolbar_back);

        info_backBtn = (ImageView) findViewById(R.id.info_back);
        messageBtn = (ImageView) findViewById(R.id.info_msgBtn);
        profilePic = (CircleImageView) findViewById(R.id.info_profilepic);
        infoName = (TextView) findViewById(R.id.info_name);
        infoEmail = (TextView) findViewById(R.id.info_email);
        infoCourse = (TextView) findViewById(R.id.info_course);
        infoHall = (TextView) findViewById(R.id.info_Hall);
        infoGender = (TextView) findViewById(R.id.info_gender);
        infoExperience = (TextView) findViewById(R.id.info_exp);
        infoBuddyRelationDate = (TextView) findViewById(R.id.info_buddyDate);
        commentFromSeniorBuddy = (TextView) findViewById(R.id.info_gotoCommentPage);
        underline = (View) findViewById(R.id.underline);

        pageLayout = (LinearLayout) findViewById(R.id.seniorbuddy_linearLayout);
        intro_section = (LinearLayout) findViewById(R.id.seniorbuddy_intro_section);
        scrollView = (ScrollView) findViewById(R.id.info_scrollview);


        btnLinearLayout = (LinearLayout) findViewById(R.id.info_buttons_layout);
        requestBtn = (Button) findViewById(R.id.info_requestBtn);
        declineBtn = (Button) findViewById(R.id.info_declineBtn);
        commentBtn = (Button) findViewById(R.id.info_commentBtn);
        appointBtn = (Button) findViewById(R.id.info_appointmentBtn);
        removeBuddyRequestHint = (TextView) findViewById(R.id.info_remove_buddy_request);

        CURRENT_STATE = "buddy";
        remove_request_type = "";
        seniorRemovedFromJuniorProfile = false;


        //switch between senior and junior layout of buddy management page
        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(userNode)) {
                    //senior buddy layout
                    seniorBuddyLayout.setVisibility(View.VISIBLE);
                    title.setText("Junior Buddy");
                    rightIcon.setVisibility(View.GONE);


                    toolbar_backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(BuddyManagementPage.this, Account.class));
                        }
                    });

                    ShowJuniorBuddyList();


                } else { //junior's senior buddy layout
                    juniorBuddyLayout.setVisibility(View.VISIBLE);
                    btnLinearLayout.removeView(declineBtn);
                    requestBtn.setText("Remove Buddy");
                    pageLayout.removeView(scrollView);


                    infoBuddyRelationDate.setVisibility(View.VISIBLE);


                    commentBtn.setVisibility(View.VISIBLE);
                    commentFromSeniorBuddy.setVisibility(View.VISIBLE);
                    underline.setVisibility(View.VISIBLE);
                    commentFromSeniorBuddy.setText("Comment From Junior Buddy");

                    senderUserID = userNode;

                    ShowSeniorBuddyInfo();

                    MaintenanceOfButtons();

                    requestBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //buddy relationship

                            if (CURRENT_STATE.equals("buddy")) {
                                SendRemoveBuddyRequest();
                            }

                            if (CURRENT_STATE.equals("remove_request_sent")) {
                                CancelRemoveBuddyRequest();
                            }

                            if (CURRENT_STATE.equals("remove_request_received")) {
                                AcceptRemoveBuddyRequest();
                            }

                        }

                    });


                    commentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PostCommentForSenior();
                        }
                    });

                    commentFromSeniorBuddy.setText("Comment From Senior Buddy");

                    commentFromSeniorBuddy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(BuddyManagementPage.this, Comment.class);
                            intent.putExtra("commentOfWho", userNode);
                            intent.putExtra("identity", "junior");
                            startActivity(intent);
                        }
                    });


                    info_backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(BuddyManagementPage.this, Account.class));
                        }
                    });


                    messageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatIntent = new Intent(BuddyManagementPage.this, Chat.class);

                            chatIntent.putExtra("from", "1to1Chat");
                            chatIntent.putExtra("senderUserID", userNode);
                            chatIntent.putExtra("receiverUserID", receiverUserID);
                            chatIntent.putExtra("receiverName", receiverName);
                            chatIntent.putExtra("receiverProfileImgUrl", receiverProfileImgUrl);

                            startActivity(chatIntent);

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void PostCommentForSenior() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        final String commentDate = sdf.format(date);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.card_applicaition_form, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog postCommentDialog = dialogBuilder.create();
        postCommentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        postCommentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        postCommentDialog.show();


        final EditText editTextComment = dialogView.findViewById(R.id.apply_intro);
        final Button postCommentBtn = dialogView.findViewById(R.id.apply_applybtn);
        ImageView closeBtn = dialogView.findViewById(R.id.apply_closeBtn);
        TextView title = dialogView.findViewById(R.id.apply_title1);
        TextView subTitle = dialogView.findViewById(R.id.apply_title2);

        title.setText("Comment your Senior");
        subTitle.setText("Comment for: " + receiverName);
        postCommentBtn.setText("Comment");
        postCommentBtn.setTextSize(16f);


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postCommentDialog.dismiss();
            }
        });

        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String seniorBuddyID = seniorBuddy;
                seniorBuddyRef = firebaseDatabase.getReference("Senior Buddy");
                String comment = editTextComment.getText().toString();

                if (!TextUtils.isEmpty(comment)) {

                    String commentKey = seniorBuddyRef.child(seniorBuddyID).push().getKey();
                    String commentRef = seniorBuddyID + "/juniorBuddyComment/" + commentKey;

                    Map commentBody = new HashMap();
                    commentBody.put("comment", comment);
                    commentBody.put("date", commentDate);
                    commentBody.put("commentedBy", userNode);

                    Map postComment = new HashMap();
                    postComment.put(commentRef, commentBody);

                    seniorBuddyRef.updateChildren(postComment).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(BuddyManagementPage.this, "Comment Posted Successfully", Toast.LENGTH_SHORT).show();
                                postCommentDialog.dismiss();

                            } else {
                                Toast.makeText(BuddyManagementPage.this, "Comment Posted Failed", Toast.LENGTH_SHORT).show();
                                editTextComment.setError("Failed, Try Again");
                            }
                        }
                    });

                } else {

                    editTextComment.setError("Comment cannot be empty");
                }

            }
        });

    }

    private void SendRemoveBuddyRequest() {

        buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("remove_request_sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            buddyRequestRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("remove_request_received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                requestBtn.setEnabled(true);
                                                CURRENT_STATE = "remove_request_sent";
                                                requestBtn.setText("Cancel Request");

                                                btnLinearLayout.removeView(declineBtn);
                                                declineBtn.setEnabled(false);
                                                Toast.makeText(getApplicationContext(), "Remove Buddy Request Sent", Toast.LENGTH_LONG).show();

                                                MaintenanceOfButtons();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void CancelRemoveBuddyRequest() {

        buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            buddyRequestRef.child(receiverUserID).child(senderUserID).child("request_type").removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                requestBtn.setEnabled(true);
                                                CURRENT_STATE = "buddy";
                                                requestBtn.setText("Remove Buddy");

                                                declineBtn.setVisibility(View.INVISIBLE);
                                                btnLinearLayout.removeView(declineBtn);
                                                declineBtn.setEnabled(false);
                                                Toast.makeText(getApplicationContext(), "Remove Buddy Request Cancelled", Toast.LENGTH_LONG).show();

                                                MaintenanceOfButtons();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void AcceptRemoveBuddyRequest() {

        seniorRemovedFromJuniorProfile = true;
        //junior buddy agree to removes  buddy relationship
        studentProfileRef.child(senderUserID).child("seniorBuddy").removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            seniorBuddyRef.child(receiverUserID).child("juniorBuddy").child(senderUserID).child("relationDate").removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {
                                                                    buddyRequestRef.child(receiverUserID).child(senderUserID).child("request_type").removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if (task.isSuccessful()) {

                                                                                        CURRENT_STATE = "not_buddy";
                                                                                        requestBtn.setText("Request");

                                                                                        btnLinearLayout.removeView(declineBtn);
                                                                                        declineBtn.setEnabled(false);
                                                                                        finish();
                                                                                        startActivity(new Intent(getApplicationContext(), Account.class));
                                                                                        Toast.makeText(getApplicationContext(), "Buddy Relationship Removed successfully", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });

                        } else {

                            Toast.makeText(getApplicationContext(), "Failed to remove senior Buddy. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }


    private void MaintenanceOfButtons() {

        buddyRequestRef.child(senderUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverUserID)) {


                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString().trim();
                            if (request_type.equals("remove_request_sent")) {

                                CURRENT_STATE = "remove_request_sent";
                                requestBtn.setText("Cancel Request");

                            } else if (request_type.equals("remove_request_received")) {

                                CURRENT_STATE = "remove_request_received";
                                requestBtn.setText("Accept remove Request");
                                requestBtn.setTextSize(14f);

                                btnLinearLayout.removeView(requestBtn);
                                btnLinearLayout.addView(requestBtn);
                                requestBtn.setEnabled(true);

                                commentBtn.setVisibility(View.VISIBLE);
                                removeBuddyRequestHint.setVisibility(View.VISIBLE);
                                removeBuddyRequestHint.setText("Your Senior Buddy Requests to Remove Buddy Relationship");
                                //btnLinearLayout.removeView(declineBtn);
                                //btnLinearLayout.addView(declineBtn);

                                //declineBtn.setEnabled(true);


                                /*
                                declineBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CancelBuddyRequest();

                                    }
                                });

                                 */


                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void ShowSeniorBuddyInfo() {

        studentProfileRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("seniorBuddy")) {

                    UserInfo seniorInfo = dataSnapshot.getValue(UserInfo.class);
                    seniorBuddy = seniorInfo.getSeniorBuddy().trim();
                    receiverUserID = seniorBuddy;

                    studentProfileRef.child(seniorBuddy).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            UserInfo seniorInfo = dataSnapshot.getValue(UserInfo.class);

                            receiverName = seniorInfo.getName().trim();
                            receiverProfileImgUrl = seniorInfo.getProfileImageUrl().trim();

                            infoName.setText(seniorInfo.getName().trim());
                            infoEmail.setText(seniorInfo.getEmail().trim());
                            infoCourse.setText("Course: " + seniorInfo.getCourse().trim());
                            infoGender.setText("Gender: " + seniorInfo.getGender().trim());
                            infoHall.setText("Hall: " + seniorInfo.getHall().trim());
                            Picasso.get().load(seniorInfo.getProfileImageUrl()).into(profilePic);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    //calculate senior buddy experience
                    seniorBuddyRef.child(seniorBuddy).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            String date = dataSnapshot.child("juniorBuddy").child(userNode).getValue(SeniorBuddyModel.class).getRelationDate();
                            infoBuddyRelationDate.setText("Buddy Since: " + date);


                            SeniorBuddyModel seniorBuddyModel = dataSnapshot.getValue(SeniorBuddyModel.class);

                            long diffMonth = 0;
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                String joinDate = seniorBuddyModel.getJoinedDate();
                                Date today = new Date();
                                today = sdf.parse(sdf.format(new Date()));
                                Date parsedJoindedDate = sdf.parse(joinDate);
                                long diff = Math.abs(today.getTime() - parsedJoindedDate.getTime());
                                long diffDays = TimeUnit.MILLISECONDS.toDays(diff);
                                diffMonth = diffDays / 30;

                                if (diffMonth <= 12) {
                                    exp = "Freshy";

                                } else if (diffMonth > 12 && diffMonth <= 24) {
                                    exp = "Senior";
                                } else {
                                    exp = "Expert";
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            infoExperience.setText("Joined Date: " + seniorBuddyModel.getJoinedDate() + "\n" + "Experience: " + diffMonth + " Months\n" + "Level: " + exp);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        info_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuddyManagementPage.this, Account.class));
            }
        });



    }


    private void ShowJuniorBuddyList() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        juniorBuddyList = new ArrayList<UserInfo>();

        seniorBuddyRef.child(userNode).child("juniorBuddy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String juniorID = dataSnapshot1.getKey().trim();

                    studentProfileRef.child(juniorID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            UserInfo juniorInfo = dataSnapshot.getValue(UserInfo.class);
                            juniorBuddyList.add(juniorInfo);

                            adapter = new MyJuniorBuddyAdapter(getApplicationContext(), juniorBuddyList);
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

            }
        });

        info_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuddyManagementPage.this, Account.class));
            }
        });


    }


}
