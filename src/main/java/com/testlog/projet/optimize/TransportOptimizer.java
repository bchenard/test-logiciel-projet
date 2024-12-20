package com.testlog.projet.optimize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.testlog.projet.ComposedTrip;
import com.testlog.projet.services.TransportService;
import com.testlog.projet.types.SimpleTrip;

public class TransportOptimizer implements ITransportOptimizer {
  private TransportService transportService = new TransportService();

  @Override
  public ComposedTrip getOptimizedTrip(String origin, String destination) {
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

      for (SimpleTrip trip : transportService.getForCity(city)) {
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
