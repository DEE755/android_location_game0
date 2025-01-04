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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


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




import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;


import java.io.BufferedReader;
import java.io.InputStreamReader;

class DistanceCalculator {

    private static final double EARTH_RADIUS = 6371000; // Earth's radius in meters

    /**
     * Calculates the distance between two geographical points using the haversine formula.
     *
     * @param point1 The first geographical point.
     * @param point2 The second geographical point.
     * @return The distance in meters.
     */
    public static int calculateDistance(GeoPoint point1, GeoPoint point2) {
        double lat1 = Math.toRadians(point1.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon2 = Math.toRadians(point2.getLongitude());

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int)(EARTH_RADIUS * c);
    }


}
class ToneGenerator {


    public void toneGenerator(float startFreq, float endFreq, int nbOfSeconds) {
        final int sampleRate = 44100; // Standard audio sample rate
        int numSamples = nbOfSeconds * sampleRate; // Total samples for the duration
        double[] sample = new double[numSamples]; // Array to hold sample data
        byte[] generatedSound = new byte[2 * numSamples]; // Output buffer for audio
        new Thread(() -> {
        // Generate the tone samples
        for (int i = 0; i < numSamples; ++i) {
            double currentFreq = startFreq + ((endFreq - startFreq) * i / numSamples); // Linear interpolation
            sample[i] = Math.sin(2 * Math.PI * i * currentFreq / sampleRate); // Sine wave calculation
        }

        // Convert to 16-bit PCM format
        int index = 0;
        for (final double value : sample) {
            // Scale to max amplitude for 16-bit PCM
            final short val = (short) ((value * 32767));
            // Little-endian format: LSB first
            generatedSound[index++] = (byte) (val & 0x00ff);
            generatedSound[index++] = (byte) ((val & 0xff00) >>> 8);
        }

        // Play the tone using AudioTrack
        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                generatedSound.length,
                AudioTrack.MODE_STATIC
        );

        audioTrack.write(generatedSound, 0, generatedSound.length);
        audioTrack.play();

        // Wait for the tone to finish
        try {
            Thread.sleep(nbOfSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        // Release the AudioTrack resources
        audioTrack.stop();
        audioTrack.release();
    }).start();
    };
}



public class MainActivity extends AppCompatActivity {



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

    // Callback interface for location results
    public interface MyLocationCallback {
        void onLocationResult(LocationData locationData);
        void onError(String errorMsg);
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
    private MapView map=null;

    private IMapController MapController;
    private Marker userMarker;

    private boolean MarkersCreatedFlag =false;
    private boolean GameStartedFlag =false;


    private List<Marker> markerList = new ArrayList<>();

    private GeoPoint myCurrentPoint=new GeoPoint(0.0,0.0);

    private double zoom_speed=1000L;

    //GpsMyLocationProvider myloc=new GpsMyLocationProvider();//make crash

    //GeoPoint mylocgeo= new GeoPoint(myloc.getLastKnownLocation().getLatitude(), myloc.getLastKnownLocation().getLongitude());

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


        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);

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
        map.getOverlays().add(myLocationOverlay);

// Set the map controller to zoom and animate to the user's location when it's obtained
        IMapController mapController = map.getController();
        //mapController.setZoom(zoom_factor);

            mapController.animateTo(new GeoPoint(48.8588443, 2.2943506), zoom_factor, speed);

    }


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

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);


        show_current_position(9.0,1000L);


        //random Lat & Long:

// Define the geographic point for the marker
       // GeoPoint point = new GeoPoint(32.0148, 34.7767);

// Create a new Marker object
        // Marker marker = new Marker(map);

// Set the position of the marker
       // marker.setPosition(point);

// Optionally, set a title for the marker
       // marker.setTitle("a first point in Holon");

// Optionally, set an icon for the marker
// marker.setIcon(getResources().getDrawable(R.drawable.marker_icon));

// Optionally, set the anchor point of the marker
// marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

// Add the marker to the map's overlay
       // map.getOverlays().add(marker);

// Refresh the map to display the marker
        //map.invalidate();







    }//oncreateend









    private void fetchLocation() {
        getCurrentLocation(new MyLocationCallback() {
            @Override
            public void onLocationResult(LocationData locationData) {
                userLatitude = locationData.getLatitude();
                userLongitude = locationData.getLongitude();
                //Log.d("Location", "Latitude: " + userLatitude + " & Longitude: " + userLongitude);

                // Enable the button now that location is available




                if(MarkersCreatedFlag ==false){
                    playsoundButton.setText("START GAME");
                    playsoundButton.setEnabled(true);
                    InputStream inputStream = getResources().openRawResource(R.raw.bus_holon_en); // Place CSV in res/raw folder
                createMarkers(inputStream);
                MarkersCreatedFlag =true;}

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


    private void createMarkers(InputStream inputStream)
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
                Marker temp_marker = new Marker(map);
                GeoPoint temp_point = new GeoPoint(
                        Double.parseDouble(values[1]), // Latitude
                        Double.parseDouble(values[2])  // Longitude
                );

                temp_marker.setPosition(temp_point);

                double distanceInMeters = DistanceCalculator.calculateDistance(temp_point, new GeoPoint(userLatitude,userLongitude));

                // Customize marker title
                temp_marker.setTitle(
                        "Bus Station " + (i + 1) + " " + values[3] +
                                "\nDistance from me (check if live?): " + distanceInMeters + 0 + " meters"
                );

                temp_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                temp_marker.setIcon(getResources().getDrawable(R.drawable.bus100_small));
                map.getOverlays().add(temp_marker);
                markerList.add(temp_marker);
                i++; // Increment only if a row is processed
            }

            // Refresh the map
            map.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Refresh the map to display the marker
        map.invalidate();
    }

    private void updateMarkerDistances() {
        GeoPoint myCurrentPoint = new GeoPoint(userLatitude, userLongitude);
        for (Marker marker : markerList) {
            GeoPoint tempPoint = marker.getPosition();
            double distance = DistanceCalculator.calculateDistance(tempPoint, myCurrentPoint);
            String time=getCurrentTime();
            marker.setTitle("Bus Station - Distance: " + distance + " meters\n"+"Last Update" + time);
        }
        map.invalidate();
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

            if (distance<min_distance)
            {
                min_distance=distance;
            }

        }
        return min_distance;
    }

}
