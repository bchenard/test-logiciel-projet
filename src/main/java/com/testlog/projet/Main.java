package com.testlog.projet;


import com.testlog.projet.optimize.TransportOptimizer;
import com.testlog.projet.services.ActivityService;
import com.testlog.projet.types.Activity;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        ActivityService activityService = new ActivityService();

        System.out.println("Activities in Rennes:");
        List<Activity> rennesActivities = activityService.getForCity("Rennes");
        rennesActivities.forEach(System.out::println);

        System.out.println("\nActivities in Nantes:");
        List<Activity> nantesActivities = activityService.getForCity("Nantes");
        nantesActivities.forEach(System.out::println);

        System.out.println("\nActivities in Unknown City:");
        List<Activity> unknownActivities = activityService.getForCity("UnknownCity");

        unknownActivities.forEach(System.out::println);

        TransportOptimizer to = new TransportOptimizer();
        ComposedTrip ct = to.getOptimizedTrip("Toulouse", "Bordeaux");

        System.out.println("Path found : " + ct.toString());
    }
    }
