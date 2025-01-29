package com.example.myapplicationtest1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
        disconnectButton.setOnClickListener(v -> signOutUser());
    }

    private void signOutUser() {
        mAuth.signOut();
        Toast.makeText(Disconnect.this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
        // Navigate to the login screen or update UI
        startActivity(new Intent(Disconnect.this, LogIn.class));
        finish();
    }
}


