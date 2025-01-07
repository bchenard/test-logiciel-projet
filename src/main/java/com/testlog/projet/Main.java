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
        TransportService transportService = new TransportService();
        HotelService hotelService = new HotelService();
        ActivityService activityService = new ActivityService();

        // TODO: To change once transport service takes transportService as parameter
        TransportOptimizer transportOptimizer = new TransportOptimizer();

        CitySolver citySolver = new CitySolver();
        CityOptimizer cityOptimizer = new CityOptimizer(hotelService, activityService, citySolver);

        Optimizer optimizer = new Optimizer(transportOptimizer, cityOptimizer);


        TransportCriteria transportCriteria = new TransportCriteria(TransportationMode.NOT_SPECIFIED, true);
        HotelCriteria hotelCriteria = new HotelCriteria(true, 3);

        List<ActivityType> categories = List.of(ActivityType.CULTURE, ActivityType.CINEMA, ActivityType.SPORT, ActivityType.MUSIC);
        ActivityCriteria activityCriteria = new ActivityCriteria(50, categories);

        LocalDateTime start = LocalDateTime.of(2022, 6, 1, 0, 0);
        Duration duration = Duration.ofDays(7);
        AdditionalCriteria additionalCriteria = new AdditionalCriteria(start, 1000, duration, "Paris", "Bordeaux");

        Package solution = optimizer.solve(transportCriteria, hotelCriteria, activityCriteria, additionalCriteria);
    }
}
