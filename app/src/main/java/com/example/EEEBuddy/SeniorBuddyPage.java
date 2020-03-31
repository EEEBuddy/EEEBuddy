package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
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

public class SeniorBuddyPage extends AppCompatActivity {

    Toolbar toolbar;
    TextView title,applyText;
    ImageView backBtn, filterIcon;

    TabLayout tabLayout;
    TabItem tab_recommendation, tab_all;
    PagerAdapter pageAdapter;
    ViewPager viewPager;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference seniorBuddyRef;
    private String userEmail, userNode;
    private FirebaseUser user;


    SpaceNavigationView spaceNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_buddy_page);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            //if user has already logged out
            finish();
            startActivity(new Intent(this,Login.class));
        }

        //customise bottom navigation
        spaceNavigationView = findViewById(R.id.navigation);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Senior Buddy", R.drawable.ic_buddy_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Study Buddy", R.drawable.ic_msg_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Chat", R.drawable.ic_chat_grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Account", R.drawable.ic_person_grey));

        spaceNavigationView.changeCurrentItem(0);
        spaceNavigationView.showIconOnly();


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(SeniorBuddyPage.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                spaceNavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                //Toast.makeText(Account.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                if(itemIndex == 0){//goto senior buddy page
                    //startActivity(new Intent(Account.this, SeniorBuddyPage.class));
                }
                if(itemIndex == 1){//goto study buddy page
                    startActivity(new Intent(SeniorBuddyPage.this, StudyBuddyPage.class));
                }
                if(itemIndex == 2){//goto chat
                    startActivity(new Intent(SeniorBuddyPage.this, ChatListPage.class));
                }
                if(itemIndex == 3){//goto profile page
                    startActivity(new Intent(SeniorBuddyPage.this, Account.class));
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(SeniorBuddyPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });
        //customise bottom navigation above





        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.toolbar_title);
        backBtn = (ImageView) findViewById(R.id.toolbar_back);
        filterIcon = (ImageView) findViewById(R.id.toolbar_right_icon);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.seniorbuddy_tablayout);
        tab_recommendation = (TabItem) findViewById(R.id.seniorbuddy_recomm);
        tab_all = (TabItem) findViewById(R.id.seniorbuddy_all);


        firebaseDatabase = FirebaseDatabase.getInstance();
        seniorBuddyRef = firebaseDatabase.getReference("Senior Buddy");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();
        userNode = userEmail.substring(0, userEmail.indexOf("@"));



        backBtn.setVisibility(View.GONE);
        title.setText("Senior Buddy");

        filterIcon.setVisibility(View.GONE);

        pageAdapter = new SeniorBuddyPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        seniorBuddyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userNode)){

                    tabLayout.getTabAt(0).setText("Buddy Requests");                        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition() == 0){
                    pageAdapter.notifyDataSetChanged();
                    filterIcon.setVisibility(View.GONE);

                }else if(tab.getPosition() == 1){
                    pageAdapter.notifyDataSetChanged();
                    filterIcon.setVisibility(View.VISIBLE);

                }



            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



    }

    private void SeniorBuddyApplication() {
        Toast.makeText(SeniorBuddyPage.this,"TODO...", Toast.LENGTH_LONG).show();
    }

}
