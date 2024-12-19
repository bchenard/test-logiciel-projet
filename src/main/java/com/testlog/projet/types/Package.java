package com.testlog.projet.types;

import com.testlog.projet.ComposedTrip;

import java.util.List;

public record Package(List<Activity> activities, Hotel hotel, ComposedTrip firstTrip, ComposedTrip returnTrip) {
}