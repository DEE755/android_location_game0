package com.example.myapplicationtest1;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;



public class Location_utils {
    // list for created markers
    static List<Marker> PlayersmarkerList = new ArrayList<>();
    static Marker create_and_place_player_marker(Player player_, MapView mapview, Player client_player) {
        // Create and position the marker
        Marker player_marker = new Marker(mapview);

        // need to pass lat and long to a geopoint before setting the position (that's an osm rule)
        GeoPoint player_loc = new GeoPoint(
                player_.getLatitude(), player_.getLongitude()
        );

        player_marker.setPosition(player_loc);
        // determining the distance from the client player
        double distanceInMeters = DistanceCalculator.calculateDistance(player_loc, new GeoPoint(client_player.getLatitude(), client_player.getLongitude()));

        // Customize marker title
        player_marker.setTitle(
                player_.getName() +
                        "\nDistance from me: " + distanceInMeters + " meters"
        );

        player_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // ADD THE PICTURE OF EACH PLAYER HERE (fetch the path from database:
        //change after to something real:
        switch (player_.getName()){
            case "Mary Poppins":
                player_marker.setIcon(mapview.getContext().getResources().getDrawable(R.drawable.mary_poppins_logo));
                break;
            case "Harry Potter":
                player_marker.setIcon(mapview.getContext().getResources().getDrawable(R.drawable.harry_potter_logo));
                break;

            case "Clark Kent":
                player_marker.setIcon(mapview.getContext().getResources().getDrawable(R.drawable.clark_kent_logo));
                break;

            case "Peter Parker":
                player_marker.setIcon(mapview.getContext().getResources().getDrawable(R.drawable.peter_parker_logo));
                break;

                case "Michael Jackson":
                player_marker.setIcon(mapview.getContext().getResources().getDrawable(R.drawable.michael_jackson_logo));
                break;

                case "Bruce Wayne":
                player_marker.setIcon(mapview.getContext().getResources().getDrawable(R.drawable.bruce_wayne_logo));
                break;


        }
        // add the marker to the map:
        mapview.getOverlays().add(player_marker);
        PlayersmarkerList.add(player_marker);
        player_.setPlayer_marker(player_marker);
        // Refresh the map
        mapview.invalidate();

        return player_marker;
    }

    void fetchOnlinePlayersData() {
        DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference("online_players");

        playersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    // Fetch part of the player's data
                    String playerId = playerSnapshot.getKey();
                    String playerName = playerSnapshot.child("name").getValue(String.class);
                    Double playerLatitude = playerSnapshot.child("latitude").getValue(Double.class);
                    Double playerLongitude = playerSnapshot.child("longitude").getValue(Double.class);

                    // Add to map
                    // You can call create_and_place_player_marker here if needed
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                System.err.println("Error fetching data: " + databaseError.getMessage());
            }
        });
    }


    //TODO: update the player's marker logo with their real picture path from database
    static public void updatePlayersMarkers() {
        //GeoPoint myCurrentPoint = new GeoPoint(player.getLatitude(), player.getLongitude());
        if (!Player.test_playerList.isEmpty()) {
            for (Player curr_player : Player.test_playerList) {
                GeoPoint player_position = curr_player.getPosition();//NEDD TO SYNC betweeen marker and player
                //GeoPoint tempPoint = marker.getPosition();
                //double distance = DistanceCalculator.calculateDistance(tempPoint, myCurrentPoint);
                //String time=getCurrentTime();
                //marker.setTitle("Bus Station - Distance: " + distance + " meters\n"+"Last Update" + time);
                curr_player.getplayer_marker().setPosition(player_position);
                //marker.setPosition();
            }

        }
    }

}