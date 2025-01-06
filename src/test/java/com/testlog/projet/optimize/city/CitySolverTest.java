package com.testlog.projet.optimize.city;

import com.testlog.projet.services.ActivityService;
import org.junit.jupiter.api.Test;

public class CitySolverTest {

    @Test
    public void runTest() {
        ActivityService activityService = new ActivityService();
        CitySolver citySolver = new CitySolver();
        int startDay = 0;
        int nbDays = 2;
        double budget = 99999;

        citySolver.solve(activityService.getForCity("Bordeaux"), startDay, nbDays, budget);
    }

    // TODO: Add more tests
}