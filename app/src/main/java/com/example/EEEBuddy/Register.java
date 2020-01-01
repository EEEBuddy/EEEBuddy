package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private Button buttonLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextCfmPassword;

    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private boolean emailValidationResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Student Profile");

        if(firebaseAuth.getCurrentUser() !=null){
            //if user has already logged in
            finish();
            startActivity(new Intent(getApplicationContext(), Logout.class));
        }

        //progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.reg_btn);
        buttonLogin = (Button) findViewById(R.id.reg_loginbtn);

        editTextEmail = (EditText) findViewById(R.id.reg_email);
        editTextPassword = (EditText) findViewById(R.id.reg_pw);
        editTextCfmPassword = (EditText) findViewById(R.id.reg_pw2);

        progressBar = findViewById(R.id.progressBar2);

        buttonRegister.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);


    }

    private void registerUser(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String cfmPassword = editTextCfmPassword.getText().toString().trim();


        if(TextUtils.isEmpty(email)){
            //if email is empty
            Toast.makeText(this,"Please Enter Email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            editTextEmail.requestFocus();
            return;

        }else{

            boolean emailValidation = emailValidation(email);

            if(!emailValidation){
                editTextEmail.setError("Email Does Not Exist in Database, Please use NTU Email");
                editTextEmail.setText("");
                return;
            }
        }


        if(TextUtils.isEmpty(password)){
            //if password is empty
            Toast.makeText(this,"Please Enter Password", Toast.LENGTH_SHORT).show();
            editTextPassword.requestFocus();
            return;
        }else{
            boolean passwordValidation = passwordValidation(password);
            System.out.print(passwordValidation);

            if(!passwordValidation){
                editTextPassword.setError("Password must contain at least 1 UpperCase Letter, 1 LowerCase Letter, " +
                        "1 Special Character and 1 Digit." +
                        "Password Length Min. 8 Charc, Max 15 Charc");
                return;
            }

        }


        if(TextUtils.isEmpty(cfmPassword)){
            //if Cfmpassword is empty
            Toast.makeText(this,"Please Re-Enter Password", Toast.LENGTH_SHORT).show();
            editTextCfmPassword.requestFocus();
            return;
        }

        if(!password.equals(cfmPassword)){
            editTextCfmPassword.setError("Password Does Not Match");
            return;
        }





        //if the email and password are not empty
        //display the progress Bar
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            //user is successfully registered
                            Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), Logout.class));

                        }else{
                            Toast.makeText(Register.this, "Registration Failed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean emailValidation(String email) {

        final String mEmail = email;

        final String childName = email.substring(0, email.indexOf("@"));
        //databaseReference.child(childName);
        databaseReference.child(childName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals("email")) {
                        String getEmail = ds.getValue().toString().trim();
                        if (!mEmail.equals(getEmail)) {
                            emailValidationResult = false;
                        }else{
                            emailValidationResult = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        return emailValidationResult;
    }

    private boolean passwordValidation(String password) {
        String pw = password;
        Pattern pattern;
        Matcher matcher;

        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(passwordPattern);
        matcher = pattern.matcher(pw);

        return matcher.matches();
    }

    @Override
    public void onClick(View view){
        if(view == buttonRegister){
            registerUser();
        }

        if(view == buttonLogin){
            finish();
            startActivity(new Intent(this, Login.class));
        }

    }
}
