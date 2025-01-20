package com.example.myapplicationtest1;



//THIS CLASS IS FOR SERVICES LIKE FOR EXEMPLE WHEN QUITTING THE GAME WITH SWIPE IT CLEANS THE PLAYER
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class MyService extends Service {

    private Database mDatabase;
    private Player client_player;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your database and player here
        mDatabase = new Database("https://android-location-game0-default-rtdb.europe-west1.firebasedatabase.app");
        client_player = new Player("Phone_owner(real_location_data)", 0, 0, "test@hit.ac.il", mDatabase);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        onQuitApp(client_player, mDatabase);
    }

    private void onQuitApp(Player player_to_remove, Database database) {
        database.removePlayerFromDatabase(player_to_remove.getPlayer_key());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
