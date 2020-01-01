package com.example.EEEBuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Logout extends AppCompatActivity implements View.OnClickListener{

    //private static final String TAG = Logout.class.getName();

    private FirebaseAuth firebaseAuth;

    private Button buttonLogout;
    private Button buttonApply;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            //if user has already logged out
            finish();
            startActivity(new Intent(this,Login.class));
        }

        buttonLogout = (Button) findViewById(R.id.logout);
        buttonApply = (Button) findViewById(R.id.apply);

        buttonLogout.setOnClickListener(this);
        buttonApply.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if(view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, Login.class));
        }

        if(view == buttonApply){
            startActivity(new Intent (this, Profile.class) );
        }

    }
}
