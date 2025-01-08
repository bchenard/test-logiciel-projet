package com.testlog.projet;


import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.AdditionalCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.optimize.Optimizer;
import com.testlog.projet.optimize.TransportOptimizer;
import com.testlog.projet.optimize.city.CityOptimizer;
import com.testlog.projet.optimize.city.CitySolver;
import com.testlog.projet.services.ActivityService;
import com.testlog.projet.services.HotelService;
import com.testlog.projet.services.TransportService;
import com.testlog.projet.types.ActivityType;
import com.testlog.projet.types.Package;
import com.testlog.projet.types.TransportationMode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Optimizer optimizer = getOptimizer();

        TransportCriteria transportCriteria = new TransportCriteria(TransportationMode.TRAIN, true);
        HotelCriteria hotelCriteria = new HotelCriteria(true, 3);

        List<ActivityType> categories = List.of(ActivityType.CULTURE, ActivityType.CINEMA, ActivityType.SPORT, ActivityType.MUSIC);
        ActivityCriteria activityCriteria = new ActivityCriteria(50, categories);

        LocalDateTime start = LocalDateTime.of(2024, 6, 3, 0, 0);
        Duration duration = Duration.ofDays(3);
        AdditionalCriteria additionalCriteria = new AdditionalCriteria(start, 1000, duration, "Paris", "Bordeaux");

        Package solution = optimizer.solve(transportCriteria, hotelCriteria, activityCriteria, additionalCriteria);
        System.out.println(solution);
    }

    private static Optimizer getOptimizer() {
        TransportService transportService = new TransportService();
        HotelService hotelService = new HotelService();
        ActivityService activityService = new ActivityService();

        TransportOptimizer transportOptimizer = new TransportOptimizer(transportService);

        CitySolver citySolver = new CitySolver();
        CityOptimizer cityOptimizer = new CityOptimizer(hotelService, activityService, citySolver);

        return new Optimizer(transportOptimizer, cityOptimizer);
    }
}
