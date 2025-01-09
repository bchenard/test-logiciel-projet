package com.testlog.projet.types;

import java.util.List;

public record Package(List<Activity> activities, Hotel hotel, ComposedTrip firstTrip, ComposedTrip returnTrip, double totalPrice) {
}