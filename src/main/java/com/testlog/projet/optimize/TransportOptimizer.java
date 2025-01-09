package com.testlog.projet.optimize;

import com.testlog.projet.ComposedTrip;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.services.ICityService;
import com.testlog.projet.services.TransportService;
import com.testlog.projet.types.SimpleTrip;
import com.testlog.projet.types.TransportationMode;

import java.time.LocalDateTime;
import java.util.*;

public class TransportOptimizer implements ITransportOptimizer {
    private final TransportService transportService;

    public TransportOptimizer(ICityService<SimpleTrip> transportService) {
        this.transportService = (TransportService) transportService;
    }

    @Override
    public ComposedTrip getOptimizedTrip(String origin, String destination, LocalDateTime date, TransportCriteria transportCriteria, Double maxPrice) {
        PriorityQueue<String> cities = new PriorityQueue<>();
        Map<String, Double> distances = new HashMap<>();
        Map<String, Double> durations = new HashMap<>();
        Map<String, SimpleTrip> optimalPaths = new HashMap<>();

        distances.put(origin, 0.0);
        durations.put(origin, 0.0);
        cities.add(origin);

        while (!cities.isEmpty()) {
            String city = cities.poll();

            if (city.equals(destination)) {
                List<SimpleTrip> optimalTrips = reconstructOptimalPath(optimalPaths, destination);
                ComposedTrip composedTrip = new ComposedTrip(optimalTrips);

                if (composedTrip.getPrice() <= maxPrice) {
                    return composedTrip;
                } else {
                    throw new IllegalArgumentException("No trips available within the budget");
                }
            }

            double distance = distances.get(city);
            double duration = durations.get(city);

            for (SimpleTrip trip : filterTransportCriteria(this.transportService.getForCity(city, date), transportCriteria)) {
                String nextCity = trip.arrivalCity();
                double newDistance = distance + trip.price();
                double newDuration = duration + calculateDuration(trip);

                boolean shouldUpdate = transportCriteria.preferMinPricesOverMinDuration()
                        ? (!distances.containsKey(nextCity) || distances.get(nextCity) > newDistance)
                        : (!durations.containsKey(nextCity) || durations.get(nextCity) > newDuration);

                if (shouldUpdate) {
                    distances.put(nextCity, newDistance);
                    durations.put(nextCity, newDuration);
                    optimalPaths.put(nextCity, trip);
                    cities.add(nextCity);
                    date = trip.arrivalTime();
                }

                // Update the date to the arrival time of the current trip

            }
        }

        return new ComposedTrip(new ArrayList<>());
    }

    private double calculateDuration(SimpleTrip trip) {
        return (double) java.time.Duration.between(trip.departureTime(), trip.arrivalTime()).toMinutes();
    }

    private List<SimpleTrip> reconstructOptimalPath(Map<String, SimpleTrip> optimalPaths, String destination) {
        LinkedList<SimpleTrip> optimalTrips = new LinkedList<>();
        SimpleTrip trip = optimalPaths.get(destination);

        while (trip != null) {
            optimalTrips.add(trip);
            trip = optimalPaths.get(trip.departureCity());
        }

        Collections.reverse(optimalTrips);
        return optimalTrips;
    }

    private List<SimpleTrip> filterTransportCriteria(List<SimpleTrip> trips, TransportCriteria transportCriteria) {
        if (transportCriteria.preferredMode().equals(TransportationMode.NOT_SPECIFIED)) {
            return trips;
        }
        return trips.stream()
                .filter(trip -> trip.mode() == transportCriteria.preferredMode())
                .toList();
    }
}