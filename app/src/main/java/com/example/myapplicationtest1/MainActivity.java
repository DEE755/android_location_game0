package com.example.myapplicationtest1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationtest1.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


import java.io.BufferedReader;
import java.io.InputStreamReader;



//FIREBASE:


//SOUND




public class MainActivity extends AppCompatActivity {


    private static final org.apache.commons.logging.Log log = LogFactory.getLog(MainActivity.class);

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

    // Member variables to store latitude and longitude
    private double userLatitude;
    private double userLongitude;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private Button playsoundButton;
    private MediaPlayer mediaPlayer;

    private ProgressBar locationProgressBar;
    private MapView mapview =null;

    private IMapController MapController;
    private Marker userMarker;

    private boolean MarkersCreatedFlag =false;
    private boolean GameStartedFlag =false;


    private List<Marker> markerList = new ArrayList<>();

    private GeoPoint myCurrentPoint=new GeoPoint(0.0,0.0);

    private double zoom_speed=1000L;

    private int current_iteration=0;

    private Database mDatabase= new Database("https://android-location-game0-default-rtdb.europe-west1.firebasedatabase.app");

    private Player client_player=new Player("Phone_owner(real_location_data)",0,0,"test@hit.ac.il"); //this is the 'client' player (the device owner)



    //TEST PLAYERS:


    private Player Michael_Jackson_player =new Player("Michael Jackson",32.0153,34.7741,"mjmusic@sony.com");

    private Player Spiderman_player =new Player("Peter Parker",32.0150,34.7720,"peter_parker@gmail.com");

    private Player Batman_player =new Player("Bruce Wayne",32.0158,34.774,"bruce-wayne@ghotam.com");

    private Player Superman_player =new Player("Clark Kent",32.0158,34.76,"Clark-Kent@dc.com");

    private Player Harry_Potter_player =new Player("Harry Potter",32.015,34.774,"hp@hogwart.uk");

    private Player Mary_Poppins_player =new Player("Mary Poppins",32.025,34.77,"marry_popping@londonmagicservice.uk");


//TODO: MAKE THE FUNCTION THAT ADDS THE PLAYERS TO THE MAP so it is not only from start of the game but updating as new players enter the game
    //probably detect when new player enter





    private double test_step = 0.00001;//for testing players moving
    //iterator for simulation:
    int p=0;







    //private FirebaseAnalytics mFirebaseAnalytics;





