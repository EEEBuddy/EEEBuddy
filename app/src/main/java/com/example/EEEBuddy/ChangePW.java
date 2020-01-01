package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePW extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSendEmail;
    private Button buttonLogin;

    private EditText editTextEmail;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);

        buttonSendEmail = (Button) findViewById(R.id.changepw_btn);
        buttonLogin = (Button) findViewById(R.id.changepw_loginbtn);
        editTextEmail = (EditText) findViewById(R.id.changepw_email);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonSendEmail.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
    }


    private void SendResetPwEmail() {
       String email = editTextEmail.getText().toString().trim();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //email sent successfully
                            Toast.makeText(ChangePW.this, "Reset Password Email Sent", Toast.LENGTH_LONG).show();
                            editTextEmail.setText(null);
                        }else{
                            Toast.makeText(ChangePW.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



    @Override
    public void onClick(View view) {

        if(view == buttonLogin){
            finish();
            startActivity(new Intent(this, Login.class));
        }

        if(view == buttonSendEmail){
            SendResetPwEmail();
        }
    }

}

