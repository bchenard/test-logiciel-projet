package com.testlog.projet.optimize;

import com.testlog.projet.criteria.AdditionalCriteria;
import com.testlog.projet.criteria.CityCriteria;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.optimize.city.CityOptimizer;
import com.testlog.projet.optimize.city.CitySolver;
import com.testlog.projet.services.ActivityService;
import com.testlog.projet.services.HotelService;
import com.testlog.projet.services.TransportService;
import com.testlog.projet.services.io.FileReader;
import com.testlog.projet.types.ActivityType;
import com.testlog.projet.types.Package;
import com.testlog.projet.types.TransportationMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptimizerIT {

    private Optimizer optimizer;

    @BeforeEach
    public void setUp() {
        FileReader sharedReader = new FileReader();

        TransportService transportService = new TransportService(sharedReader);
        HotelService hotelService = new HotelService(sharedReader);
        ActivityService activityService = new ActivityService(sharedReader);

        TransportOptimizer transportOptimizer = new TransportOptimizer(transportService);

        CitySolver citySolver = new CitySolver();
        CityOptimizer cityOptimizer = new CityOptimizer(hotelService, activityService, citySolver);

        optimizer = new Optimizer(transportOptimizer, cityOptimizer);
    }

    @Test
    public void testSolve_stringOutput() {
        TransportCriteria transportCriteria = new TransportCriteria(TransportationMode.TRAIN, true);

        List<ActivityType> categories = List.of(ActivityType.CULTURE, ActivityType.CINEMA, ActivityType.SPORT, ActivityType.MUSIC);
        CityCriteria cityCriteria = new CityCriteria(50, categories, false, 3);

        LocalDateTime start = LocalDateTime.of(2025, 1, 8, 0, 0);
        Duration duration = Duration.ofDays(10);
        AdditionalCriteria additionalCriteria = new AdditionalCriteria(start, 1200, duration, "Paris", "Bordeaux");

        Package solution = optimizer.solve(transportCriteria, cityCriteria, additionalCriteria);
        String result = solution.toString();
        System.out.println(result);

        assertTrue(result.contains("activities"));
        assertTrue(result.contains("Activity{")); // At least one activity
        assertTrue(result.contains("hotel"));
        assertTrue(result.contains("firstTrip"));
        assertTrue(result.contains("returnTrip"));
        assertTrue(result.contains("totalPrice"));
    }

    @Test
    public void testSolve_packageOutput() {
        TransportCriteria transportCriteria = new TransportCriteria(TransportationMode.TRAIN, true);

        List<ActivityType> categories = List.of(ActivityType.CULTURE, ActivityType.CINEMA, ActivityType.SPORT, ActivityType.MUSIC);
        CityCriteria cityCriteria = new CityCriteria(500, categories, false, 3);

        LocalDateTime start = LocalDateTime.of(2025, 1, 8, 0, 0);
        Duration duration = Duration.ofDays(10);
        AdditionalCriteria additionalCriteria = new AdditionalCriteria(start, 1200, duration, "Paris", "Bordeaux");

        Package solution = optimizer.solve(transportCriteria, cityCriteria, additionalCriteria);

        // Count non-null activities
        long activityCount = solution.activities().stream().filter(Objects::nonNull).count();
        assertEquals(3, activityCount);
        assertEquals(3, solution.hotel().stars());
        assertEquals(1, solution.firstTrip().getTrips().size());
        assertEquals(2, solution.returnTrip().getTrips().size());
    }

}
