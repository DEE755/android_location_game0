package com.example.myapplicationtest1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

public class LoadingScreen extends AppCompatActivity {

private Handler handler;

    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle savedInstanceState) {

        ProgressBar progressBar;


        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_waitingscreen);

        progressBar = findViewById(R.id.loadingProgressBar);
        progressBar.setProgress(50);

        //progressBar.setActivated(true);


        Intent newIntent = new Intent(LoadingScreen.this, MainActivity.class);



        handler = new Handler();
        handler.postDelayed(() -> {
            progressBar.setProgress(100);
            startActivity(newIntent);

            finish();
        }, 4000);


    }

}
