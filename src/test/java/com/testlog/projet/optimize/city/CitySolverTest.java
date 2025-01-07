package com.testlog.projet.optimize.city;

import com.testlog.projet.services.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    public void setUp() {

    }


}