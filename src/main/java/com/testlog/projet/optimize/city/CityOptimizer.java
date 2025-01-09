package com.testlog.projet.optimize.city;

import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.services.ICityService;
import com.testlog.projet.types.Activity;
import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.LatLng;
import com.testlog.projet.types.Pair;

import java.util.List;
import java.util.Objects;

public class CityOptimizer implements ICityOptimizer {

    private final ICityService<Hotel> hotelService;
    private final ICityService<Activity> activityService;
    private final ICitySolver citySolver;

    public CityOptimizer(ICityService<Hotel> hotelService, ICityService<Activity> activityService, ICitySolver citySolver) {
        this.hotelService = hotelService;
        this.activityService = activityService;
        this.citySolver = citySolver;
    }

    /**
     * Remove hotels that do not meet the criteria.
     */
    private List<Hotel> filterHotels(List<Hotel> hotels, HotelCriteria criteria) {
        return hotels.stream()
                .filter((h) -> h.stars() >= criteria.minStars())
                .toList();
    }

    /**
     * Remove activities that are too far away from the hotel or not in the right category.
     */
    private List<Activity> filterActivities(List<Activity> activities, ActivityCriteria criteria, LatLng hotelCoords) {
        return activities.stream()
                .filter((a) -> hotelCoords.distance(a.coordinates()) <= criteria.maxDistance())
                .filter((a) -> criteria.categories().contains(a.type()))
                .toList();
    }

    /**
     * Returns the total price of a hotel over multiple days and a list of activities. Null activities are ignored.
     */
    public double getTotalPrice(Pair<Hotel, List<Activity>> pair, int nbDays) {
        return pair.first().price() * nbDays + pair.second().stream().filter(Objects::nonNull).mapToDouble(Activity::price).sum();
    }

    /**
     * Count the number of activities in a list. Null values are ignored.
     */
    private double countActivities(List<Activity> activities) {
        return activities.stream().filter(Objects::nonNull).count();
    }

    /**
     * Returns true if 'a' is better than 'b' according to the given hotel criteria.
     * If 'b' is null, returns true.
     * Does not care about minStars.
     * If 'a' and 'b' look the same, 'a' is considered better.
     */
    boolean compare(Pair<Hotel, List<Activity>> a, Pair<Hotel, List<Activity>> b, HotelCriteria hotelCriteria, int nbDays) {
        if (b == null) return true;

        List<Activity> activitiesA = a.second();
        List<Activity> activitiesB = b.second();

        double activityCountA = countActivities(activitiesA);
        double activityCountB = countActivities(activitiesB);

        // According to the specification, the app primarily aims to maximize the number of activities while price < budget
        if (activityCountA > activityCountB) return true;
        if (activityCountA < activityCountB) return false;

        double priceA = getTotalPrice(a, nbDays);
        double priceB = getTotalPrice(b, nbDays);
        int starsA = a.first().stars();
        int starsB = b.first().stars();

        if (hotelCriteria.preferMinPricesOverMaxStars()) {
            if (priceA < priceB) return true;
            if (priceA > priceB) return false;
            return a.first().stars() >= b.first().stars();
        } else {
            if (starsA > starsB) return true;
            if (starsA < starsB) return false;
            return priceA <= priceB;
        }
    }

    @Override
    public Pair<Hotel, List<Activity>> optimize(String city, int startDay, int nbDays, double budget, HotelCriteria hotelCriteria, ActivityCriteria activityCriteria) {
        List<Hotel> hotels = filterHotels(hotelService.getForCity(city), hotelCriteria);
        List<Activity> activities = activityService.getForCity(city);

        Pair<Hotel, List<Activity>> optimal = null;

        for (Hotel hotel : hotels) {
            List<Activity> nearActivities = filterActivities(activities, activityCriteria, hotel.coordinates());

            // Check if hotel is in budget
            double hotelPrice = hotel.price() * nbDays;
            if (hotelPrice > budget) continue;

            // The following object can contain null multiple times
            // meaning no activity planned for the given date
            List<Activity> solution = citySolver.solve(nearActivities, startDay, nbDays, budget - hotel.price() * nbDays);

            if (compare(new Pair<>(hotel, solution), optimal, hotelCriteria, nbDays)) {
                optimal = new Pair<>(hotel, solution);
            }
        }
        if (optimal == null) {
            throw new IllegalArgumentException("No solution found : no hotel available within the budget");
        }
        return optimal;
    }
}
