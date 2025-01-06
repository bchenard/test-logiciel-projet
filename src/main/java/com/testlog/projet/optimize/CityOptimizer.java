package com.testlog.projet.optimize;

import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.services.ICityService;
import com.testlog.projet.types.Activity;
import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.LatLng;
import com.testlog.projet.types.Pair;


import com.google.ortools.Loader;
import com.google.ortools.init.OrToolsVersion;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.List;

public class CityOptimizer implements ICityOptimizer {

    private final ICityService<Hotel> hotelService;
    private final ICityService<Activity> activityService;

    public CityOptimizer(ICityService<Hotel> hotelService, ICityService<Activity> activityService) {
        this.hotelService = hotelService;
        this.activityService = activityService;
    }

    private List<Hotel> findCheapestHotel(List<Hotel> hotels, double budget) {
        double minPrice = hotels.stream()
                .mapToDouble(Hotel::price)
                .min()
                .orElse(Double.MAX_VALUE);

        if (minPrice > budget) {
            return List.of();
        }

        return hotels.stream()
                .filter(hotel -> hotel.price() == minPrice)
                .toList();
    }

    private List<Hotel> findBestHotel(List<Hotel> hotels, double budget) {
        List<Hotel> fitBudget = hotels.stream()
                .filter(hotel -> hotel.price() <= budget)
                .toList();

        double maxStars = fitBudget.stream()
                .mapToInt(Hotel::stars)
                .max()
                .orElse(0);

        return hotels.stream()
                .filter(hotel -> hotel.stars() == maxStars)
                .toList();
    }

    // Potentially return null
    // TODO: Find max activities around the hotel -> Should check activities preferences (and distance), and days of availability of the activities
    // Helper function to get activities around a hotel ?
    private Hotel findMaxActivitiesAround(List<Hotel> hotels, List<Activity> activities, double maxDistance) {
        Hotel best = null;
        int maxActivities = 0;
        for (Hotel hotel : hotels) {
            LatLng hotelCoordinates = hotel.coordinates();
            int activitiesAround = (int) activities.stream()
                    .filter(activity -> hotelCoordinates.distance(activity.coordinates()) <= maxDistance)
                    .count();

            if (activitiesAround > maxActivities) {
                best = hotel;
                maxActivities = activitiesAround;
            }
        }
        return best;
    }

    // Potentially return null
    private Hotel findHotel(HotelCriteria hotelCriteria, ActivityCriteria activityCriteria, List<Hotel> hotels, List<Activity> activities, double budget) {
        List<Hotel> candidates;
        if (hotelCriteria.preferMinPricesOverMaxStars()) {
            List<Hotel> cheapestHotels = findCheapestHotel(hotels, budget);
            candidates = findBestHotel(cheapestHotels, budget);

        } else {
            List<Hotel> bestHotels = findBestHotel(hotels, budget);
            candidates = findCheapestHotel(bestHotels, budget);
        }
        return findMaxActivitiesAround(candidates, activities, activityCriteria.maxDistance());
    }

    @Override
    public Pair<Hotel, List<Activity>> optimize(HotelCriteria hotelCriteria, ActivityCriteria activityCriteria, String city, int days, double budget) {
        List<Hotel> hotels = hotelService.getForCity(city);
        List<Activity> activities = activityService.getForCity(city);

        Hotel bestHotel = findHotel(hotelCriteria, activityCriteria, hotels, activities, (double) budget / days);
        int newBudget = (int) (budget - bestHotel.price() * days);

        // TODO: Maybe return the new budget ?

        return new Pair<>(bestHotel, null);
    }
}
