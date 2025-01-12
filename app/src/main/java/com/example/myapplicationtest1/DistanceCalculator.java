package com.example.myapplicationtest1;

import org.osmdroid.util.GeoPoint;

public class DistanceCalculator {

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

            return (int)(EARTH_RADIUS * c);
        }

}
