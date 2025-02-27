package com.example.myapplicationtest1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class Disconnect extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_disconnect);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



        Button disconnectButton = findViewById(R.id.disconnect_button);
        Button returnButton= findViewById(R.id.return_to_game_button);

        //Button for future upgrades on the app
        Button ChangePic= findViewById(R.id.change_profile_pic_button);
        ChangePic.setVisibility(View.INVISIBLE);

        TextView logStatusTextView = findViewById(R.id.log_status_text_view);

        logStatusTextView.setText("You are logged in as: \n" + mAuth.getCurrentUser().getEmail());

        disconnectButton.setOnClickListener(v -> signOutUser());
        returnButton.setOnClickListener(v -> return_to_game());
        //ChangePic.setOnClickListener(v -> change_profile_pic());

    }

    private void signOutUser() {
        mAuth.signOut();
        Toast.makeText(Disconnect.this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
        // Navigate to the login screen or update UI
        startActivity(new Intent(Disconnect.this, LogIn.class));
        finish();
    }

    private void return_to_game()
    {
        startActivity(new Intent(Disconnect.this, MainActivity.class));
        finish();
    }

/*private void change_profile_pic()
{
    CameraHandler cam_handler=new CameraHandler(Disconnect.this);
    cam_handler.takePicture(Disconnect.this);

}*/

}


