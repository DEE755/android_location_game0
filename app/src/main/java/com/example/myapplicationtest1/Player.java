package com.example.myapplicationtest1;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//TIME BASED IMPORTS
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;










public class Player {

    private String name; // Player's name

    private String email; //email also used as primary key
    private double latitude; // Current latitude of the player
    private double longitude; // Current longitude of the player
    private int currentScore; // Player's current score

    private String ref_to_logo;

    private boolean ObjectDeliveredStatus;

    public void setObjectDeliveredStatus(boolean status)
    {
        this.ObjectDeliveredStatus =status;
    }

    public boolean getObjectDeliveredStatus()
    {
        return this.ObjectDeliveredStatus;
    }
    public static List<Player> online_playerList = new ArrayList<>();

    private int rank;

    static int player_counter=0;

    private boolean is_active;

    public void setIs_active(boolean is_active)
    {
        this.is_active =is_active;
    }

    public boolean getIs_active()
    {
        return this.is_active;
    }

    private int value;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);




    //private DatabaseReference PlayerRefToMainDb;

    //public DatabaseReference getPlayerRefToMainDb(){
       /* return this.PlayerRefToMainDb;
   */

    /*public void setPlayerRefToMainDb(DatabaseReference playerRefToDb)
    {this.PlayerRefToMainDb =playerRefToDb;}*/


    //private DatabaseReference PlayerRefToOnlineDb;

    /*public void setPlayerRefToOnlineDb(DatabaseReference playerRefToDb){
        this.PlayerRefToOnlineDb = playerRefToDb;
    }

    public DatabaseReference getPlayerRefToOnlineDb(){
        return this.PlayerRefToOnlineDb;
    }*/

    //A map to store the player's key and the corresponding marker
    //it is way more efficient to store the markers in a map than in a list for searching purposes
    //(PS: the word 'Map' isn't related to any geographic map it is a data structure in java, like a dictionary in python and nothing linked to a geographic map or any GIS thing)
    // Static map to store player keys and their corresponding markers
    private static Map<String, Marker> playerMarkerMap = new HashMap<>();

    // Getters and setters for player properties
    // ...

    // Static methods to interact with the playerMarkerMap
    public static void addPlayerMarker(String key, Marker marker) {
        playerMarkerMap.put(key, marker);
    }

    private List<Object_to_collect> list_of_objects_to_collect;

    public List<Object_to_collect> getList_of_objects_to_collect() {
        return list_of_objects_to_collect;
    }

    public void setList_of_objects_to_collect(List<Object_to_collect> list_of_objects_to_collect) {
        this.list_of_objects_to_collect = list_of_objects_to_collect;
    }

    public interface DataFetchListener {
        void onDataFetched(Player player);
        void onError(DatabaseError error);
    }



    public static Map getPlayerMarkerMap() {
        return playerMarkerMap;
    }

    public static void removePlayerMarker(String key) {
        playerMarkerMap.remove(key);
    }


    //FOR TEST PURPOSE:
    Marker Player_marker =null;

    private String PlayerKey;


    private boolean is_on_map;



    Marker getPlayer_marker(){
        return this.Player_marker;
    }

    public void setPlayer_marker(Marker Player_marker) {
        this.Player_marker =Player_marker;
    }


    public Marker create_player_marker(MapView mapView, Player player) {
        Log.d("crash", "entered create_player_marker: ");
        Marker player_marker = new Marker(mapView);
        player_marker.setPosition(new GeoPoint(this.getLatitude(), this.getLongitude()));
        player_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // determining the distance from the client player
        double distanceInMeters = com.example.myapplicationtest1.Location_utils.DistanceCalculator.calculateDistance(new GeoPoint(this.getLatitude(),this.getLongitude()), new GeoPoint(player.getLatitude(), player.getLongitude()));
        Log.d("crash", "after distanceinmeters: ");
        // Customize marker title
        player_marker.setTitle(
                this.getName() +
                        "\nDistance from me: " + distanceInMeters + " meters" + "\nScore: " + this.currentScore + "\nRank: " + this.rank
        );
        player_marker.setTitle(this.getName());


        //LOGO HANDLING
        try{
            String logo_name =(player.name==null)? "default_logo":getRef_to_logo();
           player.name =(player.name==null)? "found_null":player.name;
            Log.d("crash", "logo_name: "+logo_name);

            //if(logo_name=="phone_owner(real_location_data)_logo")
            //{logo_name="harry_potter_logo";}

            logo_name="harry_potter_logo";
            int resourceId = mapView.getContext().getResources().getIdentifier(logo_name, "drawable", mapView.getContext().getPackageName());
            player_marker.setIcon(mapView.getContext().getResources().getDrawable(resourceId));
            Log.d("crash", "after logo handling: ");
        }


        catch(ArithmeticException e) { int resourceId = mapView.getContext().getResources().getIdentifier("default", "drawable", mapView.getContext().getPackageName());
            player_marker.setIcon(mapView.getContext().getResources().getDrawable(resourceId));}


        this.setPlayer_marker(player_marker);
//add to the java map:
        Player.playerMarkerMap.put(this.getPlayerKey(), player_marker);

        return player_marker;





    }

    // Constructors

     Player(Database db, String email, DataFetchListener listener) {
        DatabaseReference playerRef = db.get_db_ref().child("all_players").child(email.replace(".", "_"));
        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                if (player != null) {
                    Log.d("Player", "Player data: " + player.toString());
                    Player.this.name = player.name;
                    Player.this.latitude = player.latitude;
                    Player.this.longitude = player.longitude;
                    Player.this.currentScore = player.currentScore;
                    Player.this.email = email;
                    Player.this.PlayerKey = email.replace(".", "_");
                    Player.this.rank = player.rank;
                    Player.this.is_on_map = player.is_on_map;
                    Player.this.ObjectDeliveredStatus = player.ObjectDeliveredStatus;
                    Player.this.list_of_objects_to_collect = player.list_of_objects_to_collect;
                    //Player.this.PlayerRefToMainDb = playerRef;
                    Player.this.is_active = player.is_active;
                    Player.this.ref_to_logo = player.ref_to_logo;

                    listener.onDataFetched(Player.this);
                } else {
                    Log.d("Player", "Player not found");
                    listener.onError(DatabaseError.fromException(new Exception("Player not found")));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Player", "Error fetching player data", databaseError.toException());
                listener.onError(databaseError);
            }
        });
    }

    public Player(){}


