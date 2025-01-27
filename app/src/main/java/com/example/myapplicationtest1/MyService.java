package com.example.myapplicationtest1;

import android.app.Service;
import android.content.Intent;
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

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your database and player here
        mDatabase = new Database("https://android-location-game0-default-rtdb.europe-west1.firebasedatabase.app");
        ClientPlayer = new Player("Phone_owner(real_location_data)", 0, 0, "test@hit.ac.il", mDatabase);
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
        onQuitApp(ClientPlayer, mDatabase);
        stopSelf(); // Ensure the service stops after task is removed
    }

    @Override
    public void onDestroy() {
        Log.d("Database", "Service destroyed, removing player from database");
        onQuitApp(ClientPlayer, mDatabase);
        super.onDestroy();
    }

    private void onQuitApp(Player player_to_remove, Database database) {
        DatabaseReference playerRef = database.get_db_ref().child("online_players").child(player_to_remove.getPlayer_key());
    Log.d("Database", "Removing player: " + player_to_remove.getPlayer_key());

        playerRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Player removed successfully: " + player_to_remove.getPlayer_key());
                database.removePlayerFromDatabase(player_to_remove.getPlayer_key());
            } else {
                Log.e("Database", "Failed to remove player: " + player_to_remove.getPlayer_key(), task.getException());
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}