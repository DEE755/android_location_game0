package com.example.myapplicationtest1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
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

    private transient Bitmap profile_pic;

    @Exclude
    public Bitmap getProfile_pic() {
        return profile_pic;
    }

    @Exclude
    public void setProfile_pic(Bitmap profile_pic) {
        this.profile_pic = profile_pic;
    }


    private int score; // Player's current score



    // Getter and Setter for Current Score
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }




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

    private  List<Marker> List_of_Objects_Markers;





    private static Map<String, Marker> playerMarkerMap = new HashMap<>();


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


    public static Map<String, Marker> getPlayerMarkerMap() {
        return playerMarkerMap;
    }



    //FOR TEST PURPOSE:
    Marker Player_marker;

    private String PlayerKey;


    private boolean is_on_map;



    Marker getPlayer_marker(){
        return this.Player_marker;
    }

    public void setPlayer_marker(Marker Player_marker) {
        this.Player_marker =Player_marker;
    }




    public Marker create_player_marker(MapView mapView, Player player) {
        if (this == null) {
            Log.d("crash", "player_marker is null: ");
            return null;
        }

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
                        "Distance from me: " + distanceInMeters + " meters" + "\nScore: " + player.getScore() + "\nRank: " + player.getRank()
        );

        player_marker.setOnMarkerClickListener((marker, mapView1) -> {

            MediaPlayer mediaPlayer = MediaPlayer.create(mapView1.getContext(), R.raw.button_click);
            mediaPlayer.start();
            // Display a toast message
            Toast.makeText(mapView1.getContext(), "Player: " + this.getName() + "\nScore: " + this.getScore(), Toast.LENGTH_SHORT).show();
            Messages messages = new Messages();
            messages.input_message_and_send(MyService.getClientPlayer(), player, mapView1.getContext());
            return true;
        });

        //Profile pic logo HANDLING
        try{
            Storage_Service storage = new Storage_Service(mapView.getContext());
            storage.getPlayerImageFromStorage(player, new OnSuccessListener<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    player.setProfile_pic(bitmap);
                    //player_marker.setIcon(mapView.getContext().getResources().getDrawable(R.drawable.harry_potter_logo));
                    player_marker.setIcon(new BitmapDrawable(mapView.getContext().getResources(), player.getProfile_pic()));
                    Log.d("crash", "after logo handling: ");
                }
            });

        }


        catch(ArithmeticException e) { int resourceId = mapView.getContext().getResources().getIdentifier("default", "drawable", mapView.getContext().getPackageName());
            player_marker.setIcon(mapView.getContext().getResources().getDrawable(resourceId));}


        this.setPlayer_marker(player_marker);
//add to the java map:
        Player.playerMarkerMap.put(this.getPlayerKey(), player_marker);

        return player_marker;


    }

    public int getRank()
    {
        return rank;
    }

    // Constructors

    Player(Database db, String email, DataFetchListener listener, Context context) {
        DatabaseReference playerRef = db.get_db_ref().child("all_players").child(email.replace(".", "_"));
        Storage_Service storage = new Storage_Service(context);
        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player playerfetched = dataSnapshot.getValue(Player.class);
                if (playerfetched != null) {
                    Log.d("Player", "Player data: " + playerfetched.toString());
                    Player.this.name = playerfetched.name;
                    Player.this.latitude = playerfetched.latitude;
                    Player.this.longitude = playerfetched.longitude;
                    Player.this.score = playerfetched.score;
                    Player.this.email = email;
                    Player.this.PlayerKey = email.replace(".", "_");
                    Player.this.rank = playerfetched.rank;
                    //Player.this.is_on_map = playerfetched.is_on_map;
                    Player.this.ObjectDeliveredStatus = playerfetched.ObjectDeliveredStatus;
                    Player.this.list_of_objects_to_collect = playerfetched.list_of_objects_to_collect;
                    //Player.this.PlayerRefToMainDb = playerRef;

                    Player.this.is_active = playerfetched.is_active;
                    //Player.this.ref_to_profile_pic = playerfetched.ref_to_profile_pic;
                    Player.this.ref_to_logo = playerfetched.getPlayerKey().replace(".","_")+"_logo";

                    // Notify the listener that the data has been fetched
                    listener.onDataFetched(Player.this);
                } else { //Notify the listener that the player was not found
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

    Player(String email, String input_name, Bitmap profile_pic, Database db)
    {
        this.email=email;
        this.PlayerKey =email.replace(".", "_");
        this.name=input_name;

        this.profile_pic=profile_pic;
        this.ref_to_logo ="player_pics/"+email.toLowerCase().replace(".", "_")+"_logo";

        this.score = 0; // Default score is 0
        this.rank=0;
        this.is_on_map =false;
        this.ObjectDeliveredStatus =false;
        this.list_of_objects_to_collect =new ArrayList<>();
        this.list_of_objects_to_collect.add( new Object_to_collect());

        //this.PlayerRefToMainDb =null;
        this.is_active=true;

        player_counter++;
        ref_to_logo =email.toLowerCase().replace(" ", "_")+"_logo";
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
        this.score += points;
    }

    // Display player info
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", currentScore=" + score +
                '}';
    }


    public String getRef_to_logo() {

        if (ref_to_logo ==null){
            ref_to_logo ="default_logo";
        }
        return ref_to_logo;
    }

    public void setIs_on_map(boolean is_on_map){
        this.is_on_map = is_on_map;
    }
    public boolean  getIs_on_map(){
        return this.is_on_map;
    }

    public Object_to_collect closest_Object_to_collect()//should be nicer, use minqueue for distance ? use min detection
    {int distance, min_distance=Integer.MAX_VALUE;
        Object_to_collect closet_object = null;
        int i=0;
        for(Object_to_collect object : this.getList_of_objects_to_collect()){
            distance= Location_utils.DistanceCalculator.calculateDistance(new GeoPoint(this.getLatitude(),this.getLongitude()), object.getObjectMarker().getPosition());
            if (distance<min_distance)
            {
                min_distance=distance;
                closet_object=object;
            }
        }
        Log.d("min_distance","min dist"+ min_distance+ "min_marker" +closet_object.getObjectMarker());
        return closet_object;
    }

}