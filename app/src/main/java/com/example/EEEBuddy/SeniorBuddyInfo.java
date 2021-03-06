package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeniorBuddyInfo extends AppCompatActivity {


    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference seniorBuddyRef, studentProfileRef, buddyRequestRef, notificationRef, adminRef;
    private String userEmail, userNode, seniorNode;


    private ImageView backBtn, messageBtn;
    private ImageView expandBtn, collapsBtn;
    private CircleImageView profilePic;
    private TextView infoName, infoEmail, info_buddyDate, removeBuddyRequestHint, commentFromBuddy;
    private Button requestBtn, declineBtn, commentBtn;
    private TextView infoCourse, infoHall, infoGender, infoExperience, infoIntro;
    private String profileImageUrl, selfIntro, dateJoined;
    private View underline;

    private LinearLayout pageLayout;
    private ScrollView scrollView;

    private String reEmail, reName, reCourse, reHall, reGender, reImgUrl, reYear, buddyRequestPage, myJuniorBuddyPage;

    //private View layout;
    private LinearLayout btnLinearLayout;
    private String exp = "";

    //for request for a senior-junior relationship
    private String senderUserID, receiverUserID, CURRENT_STATE, mySeniorBuddyID;
    private String currentDate;
    private boolean seniorBuddyIdentity, removeRequestReceived, removeRequestSent, buddyRequestReceived, buddyRequestSent;


    //pass variable to chat via intent
    private String chat_receiverUserID, chat_receiverName, chat_receiverProfileImgUrl, chat_senderID, buddy_removal_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seniorbuddy_info);


        Initialisation();
        InitialiseLayout();


        declineBtn.setEnabled(false);
        btnLinearLayout.removeView(declineBtn);

        senderUserID = userNode;
        receiverUserID = seniorNode;
        CURRENT_STATE = "not_buddy";
        buddy_removal_code = "0";
        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        MaintenanceOfButtons();

        //senior buddy cannot request for a senior buddy
        if (senderUserID.equals(receiverUserID)) {
            btnLinearLayout.removeView(requestBtn);
        }


        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //HasSeniorBuddy();
                //CheckSeniorBuddyIdentity();
                //IsYearOneStudent();

                //junior buddy functions, not a senior buddy and does not have a senior buddy
                if (!senderUserID.equals(receiverUserID)) {

                    if (CURRENT_STATE.equals("not_buddy")) {

                        requestBtn.setEnabled(false);
                        SendBuddyRequest();
                    }

                    if (CURRENT_STATE.equals("buddy_request_sent")) {
                        CancelBuddyRequest("cancelOwnRequest");
                    }


                    if (CURRENT_STATE.equals("buddy_request_received")) {
                        AcceptBuddyRequest();
                    }

                    if (CURRENT_STATE.equals("buddy")) {
                        SendRemoveBuddyRequest();
                    }

                    if (CURRENT_STATE.equals("remove_request_sent")) {
                        CancelRemoveBuddyRequest();
                    }

                    if (CURRENT_STATE.equals("remove_request_received")) {

                        adminRef.child("buddy_removal_code").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                adminRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        buddy_removal_code = dataSnapshot.child("buddy_removal_code").getValue().toString();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                seniorBuddyRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(userNode) && buddy_removal_code.equals("1")) {

                                            SeniorAcceptRemoveBuddyRequest(buddy_removal_code);

                                        } else if(!dataSnapshot.hasChild(userNode) && buddy_removal_code.equals("1")){
                                            JuniorAcceptRemoveBuddyRequest(buddy_removal_code);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                }

            }
        });


        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (buddyRequestPage.equals("yes") || myJuniorBuddyPage.equals("yes") || CURRENT_STATE.equals("buddy")) {
                    messageBtn.setEnabled(true);

                    Intent chatIntent = new Intent(SeniorBuddyInfo.this, Chat.class);
                    chatIntent.putExtra("from", "1to1Chat");
                    chatIntent.putExtra("senderUserID", userNode);
                    chatIntent.putExtra("receiverUserID", chat_receiverUserID);
                    chatIntent.putExtra("receiverName", chat_receiverName);
                    chatIntent.putExtra("receiverProfileImgUrl", chat_receiverProfileImgUrl);

                    startActivity(chatIntent);

                } else {

                    Toast.makeText(getApplicationContext(),
                            "You Can Only Message the Senior Buddy after he/she accepts your request. Senior Buddy can message you upon receiving your Request",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    //senior buddy cannot apply for a senior buddy, remove the request button
    private void CheckSeniorBuddyIdentity(final String source) {
        seniorBuddyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(senderUserID)) {

                    if (source.equals("mySeniorBuddyPage")) {
                        btnLinearLayout.removeView(requestBtn);
                        requestBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

                                                GenerateNotification(senderUserID, receiverUserID, "remove_buddy_request");

                                                requestBtn.setEnabled(true);
                                                CURRENT_STATE = "remove_request_sent";
                                                requestBtn.setText("Cancel Request");

                                                btnLinearLayout.removeView(declineBtn);
                                                declineBtn.setEnabled(false);
                                                Toast.makeText(getApplicationContext(), "Remove Buddy Request Sent", Toast.LENGTH_LONG).show();
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
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    /*

    private void AcceptRemoveBuddyRequest() {
        //senior buddy agree to remove  buddy relationship

        studentProfileRef.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("seniorBuddy")) {
                    //hasSeniorBuddy = true;
                    //UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                    //mySeniorBuddyID = userInfo.getSeniorBuddy().trim();

                    studentProfileRef.child(senderUserID).child("seniorBuddy").removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {r
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        seniorBuddyRef.child(receiverUserID).child("juniorBuddy").child(senderUserID).removeValue()
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

                                                                                                    GenerateNotification(senderUserID, receiverUserID, "accept_remove_buddy_request");

                                                                                                    CURRENT_STATE = "not_buddy";
                                                                                                    btnLinearLayout.removeView(requestBtn);

                                                                                                    btnLinearLayout.removeView(declineBtn);
                                                                                                    declineBtn.setEnabled(false);
                                                                                                    finish();
                                                                                                    startActivity(new Intent(getApplicationContext(), SeniorBuddyPage.class));
                                                                                                    Toast.makeText(getApplicationContext(), "Buddy Relationship Removed successfully", Toast.LENGTH_LONG).show();
                                                                                                    MaintenanceOfButtons();
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


                } else {
                    //hasSeniorBuddy = false;
                    seniorBuddyRef.child(senderUserID).child("juniorBuddy").child(receiverUserID).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        studentProfileRef.child(receiverUserID).child("seniorBuddy").removeValue()
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

                                                                                                    GenerateNotification(senderUserID, receiverUserID, "accept_remove_buddy_request");


                                                                                                    CURRENT_STATE = "not_buddy";
                                                                                                    requestBtn.setText("Request");

                                                                                                    btnLinearLayout.removeView(declineBtn);
                                                                                                    declineBtn.setEnabled(false);
                                                                                                    finish();
                                                                                                    startActivity(new Intent(getApplicationContext(), SeniorBuddyPage.class));
                                                                                                    Toast.makeText(getApplicationContext(), "Buddy Relationship Removed successfully", Toast.LENGTH_LONG).show();
                                                                                                    MaintenanceOfButtons();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    */


    private void AcceptBuddyRequest() {

        //which date they become buddy
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentDate = currentDateFormat.format(calendar.getTime());


        //record the senior buddy in junior's profile
        seniorBuddyRef.child(senderUserID).child("juniorBuddy").child(receiverUserID).child("relationDate").setValue(currentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            //record the junior in senior buddy's profile
                            studentProfileRef.child(receiverUserID).child("seniorBuddy").setValue(senderUserID)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                //remove the record from buddy request node of both sender and receiver, as the relationship is already established

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

                                                                                        GenerateNotification(senderUserID, receiverUserID, "accept_buddy_request");

                                                                                        requestBtn.setEnabled(true);
                                                                                        CURRENT_STATE = "buddy";
                                                                                        requestBtn.setText("Remove Buddy");

                                                                                        btnLinearLayout.removeView(declineBtn);
                                                                                        declineBtn.setEnabled(false);
                                                                                        Toast.makeText(getApplicationContext(), "Buddy Relationship Established", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    private void CancelBuddyRequest(final String source) {

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

                                                if (source.equals("cancelOwnRequest")) {
                                                    requestBtn.setEnabled(true);
                                                    CURRENT_STATE = "not_buddy";
                                                    requestBtn.setText("Request");

                                                    declineBtn.setVisibility(View.INVISIBLE);
                                                    btnLinearLayout.removeView(declineBtn);
                                                    declineBtn.setEnabled(false);
                                                    Toast.makeText(getApplicationContext(), "Buddy Request Cancelled", Toast.LENGTH_LONG).show();

                                                } else if (source.equals("declineJuniorRequest")) {

                                                    GenerateNotification(senderUserID, receiverUserID, "decline_buddy_request");

                                                    CURRENT_STATE = "not_buddy";

                                                    btnLinearLayout.removeView(requestBtn);
                                                    btnLinearLayout.removeView(declineBtn);
                                                    declineBtn.setEnabled(false);
                                                    Toast.makeText(getApplicationContext(), "Buddy Request Declined", Toast.LENGTH_LONG).show();

                                                    finish();
                                                }

                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void SendBuddyRequest() {

        buddyRequestRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("buddy_request_sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            buddyRequestRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("buddy_request_received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                GenerateNotification(senderUserID, receiverUserID, "buddy_request");
                                                requestBtn.setEnabled(true);
                                                CURRENT_STATE = "buddy_request_sent";
                                                requestBtn.setText("Cancel Request");

                                                btnLinearLayout.removeView(declineBtn);
                                                declineBtn.setEnabled(false);
                                                Toast.makeText(getApplicationContext(), "Buddy Request Sent", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    private void JuniorAcceptRemoveBuddyRequest(String buddy_removal_code) {


            studentProfileRef.child(senderUserID).child("seniorBuddy").removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                seniorBuddyRef.child(receiverUserID).child("juniorBuddy").child(senderUserID).removeValue()
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

                                                                                            GenerateNotification(senderUserID, receiverUserID, "accept_remove_buddy_request");
                                                                                            CURRENT_STATE = "not_buddy";
                                                                                            btnLinearLayout.removeView(requestBtn);

                                                                                            btnLinearLayout.removeView(declineBtn);
                                                                                            declineBtn.setEnabled(false);
                                                                                            finish();
                                                                                            startActivity(new Intent(getApplicationContext(), SeniorBuddyPage.class));
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


    private void SeniorAcceptRemoveBuddyRequest(String buddy_removal_code) {

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

                                                    studentProfileRef.child(receiverUserID).child("seniorBuddy").removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        seniorBuddyRef.child(senderUserID).child("juniorBuddy").child(receiverUserID).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()) {

                                                                                            GenerateNotification(senderUserID, receiverUserID, "accept_remove_buddy_request");

                                                                                            CURRENT_STATE = "not_buddy";
                                                                                            requestBtn.setText("Request");

                                                                                            btnLinearLayout.removeView(declineBtn);
                                                                                            declineBtn.setEnabled(false);
                                                                                            finish();
                                                                                            startActivity(new Intent(getApplicationContext(), SeniorBuddyPage.class));
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


    private void GenerateNotification(String senderUserID, String receiverUserID, String type) {

        HashMap<String, String> chatNotificationMap = new HashMap<>();
        chatNotificationMap.put("from", senderUserID);
        chatNotificationMap.put("type", type);

        notificationRef.child(receiverUserID).push().setValue(chatNotificationMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Notification Generated", Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }


    private void MaintenanceOfButtons() {

        removeRequestSent = false;
        removeRequestReceived = false;
        buddyRequestReceived = false;
        buddyRequestSent = false;

        buddyRequestRef.child(senderUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverUserID)) {

                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString().trim();
                            if (request_type.equals("buddy_request_sent")) {

                                buddyRequestSent = true;
                                CURRENT_STATE = "buddy_request_sent";
                                requestBtn.setText("Cancel Request");
                                adminRef.child("buddy_removal_code").setValue("0");



                            } else if (request_type.equals("buddy_request_received")) {

                                buddyRequestReceived = true;
                                CURRENT_STATE = "buddy_request_received";
                                requestBtn.setText("Accept Request");
                                adminRef.child("buddy_removal_code").setValue("0");


                                //btnLinearLayout.removeView(declineBtn);
                                btnLinearLayout.addView(declineBtn);
                                declineBtn.setEnabled(true);

                                declineBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        CancelBuddyRequest("declineJuniorRequest");

                                    }
                                });
                            } else if (request_type.equals("remove_request_sent")) {

                                adminRef.child("buddy_removal_code").setValue("0");

                                removeRequestSent = true;
                                CURRENT_STATE = "remove_request_sent";
                                requestBtn.setText("Cancel Request");


                            } else if (request_type.equals("remove_request_received")) {

                                adminRef.child("buddy_removal_code").setValue("1");

                                removeRequestReceived = true;
                                CURRENT_STATE = "remove_request_received";
                                requestBtn.setText("Accept remove Request");
                                requestBtn.setTextSize(14f);
                                requestBtn.setEnabled(true);


                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        //for senior buddy only, check if he/is is exisiting junior buddy, then requestBtn text = remove buddy and CURRENT_STATE = buddy
        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(senderUserID)) {

                    seniorBuddyRef.child(senderUserID).child("juniorBuddy").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(receiverUserID) && removeRequestSent == false && removeRequestReceived == false) {


                                CURRENT_STATE = "buddy";
                                requestBtn.setText("Remove Buddy");
                                adminRef.child("buddy_removal_code").setValue("0");


                                pageLayout.removeView(scrollView);
                                //commentBtn.setVisibility(View.VISIBLE);

                            } else if (dataSnapshot.hasChild(receiverUserID) && removeRequestReceived == true) {

                                CURRENT_STATE = "remove_request_received";
                                requestBtn.setText("Accept Remove Request");
                                requestBtn.setTextSize(14f);
                                adminRef.child("buddy_removal_code").setValue("1");



                                pageLayout.removeView(scrollView);
                                removeBuddyRequestHint.setVisibility(View.VISIBLE);
                                removeBuddyRequestHint.setText("Your Junior Buddy <" + reName + "> Request to Remove Buddy Relationship");

                            } else if (dataSnapshot.hasChild(receiverUserID) && removeRequestSent == true) {

                                CURRENT_STATE = "remove_request_sent";
                                requestBtn.setText("Cancel Request");
                                adminRef.child("buddy_removal_code").setValue("0");


                                requestBtn.setEnabled(true);
                                pageLayout.removeView(scrollView);
                                //commentBtn.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {

                    //check which year is the applicant， only year 1 student can apply for a senior buddy
                    studentProfileRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            int year = Integer.parseInt(dataSnapshot.getValue(UserInfo.class).getYear());

                            if (year > 1 && !dataSnapshot.hasChild("seniorBuddy")) {
                                requestBtn.setBackgroundResource(R.drawable.btn_disabled);
                                requestBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(getApplicationContext(), "Only year ONE student can apply for a senior buddy, you may use Study Buddy Function", Toast.LENGTH_LONG).show();
                                    }
                                });
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

            }
        });


        studentProfileRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild("seniorBuddy") && removeRequestReceived == false && removeRequestSent == false) {

                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                    mySeniorBuddyID = userInfo.getSeniorBuddy().trim();

                    if (mySeniorBuddyID.equals(reEmail.substring(0, reEmail.indexOf("@")))) {

                        CURRENT_STATE = "buddy";
                        requestBtn.setText("Remove Buddy");

                        pageLayout.removeView(scrollView);

                    } else {

                        requestBtn.setBackgroundResource(R.drawable.btn_disabled);
                        requestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "You already have a Senior Buddy, Please check out at the Account Page", Toast.LENGTH_LONG).show();
                            }
                        });

                    }


                } else if (dataSnapshot.hasChild("seniorBuddy") && removeRequestReceived == true) {

                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                    mySeniorBuddyID = userInfo.getSeniorBuddy().trim();

                    if (mySeniorBuddyID.equals(reEmail.substring(0, reEmail.indexOf("@")))) {

                        CURRENT_STATE = "remove_request_received";
                        requestBtn.setText("Accept Remove Request");
                        requestBtn.setTextSize(14f);

                        pageLayout.removeView(scrollView);
                        //commentBtn.setVisibility(View.VISIBLE);
                        removeBuddyRequestHint.setVisibility(View.VISIBLE);
                        removeBuddyRequestHint.setText("Your Senior Buddy Requests to Remove Buddy Relationship");

                    } else {

                        requestBtn.setBackgroundResource(R.drawable.btn_disabled);
                        requestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "You already have a Senior Buddy, Please check out at the Account Page", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } else if (dataSnapshot.hasChild("seniorBuddy") && removeRequestSent == true) {

                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                    mySeniorBuddyID = userInfo.getSeniorBuddy().trim();

                    if (mySeniorBuddyID.equals(reEmail.substring(0, reEmail.indexOf("@")))) {

                        CURRENT_STATE = "remove_request_sent";
                        requestBtn.setText("Cancel Request");

                        pageLayout.removeView(scrollView);

                    } else {

                        requestBtn.setBackgroundResource(R.drawable.btn_disabled);
                        requestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "You already have a Senior Buddy, Please check out at the Account Page", Toast.LENGTH_LONG).show();
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


    private void PostCommentForJunior() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        final String commentDate = sdf.format(date);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        title.setText("Comment your Junior");
        subTitle.setText("Comment for: " + reName);
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

                String juniorID = reEmail.substring(0, reEmail.indexOf("@"));
                studentProfileRef = firebaseDatabase.getReference("Student Profile");
                String comment = editTextComment.getText().toString();

                if (!TextUtils.isEmpty(comment)) {

                    String commentKey = studentProfileRef.child(juniorID).child("seniorBuddyComment").push().getKey();
                    String commentRef = juniorID + "/seniorBuddyComment" + "/" + commentKey;

                    Map commentBody = new HashMap();
                    commentBody.put("comment", comment);
                    commentBody.put("date", commentDate);
                    commentBody.put("commentedBy", userNode);

                    Map postComment = new HashMap();
                    postComment.put(commentRef, commentBody);

                    studentProfileRef.updateChildren(postComment).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(SeniorBuddyInfo.this, "Comment Posted Successfully", Toast.LENGTH_SHORT).show();
                                postCommentDialog.dismiss();

                            } else {
                                Toast.makeText(SeniorBuddyInfo.this, "Comment Posted Failed", Toast.LENGTH_SHORT).show();
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


    private void getIncomingIntent() {

        if (getIntent().hasExtra("email")
                && getIntent().hasExtra("name")
                && getIntent().hasExtra("course")
                && getIntent().hasExtra("hall")
                && getIntent().hasExtra("gender")
                && getIntent().hasExtra("profileImg")
                && getIntent().hasExtra("year")
                && getIntent().hasExtra("buddyRequestPage")

        ) {

            reEmail = getIntent().getStringExtra("email");
            reName = getIntent().getStringExtra("name");
            reCourse = getIntent().getStringExtra("course");
            reHall = getIntent().getStringExtra("hall");
            reGender = getIntent().getStringExtra("gender");
            reImgUrl = getIntent().getStringExtra("profileImg");
            reYear = getIntent().getStringExtra("year");
            buddyRequestPage = getIntent().getStringExtra("buddyRequestPage");

            chat_receiverUserID = reEmail.substring(0, reEmail.indexOf("@"));
            chat_receiverName = reName;
            chat_receiverProfileImgUrl = reImgUrl;
            chat_senderID = userNode;

        } else if (getIntent().hasExtra("email")
                && getIntent().hasExtra("name")
                && getIntent().hasExtra("course")
                && getIntent().hasExtra("hall")
                && getIntent().hasExtra("gender")
                && getIntent().hasExtra("profileImg")
                && getIntent().hasExtra("year")
                && getIntent().hasExtra("myJuniorBuddyPage")

        ) {

            reEmail = getIntent().getStringExtra("email");
            reName = getIntent().getStringExtra("name");
            reCourse = getIntent().getStringExtra("course");
            reHall = getIntent().getStringExtra("hall");
            reGender = getIntent().getStringExtra("gender");
            reImgUrl = getIntent().getStringExtra("profileImg");
            reYear = getIntent().getStringExtra("year");
            myJuniorBuddyPage = getIntent().getStringExtra("myJuniorBuddyPage");

            chat_receiverUserID = reEmail.substring(0, reEmail.indexOf("@"));
            chat_receiverName = reName;
            chat_receiverProfileImgUrl = reImgUrl;
            chat_senderID = userNode;

        } else if (getIntent().hasExtra("email")
                && getIntent().hasExtra("name")
                && getIntent().hasExtra("course")
                && getIntent().hasExtra("hall")
                && getIntent().hasExtra("gender")
                && getIntent().hasExtra("profileImg")
                && getIntent().hasExtra("year")
                && getIntent().hasExtra("seniorBuddyPage")

        ) {

            reEmail = getIntent().getStringExtra("email");
            reName = getIntent().getStringExtra("name");
            reCourse = getIntent().getStringExtra("course");
            reHall = getIntent().getStringExtra("hall");
            reGender = getIntent().getStringExtra("gender");
            reImgUrl = getIntent().getStringExtra("profileImg");
            reYear = getIntent().getStringExtra("year");

            chat_receiverUserID = reEmail.substring(0, reEmail.indexOf("@"));
            chat_receiverName = reName;
            chat_receiverProfileImgUrl = reImgUrl;
            chat_senderID = userNode;


        }


    }


    private void InitialiseLayout() {

        myJuniorBuddyPage = "";
        buddyRequestPage = "";

        getIncomingIntent();
        infoEmail.setText(reEmail);
        infoName.setText(reName);
        infoCourse.setText(reCourse + ", Year " + reYear);
        infoGender.setText("Gender: " + reGender);
        Picasso.get().load(reImgUrl).into(profilePic);

        if (reHall.equals("no")) {
            infoHall.setText("Hall: NIL");
        } else {
            infoHall.setText("Hall: " + reHall);
        }

        seniorNode = reEmail.substring(0, reEmail.indexOf("@"));
        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check if the person is a senior buddy in (all senior buddy tab)or a junior in (buddy request tab).

                if (dataSnapshot.hasChild(seniorNode)) {

                    //layout of seniorInfo page if user(junior) go into the page from all senior buddy tab.

                    seniorBuddyRef.child(seniorNode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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


                            //infoExperience.setSingleLine(false);
                            infoExperience.setText("Joined Date: " + seniorBuddyModel.getJoinedDate() + "\n" + "Experience: " + diffMonth + " Months\n" + "Level: " + exp);
                            selfIntro = seniorBuddyModel.getSelfIntro();

                            commentFromBuddy.setVisibility(View.VISIBLE);
                            underline.setVisibility(View.VISIBLE);
                            commentFromBuddy.setText("Comment of the Senior Buddy");


                            commentFromBuddy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //see the selected senior's comment
                                    Intent intent = new Intent(SeniorBuddyInfo.this, Comment.class);
                                    intent.putExtra("commentOfWho", reEmail.substring(0, reEmail.indexOf("@")));
                                    intent.putExtra("identity", "senior");//identity of the person you are going to view the comment.
                                    startActivity(intent);
                                }
                            });

                            backBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(SeniorBuddyInfo.this, SeniorBuddyPage.class));

                                }
                            });

                            CheckSeniorBuddyIdentity("mySeniorBuddyPage");
                            //MaintenanceOfButtons();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else if (myJuniorBuddyPage.equals("yes")) {

                    //layout of juniorInfo page if user(senior) go into the page from buddy management page.

                    pageLayout.removeView(scrollView);
                    infoExperience.setVisibility(View.INVISIBLE);

                    //show when did the relationship establish
                    info_buddyDate.setVisibility(View.VISIBLE);
                    GetBuddyRelationshipDate();


                    //show comment button, allow senior to comment on his/her junior buddy
                    commentBtn.setVisibility(View.VISIBLE);
                    commentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PostCommentForJunior();
                        }
                    });

                    commentFromBuddy.setVisibility(View.VISIBLE);
                    underline.setVisibility(View.VISIBLE);
                    commentFromBuddy.setText("Comment of the Junior Buddy");
                    commentFromBuddy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //see the selected junior's comment
                            Intent intent = new Intent(SeniorBuddyInfo.this, Comment.class);
                            intent.putExtra("commentOfWho", reEmail.substring(0, reEmail.indexOf("@")));
                            intent.putExtra("identity", "junior");//identity of the person you are going to view the comment.
                            startActivity(intent);
                        }
                    });

                    backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(SeniorBuddyInfo.this, BuddyManagementPage.class));

                        }
                    });


                } else if (buddyRequestPage.equals("yes")) {

                    //layout of juniorInfo page if user(senior) go into the page from buddy request page.
                    pageLayout.removeView(scrollView);
                    infoExperience.setVisibility(View.INVISIBLE);

                    //show when did the relationship establish
                    //info_buddyDate.setVisibility(View.VISIBLE);
                    //GetBuddyRelationshipDate();

                    backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(SeniorBuddyInfo.this, SeniorBuddyPage.class));

                        }
                    });

                }
            }

            private void GetBuddyRelationshipDate() {

                seniorBuddyRef = FirebaseDatabase.getInstance().getReference("Senior Buddy");
                seniorBuddyRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(userNode)) {

                            String date = dataSnapshot.child(userNode).child("juniorBuddy").child(reEmail.substring(0, reEmail.indexOf("@"))).getValue(SeniorBuddyModel.class).getRelationDate();
                            info_buddyDate.setText("Buddy Since: " + date);

                        } else {

                            String date = dataSnapshot.child(reEmail.substring(0, reEmail.indexOf("@"))).child("juniorBuddy").child(userNode).getValue(SeniorBuddyModel.class).getRelationDate();
                            info_buddyDate.setText("Buddy Since: " + date);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void Initialisation() {

        //initialise firebase attributes
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));


        studentProfileRef = firebaseDatabase.getReference("Student Profile");
        seniorBuddyRef = firebaseDatabase.getReference("Senior Buddy");
        buddyRequestRef = firebaseDatabase.getReference().child("Buddy Requests");
        notificationRef = firebaseDatabase.getReference().child("Notification");


        profilePic = (CircleImageView) findViewById(R.id.info_profilepic);
        infoName = (TextView) findViewById(R.id.info_name);
        infoEmail = (TextView) findViewById(R.id.info_email);
        infoCourse = (TextView) findViewById(R.id.info_course);
        infoHall = (TextView) findViewById(R.id.info_Hall);
        infoGender = (TextView) findViewById(R.id.info_gender);
        infoExperience = (TextView) findViewById(R.id.info_exp);
        infoIntro = (TextView) findViewById(R.id.card_intro);
        info_buddyDate = (TextView) findViewById(R.id.info_buddyDate);

        btnLinearLayout = (LinearLayout) findViewById(R.id.info_buttons_layout);
        backBtn = (ImageView) findViewById(R.id.info_back);
        requestBtn = (Button) findViewById(R.id.info_requestBtn);
        declineBtn = (Button) findViewById(R.id.info_declineBtn);
        messageBtn = (ImageView) findViewById(R.id.info_msgBtn);
        commentBtn = (Button) findViewById(R.id.info_commentBtn);
        removeBuddyRequestHint = (TextView) findViewById(R.id.info_remove_buddy_request);
        commentFromBuddy = (TextView) findViewById(R.id.info_gotoCommentPage);
        underline = (View) findViewById(R.id.underline);


        expandBtn = (ImageView) findViewById(R.id.card_expand);
        collapsBtn = (ImageView) findViewById(R.id.card_collaps);

        pageLayout = (LinearLayout) findViewById(R.id.seniorbuddy_linearLayout);
        scrollView = (ScrollView) findViewById(R.id.info_scrollview);

        expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandBtn.setVisibility(View.GONE);
                collapsBtn.setVisibility(View.VISIBLE);
                infoIntro.setVisibility(View.VISIBLE);
                infoIntro.setText(selfIntro);
            }
        });

        collapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandBtn.setVisibility(View.VISIBLE);
                collapsBtn.setVisibility(View.GONE);
                infoIntro.setText("");
                infoIntro.setVisibility(View.GONE);
            }
        });

    }

}