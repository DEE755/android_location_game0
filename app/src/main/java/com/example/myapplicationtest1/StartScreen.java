package com.example.myapplicationtest1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class StartScreen extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        Button QuitButton;
        Button PlayButton;

        Log.d("Dashboard", "onCreate called");
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_startscreen);

        QuitButton = findViewById(R.id.quit_button);

        PlayButton = findViewById(R.id.play_button);







        //cpy as android needs


        //what happens when clicking the login button
        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(StartScreen.this, LogIn.class);
                startActivity(newIntent);
                finish();
            }


        });


        //what happens when clicking the quit button
        QuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyService.onQuitApp(null,null);
                finish();
            }
        });


    }
}
