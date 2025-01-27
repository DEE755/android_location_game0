package com.example.myapplicationtest1;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentification {

    // Receive custom token from your backend
    String customToken = "aaabbbcccc";

    void new_auth(String customToken) {
// Authenticate with Firebase using the custom token
        FirebaseAuth.getInstance().signInWithCustomToken(customToken)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Auth" ,"signInWithCustomToken:success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Auth", "signInWithCustomToken:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });

    }
}