Player(String email)
{
    this.email=email;
    this.PlayerKey =email.replace(".", "_");
    this.name=email;

    this.currentScore = 0; // Default score is 0
    this.rank=0;
    this.is_on_map =false;
    this.ObjectDeliveredStatus =false;
    this.list_of_objects_to_collect =new ArrayList<>();
    this.list_of_objects_to_collect.add( new Object_to_collect());

    //this.PlayerRefToMainDb =null;
    this.is_active=true;


    player_counter++;
    ref_to_logo=name.toLowerCase().replace(" ", "_")+"_logo";



}

    public Player(String name, double latitude, double longitude, String email, Database db) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentScore = 0; // Default score is 0
        this.email=email;
        this.PlayerKey =email.replace(".", "_");
        this.rank=0;
        this.is_on_map =false;
        this.ObjectDeliveredStatus =false;
        this.list_of_objects_to_collect =new ArrayList<>();
        this.list_of_objects_to_collect.add( new Object_to_collect());

        //this.PlayerRefToMainDb =null;
        this.is_active=true;






        player_counter++;
        ref_to_logo=name.toLowerCase().replace(" ", "_")+"_logo";


        //online_playerList.add(this);



    }





    // Getter and Setter for Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email=email;
    }

    // Getter and Setter for Latitude
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // Getter and Setter for Longitude
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Getter and Setter for Current Score
    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    // Update the player's location
    public void updateLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlayerKey(){
        return this.PlayerKey;
    }

    public void setPlayerKey(String Player_key){
        this.PlayerKey = Player_key;
    }


    // Add points to the player's score
    public void addScore(int points) {
        this.currentScore += points;
    }

    // Display player info
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", currentScore=" + currentScore +
                '}';
    }


    public String getRef_to_logo() {

        if (ref_to_logo==null){
            ref_to_logo="default_logo";
        }
        return ref_to_logo;
    }

    public void setIs_on_map(boolean online_status){
        this.is_on_map = online_status;
    }
    boolean getIs_on_map(){
        return this.is_on_map;
    }



    public void send_dead_man_check(Database db, Global_Utilities.Iterator iteration )
    {


        Runnable task = new Runnable() {
            @Override


            public void run() {

                // Update the object
                switch (iteration.increase()%2) {
                    case 0:
                        db.get_db_ref().child("online_players").child(getPlayerKey()).child("is_active").setValue(false);
                        Log.d("Player", "triggered false" + iteration.getIterator_nb());

                        break;
                    case 1:
                        db.get_db_ref().child("online_players").child(getPlayerKey()).child("is_active").setValue(true);
                        Log.d("Player" , "triggered true "+ iteration.getIterator_nb());

                        break;


                }




            }
        };

        // Schedule the task to run every 5 seconds
        scheduler.scheduleWithFixedDelay(task, 0, 5, TimeUnit.SECONDS);
    }

    public void stopPeriodicUpdate() {
        scheduler.shutdown();
    }



}