package com.testlog.projet.types;

public record LatLng(Double lat, Double lng) {
    public double distance(LatLng other) {
        double lat1 = Math.toRadians(lat);
        double lng1 = Math.toRadians(lng);
        double lat2 = Math.toRadians(other.lat);
        double lng2 = Math.toRadians(other.lng);

        double dlat = lat2 - lat1;
        double dlng = lng2 - lng1;

        double sqrtArg = 1 - Math.cos(dlat) + Math.cos(lat1) * Math.cos(lat2) * (1 - Math.cos(dlng));

        return 2 * 6371.0 * Math.asin(Math.sqrt(sqrtArg) / 2);
    }
}
