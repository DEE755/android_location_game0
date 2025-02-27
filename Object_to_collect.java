package com.example.myapplicationtest1;

import static com.example.myapplicationtest1.Location_utils.getMyCurrentGeoPoint;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.Map;

import android.view.MotionEvent;

//IMPORTANT: THIS CLASS IS USED TO FETCH DATA FROM THE DATABASE AND CREATE MARKERS FOR THE OBJECTS TO COLLECT
//SO IT DOESNT NEED TO CREATE OBJECT BUT TO FETCH THEM FROM THE DATABASE

public class Object_to_collect {

    protected String name;

    protected int icon = R.drawable.bus100_small;

    protected int click_sound=R.raw.button_click;

    protected String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    protected double latitude;
    protected double longitude;

    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String osmIDMarker;

    public void setOsmIDMarker(String osmIDMarker)
    {this.osmIDMarker = osmIDMarker;}

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    protected int points=100;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    private Marker ObjectMarker;

    public Marker getObjectMarker() {
        return ObjectMarker;
    }

    public void setObjectMarker(Marker ObjectMarker) {
        this.ObjectMarker = ObjectMarker;
    }

    static protected Map <String, Marker> object_marker_map= new HashMap<>(); //like a dictionnary to store the object's key and the corresponding marker



    //CONSTRUCTOR
    Object_to_collect() {}

    //cpy constructor
    Object_to_collect(Object_to_collect object_to_collect)
    {
        this.name = object_to_collect.name;
        this.latitude = object_to_collect.latitude;
        this.longitude = object_to_collect.longitude;
        this.id = object_to_collect.id;
        this.osmIDMarker = object_to_collect.osmIDMarker;
        this.points = object_to_collect.points;
        this.ObjectMarker = object_to_collect.ObjectMarker;
        this.icon = object_to_collect.icon;

    }




    protected void prepare_object(MapView mapview, Player client_player) {
        double distance;
        GeoPoint position = new GeoPoint(this.getLatitude(), this.getLongitude());
        this.ObjectMarker = new Marker(mapview);
        this.ObjectMarker.setPosition(new GeoPoint(position));
        this.ObjectMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        this.ObjectMarker.setIcon(mapview.getContext().getResources().getDrawable(icon));
        this.setOsmIDMarker(this.ObjectMarker.getId());
        distance = Location_utils.DistanceCalculator.calculateDistance(getMyCurrentGeoPoint(), position);

        this.ObjectMarker.setOnMarkerClickListener((marker, mapView) -> {
            MediaPlayer mediaPlayer = MediaPlayer.create(mapView.getContext(), click_sound);
            mediaPlayer.start();
            this.ObjectMarker.setTitle("Object " + this.getId() + ", Points: " + getPoints());
            this.ObjectMarker.setSnippet("Distance from me: " + distance + " meters");
            ObjectMarker.setImage(mapView.getContext().getResources().getDrawable(icon));
            ObjectMarker.showInfoWindow();
            return true;

        });

    }

    public void set_object_marker_on_map(MapView mapview) {

        //add to map
        mapview.getOverlays().add(ObjectMarker);
        //keep a reference to the marker from the ID of the marker to the marker itslef for modif/delete purposes
        object_marker_map.put(this.osmIDMarker, ObjectMarker);

        }



    }








