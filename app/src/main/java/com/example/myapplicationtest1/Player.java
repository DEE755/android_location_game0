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

    private DatabaseReference PlayerRefToDb;

    public DatabaseReference getPlayerRefToDb(){
        return this.PlayerRefToDb;
    }

    public void setPlayerRefToDb(DatabaseReference playerRefToDb)
    {this.PlayerRefToDb =playerRefToDb;}



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

    public void fetchObjectsToCollect(Database db) {
        // Fetch objects to collect from the database
        List<Object_to_collect> objects = new ArrayList<>();
        DatabaseReference objectsRef = db.getPlayerRef().getRef().child("objects_to_collect");

        objectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Object_to_collect> objects = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Object_to_collect object = snapshot.getValue(Object_to_collect.class);
                    objects.add(object);
                }
                setList_of_objects_to_collect(objects);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Player", "Error fetching objects to collect", databaseError.toException());
            }
        });
    }

    public static Map getPlayerMarkerMap() {
        return playerMarkerMap;
    }

    public static void removePlayerMarker(String key) {
        playerMarkerMap.remove(key);
    }


    //FOR TEST PURPOSE:
    Marker Player_marker =null;

    private String Player_key;


    private boolean is_on_map;



    Marker getPlayer_marker(){
        return this.Player_marker;
    }

    public void setPlayer_marker(Marker Player_marker) {
        this.Player_marker =Player_marker;
    }


    public Marker create_player_marker(MapView mapView, Player player) {
        Marker player_marker = new Marker(mapView);
        player_marker.setPosition(new GeoPoint(this.getLatitude(), this.getLongitude()));
        player_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // determining the distance from the client player
        double distanceInMeters = DistanceCalculator.calculateDistance(new GeoPoint(this.getLatitude(),this.getLongitude()), new GeoPoint(player.getLatitude(), player.getLongitude()));

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
        if (player.name=="Phone_owner(real_location_data)"){logo_name="default_logo";}
        int resourceId = mapView.getContext().getResources().getIdentifier(logo_name, "drawable", mapView.getContext().getPackageName());
        player_marker.setIcon(mapView.getContext().getResources().getDrawable(resourceId));}

catch(ArithmeticException e) { int resourceId = mapView.getContext().getResources().getIdentifier("default", "drawable", mapView.getContext().getPackageName());
    player_marker.setIcon(mapView.getContext().getResources().getDrawable(resourceId));}


        this.setPlayer_marker(player_marker);
//add to the java map:
        Player.playerMarkerMap.put(this.getPlayer_key(), player_marker);

        return player_marker;





    }

    // Constructors

    public Player(){}

    public Player(String name, double latitude, double longitude, String email, Database db) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentScore = 0; // Default score is 0
        this.email=email;
        this.Player_key =email.replace(".", "_");
        this.rank=0;
        this.is_on_map =false;
        this.ObjectDeliveredStatus =false;
        this.list_of_objects_to_collect =new ArrayList<>();
        this.list_of_objects_to_collect.add( new Object_to_collect());

        this.PlayerRefToDb =null;
        //Delete later
        try
        {
            //this.playerRef_to_db = db.get_db_ref().child("online_players").child(email);
        }

        catch(ArithmeticException e)//it does go there at some point lets understand why
        {
            Log.e("Player", "Error in setting playerRef_to_db");
        }






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

    public String getPlayer_key(){
        return this.Player_key;
    }

    public void setPlayer_key(String Player_key){
        this.Player_key = Player_key;
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


}