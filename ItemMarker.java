package com.example.myapplicationtest1;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class ItemMarker extends Marker {

    public ItemMarker(MapView mapView) {
        super(mapView);
    }

    ItemMarker(MapView mapview, Marker cpy_marker) {
        super(mapview);
        this.setPosition(cpy_marker.getPosition());
        this.setTitle(cpy_marker.getTitle());
        //this.setIcon();
    }

}
