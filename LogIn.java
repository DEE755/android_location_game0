package com.example.myapplicationtest1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;


public class LogIn extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String email;

    String name;
    //String email;
    private String password;

    private String name_input;

    private static boolean profile_picture_taken=false;

    public static void setProfile_picture_taken(boolean status) {
        profile_picture_taken = status;
    }

    private static boolean PlayerLoggedIn;

    private Bitmap profile_pic;


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

    private CameraHandler cameraHandler;

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
        Log.d("Dashboard", "onCreate called");
        super.onCreate(savedInstanceState);
        // Start the service class
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
        setContentView(R.layout.activity_waitingscreen);
        TextView waiting_text = findViewById(R.id.loading_text_view);
        waiting_text.setText("Checking if player exists");
        ProgressBar loadingBar = findViewById(R.id.loadingProgressBar);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingBar.setProgress(10);
            }
        }, 400);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingBar.setProgress(20);
            }
        }, 1000);


// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
             name = currentUser.getDisplayName();
             email = currentUser.getEmail();
            // Display a Toast message
            Toast.makeText(getApplicationContext(), "Hello " + currentUser.getEmail(), Toast.LENGTH_LONG).show();



            //THE PLAYER EXIST SO WE FETCH IT FROM DB:
            fetched_logged_in_player = new Player(m_database, currentUser.getEmail(), new Player.DataFetchListener() {
                @Override
                public void onDataFetched(Player player) {
                    //deleting from online player in case it is there by mistake:
                    MyService.onQuitApp(player, m_database);

                    //MyService.setClientPlayer(new Player(MainActivity.getmDatabase(), currentUser.getEmail()));
                    Log.d("usert", "user is logged in as :" + name + " with email: " + email);
                    Log.d("usert", "fetched player: " + fetched_logged_in_player);
                    Toast.makeText(getApplicationContext(), "YOU ARE LOGGED AT " + currentUser.getEmail() +"\nGOOD LUCK", Toast.LENGTH_LONG).show();
                    //in the main_activity the player will be created in MyService with the fetched_logged_in_player data
                    MainActivity.setPlayerExistedBefore(true);

                    //Finish Loading with fetching the image from storage
                    loadingBar.setProgress(86);
                    waiting_text.setText("Fetching profile picture");



                    Storage_Service storage=new Storage_Service(LogIn.this);
                    storage.getPlayerImageFromStorage(player, new OnSuccessListener<Bitmap>() {
                        @Override
                        public void onSuccess(Bitmap fetched_image) {
                            player.setProfile_pic(fetched_image);
                            Log.d("image", "image fetched:"+fetched_image);
                            MyService.setClientPlayer(fetched_logged_in_player);
                            startActivity(new Intent(LogIn.this, LoadingScreen.class));
                            finish();
                        }


                        public void onFailure(@NonNull Exception e) {
                            Log.e("image", "image fetching failed:"+e.getMessage());
                            Toast.makeText(getApplicationContext(), "THERE WAS A CRITICAL ERROR FETCHING " + currentUser.getEmail() + "\nYou're account will be reset", Toast.LENGTH_LONG).show();
                            setPlayerLoggedIn(true);
                            assert email != null;
                            if (name_input == null || name_input.isEmpty()) {
                                Log.d("uaaaa", "useris logged in as :" + name_input + " with email: " + email);
                                showInputDialog(email);
                            } else {
                                start_activity_with_new_player(email, name_input, profile_pic);
                            }
                        }
                    });





                }

                @Override
                public void onError(DatabaseError error) {
                    //NOT EXISTING IN DB ==> INITIALIZING NEW PLAYER
                    Log.d("error", "error fetching player data");
                    Toast.makeText(getApplicationContext(), "THERE WAS A CRITICAL ERROR FETCHING " + currentUser.getEmail() + "\nYou're account will be reset", Toast.LENGTH_LONG).show();
                    setPlayerLoggedIn(true);
                    assert email != null;
                    //name_input="test";
                    if (name_input == null || name_input.isEmpty()) {
                        Log.d("uaaaa", "useris logged in as :" + name_input + " with email: " + email);
                        showInputDialog(email);
                    } else {

                        start_activity_with_new_player(email, name_input, profile_pic);
                    }
                }

            },this);




        }

        else {
            // No user is signed in
            Toast.makeText(getApplicationContext(), "Please log in", Toast.LENGTH_LONG).show();
            setContentView(R.layout.fragment_login);




            //Toast.makeText(getApplicationContext(), "AAAAAAAA " , Toast.LENGTH_LONG).show();




            //Log.d("value", String.valueOf(emailEditText));

            EditText emailEditText;
            EditText passwordEditText;
            TextView logStatusTextView;
            Button loginButton;
            Button signupButton;


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

                    emailEditText.setVisibility(View.VISIBLE);
                    passwordEditText.setVisibility(View.VISIBLE);
                    //finalLoginButton.setVisibility(View.INVISIBLE);
                    finalLoginButton.setBackgroundColor(Color.DKGRAY);
                    finalSignupButton.setBackgroundColor(Color.BLUE);
                    //finalLoginButton.setVisibility(View.INVISIBLE);


                    if (finalEmailEditText.getText().toString().isEmpty() || finalPasswordEditText.getText().toString().isEmpty()) {
                        Toast.makeText(LogIn.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();


                    } else {
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

                                                                   showInputDialog(email);

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

    }

    private void start_activity_with_new_player(String email, String nameInput, Bitmap profile_pic)
    {
        MyService.setClientPlayer(new Player(email, name_input, profile_pic, m_database));
        Log.d("usert", "user is logged in as :" + MyService.getClientPlayer());
        MainActivity.setPlayerExistedBefore(false);
        setPlayerLoggedIn(true);
        startActivity(new Intent(LogIn.this, LoadingScreen.class));
        finish();


    }


    private void showInputDialog(String email_) {
        // Create an EditText view to get user input
        final EditText input = new EditText(this);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Player Name");
        builder.setMessage("Please enter your name:");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                name_input = input.getText().toString();

                Log.d("uaaaa", "useris logged in as :" + name_input + " with email: " + email);


                Handler handler = new Handler();
                AlertDialog.Builder builder2 = new AlertDialog.Builder(LogIn.this);
                builder2.setTitle("You need to take a profile picture");
                builder2.setMessage("Opening Camera");
                //builder2.setView(input);

                builder2.setPositiveButton("Take photo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                cameraHandler = new CameraHandler(LogIn.this);
                                //click_sound();
                                cameraHandler.takePicture(LogIn.this);

                            }
                        }
                );

                builder2.setNegativeButton("Choose from gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO IMPLEMENT GALLERY
                        finish();
                    }
                });

                builder2.show();


                //Check every two second if the profile picture has been taken

                Runnable checkProfilePictureTaken = new Runnable() {
                    @Override
                    public void run() {
                        Log.d("uaaaa", profile_picture_taken + " is the status of the profile picture");
                        if (!profile_picture_taken) {
                            handler.postDelayed(this, 2000);
                            Log.d("uaaaa", "delaying check");
                        } else {
                            Log.d("uaaaa", "entering game as photo taken");
                            start_activity_with_new_player(email_, name_input, profile_pic);

                        }
                    }
                };
                handler.post(checkProfilePictureTaken);


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    @Override
    public void onStart() {

        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("StartScreen", "Before super called");
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("StartScreen", "onActivityResult called");
        //get the picture from the camera
        profile_pic=cameraHandler.handleActivityResult(requestCode, resultCode, data);



        String destinationName = email.replace(".", "_") + "_logo";

        //uploading to firebase storage
        Storage_Service storage_service = new Storage_Service(LogIn.this);
        Log.d("CameraHandler", "Uploading image to storage: " +storage_service);

        storage_service.uploadImage(profile_pic, destinationName);

        //release the handler for the game to start
        setProfile_picture_taken(true);

    }
}












