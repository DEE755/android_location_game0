package com.example.myapplicationtest1;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;

public class MyService extends Service {

    private Database mDatabase;
    private static Player ClientPlayer;

    public static Player getClientPlayer() {
        return ClientPlayer;
    }

    public static void setClientPlayer(Player clientPlayer) {
        ClientPlayer = clientPlayer;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your database and player here
        //mDatabase = new Database("https://android-location-game0-default-rtdb.europe-west1.firebasedatabase.app");

        //ClientPlayer = LogIn.getFetched_logged_in_player();


    }

    //"Phone_owner(real_location_data)"

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service logic here
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("test", ClientPlayer.getName() + "removed from db");
        super.onTaskRemoved(rootIntent);
        Handler handler = new Handler();
        onQuitApp(ClientPlayer, mDatabase);

        handler.postDelayed(() -> {
            Log.d("test", "onTaskRemoved called");
            stopSelf();
        }, 5000);

        //stopSelf(); // Ensure the service stops after task is removed
    }

    @Override
    public void onDestroy() {
        Log.d("Database", "Service destroyed, removing player from database");
        Handler handler = new Handler();
        onQuitApp(ClientPlayer, mDatabase);
            super.onDestroy();


    }

    public static void onQuitApp(Player player_to_remove, Database database) {
        if (player_to_remove == null || database == null) {
            return;
        }
        DatabaseReference playerRef = database.get_db_ref().child("online_players").child(player_to_remove.getPlayerKey());
    Log.d("Database", "Removing player: " + player_to_remove.getPlayerKey());

        playerRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Player removed successfully: " + player_to_remove.getPlayerKey());
                database.removePlayerFromDatabase(player_to_remove.getPlayerKey());
            } else {
                Log.e("Database", "Failed to remove player: " + player_to_remove.getPlayerKey(), task.getException());
            }
        });

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}