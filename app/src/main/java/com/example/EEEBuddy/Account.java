package com.example.EEEBuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends AppCompatActivity {

    private ImageView backBtn, rightIcon;
    private TextView title;

    private CircleImageView profileImage;
    private String profileImageUrl;
    private TextView profileName;
    private ImageView profilePage;
    private ImageView myBuddy;
    private ImageView events;
    private ImageView trackStudy;
    private ImageView logout;
    private ImageView changepw;
    private String oldPass, newPass, cfmPass;

    boolean hasSeniorBuddy, hasJuniorBuddy;
    String seniorBuddy;

    //declare database stuff
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    AuthCredential authCredential;
    private DatabaseReference databaseReference, seniorBuddyRef;
    private String userID;
    private String userEmail, userNode;
    private SpaceNavigationView spaceNavigationView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //customise bottom navigation
        spaceNavigationView = findViewById(R.id.navigation);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Senior Buddy", R.drawable.ic_buddy_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Study Buddy", R.drawable.ic_msg_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Chat", R.drawable.ic_chat_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Account", R.drawable.ic_person_grey));

        spaceNavigationView.changeCurrentItem(3);
        spaceNavigationView.showIconOnly();


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(Account.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                spaceNavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                //Toast.makeText(Account.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                if(itemIndex == 0){//goto senior buddy page
                    startActivity(new Intent(Account.this, SeniorBuddyPage.class));
                }
                if(itemIndex == 1){//goto study buddy page
                    startActivity(new Intent(Account.this, StudyBuddyPage.class));
                }
                if(itemIndex == 2){//goto chat
                    startActivity(new Intent(Account.this, ChatListPage.class));
                }
                if(itemIndex == 3){//goto profile page
                    //void
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(Account.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        //customise bottom navigation above

        title = (TextView) findViewById(R.id.toolbar_title);
        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        rightIcon = (ImageView) findViewById(R.id.toolbar_right_icon);

        title.setText("My Account");
        backBtn.setVisibility(View.GONE);
        rightIcon.setVisibility(View.GONE);


        profileName = (TextView) findViewById(R.id. profile_name);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        profilePage = (ImageView) findViewById(R.id.acct_profile);
        myBuddy = (ImageView) findViewById(R.id.acct_mybuddy);
        events = (ImageView) findViewById(R.id.acct_event);
        trackStudy = (ImageView) findViewById(R.id.acct_study);
        logout = (ImageView) findViewById(R.id.acct_logout);
        changepw = (ImageView) findViewById(R.id.acct_change_pw);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Student Profile");
        seniorBuddyRef = firebaseDatabase.getReference("Senior Buddy");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));

        if(firebaseAuth.getCurrentUser() == null){
            //if user has already logged out
            finish();
            startActivity(new Intent(this,Login.class));
        }


        // Read from the database student profile pic and name
        databaseReference.child(userNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                profileName.setText(userInfo.getName());
                profileImageUrl = userInfo.getProfileImageUrl();
                Picasso.get().load(profileImageUrl).into(profileImage);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


        //onClick Listener for acct page selections
        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Account.this, Profile.class));

            }
        });




        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Account.this, RegisterdEventsPage.class));
            }
        });


        trackStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Account.this, TrackStudyPage.class);

                intent.putExtra("fromActivity", "AddStudyRecordActivity");
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(Account.this, Login.class));
            }
        });


        changepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangePassword();
            }
        });


        myBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoMySeniorBuddyInfoPage();
            }
        });

    }



    private void GotoMySeniorBuddyInfoPage() {

        hasSeniorBuddy = false;
        hasJuniorBuddy = false;


        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userNode)){

                    seniorBuddyRef.child(userNode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild("juniorBuddy")){
                                hasJuniorBuddy = true;

                                if(hasJuniorBuddy == true){
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), BuddyManagementPage.class));
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "You Do Not Have any Junior Buddy", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{

                    databaseReference.child(userNode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild("seniorBuddy")){
                                UserInfo juniorInfo = dataSnapshot.getValue(UserInfo.class);
                                seniorBuddy = juniorInfo.getSeniorBuddy().trim();
                                hasSeniorBuddy = true;

                                if(hasSeniorBuddy == true){
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), BuddyManagementPage.class));
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "You Do Not Have a Senior Buddy", Toast.LENGTH_LONG).show();
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





    }



    private void ChangePassword() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Account.this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.card_changepw, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog applicationDialog = dialogBuilder.create();
        applicationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        applicationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        applicationDialog.show();

        final EditText editTextOldPw = (EditText) dialogView.findViewById(R.id.changepw_old);
        final EditText editTextNewPw = (EditText) dialogView.findViewById(R.id.changepw_new);
        final EditText editTextCfmPw = (EditText) dialogView.findViewById(R.id.changepw_cfmpw);
        final Button changeBtn = (Button) dialogView.findViewById(R.id.changepw_changebtn);
        final ImageView closebtn = (ImageView) dialogView.findViewById(R.id.changepw_closeBtn);

        editTextOldPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextOldPw.setTextColor(ContextCompat.getColor(Account.this, R.color.mainFontColor));
                editTextOldPw.setAlpha(1);
                editTextOldPw.getText().clear();
            }
        });

        editTextNewPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNewPw.setTextColor(ContextCompat.getColor(Account.this, R.color.mainFontColor));
                editTextNewPw.setAlpha(1);
                editTextNewPw.getText().clear();
            }
        });

        editTextCfmPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextCfmPw.setTextColor(ContextCompat.getColor(Account.this, R.color.mainFontColor));
                editTextCfmPw.setAlpha(1);
                editTextCfmPw.getText().clear();
            }
        });

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applicationDialog.dismiss();
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                oldPass = editTextOldPw.getText().toString().trim();
                newPass = editTextNewPw.getText().toString().trim();
                cfmPass = editTextCfmPw.getText().toString().trim();

                if(TextUtils.isEmpty(oldPass)){
                    //if Cfmpassword is empty
                    Toast.makeText(Account.this,"Please Enter Old Password", Toast.LENGTH_SHORT).show();
                    editTextOldPw.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(newPass)){
                    //if Cfmpassword is emptyr
                    Toast.makeText(Account.this,"Please Enter New Password", Toast.LENGTH_SHORT).show();
                    editTextNewPw.requestFocus();
                    return;
                }else{
                    boolean passwordValidation = passwordFormatValidation(newPass);

                    if(!passwordValidation){

                        editTextNewPw.getText().clear();
                        editTextNewPw.setTextColor(Color.parseColor("#EEEBEB"));
                        editTextNewPw.setText("New Password");

                        editTextCfmPw.getText().clear();
                        editTextCfmPw.setTextColor(Color.parseColor("#EEEBEB"));
                        editTextCfmPw.setText("Confirm Password");

                        editTextNewPw.requestFocus();
                        editTextNewPw.setError("Password must contain at least 1 UpperCase Letter, 1 LowerCase Letter, " +
                                "1 Special Character and 1 Digit." +
                                "Password Length Min. 8 Charc, Max 15 Charc");
                        return;
                    }

                }

                if(TextUtils.isEmpty(cfmPass)) {
                    //if Cfmpassword is empty
                    Toast.makeText(Account.this, "Please Re-Enter New Password", Toast.LENGTH_SHORT).show();
                    editTextCfmPw.requestFocus();
                    return;
                }

                if(oldPass.equals(newPass)){
                    Toast.makeText(Account.this,"Old Password Cannot be the Same as New password", Toast.LENGTH_LONG).show();
                    return;
                }

                if(newPass.equals(cfmPass)){

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    authCredential = EmailAuthProvider.getCredential(userEmail,oldPass);
                    user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(Account.this, "Password update successfully", Toast.LENGTH_LONG).show();
                                            applicationDialog.dismiss();
                                        }else{
                                            Toast.makeText(Account.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }else{
                                editTextOldPw.setError("Wrong Password");
                                //Toast.makeText(Account.this,"Wrong password", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }else {
                    editTextCfmPw.setError("Password does not match");
                }
            }
        });

    }

    public Boolean passwordFormatValidation(String password){
        String pw = password;
        Pattern pattern;
        Matcher matcher;

        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(passwordPattern);
        matcher = pattern.matcher(pw);

        return matcher.matches();
    }

}

