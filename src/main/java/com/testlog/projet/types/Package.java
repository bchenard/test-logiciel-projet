package com.testlog.projet.types;

import java.util.List;

public record Package(List<Activity> activities, Hotel hotel, ComposedTrip firstTrip, ComposedTrip returnTrip, double totalPrice) {
    @Override
    public String toString() {
        StringBuilder activitiesString = new StringBuilder();
        for (Activity activity : activities) {
            activitiesString.append(activity).append("\n");
        }

        return "{" +
                "\n activities=\n" + activitiesString +
                "\n hotel=" + hotel +
                "\n firstTrip=" + firstTrip +
                "\n returnTrip=" + returnTrip +
                "\n totalPrice=" + totalPrice +
                "\n}";
    }
}