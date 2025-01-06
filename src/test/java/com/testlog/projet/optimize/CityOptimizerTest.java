package com.testlog.projet.optimize;

import com.testlog.projet.services.ActivityService;
import com.testlog.projet.services.HotelService;
import org.junit.jupiter.api.Test;

public class CityOptimizerTest {

    @Test
    public void runTest() {
        HotelService hotelService = new HotelService();
        ActivityService activityService = new ActivityService();
        CityOptimizer cityOptimizer = new CityOptimizer(hotelService, activityService);
        int startDay = 0;
        int nbDays = 2;
        double budget = 99999;
        cityOptimizer.solve(activityService.getForCity("Bordeaux"), startDay, nbDays, budget);
    }
}