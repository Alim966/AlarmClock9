package com.example.alarmclock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {

            public void run(){
                Intent perexod = new Intent(SplashActivity.this, MainActivity.class);

                startActivity(perexod);

                finish();
            }
        } ,3000);
    }
}