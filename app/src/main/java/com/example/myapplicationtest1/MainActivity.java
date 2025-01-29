package com.example.myapplicationtest1;

import static com.example.myapplicationtest1.Location_utils.getUserLatitude;
import static com.example.myapplicationtest1.Location_utils.getUserLongitude;
import static com.example.myapplicationtest1.Location_utils.setUserLatitude;
import static com.example.myapplicationtest1.Location_utils.setUserLongitude;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationtest1.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;


//TODO//TODO IT SEEMS THAT THE WRONG PLAYER IS REMOVED FROM THE DATABASE THEN IT IS CREATED/DELETED AND SOME STAYS IN THE DATABASE
//TODO DELETING ALL AT ONCE SEEMS NOT TO WORK
//WEIRD THING THE PLAYERS ARE ADDED EVEN IF THERE IS NO INTERNET ACCESS: CHECK CODE

//FIREBASE:


//SOUND




public class MainActivity extends AppCompatActivity {


    private static final org.apache.commons.logging.Log log = LogFactory.getLog(MainActivity.class);
    private static boolean PlayerExistedBefore;

    public static boolean getPlayerExistedBefore() {
        return PlayerExistedBefore;
    }

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

    //callback for results from server:
    public interface ValueCallback<T> {
        void onValueReceived(T value);
    }


    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;

    // Member variables to store local latitude and longitude


    private boolean executed = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private Button MainButton;

    private MediaPlayer mediaPlayer;

    private ProgressBar locationProgressBar;
    private MapView mapview =null;

    public MapView getMapview() {
        return mapview;
    }
    private IMapController MapController;
    private Marker userMarker;

    private boolean MarkersCreatedFlag =false;
    private boolean GameStartedFlag =false;


    private List<Marker> markerList = new ArrayList<>();

    private GeoPoint myCurrentPoint=new GeoPoint(0.0,0.0);

    private double zoom_speed=1000L;

    private int current_iteration=0;

    private static Database mDatabase= new Database("https://android-location-game0-default-rtdb.europe-west1.firebasedatabase.app");

    public static Database getmDatabase() {
        return mDatabase;
    }



    //private Player client_player=new Player("Phone_owner(real_location_data)",0,0,"test@hit.ac.il",mDatabase); //this is the 'client' player (the device owner)

    private int iteration;

    //TEST PLAYERS:
    //we need to check why they appears at same location as the client player


    private Player Michael_Jackson_player =new Player("Michael Jackson",32.0240,34.7741,"mjmusic@sony.com",mDatabase);

    private Player Spiderman_player =new Player("Peter Parker",32.0853,34.7818,"peter_parker@gmail.com",mDatabase);

    private Player Batman_player =new Player("Bruce Wayne",31.8771,34.7385,"bruce-wayne@ghotam.com",mDatabase);

    private Player Superman_player =new Player("Clark Kent",32.01205,34.76,"Clark-Kent@dc.com",mDatabase);

    private Player Harry_Potter_player =new Player("Harry Potter",32.015,34.774,"hp@hogwart.uk",mDatabase);

    private Player Mary_Poppins_player =new Player("Mary Poppins",32.025,34.77,"marry_popping@londonmagicservice.uk",mDatabase);


    private double test_step = 0.00001;//for testing players moving
    //iterator for simulation:
    int p=0;



    protected BottomNavigationView navView;

    public BottomNavigationView getNavView() {
        return navView;
    }

    void setNavView(BottomNavigationView navView) {
        this.navView = navView;
    }

    protected AppBarConfiguration appBarConfiguration;

    public AppBarConfiguration getAppBarConfiguration() {
        return appBarConfiguration;
    }

    void setAppBarConfiguration(AppBarConfiguration appBarConfiguration) {
        this.appBarConfiguration = appBarConfiguration;
    }




    //private FirebaseAnalytics mFirebaseAnalytics;


