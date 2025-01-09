package com.testlog.projet.types;

import java.util.List;

public record Activity(String name, String address, String city, LatLng coordinates, ActivityType type, Double price,
                       List<Boolean> availability) {
    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", coordinates=" + coordinates +
                ", type=" + type +
                ", price=" + price +
                '}';
    }
}