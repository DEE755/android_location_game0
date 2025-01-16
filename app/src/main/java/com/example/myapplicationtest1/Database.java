package com.example.myapplicationtest1;

import static com.example.myapplicationtest1.Player.player_counter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;



public class Database {

    private DatabaseReference db_ref;

    private DatabaseReference Player_ref;
    private FirebaseDatabase firebase_database;


    private Map<String, Object> map_long_lat = new HashMap<>();





    //constructor
    Database(String url){
        //initialize database:
        this.db_ref = FirebaseDatabase.getInstance(url).getReference();
        Player_ref=null;

    }

    DatabaseReference get_db_ref(){
        return this.db_ref;
    }
    DatabaseReference get_Player_ref(){
        return this.Player_ref;
    }


    void setPlayer_ref(DatabaseReference Player_ref)
    {
        this.Player_ref=Player_ref;
    }

    void add_player_to_db(Player player)
    {
        //CREATE A KEY THAT IS THE EMAIL BUT WITHOUT . BECAUSE FIREBASE DOESNT ALLOW DOTS IN KEYS
        String player_key= player.getEmail().replace(".", "_");
        db_ref.child("online_players").child(player_key).setValue(player);

        //String uniqueKey = player.getEmail().replace(".", "_");

       setPlayer_ref(db_ref.child("online_players").child(player_key));

        //map_long_lat.put("latitude", player.getLatitude());
        //map_long_lat.put("longitude", player.getLongitude());


       // this.db_ref.child("online_players").child(player_key).setValue(player.getName());
        //this.db_ref.child("online_players").child(player_key).child("location").updateChildren(map_long_lat);

    }

    public void removePlayerFromDatabase(String email) {
        // Replace dots in the email to avoid Firebase key issues
        String playerKey = email.replace(".", "_");
        //DatabaseReference playerRef = FirebaseDatabase.getInstance().getReference("online_players").child(playerKey);

        get_Player_ref().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firebase", "Player node removed successfully.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase", "Error removing player node", e);
            }
        });

    }

    //the Player field is for testing since we dont really update others players location
    void update_player_loc_db(Player player, double lat, double lon )
        {


        if (this.get_Player_ref()!= null)
        {
            // updateChildren method in Firebase Realtime Database requires a Map
            // to specify the fields to be updated
            Map<String, Object> updates = new HashMap<>();
            updates.put("latitude", lat);

            this.Player_ref.updateChildren(updates);
        }



    }


    /*long count_child(String node_path, MainActivity.ValueCallback<String> callback)
    {
        DatabaseReference specific_node_ref= FirebaseDatabase.getInstance().getReference(node_path);

        specific_node_ref.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {

                specific_node_ref.get().addOnSuccessListener(snapshot -> {
                    String value = snapshot.getValue(String.class);
                    callback.onValueReceived(value); // Pass the value to the callback
                }).addOnFailureListener(e -> {
                    callback.onValueReceived(null); // Pass null in case of an error
                });

            } else {
                Log.d("Firebase", "Root node has no children.");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Failed to read from the database: " + e.getMessage());
        });
    }*/



    void test_database(int current_iteration, Player mplayer)
    {


        switch(current_iteration) {

            case 0:
                this.add_player_to_db(mplayer);


                break;
            case 30:
                this.add_player_to_db(mplayer);
                break;
            case 80:
                this.add_player_to_db(mplayer);
                break;
            case 120:
                this.add_player_to_db(mplayer);
                break;
            case 260:
                // Correctly remove the 'online_players' node
                this.db_ref.child("online_players").removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Successfully deleted the node
                                Log.d("Firebase", "Online_players node deleted successfully.");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to delete the node
                                Log.e("Firebase", "Error deleting online_players node", e);
                            }
                        });
                break;

        }



    }

}
