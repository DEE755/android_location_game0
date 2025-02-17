package com.example.myapplicationtest1;

import static com.example.myapplicationtest1.Location_utils.getUserLatitude;
import static com.example.myapplicationtest1.Location_utils.getUserLongitude;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class Time_based_operations
{
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    protected void onCreate(Bundle savedInstanceState) {

        // Initialize the runnable
        runnable = new Runnable() {
            @Override
            public void run() {
                // Code to execute every 5 seconds
                updatePlayerLocation();

                // Re-post the runnable with a delay of 5 seconds
                handler.postDelayed(this, 5000);
            }
        };

        // Start the runnable for the first time
        handler.post(runnable);
    }

    public static void updatePlayerLocation() {
        // Your code to update the player location
        MainActivity.getmDatabase().update_player_loc_db(MyService.getClientPlayer(), getUserLatitude(),getUserLongitude());


    }







}
