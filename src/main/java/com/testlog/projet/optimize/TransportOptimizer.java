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

    @Override
    public ComposedTrip getOptimizedTrip(String origin, String destination, LocalDateTime date, TransportCriteria transportCriteria, Double maxPrice) {
        List<List<SimpleTrip>> allPaths = new ArrayList<>();
        findAllPaths(origin, destination, date, transportCriteria, new ArrayList<>(), allPaths, new HashSet<>());

        List<SimpleTrip> optimalPath = null;
        double optimalValue = Double.MAX_VALUE;

        for (List<SimpleTrip> path : allPaths) {
            double totalPrice = path.stream().mapToDouble(SimpleTrip::price).sum();
            double totalDuration = path.stream().mapToDouble(this::calculateDuration).sum();

            if (totalPrice > maxPrice) continue;

            double currentValue = transportCriteria.preferMinPricesOverMinDuration() ? totalPrice : totalDuration;
            if (currentValue < optimalValue) {
                optimalValue = currentValue;
                optimalPath = path;
            }
        }

        if (optimalPath == null) {
            throw new IllegalArgumentException("No trips available within the budget");
        }

        return new ComposedTrip(optimalPath);
    }

    private void findAllPaths(String currentCity, String destination, LocalDateTime date, TransportCriteria transportCriteria, List<SimpleTrip> currentPath, List<List<SimpleTrip>> allPaths, Set<String> visitedCities) {
        if (currentCity.equals(destination)) {
            allPaths.add(new ArrayList<>(currentPath));
            return;
        }

        visitedCities.add(currentCity);

        for (SimpleTrip trip : filterTransportCriteria(this.transportService.getForCity(currentCity, date), transportCriteria)) {
            if (visitedCities.contains(trip.arrivalCity()) || trip.departureTime().isBefore(date)) continue;

            currentPath.add(trip);
            findAllPaths(trip.arrivalCity(), destination, trip.arrivalTime(), transportCriteria, currentPath, allPaths, visitedCities);
            currentPath.removeLast();
        }

        visitedCities.remove(currentCity);
    }

    private double calculateDuration(SimpleTrip trip) {
        return (double) java.time.Duration.between(trip.departureTime(), trip.arrivalTime()).toMinutes();
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