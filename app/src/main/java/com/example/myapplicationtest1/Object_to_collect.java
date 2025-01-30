package com.example.myapplicationtest1;

import static com.example.myapplicationtest1.Location_utils.getMyCurrentGeoPoint;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
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

    private String osmIDMarker;

    public void setOsmIDMarker(String osmIDMarker)
    {this.osmIDMarker = osmIDMarker;}

    public String getOsmIDMarker()
    {return this.osmIDMarker;}

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

    private Marker ObjectMarker;

    public Marker getObjectMarker() {
        return ObjectMarker;
    }

    public void setObjectMarker(Marker ObjectMarker) {
        this.ObjectMarker = ObjectMarker;
    }

    static private Map <String, Marker> object_marker_map= new HashMap<>(); //like a dictionnary to store the object's key and the corresponding marker

    static public Map <String, Marker> getObject_marker_map()
    {
        return object_marker_map;
    }
    //static private PriorityQueue<Map<String,Marker>> MinMarkerHeap = new PriorityQueue<>();

    //static PriorityQueue<Map<String,Marker>> getMinMarkerHeap()
    {
       // return MinMarkerHeap;
    }






    //CONSTRUCTOR
    Object_to_collect()
    {

    }





    //Object_to_collect(boolean dummy_placeholder)
    //{

    //}



    public void create_object_marker(MapView mapview, Player client_player) {
        double distance;
        GeoPoint position = new GeoPoint(this.getLatitude(), this.getLongitude());
        this.ObjectMarker = new Marker(mapview);
        this.ObjectMarker.setPosition(new GeoPoint(position));
        this.ObjectMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        this.ObjectMarker.setIcon(mapview.getContext().getResources().getDrawable(R.drawable.bus100_small));
        this.setOsmIDMarker(this.ObjectMarker.getId());
        distance = Location_utils.DistanceCalculator.calculateDistance(getMyCurrentGeoPoint(), position);
        this.ObjectMarker.setTitle(this.getName() + "\nDistance from me: " + distance + " meters");
        //add to map
        mapview.getOverlays().add(ObjectMarker);
        //keep a reference to the marker from the ID of the marker to the marker itslef for modif/delete purposes
        object_marker_map.put(this.osmIDMarker, ObjectMarker);

        //client_player.getList_of_Objects_Markers().add(object_marker);

        int i = 0;


        }

    }








