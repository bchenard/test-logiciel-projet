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
import com.testlog.projet.services.io.FileReader;
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
        HotelCriteria hotelCriteria = new HotelCriteria(false, 3);

        List<ActivityType> categories = List.of(ActivityType.CULTURE, ActivityType.CINEMA, ActivityType.SPORT, ActivityType.MUSIC);
        ActivityCriteria activityCriteria = new ActivityCriteria(50, categories);

        LocalDateTime start = LocalDateTime.of(2025, 1, 8, 0, 0);
        Duration duration = Duration.ofDays(10);
        AdditionalCriteria additionalCriteria = new AdditionalCriteria(start, 500, duration, "Paris", "Bordeaux");

        Package solution = optimizer.solve(transportCriteria, hotelCriteria, activityCriteria, additionalCriteria);
        System.out.println("Forfait proposé :\n" + solution.toString());
    }

    private static Optimizer getOptimizer() {
        FileReader sharedReader = new FileReader();

        TransportService transportService = new TransportService(sharedReader);
        HotelService hotelService = new HotelService(sharedReader);
        ActivityService activityService = new ActivityService(sharedReader);

        TransportOptimizer transportOptimizer = new TransportOptimizer(transportService);

        CitySolver citySolver = new CitySolver();
        CityOptimizer cityOptimizer = new CityOptimizer(hotelService, activityService, citySolver);

        return new Optimizer(transportOptimizer, cityOptimizer);
    }
}
