package com.example.myapplicationtest1;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name; // Player's name

    private String email; //email also used as primary key
    private double latitude; // Current latitude of the player
    private double longitude; // Current longitude of the player
    private int currentScore; // Player's current score

    public static List<Player> test_playerList;


    private int rank;

    static int player_counter=0;
    // Constructors

    public Player(){}

    public Player(String name, double latitude, double longitude, String email) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentScore = 0; // Default score is 0
        this.rank=0;
        this.email=email;
        player_counter++;

        test_playerList = new ArrayList<>();
        test_playerList.add(this);

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
}