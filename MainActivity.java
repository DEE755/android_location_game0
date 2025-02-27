package com.example.myapplicationtest1;

import static com.example.myapplicationtest1.Location_utils.getUserLatitude;
import static com.example.myapplicationtest1.Location_utils.getUserLongitude;
import static com.example.myapplicationtest1.Location_utils.setUserLatitude;
import static com.example.myapplicationtest1.Location_utils.setUserLongitude;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationtest1.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

//OSM
import org.apache.commons.logging.LogFactory;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;






public class MainActivity extends AppCompatActivity {


    private static final org.apache.commons.logging.Log log = LogFactory.getLog(MainActivity.class);
    private static boolean PlayerExistedBefore;

    public static void setPlayerExistedBefore(boolean existedBefore) {
        MainActivity.PlayerExistedBefore = existedBefore;
    }


    // Inner class to hold location data
    public class LocationData {
        private double latitude;
        private double longitude;

        public LocationData(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        // Getters
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }

    }

    //CALLBACKS FOR ASYNCHRONOUS OPERATIONS:

    // Callback interface for location results
    public interface MyLocationCallback {
        void onLocationResult(LocationData locationData);
        void onError(String errorMsg);
    }


    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;



    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Button MainButton;

    private TextView Score_label;

    private MediaPlayer mediaPlayer;

    private MapView mapview =null;



    private boolean game_started_flag =false;
    private boolean GameStartedFlag =false;


    private List<Marker> markerList = new ArrayList<>();


    private double zoom_speed=1000L;



    private static Database mDatabase= new Database("https://android-location-game0-default-rtdb.europe-west1.firebasedatabase.app");

    public static Database getmDatabase() {
        return mDatabase;
    }



    protected BottomNavigationView navView;


    protected AppBarConfiguration appBarConfiguration;


    public static boolean new_message_flag;


    //fragments:
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    LeaderBoard leaderboardFragment;
    FrameLayout fragmentContainer;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private final int interval = 2000; // Interval in milliseconds (e.g., 5000ms = 5 seconds)

    private void start_executing_time_operations() {
        //includes fetching location of other player and updating the player location
        //includes checking new messages and toast them
        runnable = new Runnable() {
            @Override
            public void run() {
                // Code to execute every x seconds
                fetchLocation();
                if (Messages.last_message!=null && new_message_flag)
                {
                    Toast.makeText(MainActivity.this, "You got a new message: " + Messages.last_message, Toast.LENGTH_LONG).show();
                    
                    //TODO REPLACE WITH SOUND MESSAGE
                    MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.sound_bonus2);
                    mediaPlayer.start();
                    new_message_flag=false;


                }
                //TODO check this next line again
                if (MyService.getClientPlayer().getIs_on_map())
                {
                    mDatabase.update_player_loc_db(MyService.getClientPlayer(), getUserLatitude(), getUserLongitude());
                }
                //update distance from bus:
                updateMarkerDistances();


                // Re-post the runnable with a delay of x seconds
                handler.postDelayed(this, interval);
            }
        };
        handler.post(runnable);
    }



    private void stop_cycling_fetching_location() {
        handler.removeCallbacks(runnable);
    }



     public void show_current_position(double zoom_factor, Long speed, Bitmap pic){//both displays and output it


        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapview);

// Enable the MyLocation overlay
        myLocationOverlay.enableMyLocation();

// enable following the user's location
        myLocationOverlay.enableFollowLocation();


        try {
            Bitmap userIconBitmap = MyService.getClientPlayer().getProfile_pic();
            if (userIconBitmap != null) {
                runOnUiThread(() -> myLocationOverlay.setPersonIcon(userIconBitmap));
            }
        }
        catch (Exception e)
        {
            Log.e("error", String.valueOf(e));
        }

// Add the overlay to the map
        mapview.getOverlays().add(myLocationOverlay);

