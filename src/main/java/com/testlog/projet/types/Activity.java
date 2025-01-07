package com.testlog.projet.types;

import java.util.List;

public record Activity(String name, String address, String city, LatLng coordinates, ActivityType type, Double price,
                       List<Boolean> availability) {
}