    private void readCsvFile(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int nameColumnIndex = -1;
            boolean isFirstRow = true;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (isFirstRow) {
                    // Log the headers for reference
                    Log.d("CSV", "Headers: " + String.join(", ", values));
                    isFirstRow = false;
                } else {
                    // Log all columns for each row
                    Log.d("CSV", "Row: " + String.join(", ", values));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

            mapController.animateTo(new GeoPoint(48.8588443, 2.2943506), zoom_factor, speed);

    }
    private int l=0;

    private GeoPoint pos_extractor(){//for some reason cancels others markers


        GpsMyLocationProvider temp= new GpsMyLocationProvider(this);
       GeoPoint myloc = new GeoPoint(temp.getLastKnownLocation().getLatitude(),temp.getLastKnownLocation().getLatitude());
        return myloc;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize UI components
        playsoundButton = findViewById(R.id.playsoundButton);
        mediaPlayer = MediaPlayer.create(this, R.raw.sound1);

        // Setup Bottom Navigation
        BottomNavigationView navView = binding.navView; // Use binding to avoid redundancy
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        //set the test Players:




        // Set button initially disabled until location is fetched
        playsoundButton.setEnabled(false);
        playsoundButton.setText("Fetching Location...");




        // Handle button click
        playsoundButton.setOnClickListener(v -> {
           // if (mediaPlayer != null) {
                //mediaPlayer.start();}



                ToneGenerator Tonegen=new ToneGenerator();
                Handler handler = new Handler(Looper.getMainLooper());

                //Tonegen.toneGenerator(200,4000,(int)zoom_speed/1000);
                //String locationText = "Latitude: " + userLatitude + "\nLongitude: " + userLongitude;

                if(GameStartedFlag==false) {
                    show_current_position(15.0, 1000L);
                    Tonegen.toneGenerator(400, 4000, (int) zoom_speed / 1000);
                    GameStartedFlag=true;
                    // Reset button text after 4 seconds
                    playsoundButton.setText("GAME STARTING");

                    //adding local player to the game server:
                    client_player.setLatitude(userLatitude);
                    client_player.setLongitude(userLongitude);
                    mDatabase.add_player_to_db(client_player);


                    handler.postDelayed(() -> playsoundButton.setText("READY 2 CATCH ITEM"), 2000);
                }

                else {



                    //update distance from bus:
                    updateMarkerDistances();

                    if (closest_item() <= 8) {
                        playsoundButton.setText("CONGRATS\n +100POINTS");
                        Tonegen.toneGenerator(4000, 500, (int) zoom_speed / 1000);

                    } else {
                        playsoundButton.setText("NO ITEM HERE");
                        Tonegen.toneGenerator(800, 500, (int) zoom_speed / 1000);
                        handler.postDelayed(() -> playsoundButton.setText("READY 2 CATCH ITEM"), 2000);
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
                userLatitude = locationData.getLatitude();
                userLongitude = locationData.getLongitude();




                if(MarkersCreatedFlag ==false){
                    playsoundButton.setText("START GAME");
                    playsoundButton.setEnabled(true);
                    InputStream inputStream = getResources().openRawResource(R.raw.bus_holon_en); // Place CSV in res/raw folder
                    //Bus markers and put them on the mapview
                    create_all_bus_Markers(inputStream);
                    //create current online players markers and put them on the mapview
                    for (Player player : Player.test_playerList){
                        Location_utils.create_and_place_player_marker(player, mapview, client_player);

                    }
                MarkersCreatedFlag =true;}

                else {

                    //ACTUAL LOCAL PLAYER:
                    //first loc is sent to the server after pressing the start button then updated here
                    mDatabase.update_player_loc_db(client_player, userLatitude, userLongitude);

                    //update the test players marker:

                    Location_utils.updatePlayersMarkers(); //make crash

                    mapview.invalidate();
                }

                //SIMULATION OF PLAYERS ENTERING THE GAME

                    switch (p){
                        case 0:
                            mDatabase.add_player_to_db(Michael_Jackson_player);
                            Log.d("MainActivity", "Michael J entered the online  db");
                            break;

                        case 30:
                            mDatabase.add_player_to_db(Spiderman_player);
                            Log.d("MainActivity", "Spiderman entered the online db");
                            break;

                        case 75:
                            mDatabase.add_player_to_db(Batman_player);
                            Log.d("MainActivity", "New player entered the online db");
                            break;

                        case 150:

                            mDatabase.add_player_to_db(Superman_player);
                            Log.d("MainActivity", "New player entered the online db");
                            break;

                        case 200:
                        mDatabase.add_player_to_db(Harry_Potter_player);
                            Log.d("MainActivity", "New player entered the online db");
                            break;


                        case 250:
                        mDatabase.add_player_to_db(Mary_Poppins_player);
                            Log.d("MainActivity", "New player entered the online db");
                            break;

                        case 300:
                            // Remove the 'online_players' node
                            mDatabase.get_db_ref().child("online_players").removeValue()
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


                p=p%330 +1;


                //SIMULATION: UPDATE test_players location
                if (!Player.test_playerList.isEmpty()) {
                    for (Player test_player : Player.test_playerList) {
                        test_player.setLatitude(test_player.getLatitude() + test_step);
                        test_player.setLongitude(test_player.getLongitude() - test_step);
                        mDatabase.update_player_loc_db(test_player, test_player.getLatitude(), test_player.getLongitude());
                    }
                }


            }

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
                    playsoundButton.setText("Location Disabled");
                }
            }
        }
    }




    // Release MediaPlayer resources when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public GeoPoint getMyCurrentGeoPoint() {
        if (userLatitude == 0.0 && userLongitude == 0.0) {
            // Either return null or a default location
            return new GeoPoint(0, 0);
        } else {
            return new GeoPoint(userLatitude, userLongitude);
        }
    }


    private void create_all_bus_Markers(InputStream inputStream)//REPLACE WITH DATABASE INSTEAD OF CSV
    {


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int nameColumnIndex = -1;


            List<String[]> allRows = new ArrayList<>();
            Random random = new Random();
            boolean isFirstRow = true;

            // Read all rows into a list
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (isFirstRow) {
                    // Log the headers for reference
                    Log.d("CSV", "Headers: " + String.join(", ", values));
                    isFirstRow = false;
                } else {
                    allRows.add(values); // Add each row to the list
                }
            }

            // Process up to 30 random rows
            int i = 0;
            int maxIterations = 30; // Max random selections
            while (i < maxIterations) {
                if (allRows.size() == 0) {
                    Log.e("CSV", "No rows available for processing.");
                    break;
                }

                // Randomly pick a row
                int randomIndex = random.nextInt(allRows.size());
                String[] values = allRows.get(randomIndex);

                // Log the selected row
                Log.d("CSV", "Random Row: " + String.join(", ", values));

                // Create and position the marker
                Marker temp_marker = new Marker(mapview);
                GeoPoint temp_point = new GeoPoint(
                        Double.parseDouble(values[1]), // Latitude
                        Double.parseDouble(values[2])  // Longitude
                );

                temp_marker.setPosition(temp_point);

                double distanceInMeters = DistanceCalculator.calculateDistance(temp_point, new GeoPoint(userLatitude,userLongitude));

                // Customize marker title
                temp_marker.setTitle(
                        "Bus Station " + (i + 1) + " " + values[3] +
                                "\nDistance from me : " + distanceInMeters + 0 + " meters"
                );

                temp_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                temp_marker.setIcon(getResources().getDrawable(R.drawable.bus100_small));
                mapview.getOverlays().add(temp_marker);
                markerList.add(temp_marker);
                i++; // Increment only if a row is processed
            }

            // Refresh the map
            mapview.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Refresh the map to display the marker
        mapview.invalidate();
    }

    private void updateMarkerDistances() {
        GeoPoint myCurrentPoint = new GeoPoint(userLatitude, userLongitude);
        for (Marker marker : markerList) {
            GeoPoint tempPoint = marker.getPosition();
            double distance = DistanceCalculator.calculateDistance(tempPoint, myCurrentPoint);
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

    private int closest_item()//should be nicer, use minqueue for distance ? use min detection
    {int distance, min_distance=9999999;
        for(Marker marker : markerList ){
            distance=DistanceCalculator.calculateDistance(getMyCurrentGeoPoint(), marker.getPosition());
//test
            if (distance<min_distance)
            {
                min_distance=distance;
            }

        }
        return min_distance;
    }

}