    private void show_current_position(double zoom_factor, Long speed){//both displays and output it


        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapview);

// Enable the MyLocation overlay
        myLocationOverlay.enableMyLocation();

// Optionally, enable following the user's location
        myLocationOverlay.enableFollowLocation();

        //change_icon:
        //Drawable customIcon = getResources().getDrawable(R.drawable.bus_small, null);
        Bitmap userIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_logo_png2);
        myLocationOverlay.setPersonIcon(userIconBitmap);

        //myLocationOverlay.setPersonIcon();
// Add the overlay to the map
        mapview.getOverlays().add(myLocationOverlay);

// Set the map controller to zoom and animate to the user's location when it's obtained
        IMapController mapController = mapview.getController();
        //mapController.setZoom(zoom_factor);

            mapController.animateTo(new GeoPoint(32.08, 34.7), zoom_factor, speed);

    }
    private int l=0;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize UI components
        MainButton = findViewById(R.id.MainButton);
        mediaPlayer = MediaPlayer.create(this, R.raw.sound1);




        // Setup Bottom Navigation
         navView = binding.navView; // Use binding to avoid redundancy
         appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_login, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);



        // Start the service class
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);




        // Set button initially disabled until location is fetched
        MainButton.setEnabled(false);
        MainButton.setText("Fetching Location...");


         //navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_login) {
                    if (!LogIn.getPlayerLoggedIn()) {
                        //makes crash we dont know why
                        Intent intent = new Intent(MainActivity.this, Disconnect.class);
                        startActivity(intent);
                        return true;
                    }
                }

                return false;
            }

            private void start_activity_login() {
                    Intent intent = new Intent(MainActivity.this, LogIn.class);
                    startActivity(intent);
                }



        });

        // Handle button click
        MainButton.setOnClickListener(v -> {
           // if (mediaPlayer != null) {
                //mediaPlayer.start();}



            //Utilities initialization:
            // Inside onCreate method
            Global_Utilities utilities = new Global_Utilities();
            Global_Utilities.Iterator iteration = new Global_Utilities.Iterator();

                //Sound.ToneGenerator Tonegen=new Sound.ToneGenerator();
                Handler handler = new Handler(Looper.getMainLooper());

                //Tonegen.toneGenerator(200,4000,(int)zoom_speed/1000);
                //String locationText = "Latitude: " + userLatitude + "\nLongitude: " + userLongitude;

                if(GameStartedFlag==false) {
                    show_current_position(15.0, 1000L);
                    Sound.ToneGenerator.toneGenerator(400, 4000, (int) zoom_speed / 1000);
                    GameStartedFlag=true;
                    // Reset button text after 4 seconds
                    MainButton.setText("GAME STARTING");


                    //ACTUAL LOCAL PLAYER:
                    //first loc is sent to the server after pressing the start button then updated here
                    MyService.getClientPlayer().setLatitude(getUserLatitude());
                    MyService.getClientPlayer().setLongitude(getUserLongitude());


                    // Adding the client_player to the game db

                        Player clientPlayer0 = MyService.getClientPlayer();

                        Log.d("Client Player", "Client player: " + clientPlayer0);
                        if (clientPlayer0 != null) {
                            if(PlayerExistedBefore==false){
                                Log.d("ClientPlayer", "Client player: " + "Player didnt existed before" + PlayerExistedBefore);mDatabase.addPlayerIfNotExists(clientPlayer0);
                            }

                            mDatabase.add_player_to_online_db(clientPlayer0, mapview);


                            //USING BOTH MAKES CRASH !! even after completion
                            //but doing allplayers then only the client player makes crash




                        }


                    //update the test players marker:

                    //client_player.setPlayerref_to_db(mDatabase.get_db_ref().child("online_players").child(client_player.getPlayer_key()));


                    //TODO: we probably need a listener or a flag to check that the list of objects from the server is ready when this line happens
                    //fetch objects locations for the client player
                    //replaced by listener actions
                    //client_player.fetchObjectsToCollect(mDatabase);


                    handler.postDelayed(() -> MainButton.setText("READY TO CATCH ITEM"), 2000);
                }

                else {

                    //update distance from bus:
                    updateMarkerDistances();

                    Marker min_marker=Location_utils.closest_marker();
                    int distance=Location_utils.DistanceCalculator.calculateDistance(min_marker.getPosition(), Location_utils.getMyCurrentGeoPoint());
                    Log.d("distance", String.valueOf(min_marker.getPosition()));
                    if (distance <= 300 + 960-240){//TODO: maybe doesnt work in term of distance {
                        MainButton.setText("CONGRATS\n +100POINTS");
                        Sound.ToneGenerator.toneGenerator(4000, 500, (int) zoom_speed / 1000);
                        MyService.getClientPlayer().addScore(100);
                        //MyService.getClientPlayer().getPlayerRefToDb().child("currentScore").setValue(MyService.getClientPlayer().getCurrentScore());

                        //make crash
                        Log.d("db_ref", String.valueOf(mDatabase.get_db_ref()));
                        mDatabase.get_db_ref().child("online_players").child(MyService.getClientPlayer().getPlayerKey()).child("currentScore").setValue(MyService.getClientPlayer().getCurrentScore());

                        //set logo to validation color
                        min_marker.setIcon(mapview.getContext().getResources().getDrawable(R.drawable.bus_logo));
                        //ADD A FLAG TO ALREADY_TAKEN or remove from db
//TODO either add on the object + update full object or add on db the increase of score
                        //TODO TODO: PREVENT THE SAME ITEM TO BE TAKEN MORE THEN ONE TIME ADD A FLAG


                    } else {
                        MainButton.setText("NO ITEM HERE");
                        Sound.ToneGenerator.toneGenerator(800, 500, (int) zoom_speed / 1000);
                        handler.postDelayed(() -> MainButton.setText("READY TO CATCH ITEM"), 2000);
                        Log.d("distance" , String.valueOf(Location_utils.DistanceCalculator.calculateDistance(min_marker.getPosition(), Location_utils.getMyCurrentGeoPoint())));
                    }

                }

                //ANALYTICS:
            //mFirebaseAnalytics.logEvent("button_clickj", null);
        });



        // Check and request location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed with getting location
            fetchLocation();
        }

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //inflate and create the map
        //setContentView(R.layout.activity_main);// makes crashing

        mapview = findViewById(R.id.map);
        mapview.setTileSource(TileSourceFactory.MAPNIK);


        show_current_position(9.0,1000L);


    }//oncreateend


    private void fetchLocation() {
        getCurrentLocation(new MyLocationCallback() {
            @Override
            public void onLocationResult(LocationData locationData) {
                setUserLatitude(locationData.getLatitude());
                setUserLongitude(locationData.getLongitude());




                if(MarkersCreatedFlag ==false){
                    MainButton.setText("START GAME");
                    MainButton.setEnabled(true);
                    InputStream inputStream = getResources().openRawResource(R.raw.bus_holon_en); // Place CSV in res/raw folder
                    //Bus markers and put them on the mapview
                    //create_all_bus_Markers(inputStream);

//TODO: SOMETHING MAKES CRASH VERIFY THE ORDER OF HOW THINGS HAPPENS PROBABLY ORDER WRONG THEN SOMETHING IS NULL
                           // there is the fetchObjectsToCollect method that is called before






                    //The players are fetch from the DB to a java_player object
                    //This is a bit complicated to understand: ASYNCHRONOUS BEHAVIOR CALLBACKS
                    //we pass the mapview to be able to create the markers

                    //put all Players in the list online_playerList
                    //the list_online_playerList is reconstituted every time some of the data changes in the database


                    //for (Player player : Player.online_playerList){//it is empty!! tha's because the result werent ready

                        //the markers now are already created and placed on the field of the Player

                        //Location_utils.create_and_place_player_marker(player, mapview, client_player);
                        // add the marker to the map:


                        //mapview.getOverlays().add(player.getPlayer_marker());
                        //PlayersmarkerList.add(player_marker);

                        // Refresh the map
                        //mapview.invalidate();
                   // }
                MarkersCreatedFlag =true;
                }

                else {

                    //mDatabase.update_player_loc_db(MyService.getClientPlayer(), getUserLatitude(),getUserLongitude());


                    //Location_utils.updatePlayersMarkers();

                    mapview.invalidate();
                }


                //SIMULATION OF PLAYERS ENTERING THE GAME

                   // switch (p){
                        //case 15:
                          //  mDatabase.add_player_to_online_db(Spiderman_player,mapview);

                          //  Log.d("MainActivity", "Michael J entered the online  db");
                        //    break;

                    //    case 30:
                         //   mDatabase.add_player_to_online_db(Michael_Jackson_player, mapview);

                         //   break;

                       // case 50:
                        //    mDatabase.removePlayerFromDatabase(Michael_Jackson_player.getEmail());

                        //    Log.d("MainActivity", "Spiderman entered the online db");
                        //    break;


                      //  case 75:
                            //mDatabase.add_player_to_db(Batman_player, mapview);
                      //      Log.d("MainActivity", "New player entered the online db");
                      //      break;


                      //  case 85:mDatabase.removePlayerFromDatabase(Spiderman_player.getEmail());
                       //     break;

                      //  case 150:

                         //   mDatabase.add_player_to_online_db(Superman_player, mapview);


                         //   Log.d("MainActivity", "New player entered the online db");
                          //  break;

                       // case 200:
                         //   mDatabase.add_player_to_online_db(Harry_Potter_player, mapview);
                         //   Log.d("MainActivity", "New player entered the online db");
                         //   break;

                       // case 220:
                           // mDatabase.removePlayerFromDatabase(Superman_player.getEmail());
                           // break;
                        //case 250:
                            //mDatabase.add_player_to_online_db(Mary_Poppins_player, mapview);

                            //Log.d("MainActivity", "New player entered the online db");
                            //break;

                        //case 270: mDatabase.removePlayerFromDatabase(Mary_Poppins_player.getEmail());

                        //case 300:
                            //mDatabase.removePlayerFromDatabase(Harry_Potter_player.getEmail());

                            // Remove the 'online_players' node
                            /*mDatabase.get_db_ref().child("online_players").removeValue()
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
                                });*/
                //break;
                    }


            //p=p%330 +1;


                //SIMULATION: UPDATE test_players location
                //if (!Player.online_playerList.isEmpty()) {
                    //for (Player test_player : Player.online_playerList) {
                        //test_player.setLatitude(test_player.getLatitude() + test_step);
                        //test_player.setLongitude(test_player.getLongitude() - test_step);
                        //add here a if the player is still in the game, then because else it creates a new partial player thats bad.
                        //mDatabase.update_player_loc_db(test_player, test_player.getLatitude()+ test_step, test_player.getLongitude()- test_step);
                    //}
                //}


           // }

            @Override
            public void onError(String errorMsg) {
                Log.e("Location", errorMsg);
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();


            }
        });
    }





    // Method to get current location : USELESS SINCE AUTOMATIC WITH OSM (just get the numbers out)
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

    // Handle the result of permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean fineLocationGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean coarseLocationGranted = grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (fineLocationGranted || coarseLocationGranted) {
                    // Permissions granted, proceed with fetching location
                    fetchLocation();
                } else {
                    // Permissions denied, disable location functionality
                    Toast.makeText(this, "Location permissions denied.", Toast.LENGTH_SHORT).show();
                    MainButton.setText("Location Disabled");
                }
            }
        }
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
    }


}
