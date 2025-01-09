package com.testlog.projet.types;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComposedTrip {
    private final List<SimpleTrip> trips;

    public ComposedTrip(List<SimpleTrip> trips) {
        this.trips = trips;
    }

    public String getDepartureCity() {
        return trips.getFirst().departureCity();
    }

    public String getArrivalCity() {
        return trips.getLast().arrivalCity();
    }

    public List<TransportationMode> getMode() {
        List<TransportationMode> modes = new ArrayList<>();
        for (SimpleTrip trip : trips) {
            modes.add(trip.mode());
        }
        return modes;
    }

    public double getPrice() {
        double price = 0;
        for (SimpleTrip trip : trips) {
            price += trip.price();
        }
        return price;
    }

    public LocalDateTime getDepartureTime() {
        return trips.getFirst().departureTime();
    }

    public LocalDateTime getArrivalTime() {
        return trips.getLast().arrivalTime();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (SimpleTrip trip : trips) {
            stringBuilder.append(trip).append("\n");
        }

        return stringBuilder.toString();
    }
}
