package com.example.myapplicationtest1;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.media.MediaPlayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;


public class StartScreen extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private Sound sound1;

    private MediaPlayer mediaPlayer2;

    private CameraHandler cameraHandler;
    public void click_sound() {
        if (mediaPlayer2 != null && mediaPlayer2.isPlaying()) {
            mediaPlayer2.stop();
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
        mediaPlayer2 = MediaPlayer.create(this, R.raw.button_click);
        mediaPlayer2.start();
    }

    protected void onCreate(Bundle savedInstanceState) {

        Button QuitButton;
        Button PlayButton;
        Button CreditsButton;
        sound1 = new Sound();
        Log.d("Dashboard", "onCreate called");
        super.onCreate(savedInstanceState);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Coin Rush - Main Menu");
        }

        setContentView(R.layout.activity_startscreen);

        QuitButton = findViewById(R.id.quit_button);

        PlayButton = findViewById(R.id.play_button);

        CreditsButton = findViewById(R.id.credits_button);

        mediaPlayer = MediaPlayer.create(this, R.raw.coin_catch);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

        Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.button_zoom);
        if (zoomAnimation != null) {
            Log.d("StartScreen", "Animation loaded successfully");
            PlayButton.startAnimation(zoomAnimation);
        } else {
            Log.e("StartScreen", "Failed to load animation");
        }


        // Set a listener to restart the animation once it ends
        zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Restart the animation
                PlayButton.startAnimation(zoomAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        PlayButton.startAnimation(zoomAnimation);


        //what happens when clicking the login button
        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound();
                Intent newIntent = new Intent(StartScreen.this, LogIn.class);
                startActivity(newIntent);
                finish();
            }

        });


        //what happens when clicking the quit button
        QuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sound();
                //sound1.Sound_Click();
                MyService.onQuitApp(null, null);
                finish();
            }
        });


        CreditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraHandler = new CameraHandler(StartScreen.this);
                click_sound();
                cameraHandler.takePicture(StartScreen.this);

            }

        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("StartScreen", "Before super called");
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("StartScreen", "onActivityResult called");
        cameraHandler.handleActivityResult(requestCode, resultCode, data);
    }
}
