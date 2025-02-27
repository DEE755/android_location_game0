package com.example.myapplicationtest1;

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



    //constructor
    Database(String url) {
        //initialize database:
        //TODO: USE CALLBACK
        this.db_ref = FirebaseDatabase.getInstance(url).getReference();

    }

    DatabaseReference get_db_ref() {
        return this.db_ref;
    }



    void add_player_to_online_db(Player player, MapView mapview) {
        //CREATE A KEY THAT IS THE EMAIL BUT WITHOUT . BECAUSE FIREBASE DOESNT ALLOW DOTS IN KEYS
        String player_key = player.getEmail().replace(".", "_");
        db_ref.child("online_players").child(player_key).setValue(player)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listenForObjectsToCollect(player, mapview);
                        player.setIs_on_map(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Action to perform on failure
                        Log.e("Database", "Failed to add player", e);
                    }
                });
    }


    //Firebase doesnt always delete well the player
    //this method does a transaction to delete the player it works a little bit better but still not 100%
    public void removePlayerFromDatabase(String playerKey) {
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


    public void update_player_loc_db(Player clientPlayer, double userLatitude, double userLongitude)
    {
        // Update the player's location in the database
        // Create a map to store the new location
        Map<String, Object> updates = new HashMap<>();
        updates.put("latitude", userLatitude);
        updates.put("longitude", userLongitude);

        // Update the player's location in the database
        db_ref.child("online_players").child(clientPlayer.getPlayerKey()).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Databaase", "Player location updated successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Databaase", "Failed to update player location", e);
                });
    }



    public void listenForNewOnlinePlayers(MapView mapview, Player local_player) {
        DatabaseReference onlinePlayersRef = get_db_ref().child("online_players");

        onlinePlayersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Player newPlayer = dataSnapshot.getValue(Player.class);
                if (newPlayer != null) {
                    Log.d("crash", "player not null");
                    try {
                        //prevent self recognition
                    if (Objects.equals(newPlayer.getPlayerKey(), MyService.getClientPlayer().getPlayerKey()))
                    {return;}
                    } catch (Exception e) {
                        Log.d("crash", "crashing here");
                        throw new RuntimeException(e);

                    }

                    newPlayer.setPlayerKey(dataSnapshot.getKey());

                    newPlayer.setEmail(dataSnapshot.getKey().replace("_", "."));
                    Log.d("crash", "player key is: " + newPlayer.getPlayerKey());


                    newPlayer.setPlayer_marker(newPlayer.create_player_marker(mapview, newPlayer));
                    Log.d("crash", "player marker for: " + newPlayer.getPlayerKey());

                    newPlayer.setName(dataSnapshot.child("name").getValue(String.class));
                    Log.d("crash", "new player : " + newPlayer.getName());
                    Boolean isOnMap = dataSnapshot.child("is_on_map").getValue(Boolean.class);

                    //newPlayer.setIs_on_map(isOnMap != null ? isOnMap : true);
                    if (Player.online_playerList == null) {
                        Player.online_playerList = new ArrayList<>();
                        Log.d("crash", "Player list is null");
                    }

                        Player.online_playerList.add(newPlayer);
                        Log.d("crash", "Player added: " + newPlayer.getEmail());


                    // Update the map view
                    if (newPlayer.getPlayer_marker() != null) {
                        Log.d("crash", "player marker not null");
                        mapview.getOverlays().add(newPlayer.getPlayer_marker());
                        mapview.invalidate();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                Player changedPlayer = dataSnapshot.getValue(Player.class);

                if (changedPlayer != null) {
                    Log.d("crash", "player not null");
                    try {
                        //prevent self recognition
                        String has_moved_player_key = changedPlayer.getPlayerKey();
                        if (Objects.equals(has_moved_player_key, MyService.getClientPlayer().getPlayerKey())) {
                            return;
                        }

                        //GO TO THE LOCAL MARKER OF THAT SPECIFIC PLAYER TO CHANGE ITS POSITION
                        Marker has_moved_player_marker = Player.getPlayerMarkerMap().get(has_moved_player_key);
                        if (has_moved_player_marker == null) {
                            Log.d("crash", "player marker is null");
                            return;
                        }
                        GeoPoint new_position=new GeoPoint(changedPlayer.getLatitude(), changedPlayer.getLongitude());
                        GeoPoint local_player_loc=new GeoPoint(MyService.getClientPlayer().getLatitude(), MyService.getClientPlayer().getLongitude());
                        has_moved_player_marker.setPosition(new_position);
                        //TODO MAKE THIS FUNCTIONAL:
                        has_moved_player_marker.setTitle(changedPlayer.getName()+" Distance from me:" + Location_utils.DistanceCalculator.calculateDistance(local_player_loc, new_position));
                    } catch (Exception e) {
                        Log.d("crash", "crashing here");
                        throw new RuntimeException(e);

                    }
                }
            }


                    @Override
                    public void onChildRemoved (DataSnapshot dataSnapshot){
                        mapview.getOverlays().remove(Player.getPlayerMarkerMap().get(dataSnapshot.getKey()));
                        //delete the java map object
                        Player.getPlayerMarkerMap().remove(dataSnapshot.getKey());
                        //update the geographic map
                        mapview.invalidate();

                    }

                    @Override
                    public void onChildMoved (DataSnapshot dataSnapshot, String previousChildName){
                ///TODO: Check if this is necessary and if it ever gets called
                        Log.d("Database", "Player moved: " + dataSnapshot.getKey());
                        GeoPoint new_coordinates = new GeoPoint(dataSnapshot.child("latitude").getValue(Double.class), dataSnapshot.child("longitude").getValue(Double.class));
                        Marker marker_to_update = Player.getPlayerMarkerMap().get(dataSnapshot.getKey());
                        marker_to_update.setPosition(new_coordinates);
                    }

                    @Override
                    public void onCancelled (DatabaseError databaseError){
                        Log.e("Database", "Error listening for new online players", databaseError.toException());
                    }

        });
    }

    public void add_message_to_db(String destination_player, String message){
        this.db_ref.child("online_players").child(destination_player).child("messages").setValue(message);
        //TODO: ADD A LISTENER FOR SUCCESSFULLY SENT MESSAGE (OPT) (DB +DEST PLAYER)

    }

    //when it detects that the list of objects' been added to the localPlayer
    //cascade of listening
    public void listenForObjectsToCollect(Player localPlayer, MapView mapview) {
        DatabaseReference objectsRef = get_db_ref().child("online_players").child(localPlayer.getPlayerKey()).child("list_of_objects_to_collect");
Log.d("prints", "db ref for objects of " + localPlayer.getName() + " is :" +objectsRef);
        objectsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Object_to_collect object = dataSnapshot.getValue(Object_to_collect.class);
                Bonus_Objects bonus_object;



                // Update the field "ObjectDelivered" to true:
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


                    for(Object_to_collect obj: localPlayer.getList_of_objects_to_collect())

                    {//all the objects to collect receive their on marker in the field object_marker
                        if (obj.id == 10 && !Bonus_Objects.getIsSetUp())
                        {//sets the bonus object accordingly to id =10 (random since the whole list is random)
                            //cpy constructor
                            obj=new Bonus_Objects(obj);
                            Bonus_Objects.setIs_set_up(true);
                            Log.d("objects", "bonus object=");
                        }
                        //POLYMORPHISM: will use the functions of the bonus object if bonus and regular if regular
                        obj.prepare_object(mapview, localPlayer);
                        obj.set_object_marker_on_map(mapview);
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

    public void addPlayerIfNotExists(Player clientPlayer) {
        Log.d("Databasse", "Adding player to all_players: " + clientPlayer.getPlayerKey());
        String key = clientPlayer.getPlayerKey();

        DatabaseReference dbPlayerRef = db_ref.child("all_players").child(key);

        Log.d("Databasse", "Checking" + dbPlayerRef);

        dbPlayerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Player does not exist, add to the database
                    dbPlayerRef.setValue(clientPlayer)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Databasse", "Player added to all_players: " + clientPlayer.getName());
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Databasse", "Failed to add player to all_players", e);
                            });
                } else {
                    Log.d("Databasse", "Player already exists in all_players: " + clientPlayer.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Database", "Error checking if player exists in all_players", databaseError.toException());
            }
        });
    }

}
