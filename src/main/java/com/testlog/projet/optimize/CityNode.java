package com.testlog.projet.optimize;

public  class CityNode {
    private final String cityName;
    private final double distance;

    public CityNode(String cityName, double distance) {
        this.cityName = cityName;
        this.distance = distance;
    }

    public String getCityName() {
        return cityName;
    }

    public double getDistance() {
        return distance;
    }
}