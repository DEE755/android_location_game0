package com.example.myapplicationtest1;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class Location_utils {


    public static Marker closest_marker(Player client_player)//should be nicer, use minqueue for distance ? use min detection
    {int distance, min_distance=Integer.MAX_VALUE;
        Marker min_marker = null;
        int i=0;
        /*for(Map.Entry<String, Marker> entry : Object_to_collect.getObject_marker_map().entrySet() ){
            distance=DistanceCalculator.calculateDistance(getMyCurrentGeoPoint(), entry.getValue().getPosition());
    Log.d("distance", distance+ entry.getValue().getTitle() + "iteration" +i++);*/

        for(Object_to_collect object : client_player.getList_of_objects_to_collect()){
            distance=DistanceCalculator.calculateDistance(new GeoPoint(client_player.getLatitude(), client_player.getLongitude()), object.getObjectMarker().getPosition());
            if (distance<min_distance)
            {
                min_distance=distance;
                min_marker=object.getObjectMarker();
            }
        }
        Log.d("min_distance","min dist"+ min_distance+ "min_marker" +min_marker);
        return min_marker;
    }

    public static GeoPoint getMyCurrentGeoPoint() {
        if (UserLatitude == 0.0 && UserLongitude == 0.0) {
            // Either return null or a default location
            return new GeoPoint(0.0, 0.0);
        } else {
            return new GeoPoint(UserLatitude, UserLongitude);
        }
    }



    private static double UserLatitude;
    private static double UserLongitude;

    public static double getUserLatitude() {
        return UserLatitude;
    }

    public static void setUserLatitude(double userLatitude) {
        UserLatitude = userLatitude;
    }

    public static double getUserLongitude() {
        return UserLongitude;
    }

    public static void setUserLongitude(double userLongitude) {
        UserLongitude = userLongitude;
    }

    // list for created markers
    static List<Marker> PlayersmarkerList = new ArrayList<>();

    static Marker create_and_place_player_marker(Player player_, MapView mapview, Player client_player) {
        // Create and position the marker
        Marker player_marker = player_.create_player_marker(mapview, player_);

        // determining the distance from the client player
        double distanceInMeters = DistanceCalculator.calculateDistance(new GeoPoint(player_.getLatitude(), player_.getLongitude()), new GeoPoint(client_player.getLatitude(), client_player.getLongitude()));

        // Customize marker title
        player_marker.setTitle(
                player_.getName() +
                        "\nDistance from me: " + distanceInMeters + " meters"
        );

        player_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // ADD THE PICTURE OF EACH PLAYER HERE (fetch the path from database:
        //change after to something real:
        switch (player_.getName()) {
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


    void place_marker_on_map(Player player_, Player client_player, MapView mapview) {

        // add the marker to the map:
        mapview.getOverlays().add(player_.getPlayer_marker());
        //PlayersmarkerList.add(player_marker);

        // Refresh the map
        mapview.invalidate();

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
        if (!Player.online_playerList.isEmpty()) {
            for (Player curr_player : Player.online_playerList) {

                GeoPoint player_position = new GeoPoint(curr_player.getLatitude(), curr_player.getLongitude());
                //GeoPoint tempPoint = marker.getPosition();
                //double distance = DistanceCalculator.calculateDistance(tempPoint, myCurrentPoint);
                //String time=getCurrentTime();
                //marker.setTitle("Bus Station - Distance: " + distance + " meters\n"+"Last Update" + time);
                curr_player.getPlayer_marker().setPosition(player_position);
                //marker.setPosition();
            }

        }
    }

    public static class DistanceCalculator {

        //test
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

            return (int) (EARTH_RADIUS * c);
        }


    }
}