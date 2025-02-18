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
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;


public class StartScreen extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private Sound sound1;

    private MediaPlayer mediaPlayer2;

    private CameraHandler cameraHandler;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    LeaderBoard leaderboardFragment;
    FrameLayout fragmentContainer;




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
        Button LeaderBoardButton;

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

        LeaderBoardButton = findViewById(R.id.leaderboard_button);

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

        LeaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaderboardFragment = new LeaderBoard();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_leaderboard, leaderboardFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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

                click_sound();
                Intent newIntent = new Intent(StartScreen.this, Credits.class);
                startActivity(newIntent);
                finish();

            }

        });



    }

}

