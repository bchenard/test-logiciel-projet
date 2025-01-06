package com.testlog.projet.optimize.city;

import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.services.ActivityService;
import com.testlog.projet.services.HotelService;
import com.testlog.projet.types.ActivityType;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CityOptimizerTest {

    @Test
    public void runTest() {
        HotelService hotelService = new HotelService();
        ActivityService activityService = new ActivityService();
        CitySolver citySolver = new CitySolver();
        CityOptimizer cityOptimizer = new CityOptimizer(hotelService, activityService, citySolver);
        int startDay = 0;
        int nbDays = 2;
        double budget = 99999;

        HotelCriteria hotelCriteria = new HotelCriteria(true, 3);
        List<ActivityType> types = List.of(ActivityType.MUSIC, ActivityType.CINEMA, ActivityType.CULTURE, ActivityType.SPORT);
        ActivityCriteria activityCriteria = new ActivityCriteria(300, types);

        cityOptimizer.optimize("Bordeaux", startDay, nbDays, budget, hotelCriteria, activityCriteria);
    }

    // TODO: Add more tests
}