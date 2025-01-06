package com.testlog.projet.types;

import java.util.List;

public record Activity(String city, LatLng coordinates, ActivityType type, Double price,
                       List<Boolean> availability) {
}
