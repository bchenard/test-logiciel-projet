package com.testlog.projet.optimize;

import com.google.ortools.Loader;
import com.google.ortools.init.OrToolsVersion;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.services.ICityService;
import com.testlog.projet.types.Activity;
import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.LatLng;
import com.testlog.projet.types.Pair;

import java.util.List;

public class CityOptimizer implements ICityOptimizer {

    private final ICityService<Hotel> hotelService;
    private final ICityService<Activity> activityService;

    public CityOptimizer(ICityService<Hotel> hotelService, ICityService<Activity> activityService) {
        this.hotelService = hotelService;
        this.activityService = activityService;
    }

    public void solve(List<Activity> activities, int startDay, int nbDays, double budget) {
        for (Activity activity : activities) {
            System.out.println(activity);
        }
        Loader.loadNativeLibraries();

        System.out.println("Google OR-Tools version: " + OrToolsVersion.getVersionString());

        MPSolver solver = MPSolver.createSolver("CBC");
        if (solver == null) {
            System.out.println("Could not create solver CBC");
            return;
        }

        MPVariable[][] vars = new MPVariable[nbDays][activities.size()];

        for (int d = 0; d < nbDays; d++) {
            for (int a = 0; a < activities.size(); a++) {
                vars[d][a] = solver.makeBoolVar("day_" + d + "_activity_" + a);
            }
        }

        for (int a = 0; a < activities.size(); a++) {
            // TODO: getter for only one day instead of getting the entire list ?
            List<Boolean> availability = activities.get(a).availability();
            for (int d = 0; d < nbDays; d++) {
                int currentDay = (startDay + d) % 7;

                if (!availability.get(currentDay)) {
                    // Setting the coefficient to 0 to make sure the activity cannot be selected
                    MPConstraint constraint = solver.makeConstraint(0, 0);
                    constraint.setCoefficient(vars[d][a], 1);
                }
            }
        }

        // Cannot have more than one activity on the same day
        for (int d = 0; d < nbDays; d++) {
            MPConstraint constraint = solver.makeConstraint(0, 1);
            for (int a = 0; a < activities.size(); a++) {
                constraint.setCoefficient(vars[d][a], 1);
            }
        }

        // Same activity cannot be selected on two different days
        for (int a = 0; a < activities.size(); a++) {
            MPConstraint constraint = solver.makeConstraint(0, 1);
            for (int d = 0; d < nbDays; d++) {
                constraint.setCoefficient(vars[d][a], 1);
            }
        }

        // sum_d(vars[d][a]) is whether or not we want to do activity 'a' on any day of the trip (0 or 1)
        // sum_d(price_a * vars[d][a]) is trivially the price of activity 'a' if we want to do it, otherwise 0
        // sum_d(sum_a(price_a * vars[d][a])) is the total price of all activities we want to do (inverse sum)

        MPConstraint budgetConstraint = solver.makeConstraint(0, budget);
        for (int a = 0; a < activities.size(); a++) { // sum_a
            double price = activities.get(a).price();
            for (int d = 0; d < nbDays; d++) { // sum_d
                // price_a * vars[d][a]
                budgetConstraint.setCoefficient(vars[d][a], price);
            }
        }

        // Maximization of the number of activities, trivially sum_a(sum_d(vars[d][a]))
        MPObjective objective = solver.objective();
        for (int a = 0; a < activities.size(); a++) {
            for (int d = 0; d < nbDays; d++) {
                objective.setCoefficient(vars[d][a], 1);
            }
        }
        objective.setMaximization();

        System.out.println("Number of variables = " + solver.numVariables());

        System.out.println("Solving with " + solver.solverVersion());
        final MPSolver.ResultStatus resultStatus = solver.solve();

        System.out.println("Status: " + resultStatus);
        if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("The problem does not have an optimal solution!");
            if (resultStatus == MPSolver.ResultStatus.FEASIBLE) {
                System.out.println("A potentially suboptimal solution was found");
            } else {
                System.out.println("The solver could not solve the problem.");
                return;
            }
        }

        System.out.println("\nSolution\n========");
        System.out.println("Objective value (number of activities) = " + objective.value());
        System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");

        for (int d = 0; d < nbDays; d++) {
            System.out.print("Day " + d + ": ");
            for (int a = 0; a < activities.size(); a++) {
                if (vars[d][a].solutionValue() > 0.5) {
                    System.out.print(activities.get(a).type() + " ");
                }
            }
            System.out.println();
        }
        // Show array of vars
        for (int d = 0; d < nbDays; d++) {
            for (int a = 0; a < activities.size(); a++) {
                System.out.print(vars[d][a].solutionValue() + " ");
            }
            System.out.println();
        }
    }

    private List<Hotel> findCheapestHotel(List<Hotel> hotels, double budget) {
        double minPrice = hotels.stream().mapToDouble(Hotel::price).min().orElse(Double.MAX_VALUE);

        if (minPrice > budget) {
            return List.of();
        }

        return hotels.stream().filter(hotel -> hotel.price() == minPrice).toList();
    }

    private List<Hotel> findBestHotel(List<Hotel> hotels, double budget) {
        List<Hotel> fitBudget = hotels.stream().filter(hotel -> hotel.price() <= budget).toList();

        double maxStars = fitBudget.stream().mapToInt(Hotel::stars).max().orElse(0);

        return hotels.stream().filter(hotel -> hotel.stars() == maxStars).toList();
    }

    // Potentially return null
    // TODO: Find max activities around the hotel -> Should check activities preferences (and distance), and days of availability of the activities
    // Helper function to get activities around a hotel ?
    private Hotel findMaxActivitiesAround(List<Hotel> hotels, List<Activity> activities, double maxDistance) {
        Hotel best = null;
        int maxActivities = 0;
        for (Hotel hotel : hotels) {
            LatLng hotelCoordinates = hotel.coordinates();
            int activitiesAround = (int) activities.stream().filter(activity -> hotelCoordinates.distance(activity.coordinates()) <= maxDistance).count();

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
