package com.testlog.projet.types;

import java.util.List;

public record Activity(String city, Double latitude, Double longitude, ActivityType type, Double price,
                       List<Boolean> availability) {
}
