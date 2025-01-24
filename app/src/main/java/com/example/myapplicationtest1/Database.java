package com.example.myapplicationtest1;
/////////////////
//TODO: IT SEEMS THAT CREATING A NEW OBJECT FROM FIREBASE DATABASE IS NOT A GOOD SOLUTION.
// INSTEAD, WE SHOULD UPDATE THE EXISTING OBJECTS IN THE LIST
//THE ASYNCHRONOUS BEHAVIOR OF FIREBASE MAKES IT DIFFICULT TO CREATE NEW OBJECTS ONLY ONE TIME
//SO IT CONTINUES CREATING ALL THE TIME NEW OBJECT WHERE IT SHOULD CREATE ONLY ONE THEN WE UPDATE IT AT EACH CHANGE







import com.google.firebase.database.Transaction;
import com.google.firebase.database.MutableData;



import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Database {

    private DatabaseReference db_ref;

    public DatabaseReference getDatabaseReference() {
        return db_ref;
    }

    private DatabaseReference Player_ref;// need to identify whos player it is (client) what about when we want to update others ?
    private FirebaseDatabase firebase_database;


    private Map<String, Object> map_long_lat = new HashMap<>();


    //constructor
    Database(String url) {
        //initialize database:
        this.db_ref = FirebaseDatabase.getInstance(url).getReference();
        Player_ref = null;

    }

    DatabaseReference get_db_ref() {
        return this.db_ref;
    }

    DatabaseReference getPlayerRef() {
        return this.Player_ref;
    }


    void setPlayer_ref(DatabaseReference Player_ref) {
        this.Player_ref = Player_ref;
    }

    void add_player_to_db(Player player, MapView mapview) {

        //CREATE A KEY THAT IS THE EMAIL BUT WITHOUT . BECAUSE FIREBASE DOESNT ALLOW DOTS IN KEYS
        String player_key = player.getEmail().replace(".", "_");
        db_ref.child("online_players").child(player_key).setValue(player)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Action to perform on success
                        Log.d("Database", "Player added successfully.");
                        //keep the database path for the player in the player object
                        Log.d("prints", "arrived before line 90 for player:" + player.getName());
                        setPlayer_ref(db_ref.child("online_players").child(player_key));
                        Log.d("prints", "arrived to line 90 for player:" + player.getName()+"\nThe ref is: "+player.getPlayerRefToDb());
                        //while(player.getPlayerRefToDb()==null){Log.d("Database", "Waiting for ref to update");}
                        listenForObjectsToCollect(player, mapview);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Action to perform on failure
                        Log.e("Database", "Failed to add player", e);
                    }
                });


        //map_long_lat.put("latitude", player.getLatitude());
        //map_long_lat.put("longitude", player.getLongitude());


        // this.db_ref.child("online_players").child(player_key).setValue(player.getName());
        //this.db_ref.child("online_players").child(player_key).child("location").updateChildren(map_long_lat);

    }


    //Firebase doesnt always delete well the player
    //this method does a transaction to delete the player it works a little bit better but still not 100%
    public void removePlayerFromDatabase(String playerKey) {
        //String playerKey = email.replace(".", "_");
        DatabaseReference playerRef = this.db_ref.child("online_players").child(playerKey);

        playerRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                mutableData.setValue(null);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e("Firebase", "Error removing player node", databaseError.toException());
                } else {
                    Log.d("Firebase", "Player node removed successfully.");
                }
            }
        });
    }


    //fetch player from database and create a Player java object for it
    //!! This is the only way BECAUSE OF ASYNCHRONOUS behavior of Firebase, when the data is ready only we can fetch it so we need CALLBACK
    // Method to fetch all players from the "online_players" node
    // In Database.java

    // In Database.java

    public void fetchAllPlayers(final PlayersCallback callback, MapView mapview, boolean flag_created, final Runnable onComplete) {
        if (flag_created) {
            return;
        }
        DatabaseReference playersRef = db_ref.child("online_players");

        playersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // List<Player> playersList = new ArrayList<>();
                if (Player.online_playerList == null) {
                    Player.online_playerList = new ArrayList<>();
                } else if (!Player.online_playerList.isEmpty()) {
                    Player.online_playerList.clear(); // Clear the existing list
                    // Remove a specific overlay from the map
                    mapview.getOverlays().clear();
                    mapview.invalidate();
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Player player = snapshot.getValue(Player.class);
                    if (player != null) {
                        player.setPlayer_key(snapshot.getKey());
                        player.setEmail(snapshot.getKey().replace("_", "."));
                        player.setPlayer_marker(player.create_player_marker(mapview, player));
                        player.setName(snapshot.child("name").getValue(String.class));

                        Boolean isOnMap = snapshot.child("is_on_map").getValue(Boolean.class);
                        //player.setIs_on_map(isOnMap != null ? isOnMap : true);
                        Player.online_playerList.add(player); // Add player to online_playerList
                        Log.d("Database", "Player added: " + player.getEmail());
                    } else {
                        Log.e("Database", "Player data is null for snapshot: " + snapshot.getKey());
                    }
                }
                callback.onPlayersFetched(Player.online_playerList);
                if (onComplete != null) {
                    onComplete.run();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }


    // Callback interface
    public interface PlayersCallback {
        void onPlayersFetched(List<Player> players);

        void onError(Exception e);
    }


    //the Player field is for testing since we dont really update others players location

    //this need to be used more
    void update_player_loc_db(Player player, double lat, double lon) {


        if (this.getPlayerRef() != null) {
            // updateChildren method in Firebase Realtime Database requires a Map
            // to specify the fields to be updated
            Map<String, Object> updates = new HashMap<>();
            updates.put("latitude", lat);
            updates.put("longitude", lon);

            this.Player_ref.updateChildren(updates);
        }


    }

    // In your Database class
    public void listenForNewOnlinePlayers(MapView mapview, Player local_player) {
        DatabaseReference onlinePlayersRef = get_db_ref().child("online_players");

        onlinePlayersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Player newPlayer = dataSnapshot.getValue(Player.class);
                if (newPlayer != null) {
                    try {
                    if (Objects.equals(newPlayer.getPlayer_key(), local_player.getPlayer_key()))
                    {return;}
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    newPlayer.setPlayer_key(dataSnapshot.getKey());
                    newPlayer.setEmail(dataSnapshot.getKey().replace("_", "."));
                    newPlayer.setPlayer_marker(newPlayer.create_player_marker(mapview, newPlayer));
                    newPlayer.setName(dataSnapshot.child("name").getValue(String.class));
                    Boolean isOnMap = dataSnapshot.child("is_on_map").getValue(Boolean.class);
                    newPlayer.setIs_on_map(isOnMap != null ? isOnMap : true);
                    if (Player.online_playerList == null) {
                        Player.online_playerList = new ArrayList<>();
                    }
                    if (newPlayer != null) {
                        Player.online_playerList.add(newPlayer);
                    }
                    Log.d("Database", "New player added: " + newPlayer.getEmail());
                    // Update the map view
                    if (newPlayer.getPlayer_marker() != null) {
                        mapview.getOverlays().add(newPlayer.getPlayer_marker());
                        mapview.invalidate();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle child changed


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Database", "Player removed: " + dataSnapshot.getKey());
                //Player deletedPlayer = dataSnapshot.getValue(Player.class);

                //deletedPlayer.setPlayer_key(dataSnapshot.getKey());
                mapview.getOverlays().remove(Player.getPlayerMarkerMap().get(dataSnapshot.getKey()));
                //delete the java map object
                Player.getPlayerMarkerMap().remove(dataSnapshot.getKey());

                mapview.invalidate();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                Log.d("Database", "Player moved: " + dataSnapshot.getKey());
                //TODO MOVE TO ONCHILD CHANGE RATHER THAN HERE

                //update in object by key: create a method to map object with key
                //.......//
                GeoPoint new_coordinates = new GeoPoint(dataSnapshot.child("latitude").getValue(Double.class), dataSnapshot.child("longitude").getValue(Double.class));
                Marker marker_to_update = (Marker) Player.getPlayerMarkerMap().get(dataSnapshot.getKey());

                //update object on map layer == update the marker
                marker_to_update.setPosition(new_coordinates);
                // Handle child moved
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database", "Error listening for new online players", databaseError.toException());
            }
        });
    }

    //when it detects that the list of objects' been added to the localPlayer
    //cascade of listening
    public void listenForObjectsToCollect(Player localPlayer, MapView mapview) {
        DatabaseReference objectsRef = get_db_ref().child("online_players").child(localPlayer.getPlayer_key()).child("list_of_objects_to_collect");
Log.d("prints", "db ref for objects of " + localPlayer.getName() + " is :" +objectsRef);
        objectsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Object_to_collect object = dataSnapshot.getValue(Object_to_collect.class);

                // Update the field "ObjectDelivered" to true
                //in obj
                localPlayer.setObjectDeliveredStatus(true);

                //in db
                objectsRef.getParent().child("ObjectDelivered").setValue(true);

                if (object != null) {

                    List<Object_to_collect> objects = localPlayer.getList_of_objects_to_collect();
                    if (objects == null) {

                        objects = new ArrayList<>();
                        localPlayer.setList_of_objects_to_collect(objects);
                    }
                    Log.d("objects", "nonnul");
                    objects.add(object);
                    Log.d("Database", "Object added: " + object.toString());

                    //localPlayer.fetchObjectsToCollect(this.firebase_database;
                    for(Object_to_collect obj: localPlayer.getList_of_objects_to_collect())
                    {

                        obj.create_object_marker(mapview);
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle child changed if necessary
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Object_to_collect object = dataSnapshot.getValue(Object_to_collect.class);
                if (object != null) {
                    List<Object_to_collect> objects = localPlayer.getList_of_objects_to_collect();
                    if (objects != null) {
                        objects.remove(object);
                        Log.d("Database", "Object removed: " + object.toString());
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle child moved if necessary
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database", "Error listening for objects to collect", databaseError.toException());
            }
        });


    }
}




