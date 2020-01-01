package com.example.EEEBuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;


public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread tread = new Thread()
        {

            @Override
            public void run()
            {
                try
                {
                    sleep(3000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Intent mainIntent = new Intent(Splash.this, Login.class);
                    startActivity(mainIntent);
                }
            }
        };
        tread.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }
}
