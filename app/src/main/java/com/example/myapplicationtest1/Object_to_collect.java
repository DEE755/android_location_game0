package com.example.myapplicationtest1;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Map;

//IMPORTANT: THIS CLASS IS USED TO FETCH DATA FROM THE DATABASE AND CREATE MARKERS FOR THE OBJECTS TO COLLECT
//SO IT DOESNT NEED TO CREATE OBJECT BUT TO FETCH THEM FROM THE DATABASE

public class Object_to_collect {


    private String name;




    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private double latitude;
    private double longitude;

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

    private int points;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    Marker object_marker;

    Map <String, Marker> object_marker_map; //like a dictionnary to store the object's key and the corresponding marker

    //CONSTRUCTOR
    Object_to_collect()
    {
    }





    //Object_to_collect(boolean dummy_placeholder)
    //{

    //}



    void create_object_marker(MapView mapview)
    {
        this.object_marker = new Marker(mapview);
        this.object_marker.setPosition(new GeoPoint(this.getLatitude(), this.getLongitude()));
        this.object_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        this.object_marker.setTitle(this.getName());
        this.object_marker.setIcon(mapview.getContext().getResources().getDrawable(R.drawable.bus100_small));
        mapview.getOverlays().add(object_marker);
    }




}
