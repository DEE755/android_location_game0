package com.example.myapplicationtest1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;


public class LogIn extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String email;
    private String password;


    private static boolean PlayerLoggedIn;


    public static void setPlayerLoggedIn(boolean b) {
    }

    public static boolean getPlayerLoggedIn() {
        return PlayerLoggedIn;
    }


    private static Player fetched_logged_in_player;

    public static Player getFetched_logged_in_player() {
        return fetched_logged_in_player;
    }

    public void setFetched_logged_in_player(Player fetched_logged_in_player) {
        this.fetched_logged_in_player = fetched_logged_in_player;
    }


    Database m_database= new Database ("https://android-location-game0-default-rtdb.europe-west1.firebasedatabase.app");


    private void loginUser(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Login", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Navigate to the main activity or update UI
                        Toast.makeText(LogIn.this, "Authentication successful, logging you in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LogIn.this, MainActivity.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Login", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LogIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    protected void onCreate(Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //return view;


// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        EditText emailEditText;
        EditText passwordEditText;
        Button loginButton;
        Button signupButton;





        Log.d("Dashboard", "onCreate called");
        super.onCreate(savedInstanceState);



            setContentView(R.layout.fragment_login);
            //Toast.makeText(getApplicationContext(), "AAAAAAAA " , Toast.LENGTH_LONG).show();




        //Log.d("value", String.valueOf(emailEditText));


        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);

        emailEditText.setVisibility(View.INVISIBLE);
        passwordEditText.setVisibility(View.INVISIBLE);


        EditText finalEmailEditText = emailEditText;
        EditText finalPasswordEditText = passwordEditText;


       //cpy as android needs
        Button finalLoginButton1 = loginButton;
        Button finalSignupButton1 = signupButton;
        EditText finalEmailEditText1 = emailEditText;
        EditText finalPasswordEditText1 = passwordEditText;

        //what happens when clicking the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    finalEmailEditText1.setVisibility(View.VISIBLE);
                    finalPasswordEditText1.setVisibility(View.VISIBLE);
                    finalLoginButton1.setBackgroundColor(Color.BLUE);
                    finalSignupButton1.setBackgroundColor(Color.DKGRAY);
                    email = finalEmailEditText.getText().toString().trim();
                    Log.d("email", email);
                    password = finalPasswordEditText.getText().toString().trim();
                    Log.d("email", password);
                    loginUser(email, password);
                } catch (Exception e) {
                    Log.d("Error", e.getMessage());
                }
            }
        });


        //what happens when clicking the signup button
        Button finalLoginButton = loginButton;
        Button finalSignupButton = signupButton;
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    //finalLoginButton.setVisibility(View.INVISIBLE);
                    finalLoginButton.setBackgroundColor(Color.DKGRAY);
                    finalSignupButton.setBackgroundColor(Color.BLUE);
                    //finalLoginButton.setVisibility(View.INVISIBLE);


                    if (finalEmailEditText.getText().toString().isEmpty() || finalPasswordEditText.getText().toString().isEmpty()) {
                        Toast.makeText(LogIn.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();




                    }

                    else {
                        email = finalEmailEditText.getText().toString().trim();
                        Log.d("email", email);
                        password = finalPasswordEditText.getText().toString().trim();
                        Log.d("email", password);
                        loginUser(email, password);

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                                           @Override
                                                           public void onComplete(@NonNull Task<AuthResult> task) {
                                                               if (task.isSuccessful()) {
                                                                   // Sign in success, update UI with the signed-in user's information
                                                                   Log.d("authen", "createUserWithEmail:success");
                                                                   FirebaseUser user = mAuth.getCurrentUser();
                                                                   //updateUI(user);
                                                               } else {
                                                                   // If sign in fails, display a message to the user.
                                                                   Log.w("authen", "createUserWithEmail:failure", task.getException());
                                                                   Toast.makeText(LogIn.this, "Authentication failed.",
                                                                           Toast.LENGTH_SHORT).show();
                                                                   //updateUI(null);
                                                               }
                                                           }
                                                       }
                                );
                        Log.d("auth", "creating user");
                    }


            }
        });




    }




    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // After
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            // Display a Toast message
            Toast.makeText(getApplicationContext(), "Hello " + currentUser.getEmail(), Toast.LENGTH_LONG).show();

            //TODO: SHOW PLEASE WAIT WHILE FETCHING PLAYER DATA SCREEN

            //Player fetch_player_data(currentUser.getEmail());
            Log.d("databaseee", "db ref is: " + m_database.getDatabaseReference());
            fetched_logged_in_player = new Player(m_database, currentUser.getEmail(), new Player.DataFetchListener() {
                @Override
                public void onDataFetched(Player player) {

                    //MyService.setClientPlayer(new Player(MainActivity.getmDatabase(), currentUser.getEmail()));
                    Log.d("usert", "user is logged in as :" + name + " with email: " + email);
                    Log.d("usert", "fetched player: " + fetched_logged_in_player);
                    Toast.makeText(getApplicationContext(), "YOU ARE LOGGED AT " + currentUser.getEmail() +"\nGOOD LUCK", Toast.LENGTH_LONG).show();
                    //in the main_activity the player will be created in MyService with the fetched_logged_in_player data
                    MainActivity.setPlayerExistedBefore(true);
                    setPlayerLoggedIn(true);
                    startActivity(new Intent(LogIn.this, MainActivity.class));

                }

                @Override
                public void onError(DatabaseError error) {
                    //NOT EXISTING IN DB ==> INITIALIZING NEW PLAYER
                    Log.d("error", "error fetching player data");
                    Toast.makeText(getApplicationContext(), "THERE WAS A CRITICAL ERROR FETCHING " + currentUser.getEmail() +"\nYou're account will be reset", Toast.LENGTH_LONG).show();


                    assert email != null;
                    fetched_logged_in_player = new Player(email);
                    Log.d("usert", "user is logged in as :" + fetched_logged_in_player);
                    MainActivity.setPlayerExistedBefore(false);
                    setPlayerLoggedIn(true);
                    startActivity(new Intent(LogIn.this, MainActivity.class));

                }

            });

        }
    }
}












