package com.testlog.projet.types;

public record LatLng(Double lat, Double lng) {
    public double distance(LatLng other) {
        // Euclidean distance, not accurate for large distances
        return Math.sqrt(Math.pow(this.lat - other.lat, 2) + Math.pow(this.lng - other.lng, 2));
    }
}