// Set the map controller to zoom and animate to the user's location when it's obtained
        IMapController mapController = mapview.getController();

        mapController.animateTo(new GeoPoint(getUserLatitude(), getUserLongitude()), zoom_factor, speed);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Initialize UI components
        MainButton = findViewById(R.id.MainButton);
        Score_label = findViewById(R.id.Score_Label);
        Score_label.setText(MyService.getClientPlayer().getName()+ "-Score: 0");
        Score_label.setBackgroundColor(Color.BLUE);


        // Setup Bottom Navigation
         navView = binding.navView; // Use binding to avoid redundancy
         appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_game, R.id.navigation_login, R.id.navigation_leaderboards)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);


        // Set button initially disabled until location is fetched
        MainButton.setEnabled(false);
        MainButton.setText("Fetching Location...");


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_game) {
                    if (leaderboardFragment!=null)
                    {   fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        //fragmentTransaction.remove(leaderboardFragment);

                        fragmentTransaction.hide(leaderboardFragment);
                        fragmentTransaction.commit();
                        leaderboardFragment.getMediaPlayer().pause();
                    }
                    return true;
                }

                if (item.getItemId() == R.id.navigation_login) {
                    if (!LogIn.getPlayerLoggedIn()) {

                        Intent intent = new Intent(MainActivity.this, Disconnect.class);
                        startActivity(intent);

                        return true;
                    }

                }

                if (item.getItemId() == R.id.navigation_leaderboards)
                {

                    // Add the fragment to the 'fragment_container' FrameLayout
                    if (leaderboardFragment == null) {
                        Log.d("nav", "was null" );
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        leaderboardFragment = new LeaderBoard();
                        fragmentTransaction.replace(R.id.fragment_leaderboard, leaderboardFragment);
                        fragmentTransaction.commit();
                        fragmentContainer = findViewById(R.id.fragment_leaderboard);
                        fragmentContainer.bringToFront();
                        //leaderboardFragment.getView().bringToFront();
                    }
                    else {
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.show(leaderboardFragment);
                        fragmentTransaction.commit();
                        leaderboardFragment.getMediaPlayer().start();
                        fragmentContainer = findViewById(R.id.fragment_leaderboard);
                        fragmentContainer.bringToFront();


                    }

                    return true;

                }


                return false;
            }





        });

        //start listening for new messages
        Messages msg_init=new Messages();
        msg_init.start_listening_for_messages();


        // Handle button click
        MainButton.setOnClickListener(v -> {

            //set handler for delayed actions
                Handler handler = new Handler(Looper.getMainLooper());

                if(GameStartedFlag==false) {
                    show_current_position(15.0, 1000L,null);
                    Sound.ToneGenerator.toneGenerator(400, 4000, (int) zoom_speed / 1000);
                    GameStartedFlag=true;
                    // Reset button text after 4 seconds
                    MainButton.setText("GAME STARTING");
                    Toast.makeText(this,"Your items are being fetch, please wait", Toast.LENGTH_LONG).show();

                    MyService.getClientPlayer().setLatitude(getUserLatitude());
                    MyService.getClientPlayer().setLongitude(getUserLongitude());

                    // Adding the client_player to the game db
                        Player clientPlayer0 = MyService.getClientPlayer();

                        if (clientPlayer0 != null) {
                            if(!PlayerExistedBefore){
                                Log.d("ClientPlayer", "Client player: " + "Player didnt existed before" + PlayerExistedBefore);
                                mDatabase.addPlayerIfNotExists(clientPlayer0);
                            }
                            mDatabase.add_player_to_online_db(clientPlayer0, mapview);
                            Score_label.setText(clientPlayer0.getName()+ "- Score: " + MyService.getClientPlayer().getScore());
                        }


                    handler.postDelayed(() -> MainButton.setText("CATCH ITEMS"), 2000);
                }

                else {


                    Object_to_collect closest_item=MyService.getClientPlayer().closest_Object_to_collect();
                    int distance=Location_utils.DistanceCalculator.calculateDistance(closest_item.getObjectMarker().getPosition(), Location_utils.getMyCurrentGeoPoint());
                    Log.d("distance", String.valueOf(closest_item.getObjectMarker().getPosition()));
                    if (distance <= 12){

                        MainButton.setText("CONGRATS\n +100POINTS");
                        Sound.ToneGenerator.toneGenerator(4000, 500, (int) zoom_speed / 1000);

                        MyService.getClientPlayer().addScore(100);

                        mDatabase.get_db_ref().child("online_players").child(MyService.getClientPlayer().getPlayerKey()).child("score").setValue(MyService.getClientPlayer().getScore());

                        mDatabase.get_db_ref().child("all_players").child(MyService.getClientPlayer().getPlayerKey()).child("score").setValue(MyService.getClientPlayer().getScore());


                        //set logo to validation color
                        closest_item.getObjectMarker().setIcon(mapview.getContext().getResources().getDrawable(R.drawable.bus_logo));
                        //remove the taken object from the list
                        MyService.getClientPlayer().getList_of_objects_to_collect().remove(closest_item);

                        Score_label.setText("Score: " + MyService.getClientPlayer().getScore());

                        handler.postDelayed(() -> MainButton.setText("CATCH MORE ITEMS"), 2000);
                    }
                    else {
                        MainButton.setText("NO ITEM HERE");
                        Sound.ToneGenerator.toneGenerator(800, 500, (int) zoom_speed / 1000);
                        handler.postDelayed(() -> MainButton.setText("CATCH ITEMS"), 2000);
                        Log.d("distance to closest obj: " , String.valueOf(Location_utils.DistanceCalculator.calculateDistance(closest_item.getObjectMarker().getPosition(), Location_utils.getMyCurrentGeoPoint())));
                    }

                }

        });

        // Check and request location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d("MainAcctivity", "Location permission already granted");

            //operations that repeat every x seconds
            start_executing_time_operations();
        }

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //inflate and create the map
        mapview = findViewById(R.id.map);
        mapview.setTileSource(TileSourceFactory.MAPNIK);

        show_current_position(9.0,1000L,null);

        Button Quit_button=findViewById(R.id.quit_button);
        Quit_button.setBackgroundColor(Color.RED);

        Quit_button.setOnClickListener(v -> {
            MyService.onQuitApp(MyService.getClientPlayer(),mDatabase);
            finish();
        });

        //start listening for new online players
        mDatabase.listenForNewOnlinePlayers(mapview,MyService.getClientPlayer());

    }//oncreateend

    @Override
    protected void onPause() {//NEVER CALLED
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
    Log.d("MainActivityP", "onPause called");
            if (true)
            {
                MyService.onQuitApp(MyService.getClientPlayer(),mDatabase);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();

        }
    }


    public void fetchLocation() {
        getCurrentLocation(new MyLocationCallback() {
            @Override
            public void onLocationResult(LocationData locationData) {
                setUserLatitude(locationData.getLatitude());
                setUserLongitude(locationData.getLongitude());
                MyService.getClientPlayer().setLatitude(getUserLatitude());
                MyService.getClientPlayer().setLongitude(getUserLongitude());


                if (!game_started_flag) {
                    MainButton.setText("START GAME");
                    MainButton.setEnabled(true);
                    MainButton.setBackgroundColor(Color.GREEN);
                    game_started_flag = true;

                } else {

                    if (MyService.getClientPlayer().getIs_on_map())
                    {
                        mDatabase.update_player_loc_db(MyService.getClientPlayer(), getUserLatitude(), getUserLongitude());
                    }

                    //Update the map view
                    mapview.invalidate();
                }

            }

            @Override
            public void onError(String errorMsg) {
                Log.e("Location", errorMsg);
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    double lat;
    double lng;
    private void getCurrentLocation(MyLocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions are not granted; handle accordingly
            callback.onError("Location permissions are not granted.");
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        LocationData locationData = new LocationData(lat, lng);
                        callback.onLocationResult(locationData);
                    } else {
                        callback.onError("Can't fetch location.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to get location: " + e.getMessage());
                });
    }


    private void updateMarkerDistances() {
        GeoPoint myCurrentPoint = new GeoPoint(getUserLatitude(), getUserLongitude());
        for (Marker marker : markerList) {
            GeoPoint tempPoint = marker.getPosition();
            double distance = Location_utils.DistanceCalculator.calculateDistance(tempPoint, myCurrentPoint);
            String time=getCurrentTime();
            marker.setTitle("Bus Station - Distance: " + distance + " meters\n"+"Last Update" + time);
        }
        mapview.invalidate();
    }


    public String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); // Thread-safe if used locally
        Date date = new Date();
        return formatter.format(date);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Log.d("MainActivity", "onDestroy called");
        Intent serviceIntent = new Intent(this, MyService.class);
        stopService(serviceIntent);
        stop_cycling_fetching_location();
    }

}
