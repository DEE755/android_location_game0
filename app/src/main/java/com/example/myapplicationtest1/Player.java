package com.example.myapplicationtest1;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name; // Player's name

    private String email; //email also used as primary key
    private double latitude; // Current latitude of the player
    private double longitude; // Current longitude of the player
    private int currentScore; // Player's current score

    private String ref_to_logo;
    public static List<Player> online_playerList = new ArrayList<>();

    private int rank;

    static int player_counter=0;

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

        return player_marker;





    }

    // Constructors

    public Player(){}

    public Player(String name, double latitude, double longitude, String email) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentScore = 0; // Default score is 0
        this.email=email;
        this.Player_key =email.replace(".", "_");
        this.rank=0;
        this.is_on_map =false;



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