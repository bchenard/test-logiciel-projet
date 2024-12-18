package com.testlog.projet;

public class Hotel {
    private String city;
    private Double latitude;
    private Double longitude;
    private int stars;
    private String name;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hotel(String city, Double latitude, Double longitude, int stars, String name) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.stars = stars;
        this.name = name;
    }
}
