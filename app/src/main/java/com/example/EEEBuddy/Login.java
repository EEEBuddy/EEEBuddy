package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private Button buttonLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewForgotPW;

    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() !=null){
            //if user has already logged in
            finish();
            startActivity(new Intent(getApplicationContext(), SeniorBuddyPage.class));
        }


        buttonRegister = (Button) findViewById(R.id.login_signupbtn);
        buttonLogin = (Button) findViewById(R.id.login_btn);

        editTextEmail = (EditText) findViewById(R.id.login_email);
        editTextPassword = (EditText) findViewById(R.id.login_pw);
        textViewForgotPW = (TextView) findViewById(R.id.login_forgotpw);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imageView = findViewById(R.id.loading);

        buttonRegister.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        textViewForgotPW.setOnClickListener(this);
        Glide.with(this).asGif().load(R.drawable.loading).into(imageView);
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //if email is empty
            Toast.makeText(this,"Please Enter Email", Toast.LENGTH_SHORT).show();
            editTextEmail.requestFocus();
            //stopping the function execution further
            return;
        }

        if(TextUtils.isEmpty(password)){
            //if password is empty
            Toast.makeText(this,"Please Enter Password", Toast.LENGTH_SHORT).show();
            editTextPassword.requestFocus();
            return;
        }

        //if the email and password are not empty
        //display the progress Bar
        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), Account.class));
                        }else{
                            Toast.makeText(Login.this, "Login Failed." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    @Override
    public void onClick(View view){
        if(view == buttonLogin){
            userLogin();
        }

        if(view == buttonRegister){
            finish();
            startActivity(new Intent(this, Register.class));
        }
        if(view == textViewForgotPW){
            finish();
            startActivity(new Intent(this, ChangePW.class));
        }

    }
}
