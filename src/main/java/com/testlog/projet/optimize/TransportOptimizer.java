package com.testlog.projet.optimize;

import com.testlog.projet.ComposedTrip;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.services.ICityService;
import com.testlog.projet.services.TransportService;
import com.testlog.projet.types.SimpleTrip;
import com.testlog.projet.types.TransportationMode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TransportOptimizer implements ITransportOptimizer {
    private final TransportService transportService;

    public TransportOptimizer(ICityService<SimpleTrip> transportService) {
        this.transportService = (TransportService) transportService;
    }

    public List<SimpleTrip> filterTransportCriteria(List<SimpleTrip> Trips, TransportCriteria transportCriteria) {
        if (transportCriteria.preferredMode().equals(TransportationMode.NOT_SPECIFIED)) {
            return Trips;
        }
        
         return Trips.stream()
                 .filter(trip -> trip.mode() == transportCriteria.preferredMode())
                 .toList();
    }
    @Override
    public ComposedTrip getOptimizedTrip(String origin, String destination, LocalDateTime date, TransportCriteria transportCriteria, Double maxPrice) {
        PriorityQueue<String> cities = new PriorityQueue<>();
        boolean optimizeForDuration = true;

        // Distances is either the cost or duration to get to a city, trying to minimize it
        Map<String, Double> distances = new HashMap<>();
        Map<String, SimpleTrip> optimalPaths = new HashMap<>();

        distances.put(origin, 0.0);
        cities.add(origin);

        while (!cities.isEmpty()) {
            String city = cities.poll();

            if (city.equals(destination)) {
                // Found the destination
                List<SimpleTrip> optimalTrips = reconstructOptimalPath(optimalPaths, destination);
                ComposedTrip composedTrip = new ComposedTrip(optimalTrips);

                if (composedTrip.getPrice() <= maxPrice) {
                    return composedTrip;
                } else {
                    throw new IllegalArgumentException("No trips available");
                }
            }

            double distance = distances.get(city);

            for (SimpleTrip trip : filterTransportCriteria(this.transportService.getForCity(city, date), transportCriteria)) {
                String nextCity = trip.arrivalCity();
                Duration duration = Duration.between(trip.departureTime(), trip.arrivalTime());
                double tripDuration = duration.toMinutes() / 60.0;

                double newDistance = distance + (transportCriteria.preferMinPricesOverMinDuration() ? trip.price() : tripDuration);


                if (!distances.containsKey(nextCity) || distances.get(nextCity) > newDistance) {
                    distances.put(nextCity, newDistance);
                    optimalPaths.put(nextCity, trip);
                    cities.add(nextCity);
                }
            }
        }

        // No path found, return an empty trip
        return new ComposedTrip(new ArrayList<>());
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
}
