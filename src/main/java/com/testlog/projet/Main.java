package com.testlog.projet;


import com.testlog.projet.services.TransportService;
import com.testlog.projet.types.SimpleTrip;

import java.util.List;
import java.util.Map;

public class Main {

        public static void main(String[] args) {
            try {
                // Create an instance of TransportService
                TransportService transportService = new TransportService();

                // Specify the city to search for trips
                String city = "Bordeaux";

                // Fetch trips for the city
                List<SimpleTrip> trips = transportService.getForCity(city);

                // Print the results
                System.out.println("Trips for city: " + city);
                if (trips.isEmpty()) {
                    System.out.println("No trips found.");
                } else {
                    for (SimpleTrip trip : trips) {
                        System.out.println(trip);
                    }
                }
            } catch (Exception e) {
                // Handle any exceptions
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
