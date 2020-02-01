package com.example.EEEBuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class SeniorBuddyPage extends AppCompatActivity {

    Toolbar toolbar;
    TextView title;
    ImageView backBtn, filterIcon;

    TabLayout tabLayout;
    TabItem tab_recommendation, tab_all;
    PagerAdapter pageAdapter;
    ViewPager viewPager;

    SpaceNavigationView spaceNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_buddy_page);

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

        //cumtomise toolbar
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        backBtn.setVisibility(View.GONE);
        title.setText("Senior Buddy");
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SeniorBuddyPage.this, "TODO", Toast.LENGTH_LONG).show();
            }
        });

        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterIcon.setImageResource(R.drawable.ic_filter_colour2);
                Toast.makeText(SeniorBuddyPage.this, "TODO", Toast.LENGTH_LONG).show();
            }
        });

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition() == 0){
                    pageAdapter.notifyDataSetChanged();
                }else if(tab.getPosition() == 1){
                    pageAdapter.notifyDataSetChanged();
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
}
