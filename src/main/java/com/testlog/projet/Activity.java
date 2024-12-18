package com.testlog.projet;

public class Activity {
    private String city;
    private Double latitude;
    private Double longitude;
    private ActivityType type;
    private Double price;

    public Activity(String city, Double latitude, Double longitude, ActivityType type, Double price) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.price = price;
    }

    public String getVille() {
        return city;
    }

    public void setVille(String ville) {
        this.city = ville;
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

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public Double getPrix() {
        return price;
    }

    public void setPrix(Double prix) {
        this.price = prix;
    }
}
