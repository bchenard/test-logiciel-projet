package com.testlog.projet.optimize;

import com.testlog.projet.types.ComposedTrip;
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

    public List<SimpleTrip> filterTransportCriteria(List<SimpleTrip> Trips, TransportCriteria transportCriteria) {
        if (transportCriteria.preferredMode().equals(TransportationMode.NOT_SPECIFIED)) {
            return Trips;
        }
         return Trips.stream()
                 .filter(trip -> trip.mode() == transportCriteria.preferredMode())
                 .toList();
    }

    @Override
    public ComposedTrip getOptimizedTrip(String origin, String destination, LocalDateTime date, TransportCriteria transportCriteria) {
        PriorityQueue<String> cities = new PriorityQueue<>();

        // Distances is the price to get to a city, trying to minimize it
        Map<String, Double> distances = new HashMap<>();
        Map<String, SimpleTrip> optimalPaths = new HashMap<>();

        distances.put(origin, 0.0);
        cities.add(origin);

        while (!cities.isEmpty()) {
            String city = cities.poll();

            if (city.equals(destination)) {
                // Found the destination
                List<SimpleTrip> optimalTrips = reconstructOptimalPath(optimalPaths, destination);
                return new ComposedTrip(optimalTrips);
            }

            double distance = distances.get(city);

            for (SimpleTrip trip : filterTransportCriteria(this.transportService.getForCity(city, date), transportCriteria)) {
                String nextCity = trip.arrivalCity();
                double newDistance = distance + trip.price();

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
